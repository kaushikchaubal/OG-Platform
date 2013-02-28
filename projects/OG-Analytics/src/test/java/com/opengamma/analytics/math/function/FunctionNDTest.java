package com.opengamma.analytics.math.function;

import org.testng.annotations.Test;

public class FunctionNDTest {
  private static final FunctionND<Double, Double> F = new FunctionND<Double, Double>() {

    @Override
    protected Double evaluateFunction(final Double[] x) {
      return x[1] + x[2] * x[0];
    }

  };

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullParameters() {
    F.evaluate((Double[]) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullParameter() {
    F.evaluate(1., null, 2.);
  }

}
