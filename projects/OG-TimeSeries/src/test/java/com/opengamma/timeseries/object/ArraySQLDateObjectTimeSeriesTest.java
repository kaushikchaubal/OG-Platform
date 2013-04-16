/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.timeseries.object;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.testng.annotations.Test;

import com.opengamma.timeseries.ObjectTimeSeries;
import com.opengamma.timeseries.sqldate.ArraySQLDateObjectTimeSeries;
import com.opengamma.timeseries.sqldate.SQLDateObjectTimeSeries;

/**
 * Test.
 */
@Test(groups = "unit")
public class ArraySQLDateObjectTimeSeriesTest extends SQLDateObjectTimeSeriesTest {

  @Override
  protected SQLDateObjectTimeSeries<BigDecimal> createEmptyTimeSeries() {
    return new ArraySQLDateObjectTimeSeries<BigDecimal>();
  }

  @Override
  protected SQLDateObjectTimeSeries<BigDecimal> createTimeSeries(Date[] times, BigDecimal[] values) {
    return new ArraySQLDateObjectTimeSeries<BigDecimal>(times, values);
  }

  @Override
  protected SQLDateObjectTimeSeries<BigDecimal> createTimeSeries(List<Date> times, List<BigDecimal> values) {
    return new ArraySQLDateObjectTimeSeries<BigDecimal>(times, values);
  }

  @Override
  protected ObjectTimeSeries<Date, BigDecimal> createTimeSeries(ObjectTimeSeries<Date, BigDecimal> dts) {
    return new ArraySQLDateObjectTimeSeries<BigDecimal>(dts);
  }

}