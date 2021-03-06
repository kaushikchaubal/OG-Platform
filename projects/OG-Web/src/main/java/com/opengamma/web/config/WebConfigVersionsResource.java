/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.config;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.id.UniqueId;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigHistoryRequest;
import com.opengamma.master.config.ConfigHistoryResult;
import com.opengamma.util.paging.PagingRequest;
import com.opengamma.web.WebPaging;

/**
 * RESTful resource for all versions of an config.
 */
@Path("/configs/{configId}/versions")
@Produces(MediaType.TEXT_HTML)
public class WebConfigVersionsResource extends AbstractWebConfigResource {

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  public WebConfigVersionsResource(final AbstractWebConfigResource parent) {
    super(parent);
  }

  //-------------------------------------------------------------------------
  @GET
  @SuppressWarnings({"unchecked", "rawtypes" })
  public String getHTML() {
    ConfigHistoryRequest request = new ConfigHistoryRequest(data().getConfig().getUniqueId(), Object.class);
    ConfigHistoryResult<?> result = data().getConfigMaster().history(request);
    
    FlexiBean out = createRootData();
    out.put("versionsResult", result);
    out.put("versions", result.getValues());
    return getFreemarker().build(HTML_DIR + "configversions.ftl", out);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @SuppressWarnings({"unchecked", "rawtypes" })
  public Response getJSON(
      @QueryParam("pgIdx") Integer pgIdx,
      @QueryParam("pgNum") Integer pgNum,
      @QueryParam("pgSze") Integer pgSze) {
    PagingRequest pr = buildPagingRequest(pgIdx, pgNum, pgSze);
    ConfigHistoryRequest request = new ConfigHistoryRequest(data().getConfig().getUniqueId(), Object.class);
    request.setPagingRequest(pr);
    ConfigHistoryResult<?> result = data().getConfigMaster().history(request);
    
    FlexiBean out = createRootData();
    out.put("versionsResult", result);
    out.put("versions", result.getValues());
    out.put("paging", new WebPaging(result.getPaging(), data().getUriInfo()));
    String json = getFreemarker().build(JSON_DIR + "configversions.ftl", out);
    return Response.ok(json).build();
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = super.createRootData();
    ConfigDocument doc = data().getConfig();
    out.put("configDoc", doc);
    out.put("config", doc.getConfig().getValue());
    out.put("deleted", !doc.isLatest());
    return out;
  }

  //-------------------------------------------------------------------------
  @Path("{versionId}")
  public WebConfigVersionResource findVersion(@PathParam("versionId") String idStr) {
    data().setUriVersionId(idStr);
    ConfigDocument doc = data().getConfig();
    UniqueId combined = doc.getUniqueId().withVersion(idStr);
    if (doc.getUniqueId().equals(combined) == false) {
      ConfigDocument versioned = data().getConfigMaster().get(combined);
      data().setVersioned(versioned);
    } else {
      data().setVersioned(doc);
    }
    return new WebConfigVersionResource(this);
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebConfigData data) {
    String configId = data.getBestConfigUriId(null);
    return data.getUriInfo().getBaseUriBuilder().path(WebConfigVersionsResource.class).build(configId);
  }

}
