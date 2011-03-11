/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic.formula;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.statistics.distribution.NonCentralChiSquareDistribution;

/**
 * 
 */
public class CEVPriceFunction implements OptionPriceFunction<CEVFunctionData> {
  private static final BlackPriceFunction BLACK_PRICE_FUNCTION = new BlackPriceFunction();
  private static final NormalPriceFunction NORMAL_PRICE_FUNCTION = new NormalPriceFunction();

  @Override
  public Function1D<CEVFunctionData, Double> getPriceFunction(final EuropeanVanillaOption option) {
    final double k = option.getStrike();
    final double t = option.getTimeToExpiry();
    final boolean isCall = option.isCall();
    return new Function1D<CEVFunctionData, Double>() {

      @SuppressWarnings("synthetic-access")
      @Override
      public Double evaluate(final CEVFunctionData data) {
        Validate.notNull(data, "data");
        final double f = data.getForward();
        final double discountFactor = data.getDiscountFactor();
        final double sigma = data.getSimga();
        final double beta = data.getBeta();
        if (beta == 1.0) {
          final Function1D<BlackFunctionData, Double> blackFormula = BLACK_PRICE_FUNCTION.getPriceFunction(option);
          return blackFormula.evaluate(data);
        }
        if (beta == 0.0) {
          final Function1D<BlackFunctionData, Double> normalFormula = NORMAL_PRICE_FUNCTION.getPriceFunction(option);
          return normalFormula.evaluate(data);
        }
        final double b = 1.0 / (1 - beta);
        final double x = b * b / sigma / sigma / t;
        final double a = Math.pow(k, 2 * (1 - beta)) * x;
        final double c = Math.pow(f, 2 * (1 - beta)) * x;
        if (beta < 1) {
          final NonCentralChiSquareDistribution chiSq1 = new NonCentralChiSquareDistribution(b + 2, c);
          final NonCentralChiSquareDistribution chiSq2 = new NonCentralChiSquareDistribution(b, a);
          if (isCall) {
            return discountFactor * (f * (1 - chiSq1.getCDF(a)) - k * chiSq2.getCDF(c));
          }
          return discountFactor * (k * (1 - chiSq2.getCDF(c)) - f * chiSq1.getCDF(a));
        }
        final NonCentralChiSquareDistribution chiSq1 = new NonCentralChiSquareDistribution(-b, a);
        final NonCentralChiSquareDistribution chiSq2 = new NonCentralChiSquareDistribution(2 - b, c);
        if (isCall) {
          return discountFactor * (f * (1 - chiSq1.getCDF(c)) - k * chiSq2.getCDF(a));
        }
        return discountFactor * (k * (1 - chiSq2.getCDF(a)) - f * chiSq1.getCDF(c));
      }
    };
  }
}
