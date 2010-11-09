/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.exchange.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.util.db.Paging;

/**
 * Result providing the history of an exchange.
 * <p>
 * The returned documents may be a mixture of versions and corrections.
 * The document instant fields are used to identify which are which.
 * See {@link ExchangeHistoryRequest} for more details.
 */
@BeanDefinition
public class ExchangeHistoryResult extends DirectBean {

  /**
   * The paging information.
   */
  @PropertyDefinition
  private Paging _paging;
  /**
   * The list of matched exchange documents, not null.
   */
  @PropertyDefinition
  private final List<ExchangeDocument> _documents = new ArrayList<ExchangeDocument>();

  /**
   * Creates an instance.
   */
  public ExchangeHistoryResult() {
  }

  /**
   * Creates an instance.
   * @param coll  the collection of documents to add, not null
   */
  public ExchangeHistoryResult(Collection<ExchangeDocument> coll) {
    _documents.addAll(coll);
    _paging = Paging.of(coll);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the returned exchanges from within the documents.
   * @return the exchanges, not null
   */
  public List<ManageableExchange> getExchanges() {
    List<ManageableExchange> result = new ArrayList<ManageableExchange>();
    if (_documents != null) {
      for (ExchangeDocument doc : _documents) {
        result.add(doc.getExchange());
      }
    }
    return result;
  }

  /**
   * Gets the first document, or null if no documents.
   * @return the first document, null if none
   */
  public ExchangeDocument getFirstDocument() {
    return getDocuments().size() > 0 ? getDocuments().get(0) : null;
  }

  /**
   * Gets the first exchange, or null if no documents.
   * @return the first exchange, null if none
   */
  public ManageableExchange getFirstExchange() {
    return getDocuments().size() > 0 ? getDocuments().get(0).getExchange() : null;
  }

  /**
   * Gets the single result expected from a query.
   * <p>
   * This throws an exception if more than 1 result is actually available.
   * Thus, this method implies an assumption about uniqueness of the queried exchange.
   * @return the matching exchange, or null if none
   */
  public ManageableExchange getSingleExchange() {
    if (_documents.size() > 1) {
      throw new OpenGammaRuntimeException("Expecting zero or single resulting match, and was " + _documents.size());
    } else {
      return getFirstExchange();
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ExchangeHistoryResult}.
   * @return the meta-bean, not null
   */
  public static ExchangeHistoryResult.Meta meta() {
    return ExchangeHistoryResult.Meta.INSTANCE;
  }

  @Override
  public ExchangeHistoryResult.Meta metaBean() {
    return ExchangeHistoryResult.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        return getPaging();
      case 943542968:  // documents
        return getDocuments();
    }
    return super.propertyGet(propertyName);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -995747956:  // paging
        setPaging((Paging) newValue);
        return;
      case 943542968:  // documents
        setDocuments((List<ExchangeDocument>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the paging information.
   * @return the value of the property
   */
  public Paging getPaging() {
    return _paging;
  }

  /**
   * Sets the paging information.
   * @param paging  the new value of the property
   */
  public void setPaging(Paging paging) {
    this._paging = paging;
  }

  /**
   * Gets the the {@code paging} property.
   * @return the property, not null
   */
  public final Property<Paging> paging() {
    return metaBean().paging().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of matched exchange documents, not null.
   * @return the value of the property
   */
  public List<ExchangeDocument> getDocuments() {
    return _documents;
  }

  /**
   * Sets the list of matched exchange documents, not null.
   * @param documents  the new value of the property
   */
  public void setDocuments(List<ExchangeDocument> documents) {
    this._documents.clear();
    this._documents.addAll(documents);
  }

  /**
   * Gets the the {@code documents} property.
   * @return the property, not null
   */
  public final Property<List<ExchangeDocument>> documents() {
    return metaBean().documents().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ExchangeHistoryResult}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code paging} property.
     */
    private final MetaProperty<Paging> _paging = DirectMetaProperty.ofReadWrite(this, "paging", Paging.class);
    /**
     * The meta-property for the {@code documents} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<ExchangeDocument>> _documents = DirectMetaProperty.ofReadWrite(this, "documents", (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("paging", _paging);
      temp.put("documents", _documents);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public ExchangeHistoryResult createBean() {
      return new ExchangeHistoryResult();
    }

    @Override
    public Class<? extends ExchangeHistoryResult> beanType() {
      return ExchangeHistoryResult.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code paging} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Paging> paging() {
      return _paging;
    }

    /**
     * The meta-property for the {@code documents} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<ExchangeDocument>> documents() {
      return _documents;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
