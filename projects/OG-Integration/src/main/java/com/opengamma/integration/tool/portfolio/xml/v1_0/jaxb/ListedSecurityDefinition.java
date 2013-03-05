/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma
 group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.tool.portfolio.xml.v1_0.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.joda.beans.BeanDefinition;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;

import com.opengamma.util.money.Currency;
import java.util.Map;
import org.joda.beans.BeanBuilder;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

@XmlSeeAlso({OptionSecurityDefinition.class, FutureSecurityDefinition.class, FutureOptionSecurityDefinition.class})
@BeanDefinition
public abstract class ListedSecurityDefinition extends DirectBean {

  @XmlElement(name = "underlyingId", required = true)
  @PropertyDefinition
  private IdWrapper _underlyingId;

  @XmlElement(name = "pointValue", required = true)
  @PropertyDefinition
  private int _pointValue;

  @XmlElement(name = "currency", required = true)
  @PropertyDefinition
  private Currency _currency;

  @XmlElement(name = "exchange", required = true)
  @PropertyDefinition
  private String _exchange;

  public abstract ListedSecurityExtractor getSecurityExtractor();

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ListedSecurityDefinition}.
   * @return the meta-bean, not null
   */
  public static ListedSecurityDefinition.Meta meta() {
    return ListedSecurityDefinition.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(ListedSecurityDefinition.Meta.INSTANCE);
  }

  @Override
  public ListedSecurityDefinition.Meta metaBean() {
    return ListedSecurityDefinition.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -771625640:  // underlyingId
        return getUnderlyingId();
      case 1257391553:  // pointValue
        return getPointValue();
      case 575402001:  // currency
        return getCurrency();
      case 1989774883:  // exchange
        return getExchange();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -771625640:  // underlyingId
        setUnderlyingId((IdWrapper) newValue);
        return;
      case 1257391553:  // pointValue
        setPointValue((Integer) newValue);
        return;
      case 575402001:  // currency
        setCurrency((Currency) newValue);
        return;
      case 1989774883:  // exchange
        setExchange((String) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ListedSecurityDefinition other = (ListedSecurityDefinition) obj;
      return JodaBeanUtils.equal(getUnderlyingId(), other.getUnderlyingId()) &&
          JodaBeanUtils.equal(getPointValue(), other.getPointValue()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getExchange(), other.getExchange());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnderlyingId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPointValue());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getExchange());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlyingId.
   * @return the value of the property
   */
  public IdWrapper getUnderlyingId() {
    return _underlyingId;
  }

  /**
   * Sets the underlyingId.
   * @param underlyingId  the new value of the property
   */
  public void setUnderlyingId(IdWrapper underlyingId) {
    this._underlyingId = underlyingId;
  }

  /**
   * Gets the the {@code underlyingId} property.
   * @return the property, not null
   */
  public final Property<IdWrapper> underlyingId() {
    return metaBean().underlyingId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the pointValue.
   * @return the value of the property
   */
  public int getPointValue() {
    return _pointValue;
  }

  /**
   * Sets the pointValue.
   * @param pointValue  the new value of the property
   */
  public void setPointValue(int pointValue) {
    this._pointValue = pointValue;
  }

  /**
   * Gets the the {@code pointValue} property.
   * @return the property, not null
   */
  public final Property<Integer> pointValue() {
    return metaBean().pointValue().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property
   */
  public void setCurrency(Currency currency) {
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * @return the property, not null
   */
  public final Property<Currency> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the exchange.
   * @return the value of the property
   */
  public String getExchange() {
    return _exchange;
  }

  /**
   * Sets the exchange.
   * @param exchange  the new value of the property
   */
  public void setExchange(String exchange) {
    this._exchange = exchange;
  }

  /**
   * Gets the the {@code exchange} property.
   * @return the property, not null
   */
  public final Property<String> exchange() {
    return metaBean().exchange().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ListedSecurityDefinition}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code underlyingId} property.
     */
    private final MetaProperty<IdWrapper> _underlyingId = DirectMetaProperty.ofReadWrite(
        this, "underlyingId", ListedSecurityDefinition.class, IdWrapper.class);
    /**
     * The meta-property for the {@code pointValue} property.
     */
    private final MetaProperty<Integer> _pointValue = DirectMetaProperty.ofReadWrite(
        this, "pointValue", ListedSecurityDefinition.class, Integer.TYPE);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", ListedSecurityDefinition.class, Currency.class);
    /**
     * The meta-property for the {@code exchange} property.
     */
    private final MetaProperty<String> _exchange = DirectMetaProperty.ofReadWrite(
        this, "exchange", ListedSecurityDefinition.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "underlyingId",
        "pointValue",
        "currency",
        "exchange");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -771625640:  // underlyingId
          return _underlyingId;
        case 1257391553:  // pointValue
          return _pointValue;
        case 575402001:  // currency
          return _currency;
        case 1989774883:  // exchange
          return _exchange;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ListedSecurityDefinition> builder() {
      throw new UnsupportedOperationException("ListedSecurityDefinition is an abstract class");
    }

    @Override
    public Class<? extends ListedSecurityDefinition> beanType() {
      return ListedSecurityDefinition.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code underlyingId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IdWrapper> underlyingId() {
      return _underlyingId;
    }

    /**
     * The meta-property for the {@code pointValue} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> pointValue() {
      return _pointValue;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code exchange} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> exchange() {
      return _exchange;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
