/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.payments.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIbor;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.ForwardSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Method to compute present value and present value sensitivity for Ibor coupon with gearing factor and spread.
 */
public final class CouponIborDiscountingProviderMethod {

  /**
   * The method unique instance.
   */
  private static final CouponIborDiscountingProviderMethod INSTANCE = new CouponIborDiscountingProviderMethod();

  /**
   * Return the unique instance of the class.
   * @return The instance.
   */
  public static CouponIborDiscountingProviderMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Private constructor.
   */
  private CouponIborDiscountingProviderMethod() {
  }

  /**
   * Compute the present value of a Ibor coupon by discounting.
   * @param coupon The coupon.
   * @param multicurves The multi-curve provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(final CouponIbor coupon, final MulticurveProviderInterface multicurves) {
    ArgumentChecker.notNull(coupon, "Coupon");
    ArgumentChecker.notNull(multicurves, "Multi-curves provider");
    final double forward = multicurves.getForwardRate(coupon.getIndex(), coupon.getFixingPeriodStartTime(), coupon.getFixingPeriodEndTime(), coupon.getFixingAccrualFactor());
    final double df = multicurves.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    final double pv = coupon.getNotional() * coupon.getPaymentYearFraction() * forward * df;
    return MultipleCurrencyAmount.of(coupon.getCurrency(), pv);
  }

  /**
   * Compute the present value sensitivity to yield for discounting curve and forward rate (in index convention) for forward curve.
   * @param coupon The coupon.
   * @param multicurve The multi-curve provider.
   * @return The present value sensitivity.
   */
  public MultipleCurrencyMulticurveSensitivity presentValueCurveSensitivity(final CouponIbor coupon, final MulticurveProviderInterface multicurve) {
    ArgumentChecker.notNull(coupon, "Coupon");
    ArgumentChecker.notNull(multicurve, "Curves");
    final double forward = multicurve.getForwardRate(coupon.getIndex(), coupon.getFixingPeriodStartTime(), coupon.getFixingPeriodEndTime(), coupon.getFixingAccrualFactor());
    final double df = multicurve.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    // Backward sweep
    final double pvBar = 1.0;
    final double forwardBar = coupon.getNotional() * coupon.getPaymentYearFraction() * df * pvBar;
    final double dfBar = coupon.getNotional() * coupon.getPaymentYearFraction() * forward * pvBar;
    final Map<String, List<DoublesPair>> mapDsc = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listDiscounting = new ArrayList<DoublesPair>();
    listDiscounting.add(new DoublesPair(coupon.getPaymentTime(), -coupon.getPaymentTime() * df * dfBar));
    mapDsc.put(multicurve.getName(coupon.getCurrency()), listDiscounting);
    final Map<String, List<ForwardSensitivity>> mapFwd = new HashMap<String, List<ForwardSensitivity>>();
    final List<ForwardSensitivity> listForward = new ArrayList<ForwardSensitivity>();
    listForward.add(new ForwardSensitivity(coupon.getFixingPeriodStartTime(), coupon.getFixingPeriodEndTime(), coupon.getFixingAccrualFactor(), forwardBar));
    mapFwd.put(multicurve.getName(coupon.getIndex()), listForward);
    return MultipleCurrencyMulticurveSensitivity.of(coupon.getCurrency(), MulticurveSensitivity.of(mapDsc, mapFwd));
  }

}
