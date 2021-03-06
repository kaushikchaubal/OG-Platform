/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.inflation.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationZeroCouponMonthlyGearing;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.InflationSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.MultipleCurrencyInflationSensitivity;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Pricing method for inflation zero-coupon. The price is computed by index estimation and discounting.
 */
public class CouponInflationZeroCouponMonthlyGearingDiscountingMethod {

  /**
   * Computes the net amount of the zero-coupon coupon with reference index at start of the month and multiplicative factor.
   * @param coupon The zero-coupon payment.
   * @param inflation The inflation provider.
   * @return The net amount.
   */
  public MultipleCurrencyAmount netAmount(CouponInflationZeroCouponMonthlyGearing coupon, final InflationProviderInterface inflation) {
    Validate.notNull(coupon, "Coupon");
    Validate.notNull(inflation, "Inflation");
    final double estimatedIndex = indexEstimation(coupon, inflation);
    final double netAmount = coupon.getFactor() * (estimatedIndex / coupon.getIndexStartValue() - (coupon.payNotional() ? 0.0 : 1.0)) * coupon.getNotional();
    return MultipleCurrencyAmount.of(coupon.getCurrency(), netAmount);
  }

  /**
   * Computes the present value of the zero-coupon coupon with reference index at start of the month and multiplicative factor.
   * @param coupon The zero-coupon payment.
   * @param inflation The inflation provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(CouponInflationZeroCouponMonthlyGearing coupon, final InflationProviderInterface inflation) {
    Validate.notNull(coupon, "Coupon");
    Validate.notNull(inflation, "Inflation");
    final double discountFactor = inflation.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    return netAmount(coupon, inflation).multipliedBy(discountFactor);
  }

  /**
   * Computes the estimated index with the weight and the reference end date.
   * @param coupon The zero-coupon payment.
   * @param inflation The inflation provider.
   * @return The net amount.
   */
  public double indexEstimation(CouponInflationZeroCouponMonthlyGearing coupon, final InflationProviderInterface inflation) {
    final double estimatedIndex = inflation.getPriceIndex(coupon.getPriceIndex(), coupon.getReferenceEndTime());
    return estimatedIndex;
  }

  /**
   * Compute the present value sensitivity to rates of a Inflation coupon.
   * @param coupon The coupon.
   * @param inflation The inflation provider.
   * @return The present value sensitivity.
   */
  public MultipleCurrencyInflationSensitivity presentValueCurveSensitivity(final CouponInflationZeroCouponMonthlyGearing coupon, final InflationProviderInterface inflation) {
    Validate.notNull(coupon, "Coupon");
    Validate.notNull(inflation, "Inflation");
    double estimatedIndex = inflation.getPriceIndex(coupon.getPriceIndex(), coupon.getReferenceEndTime());
    double discountFactor = inflation.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    // Backward sweep
    final double pvBar = 1.0;
    final double discountFactorBar = coupon.getFactor() * (estimatedIndex / coupon.getIndexStartValue() - (coupon.payNotional() ? 0.0 : 1.0)) * coupon.getNotional() * pvBar;
    final double estimatedIndexBar = coupon.getFactor() / coupon.getIndexStartValue() * discountFactor * coupon.getNotional() * pvBar;
    final Map<String, List<DoublesPair>> resultMapDisc = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listDiscounting = new ArrayList<DoublesPair>();
    listDiscounting.add(new DoublesPair(coupon.getPaymentTime(), -coupon.getPaymentTime() * discountFactor * discountFactorBar));
    resultMapDisc.put(inflation.getName(coupon.getCurrency()), listDiscounting);
    final Map<String, List<DoublesPair>> resultMapPrice = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listPrice = new ArrayList<DoublesPair>();
    listPrice.add(new DoublesPair(coupon.getReferenceEndTime(), estimatedIndexBar));
    resultMapPrice.put(inflation.getName(coupon.getPriceIndex()), listPrice);
    final InflationSensitivity inflationSensitivity = InflationSensitivity.ofYieldDiscountingAndPriceIndex(resultMapDisc, resultMapPrice);
    return MultipleCurrencyInflationSensitivity.of(coupon.getCurrency(), inflationSensitivity);
  }

}
