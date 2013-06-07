/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.inflation;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.Test;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.instrument.index.IndexPrice;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CapFloorInflationZeroCouponMonthly;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Coupon;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.timeseries.DoubleTimeSeries;
import com.opengamma.timeseries.precise.zdt.ImmutableZonedDateTimeDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

/**
 * 
 */
public class CapFloorInflationZeroCouponMonthlyDefinitionTest {

  private static final String NAME = "Euro HICP x";
  private static final Currency CUR = Currency.EUR;
  private static final IndexPrice PRICE_INDEX = new IndexPrice(NAME, CUR);
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final ZonedDateTime START_DATE = DateUtils.getUTCDate(2008, 8, 18);
  private static final ZonedDateTime LAST_KNOWN_FIXING_DATE = DateUtils.getUTCDate(2008, 6, 1);
  private static final int MATURITY = 10;
  private static final Period COUPON_TENOR = Period.ofYears(MATURITY);
  private static final ZonedDateTime PAYMENT_DATE = ScheduleCalculator.getAdjustedDate(START_DATE, COUPON_TENOR, BUSINESS_DAY, CALENDAR);
  private static final ZonedDateTime ACCRUAL_END_DATE = PAYMENT_DATE;
  private static final ZonedDateTime ACCRUAL_START_DATE = START_DATE;
  private static final double NOTIONAL = 98765432;
  private static final int MONTH_LAG = 3;
  private static final double STRIKE = .02;
  private static final boolean IS_CAP = true;
  private static final ZonedDateTime REFERENCE_START_DATE = ACCRUAL_START_DATE.minusMonths(MONTH_LAG).withDayOfMonth(1);
  private static final ZonedDateTime REFERENCE_END_DATE = PAYMENT_DATE.minusMonths(MONTH_LAG).withDayOfMonth(1);
  private static final double INDEX_START_VALUE = 100;
  private static final double WEIGHT_START = 0.2;
  private static final double WEIGHT_END = 0.8;
  private static final CapFloorInflationZeroCouponMonthlyDefinition ZERO_COUPON_CAP_DEFINITION = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
      ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  private static final String DISCOUNTING_CURVE_NAME = "Discounting";
  private static final String PRICE_INDEX_CURVE_NAME = "Price index";
  private static final String[] CURVE_NAMES = new String[] {DISCOUNTING_CURVE_NAME, PRICE_INDEX_CURVE_NAME };
  private static final DayCount ACT_ACT = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCurrency() {
    new CapFloorInflationZeroCouponMonthlyDefinition(null, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullPay() {
    new CapFloorInflationZeroCouponMonthlyDefinition(CUR, null, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullStart() {
    new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, null,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullEnd() {
    new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        null, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullIndex() {
    new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, null, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullRefEnd() {
    new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, null, STRIKE, IS_CAP);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullIsCap() {
    new CapFloorInflationYearOnYearMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, null, MONTH_LAG, REFERENCE_START_DATE, REFERENCE_END_DATE, STRIKE, IS_CAP);
  }

  @Test
  /**
   * Tests the class getter.
   */
  public void getter() {
    assertEquals("Inflation Zero Coupon cap: getter", CUR, ZERO_COUPON_CAP_DEFINITION.getCurrency());
    assertEquals("Inflation Zero Coupon cap: getter", PAYMENT_DATE, ZERO_COUPON_CAP_DEFINITION.getPaymentDate());
    assertEquals("Inflation Zero Coupon cap: getter", ACCRUAL_START_DATE, ZERO_COUPON_CAP_DEFINITION.getAccrualStartDate());
    assertEquals("Inflation Zero Coupon cap: getter", ACCRUAL_END_DATE, ZERO_COUPON_CAP_DEFINITION.getAccrualEndDate());
    assertEquals("Inflation Zero Coupon cap: getter", LAST_KNOWN_FIXING_DATE, ZERO_COUPON_CAP_DEFINITION.getlastKnownFixingDate());
    assertEquals("Inflation Zero Coupon cap: getter", 1.0, ZERO_COUPON_CAP_DEFINITION.getPaymentYearFraction());
    assertEquals("Inflation Zero Coupon cap: getter", NOTIONAL, ZERO_COUPON_CAP_DEFINITION.getNotional());
    assertEquals("Inflation Zero Coupon cap: getter", PRICE_INDEX, ZERO_COUPON_CAP_DEFINITION.getPriceIndex());
    assertEquals("Inflation Zero Coupon cap: getter", INDEX_START_VALUE, ZERO_COUPON_CAP_DEFINITION.getIndexStartValue());
    assertEquals("Inflation Zero Coupon cap: getter", REFERENCE_END_DATE, ZERO_COUPON_CAP_DEFINITION.getReferenceEndDate());
    assertEquals("Inflation Zero Coupon cap: getter", MONTH_LAG, ZERO_COUPON_CAP_DEFINITION.getConventionalMonthLag());
    assertEquals("Inflation Zero Coupon cap: getter", IS_CAP, ZERO_COUPON_CAP_DEFINITION.isCap());
    assertEquals("Inflation Zero Coupon cap: getter", 1.0, ZERO_COUPON_CAP_DEFINITION.getPaymentYearFraction());
    assertEquals("Inflation Zero Coupon cap: getter", STRIKE, ZERO_COUPON_CAP_DEFINITION.getStrike());

  }

  @Test
  /**
   * Tests the equal and hash-code methods.
   */
  public void equalHash() {
    assertEquals(ZERO_COUPON_CAP_DEFINITION, ZERO_COUPON_CAP_DEFINITION);
    CapFloorInflationZeroCouponMonthlyDefinition couponDuplicate = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertEquals(ZERO_COUPON_CAP_DEFINITION, couponDuplicate);
    assertEquals(ZERO_COUPON_CAP_DEFINITION.hashCode(), couponDuplicate.hashCode());
    CapFloorInflationZeroCouponMonthlyDefinition modified;
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE.minusDays(1), ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE.minusDays(1),
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE.minusDays(1), 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    final double modifiedIndexStartValue = INDEX_START_VALUE - 1;
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, modifiedIndexStartValue, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    final ZonedDateTime modifiedReferenceEndDate = REFERENCE_END_DATE.minusDays(1);
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, modifiedIndexStartValue, modifiedReferenceEndDate, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 2.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL + 1.0, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE + .01, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, false);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE.minusDays(1), MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG - 1, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
    final IndexPrice modifiedPriceIndex = new IndexPrice("US CPI x", Currency.USD);
    modified = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, modifiedPriceIndex, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);
    assertFalse(ZERO_COUPON_CAP_DEFINITION.equals(modified));
  }

  @Test
  /**
   * Tests the first based on indexation lag.
   */
  public void from() {
    CapFloorInflationZeroCouponMonthlyDefinition constructor = new CapFloorInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, LAST_KNOWN_FIXING_DATE, MONTH_LAG, MATURITY, INDEX_START_VALUE, REFERENCE_END_DATE, STRIKE, IS_CAP);

    CouponInflationZeroCouponMonthlyDefinition zeroCoupon = new CouponInflationZeroCouponMonthlyDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE,
        ACCRUAL_END_DATE, 1.0, NOTIONAL, PRICE_INDEX, MONTH_LAG, REFERENCE_START_DATE, INDEX_START_VALUE, REFERENCE_END_DATE, false);
    CapFloorInflationZeroCouponMonthlyDefinition from = CapFloorInflationZeroCouponMonthlyDefinition.from(zeroCoupon, LAST_KNOWN_FIXING_DATE, MATURITY, STRIKE, IS_CAP);
    assertEquals("Inflation zero-coupon : from", constructor, from);
  }

  @Test
  public void toDerivativesNoData() {
    final ZonedDateTime pricingDate = DateUtils.getUTCDate(2011, 7, 29);
    Coupon zeroCouponConverted = ZERO_COUPON_CAP_DEFINITION.toDerivative(pricingDate, CURVE_NAMES);
    //lastKnownFixingTime could be negatif so we don't use the dayfraction
    final double lastKnownFixingTime = TimeCalculator.getTimeBetween(pricingDate, LAST_KNOWN_FIXING_DATE);
    final double paymentTime = ACT_ACT.getDayCountFraction(pricingDate, PAYMENT_DATE);
    final double referenceEndTime = ACT_ACT.getDayCountFraction(pricingDate, REFERENCE_END_DATE);

    CapFloorInflationZeroCouponMonthly zeroCoupon = new CapFloorInflationZeroCouponMonthly(CUR, paymentTime, 1.0, NOTIONAL, PRICE_INDEX, lastKnownFixingTime,
        INDEX_START_VALUE, referenceEndTime, MONTH_LAG, MATURITY, STRIKE, IS_CAP);
    assertEquals("Inflation zero-coupon: toDerivative", zeroCouponConverted, zeroCoupon);
  }

  @Test
  public void toDerivativesStartMonthNotknown() {
    final ZonedDateTime pricingDate = DateUtils.getUTCDate(2011, 7, 29);
    final DoubleTimeSeries<ZonedDateTime> priceIndexTS = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[] {DateUtils.getUTCDate(2017, 5, 1),
        DateUtils.getUTCDate(2017, 6, 1), DateUtils.getUTCDate(2018, 5, 1), DateUtils.getUTCDate(2018, 6, 1) },
        new double[] {
            127.23, 127.43, 128.23, 128.43 });
    Coupon zeroCouponConverted = ZERO_COUPON_CAP_DEFINITION.toDerivative(pricingDate, priceIndexTS, CURVE_NAMES);
    // lastKnownFixingTime could be negatif so we don't use the dayfraction
    final double lastKnownFixingTime = TimeCalculator.getTimeBetween(pricingDate, LAST_KNOWN_FIXING_DATE);
    double paymentTime = ACT_ACT.getDayCountFraction(pricingDate, PAYMENT_DATE);
    final double referenceEndTime = ACT_ACT.getDayCountFraction(pricingDate, REFERENCE_END_DATE);

    CapFloorInflationZeroCouponMonthly zeroCoupon = new CapFloorInflationZeroCouponMonthly(CUR, paymentTime, 1.0, NOTIONAL, PRICE_INDEX, lastKnownFixingTime,
        INDEX_START_VALUE, referenceEndTime, MONTH_LAG, MATURITY, STRIKE, IS_CAP);
    assertEquals("Inflation zero-coupon: toDerivative", zeroCoupon, zeroCouponConverted);
  }

  @Test
  public void toDerivativesStartMonthKnown() {
    final ZonedDateTime pricingDate = DateUtils.getUTCDate(2018, 6, 25);
    final DoubleTimeSeries<ZonedDateTime> priceIndexTS = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[] {DateUtils.getUTCDate(2017, 5, 1),
        DateUtils.getUTCDate(2017, 6, 1), DateUtils.getUTCDate(2018, 5, 1), DateUtils.getUTCDate(2018, 6, 1) },
        new double[] {
            127.23, 127.43, 128.23, 128.43 });
    Coupon zeroCouponConverted = ZERO_COUPON_CAP_DEFINITION.toDerivative(pricingDate, priceIndexTS, CURVE_NAMES);
    double paymentTime = ACT_ACT.getDayCountFraction(pricingDate, PAYMENT_DATE);
    CouponFixed zeroCoupon = new CouponFixed(CUR, paymentTime, DISCOUNTING_CURVE_NAME, 1.0, NOTIONAL, Math.max(128.23 /
        INDEX_START_VALUE - 1.0 - Math.pow(1 + STRIKE, MATURITY), 0.0));
    assertEquals("Inflation zero-coupon: toDerivative", zeroCoupon, zeroCouponConverted);
  }

}
