/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.time.Tenor;

/**
 *
 */
@BeanDefinition
public class OISLegConvention extends Convention {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The overnight index convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _overnightIndexConvention;

  /**
   * The payment tenor.
   */
  @PropertyDefinition(validate = "notNull")
  private Tenor _paymentTenor;

  /**
   * The payment delay in days.
   */
  @PropertyDefinition
  private int _paymentDelay;

  /**
   * For the builder.
   */
  public OISLegConvention() {
    super();
  }

  public OISLegConvention(final String name, final ExternalIdBundle externalIdBundle, final ExternalId overnightIndexConvention, final Tenor paymentTenor,
      final int paymentDelay) {
    super(name, externalIdBundle);
    setOvernightIndexConvention(overnightIndexConvention);
    setPaymentTenor(paymentTenor);
    setPaymentDelay(paymentDelay);
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code OISLegConvention}.
   * @return the meta-bean, not null
   */
  public static OISLegConvention.Meta meta() {
    return OISLegConvention.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(OISLegConvention.Meta.INSTANCE);
  }

  @Override
  public OISLegConvention.Meta metaBean() {
    return OISLegConvention.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(final String propertyName, final boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1218695809:  // overnightIndexConvention
        return getOvernightIndexConvention();
      case -507548582:  // paymentTenor
        return getPaymentTenor();
      case -522327267:  // paymentDelay
        return getPaymentDelay();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(final String propertyName, final Object newValue, final boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1218695809:  // overnightIndexConvention
        setOvernightIndexConvention((ExternalId) newValue);
        return;
      case -507548582:  // paymentTenor
        setPaymentTenor((Tenor) newValue);
        return;
      case -522327267:  // paymentDelay
        setPaymentDelay((Integer) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_overnightIndexConvention, "overnightIndexConvention");
    JodaBeanUtils.notNull(_paymentTenor, "paymentTenor");
    super.validate();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      final OISLegConvention other = (OISLegConvention) obj;
      return JodaBeanUtils.equal(getOvernightIndexConvention(), other.getOvernightIndexConvention()) &&
          JodaBeanUtils.equal(getPaymentTenor(), other.getPaymentTenor()) &&
          JodaBeanUtils.equal(getPaymentDelay(), other.getPaymentDelay()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getOvernightIndexConvention());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPaymentTenor());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPaymentDelay());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the overnight index convention.
   * @return the value of the property, not null
   */
  public ExternalId getOvernightIndexConvention() {
    return _overnightIndexConvention;
  }

  /**
   * Sets the overnight index convention.
   * @param overnightIndexConvention  the new value of the property, not null
   */
  public void setOvernightIndexConvention(final ExternalId overnightIndexConvention) {
    JodaBeanUtils.notNull(overnightIndexConvention, "overnightIndexConvention");
    this._overnightIndexConvention = overnightIndexConvention;
  }

  /**
   * Gets the the {@code overnightIndexConvention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> overnightIndexConvention() {
    return metaBean().overnightIndexConvention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payment tenor.
   * @return the value of the property, not null
   */
  public Tenor getPaymentTenor() {
    return _paymentTenor;
  }

  /**
   * Sets the payment tenor.
   * @param paymentTenor  the new value of the property, not null
   */
  public void setPaymentTenor(final Tenor paymentTenor) {
    JodaBeanUtils.notNull(paymentTenor, "paymentTenor");
    this._paymentTenor = paymentTenor;
  }

  /**
   * Gets the the {@code paymentTenor} property.
   * @return the property, not null
   */
  public final Property<Tenor> paymentTenor() {
    return metaBean().paymentTenor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the payment delay in days.
   * @return the value of the property
   */
  public int getPaymentDelay() {
    return _paymentDelay;
  }

  /**
   * Sets the payment delay in days.
   * @param paymentDelay  the new value of the property
   */
  public void setPaymentDelay(final int paymentDelay) {
    this._paymentDelay = paymentDelay;
  }

  /**
   * Gets the the {@code paymentDelay} property.
   * @return the property, not null
   */
  public final Property<Integer> paymentDelay() {
    return metaBean().paymentDelay().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code OISLegConvention}.
   */
  public static class Meta extends Convention.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code overnightIndexConvention} property.
     */
    private final MetaProperty<ExternalId> _overnightIndexConvention = DirectMetaProperty.ofReadWrite(
        this, "overnightIndexConvention", OISLegConvention.class, ExternalId.class);
    /**
     * The meta-property for the {@code paymentTenor} property.
     */
    private final MetaProperty<Tenor> _paymentTenor = DirectMetaProperty.ofReadWrite(
        this, "paymentTenor", OISLegConvention.class, Tenor.class);
    /**
     * The meta-property for the {@code paymentDelay} property.
     */
    private final MetaProperty<Integer> _paymentDelay = DirectMetaProperty.ofReadWrite(
        this, "paymentDelay", OISLegConvention.class, Integer.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "overnightIndexConvention",
        "paymentTenor",
        "paymentDelay");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(final String propertyName) {
      switch (propertyName.hashCode()) {
        case -1218695809:  // overnightIndexConvention
          return _overnightIndexConvention;
        case -507548582:  // paymentTenor
          return _paymentTenor;
        case -522327267:  // paymentDelay
          return _paymentDelay;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends OISLegConvention> builder() {
      return new DirectBeanBuilder<OISLegConvention>(new OISLegConvention());
    }

    @Override
    public Class<? extends OISLegConvention> beanType() {
      return OISLegConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code overnightIndexConvention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> overnightIndexConvention() {
      return _overnightIndexConvention;
    }

    /**
     * The meta-property for the {@code paymentTenor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Tenor> paymentTenor() {
      return _paymentTenor;
    }

    /**
     * The meta-property for the {@code paymentDelay} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Integer> paymentDelay() {
      return _paymentDelay;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
