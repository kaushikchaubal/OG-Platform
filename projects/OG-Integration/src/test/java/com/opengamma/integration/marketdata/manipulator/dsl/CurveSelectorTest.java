/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.Test;

import com.opengamma.core.marketdatasnapshot.YieldCurveKey;
import com.opengamma.engine.marketdata.manipulator.MarketDataSelector;
import com.opengamma.engine.marketdata.manipulator.StructureIdentifier;
import com.opengamma.util.money.Currency;

public class CurveSelectorTest {

  private static final Scenario SCENARIO = new Scenario();
  private static final String CALC_CONFIG_NAME = "calcConfigName";

  private static StructureIdentifier structureId(String curveName) {
    return StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
  }

  /** if no criteria are specified the selector should match any curve */
  @Test
  public void noCriteria() {
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    MarketDataSelector selector = curve.selector();
    assertEquals(selector, selector.findMatchingSelector(structureId("curveName1"), CALC_CONFIG_NAME));
    assertEquals(selector, selector.findMatchingSelector(structureId("curveName2"), CALC_CONFIG_NAME));
  }

  /** match a single name and fail any other names */
  @Test
  public void singleName() {
    String curveName = "curveName";
    String calcConfigName = "calcConfigName";
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, calcConfigName);
    curve.named(curveName);
    MarketDataSelector selector = curve.selector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName), calcConfigName));
    assertNull(selector.findMatchingSelector(structureId("otherName"), calcConfigName));
  }

  /** match any one of multiple curve names, fail other names */
  @Test
  public void multipleNames() {
    String curveName1 = "curveName1";
    String curveName2 = "curveName2";
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    curve.named(curveName1, curveName2);
    MarketDataSelector selector = curve.selector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName1), CALC_CONFIG_NAME));
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName2), CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structureId("otherName"), CALC_CONFIG_NAME));
  }

  /** don't match if the calc config name doesn't match */
  @Test
  public void calcConfigName() {
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, "calcConfigName");
    MarketDataSelector selector = curve.selector();
    assertNull(selector.findMatchingSelector(structureId("curveName"), "otherCalcConfigName"));
  }

  /** match if the curve name matches a regular expression */
  @Test
  public void nameRegex() {
    String curve3M = "curve3M";
    String curve6M = "curve6M";
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    curve.nameMatches(".*3M");
    MarketDataSelector selector = curve.selector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curve3M), CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structureId(curve6M), CALC_CONFIG_NAME));
  }

  /** match if the curve currency is specified */
  @Test
  public void singleCurrency() {
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    curve.currencies("GBP");
    MarketDataSelector selector = curve.selector();
    String curveName = "curveName";
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structure2, CALC_CONFIG_NAME));
  }

  /** match if the curve currency matches any of the specified currency codes */
  @Test
  public void multipleCurrencies() {
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    curve.currencies("GBP", "USD");
    MarketDataSelector selector = curve.selector();
    String curveName = "curveName";
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName));
    StructureIdentifier structure3 = StructureIdentifier.of(new YieldCurveKey(Currency.AUD, curveName));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME));
    assertEquals(selector, selector.findMatchingSelector(structure2, CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structure3, CALC_CONFIG_NAME));
  }

  /** match if the curve satisfies all criteria, fail if it fails any of them */
  @Test
  public void multipleCriteria() {
    CurveSelector.Builder curve = new CurveSelector.Builder(SCENARIO, CALC_CONFIG_NAME);
    String curveName1 = "curveName1";
    String curveName2 = "curveName2";
    String curveName3 = "curveName3";
    curve.named(curveName1, curveName2).currencies("USD", "GBP");
    MarketDataSelector selector = curve.selector();
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName1));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName2));
    StructureIdentifier structure3 = StructureIdentifier.of(new YieldCurveKey(Currency.AUD, curveName1));
    StructureIdentifier structure4 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName3));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME));
    assertEquals(selector, selector.findMatchingSelector(structure2, CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structure3, CALC_CONFIG_NAME));
    assertNull(selector.findMatchingSelector(structure4, CALC_CONFIG_NAME));
  }
}