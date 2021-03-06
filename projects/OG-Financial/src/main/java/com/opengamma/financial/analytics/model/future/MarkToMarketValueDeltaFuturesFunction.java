/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.future;

import com.opengamma.analytics.financial.future.MarkToMarketFuturesCalculator;
import com.opengamma.engine.value.ValueRequirementNames;

/**
 * Calculates the {@link ValueRequirementNames#VALUE_DELTA} "ValueDelta" of a Future.<p>
 * ValueDelta is defined as S(t) * dV/dS, hence it should be equal to unitAmount * market_price. <p>
 * ValueDelta represents the cash value of the position or, the value of money one would make if the underlying increased in price by 100%.<p>
 * Observe: PNL = dV/dS * (change in S) = S(t) * dV/dS * (S(T) - S(t)) / S(t),
 * thus S(t)* dV/dS (ValueDelta) would be the PNL if 1.0 = (S(T) - S(t)) / S(t) => S(T) = 2*S(t),
 * i.e. if the underlying doubled (increased by 100%). It thus gives a measure of the sensitivity as a relative measure.
 */
public class MarkToMarketValueDeltaFuturesFunction extends MarkToMarketFuturesFunction<Double> {

  /**
   * @param closingPriceField The field name of the historical time series for price, e.g. "PX_LAST", "Close". Set in *FunctionConfiguration
   * @param costOfCarryField The field name of the historical time series for cost of carry e.g. "COST_OF_CARRY". Set in *FunctionConfiguration
   * @param resolutionKey The key defining how the time series resolution is to occur e.g. "DEFAULT_TSS_CONFIG"
   */
  public MarkToMarketValueDeltaFuturesFunction(final String closingPriceField, final String costOfCarryField, final String resolutionKey) {
    super(ValueRequirementNames.VALUE_DELTA, MarkToMarketFuturesCalculator.ValueDeltaCalculator.getInstance(), closingPriceField, costOfCarryField, resolutionKey);
  }
}
