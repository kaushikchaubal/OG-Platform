/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.blotter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.time.calendar.ZonedDateTime;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.joda.beans.Bean;
import org.joda.beans.MetaBean;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opengamma.DataNotFoundException;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.conversion.JodaBeanConverters;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.LongShort;
import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;
import com.opengamma.financial.security.capfloor.CapFloorSecurity;
import com.opengamma.financial.security.equity.EquityVarianceSwapSecurity;
import com.opengamma.financial.security.fra.FRASecurity;
import com.opengamma.financial.security.future.InterestRateFutureSecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.option.BarrierDirection;
import com.opengamma.financial.security.option.BarrierType;
import com.opengamma.financial.security.option.ExerciseType;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.MonitoringType;
import com.opengamma.financial.security.option.NonDeliverableFXOptionSecurity;
import com.opengamma.financial.security.option.SamplingFrequency;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.FixedInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingGearingIRLeg;
import com.opengamma.financial.security.swap.FloatingInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingRateType;
import com.opengamma.financial.security.swap.FloatingSpreadIRLeg;
import com.opengamma.financial.security.swap.InterestRateNotional;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.id.ObjectId;
import com.opengamma.id.UniqueId;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.master.position.ManageablePosition;
import com.opengamma.master.position.ManageableTrade;
import com.opengamma.master.position.PositionMaster;
import com.opengamma.master.position.PositionSearchRequest;
import com.opengamma.master.position.PositionSearchResult;
import com.opengamma.master.security.ManageableSecurity;
import com.opengamma.master.security.ManageableSecurityLink;
import com.opengamma.master.security.SecurityDocument;
import com.opengamma.master.security.SecurityMaster;
import com.opengamma.master.security.SecuritySearchRequest;
import com.opengamma.master.security.SecuritySearchResult;
import com.opengamma.master.security.impl.MasterSecuritySource;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.OpenGammaClock;
import com.opengamma.web.FreemarkerOutputter;

/**
 * TODO move some of this into subresources?
 * TODO date and time zone handling
 */
@Path("blotter")
public class BlotterResource {

  // TODO this should be configurable, should be able to add from client projects
  // TODO where should these live? is this the right place?
  /**
   * All the securities and related types supported by the blotter.
   */
  /* package */ static final Set<MetaBean> s_metaBeans = Sets.<MetaBean>newHashSet(
      FXForwardSecurity.meta(),
      SwapSecurity.meta(),
      SwaptionSecurity.meta(),
      FixedInterestRateLeg.meta(),
      FloatingInterestRateLeg.meta(),
      FloatingSpreadIRLeg.meta(),
      FloatingGearingIRLeg.meta(),
      InterestRateNotional.meta(),
      CapFloorCMSSpreadSecurity.meta(),
      NonDeliverableFXOptionSecurity.meta(),
      FXOptionSecurity.meta(),
      FRASecurity.meta(),
      CapFloorSecurity.meta(),
      EquityVarianceSwapSecurity.meta(),
      FXBarrierOptionSecurity.meta(),
      NonDeliverableFXOptionSecurity.meta());
  private final NewOtcTradeBuilder _newTradeBuilder;

  private static final Map<Class<?>, Class<?>> s_underlyingSecurityTypes = ImmutableMap.<Class<?>, Class<?>>of(
      IRFutureOptionSecurity.class, InterestRateFutureSecurity.class,
      SwaptionSecurity.class, SwapSecurity.class);

  private static final Map<Class<?>, String> s_endpoints = Maps.newHashMap();

  // TODO this is an ugly but it's only temporay - fix or remove when the HTML bean info isn't needed
  static {
    s_endpoints.put(Frequency.class, "frequencies");
    s_endpoints.put(ExerciseType.class, "exercisetypes");
    s_endpoints.put(DayCount.class, "daycountconventions");
    s_endpoints.put(BusinessDayConvention.class, "businessdayconventions");
    s_endpoints.put(BarrierType.class, "barriertypes");
    s_endpoints.put(BarrierDirection.class, "barrierdirections");
    s_endpoints.put(SamplingFrequency.class, "samplingfrequencies");
    s_endpoints.put(FloatingRateType.class, "floatingratetypes");
    s_endpoints.put(LongShort.class, "longshort");
    s_endpoints.put(MonitoringType.class, "monitoringtype");
  }

  private static final List<String> s_otherTypeNames = Lists.newArrayList();
  private static final List<String> s_securityTypeNames = Lists.newArrayList();
  private static final Map<String, MetaBean> s_metaBeansByTypeName = Maps.newHashMap();

