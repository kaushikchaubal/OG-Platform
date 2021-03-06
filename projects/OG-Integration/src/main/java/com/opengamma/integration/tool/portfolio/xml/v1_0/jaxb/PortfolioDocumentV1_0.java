/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.portfolio.xml.v1_0.jaxb;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.beans.BeanDefinition;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import java.util.Map;
import org.joda.beans.BeanBuilder;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.Property;
import org.joda.beans.impl.direct.DirectMetaProperty;

@XmlRootElement(name = "og-portfolio")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {}) // Indicate we don't care about element ordering
@BeanDefinition
public class PortfolioDocumentV1_0 extends DirectBean {

  @XmlAttribute(name = "schemaVersion", required = true)
  @PropertyDefinition(validate = "notNull")
  private String _schemaVersion;

  @XmlElementWrapper(name = "portfolios")
  @XmlElement(name = "portfolio")
  @PropertyDefinition
  private Set<Portfolio> _portfolios;

  @XmlElementWrapper(name = "positions")
  @XmlElement(name = "position")
  @PropertyDefinition
  private Set<Position> _positions;

  @XmlElementWrapper(name = "trades")
  // Ensure the trade type is derived from the element name (e.g. trade, swapTrade, ...)
  @XmlElementRef
  @PropertyDefinition
  private Set<Trade> _trades;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PortfolioDocumentV1_0}.
   * @return the meta-bean, not null
   */
  public static PortfolioDocumentV1_0.Meta meta() {
    return PortfolioDocumentV1_0.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(PortfolioDocumentV1_0.Meta.INSTANCE);
  }

  @Override
  public PortfolioDocumentV1_0.Meta metaBean() {
    return PortfolioDocumentV1_0.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -233564169:  // schemaVersion
        return getSchemaVersion();
      case 415474731:  // portfolios
        return getPortfolios();
      case 1707117674:  // positions
        return getPositions();
      case -865715313:  // trades
        return getTrades();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -233564169:  // schemaVersion
        setSchemaVersion((String) newValue);
        return;
      case 415474731:  // portfolios
        setPortfolios((Set<Portfolio>) newValue);
        return;
      case 1707117674:  // positions
        setPositions((Set<Position>) newValue);
        return;
      case -865715313:  // trades
        setTrades((Set<Trade>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_schemaVersion, "schemaVersion");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      PortfolioDocumentV1_0 other = (PortfolioDocumentV1_0) obj;
      return JodaBeanUtils.equal(getSchemaVersion(), other.getSchemaVersion()) &&
          JodaBeanUtils.equal(getPortfolios(), other.getPortfolios()) &&
          JodaBeanUtils.equal(getPositions(), other.getPositions()) &&
          JodaBeanUtils.equal(getTrades(), other.getTrades());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getSchemaVersion());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPortfolios());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPositions());
    hash += hash * 31 + JodaBeanUtils.hashCode(getTrades());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the schemaVersion.
   * @return the value of the property, not null
   */
  public String getSchemaVersion() {
    return _schemaVersion;
  }

  /**
   * Sets the schemaVersion.
   * @param schemaVersion  the new value of the property, not null
   */
  public void setSchemaVersion(String schemaVersion) {
    JodaBeanUtils.notNull(schemaVersion, "schemaVersion");
    this._schemaVersion = schemaVersion;
  }

  /**
   * Gets the the {@code schemaVersion} property.
   * @return the property, not null
   */
  public final Property<String> schemaVersion() {
    return metaBean().schemaVersion().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolios.
   * @return the value of the property
   */
  public Set<Portfolio> getPortfolios() {
    return _portfolios;
  }

  /**
   * Sets the portfolios.
   * @param portfolios  the new value of the property
   */
  public void setPortfolios(Set<Portfolio> portfolios) {
    this._portfolios = portfolios;
  }

  /**
   * Gets the the {@code portfolios} property.
   * @return the property, not null
   */
  public final Property<Set<Portfolio>> portfolios() {
    return metaBean().portfolios().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the positions.
   * @return the value of the property
   */
  public Set<Position> getPositions() {
    return _positions;
  }

  /**
   * Sets the positions.
   * @param positions  the new value of the property
   */
  public void setPositions(Set<Position> positions) {
    this._positions = positions;
  }

  /**
   * Gets the the {@code positions} property.
   * @return the property, not null
   */
  public final Property<Set<Position>> positions() {
    return metaBean().positions().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trades.
   * @return the value of the property
   */
  public Set<Trade> getTrades() {
    return _trades;
  }

  /**
   * Sets the trades.
   * @param trades  the new value of the property
   */
  public void setTrades(Set<Trade> trades) {
    this._trades = trades;
  }

  /**
   * Gets the the {@code trades} property.
   * @return the property, not null
   */
  public final Property<Set<Trade>> trades() {
    return metaBean().trades().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PortfolioDocumentV1_0}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code schemaVersion} property.
     */
    private final MetaProperty<String> _schemaVersion = DirectMetaProperty.ofReadWrite(
        this, "schemaVersion", PortfolioDocumentV1_0.class, String.class);
    /**
     * The meta-property for the {@code portfolios} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<Portfolio>> _portfolios = DirectMetaProperty.ofReadWrite(
        this, "portfolios", PortfolioDocumentV1_0.class, (Class) Set.class);
    /**
     * The meta-property for the {@code positions} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<Position>> _positions = DirectMetaProperty.ofReadWrite(
        this, "positions", PortfolioDocumentV1_0.class, (Class) Set.class);
    /**
     * The meta-property for the {@code trades} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<Trade>> _trades = DirectMetaProperty.ofReadWrite(
        this, "trades", PortfolioDocumentV1_0.class, (Class) Set.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "schemaVersion",
        "portfolios",
        "positions",
        "trades");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -233564169:  // schemaVersion
          return _schemaVersion;
        case 415474731:  // portfolios
          return _portfolios;
        case 1707117674:  // positions
          return _positions;
        case -865715313:  // trades
          return _trades;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends PortfolioDocumentV1_0> builder() {
      return new DirectBeanBuilder<PortfolioDocumentV1_0>(new PortfolioDocumentV1_0());
    }

    @Override
    public Class<? extends PortfolioDocumentV1_0> beanType() {
      return PortfolioDocumentV1_0.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code schemaVersion} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> schemaVersion() {
      return _schemaVersion;
    }

    /**
     * The meta-property for the {@code portfolios} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<Portfolio>> portfolios() {
      return _portfolios;
    }

    /**
     * The meta-property for the {@code positions} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<Position>> positions() {
      return _positions;
    }

    /**
     * The meta-property for the {@code trades} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<Trade>> trades() {
      return _trades;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