  static {
    JodaBeanConverters.getInstance();
    for (MetaBean metaBean : s_metaBeans) {
      Class<? extends Bean> beanType = metaBean.beanType();
      String typeName = beanType.getSimpleName();
      s_metaBeansByTypeName.put(typeName, metaBean);
      if (isSecurity(beanType)) {
        s_securityTypeNames.add(typeName);
      } else {
        s_otherTypeNames.add(typeName);
      }
    }
    // TODO use constants for these
    s_otherTypeNames.add("OtcTrade");
    s_otherTypeNames.add("FungibleTrade");
    Collections.sort(s_otherTypeNames);
    Collections.sort(s_securityTypeNames);
  }

  private final SecurityMaster _securityMaster;
  private final PortfolioMaster _portfolioMaster;
  private final PositionMaster _positionMaster;
  private final MasterSecuritySource _securitySource;

  private FreemarkerOutputter _freemarker;

  public BlotterResource(SecurityMaster securityMaster, PortfolioMaster portfolioMaster, PositionMaster positionMaster) {
    ArgumentChecker.notNull(securityMaster, "securityMaster");
    ArgumentChecker.notNull(portfolioMaster, "portfolioMaster");
    ArgumentChecker.notNull(positionMaster, "positionMaster");
    _securityMaster = securityMaster;
    _portfolioMaster = portfolioMaster;
    _positionMaster = positionMaster;
    _newTradeBuilder = new NewOtcTradeBuilder(_securityMaster, _positionMaster, s_metaBeans);
    _securitySource = new MasterSecuritySource(_securityMaster);
  }

  /* package */
  static boolean isSecurity(Class<?> type) {
    if (type == null) {
      return false;
    } else if (ManageableSecurity.class.equals(type)) {
      return true;
    } else {
      return isSecurity(type.getSuperclass());
    }
  }

  @Context
  public void setServletContext(final ServletContext servletContext) {
    _freemarker = new FreemarkerOutputter(servletContext);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("types")
  public String getTypes() {
    Map<Object, Object> data = map("title", "Types",
                                   "securityTypeNames", s_securityTypeNames,
                                   "otherTypeNames", s_otherTypeNames);
    return _freemarker.build("blotter/bean-types.ftl", data);
  }

  @SuppressWarnings("unchecked")
  @GET
  @Produces(MediaType.TEXT_HTML)
  @Path("types/{typeName}")
  public String getStructure(@PathParam("typeName") String typeName) {
    Map<String, Object> beanData;
    if (typeName.equals("OtcTrade")) {
      beanData = OtcTradeBuilder.tradeStructure();
    } else if (typeName.equals("FungibleTrade")) {
      beanData = FungibleTradeBuilder.tradeStructure();
    } else {
      MetaBean metaBean = s_metaBeansByTypeName.get(typeName);
      if (metaBean == null) {
        throw new DataNotFoundException("Unknown type name " + typeName);
      }
      BeanStructureBuilder structureBuilder = new BeanStructureBuilder(s_metaBeans,
                                                                       s_underlyingSecurityTypes,
                                                                       s_endpoints);
      BeanVisitorDecorator propertyNameFilter = new PropertyNameFilter("externalIdBundle", "securityType");
      BeanTraverser traverser = new BeanTraverser(propertyNameFilter);
      beanData = (Map<String, Object>) traverser.traverse(metaBean, structureBuilder);
    }
    return _freemarker.build("blotter/bean-structure.ftl", beanData);
  }

  // TODO move this logic to trade builder? can it populate a BeanDataSink to build the JSON?
  // TODO refactor this, it's ugly. can the security and trade logic be cleanly moved into classes for OTC & fungible?
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("trades/{tradeId}")
  public String getJSON(@PathParam("tradeId") String tradeIdStr) {
    ManageableTrade trade = findTrade(tradeIdStr);
    ManageableSecurity security = findSecurity(trade.getSecurityLink());
    // TODO visitor that returns trade builder? what about underlying?
    boolean isOtc;
    if (security instanceof FinancialSecurity) {
      isOtc = ((FinancialSecurity) security).accept(new OtcSecurityVisitor());
    } else {
      isOtc = false;
    }
    JSONObject root = new JSONObject();
    try {
      JsonDataSink tradeSink = new JsonDataSink();
      if (isOtc) {
        OtcTradeBuilder.extractTradeData(trade, tradeSink);
        MetaBean securityMetaBean = s_metaBeansByTypeName.get(security.getClass().getSimpleName());
        if (securityMetaBean == null) {
          throw new DataNotFoundException("No MetaBean is registered for security type " + security.getClass().getName());
        }
        BeanVisitor<JSONObject> securityVisitor = new BuildingBeanVisitor<JSONObject>(security, new JsonDataSink());
        PropertyFilter securityPropertyFilter = new PropertyFilter(ManageableSecurity.meta().securityType());
        JSONObject securityJson = (JSONObject) new BeanTraverser(securityPropertyFilter).traverse(securityMetaBean, securityVisitor);
        root.put("security", securityJson);
        // TODO filter out underlyingId for securities with OTC underlying
        // TODO include underlying security for securities with OTC underlying
      } else {
        FungibleTradeBuilder.extractTradeData(trade, tradeSink);
      }
      JSONObject tradeJson = tradeSink.finish();
      root.put("trade", tradeJson);
    } catch (JSONException e) {
      throw new OpenGammaRuntimeException("Failed to build JSON", e);
    }
    return root.toString();
  }

  // TODO this is a bit of a palaver, surely there's something that already does this?
  private ManageableTrade findTrade(String tradeIdStr) {
    ObjectId tradeId = ObjectId.parse(tradeIdStr);
    PositionSearchRequest positionSearch = new PositionSearchRequest();
    positionSearch.addTradeObjectId(tradeId);
    PositionSearchResult searchResult = _positionMaster.search(positionSearch);
    ManageablePosition position = searchResult.getFirstPosition();
    if (position == null) {
      throw new DataNotFoundException("No position found with trade ID " + tradeId);
    }
    // TODO this should never fail but there's a bug in position searching
    ManageableTrade trade = position.getTrade(tradeId);
    if (trade == null) {
      throw new DataNotFoundException("No trade with ID " + tradeId + " found on position " + position.getUniqueId() +
                                             ", this is a known bug (http://jira.opengamma.com/browse/PLAT-2946)");
    }
    return trade;
  }

  private ManageableSecurity findSecurity(ManageableSecurityLink securityLink) {
    SecurityDocument securityDocument;
    if (securityLink.getObjectId() != null) {
      securityDocument = _securityMaster.get(securityLink.getObjectId(), VersionCorrection.LATEST);
    } else {
      SecuritySearchRequest searchRequest = new SecuritySearchRequest(securityLink.getExternalId());
      SecuritySearchResult searchResult = _securityMaster.search(searchRequest);
      securityDocument = searchResult.getFirstDocument();
      if (securityDocument == null) {
        throw new DataNotFoundException("No security found with external IDs " + securityLink.getExternalId());
      }
    }
    return securityDocument.getSecurity();
  }

  @POST
  @Path("trades")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public String createOtcTrade(@FormParam("trade") String tradeJsonStr) {
    return createOtcTrade(tradeJsonStr, _newTradeBuilder);
    // TODO don't return JSON, just set the created header with the URL
  }

  @PUT
  @Path("trades/{tradeIdStr}")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  // TODO the config endpoint uses form params for the JSON. why? better to use a MessageBodyWriter?
  public String updateOtcTrade(@FormParam("trade") String tradeJsonStr,
                               @PathParam("tradeIdStr") String tradeIdStr) {
    UniqueId tradeId = UniqueId.parse(tradeIdStr);
    OtcTradeBuilder tradeBuilder = new ExistingOtcTradeBuilder(tradeId, _securityMaster, _positionMaster, s_metaBeans);
    return createOtcTrade(tradeJsonStr, tradeBuilder);
  }

  private String createOtcTrade(String jsonStr, OtcTradeBuilder tradeBuilder) {
    try {
      JSONObject json = new JSONObject(jsonStr);
      JSONObject tradeJson = json.getJSONObject("trade");
      JSONObject securityJson = json.getJSONObject("security");
      JSONObject underlyingJson = json.optJSONObject("underlying");
      BeanDataSource tradeData = new JsonBeanDataSource(tradeJson);
      BeanDataSource securityData = new JsonBeanDataSource(securityJson);
      BeanDataSource underlyingData;
      if (underlyingJson != null) {
        underlyingData = new JsonBeanDataSource(underlyingJson);
      } else {
        underlyingData = null;
      }
      UniqueId tradeId = tradeBuilder.buildAndSaveTrade(tradeData, securityData, underlyingData);
      return new JSONObject(ImmutableMap.of("tradeId", tradeId)).toString();
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to parse JSON", e);
    }
  }

  private static Map<Object, Object> map(Object... values) {
    final Map<Object, Object> result = Maps.newHashMap();
    for (int i = 0; i < values.length / 2; i++) {
      result.put(values[i * 2], values[(i * 2) + 1]);
    }
    result.put("now", ZonedDateTime.now(OpenGammaClock.getInstance()));
    return result;
  }

  @Path("lookup")
  public BlotterLookupResource getLookupResource() {
    return new BlotterLookupResource();
  }

  // TODO create fungible trade - identifier and quantity

}