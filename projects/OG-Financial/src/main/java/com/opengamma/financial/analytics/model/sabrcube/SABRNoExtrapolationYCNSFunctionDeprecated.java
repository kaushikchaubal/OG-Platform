/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.sabrcube;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.interestrate.PresentValueCurveSensitivitySABRCalculator;
import com.opengamma.analytics.financial.interestrate.PresentValueNodeSensitivityCalculator;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.model.option.definition.SABRInterestRateDataBundle;
import com.opengamma.analytics.financial.model.option.definition.SABRInterestRateParameters;
import com.opengamma.analytics.financial.model.volatility.smile.function.VolatilityFunctionFactory;
import com.opengamma.analytics.math.surface.InterpolatedDoublesSurface;
import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.financial.analytics.conversion.SwapSecurityUtils;
import com.opengamma.financial.analytics.fixedincome.InterestRateInstrumentType;
import com.opengamma.financial.analytics.ircurve.YieldCurveFunction;
import com.opengamma.financial.analytics.model.volatility.SmileFittingProperties;
import com.opengamma.financial.analytics.volatility.fittedresults.SABRFittedSurfaces;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.security.FinancialSecurityTypes;
import com.opengamma.financial.security.FinancialSecurityUtils;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.util.money.Currency;

/**
 * @deprecated Use the version that does not refer to funding or forward curves
 * @see SABRNoExtrapolationYCNSFunction
 */
@Deprecated
public class SABRNoExtrapolationYCNSFunctionDeprecated extends SABRYCNSFunctionDeprecated {
  private static final PresentValueNodeSensitivityCalculator NSC = PresentValueNodeSensitivityCalculator.using(PresentValueCurveSensitivitySABRCalculator.getInstance());

  @Override
  public ComputationTargetType getTargetType() {
    return FinancialSecurityTypes.SWAPTION_SECURITY.or(FinancialSecurityTypes.SWAP_SECURITY).or(FinancialSecurityTypes.CAP_FLOOR_SECURITY);
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    final Security security = target.getSecurity();
    if (security instanceof SwapSecurity) {
      try {
        final InterestRateInstrumentType type = SwapSecurityUtils.getSwapType((SwapSecurity) security);
        if ((type != InterestRateInstrumentType.SWAP_FIXED_CMS) && (type != InterestRateInstrumentType.SWAP_CMS_CMS) && (type != InterestRateInstrumentType.SWAP_IBOR_CMS)) {
          return false;
        }
      } catch (final OpenGammaRuntimeException e) {
        return false;
      }
    }
    return true;
  }

  @Override
  protected SABRInterestRateDataBundle getModelParameters(final ComputationTarget target, final FunctionInputs inputs, final Currency currency, final DayCount dayCount,
      final ValueRequirement desiredValue) {
    final YieldCurveBundle yieldCurves = getYieldCurves(inputs, currency, desiredValue);
    final String cubeName = desiredValue.getConstraint(ValuePropertyNames.CUBE);
    final String fittingMethod = desiredValue.getConstraint(SmileFittingProperties.PROPERTY_FITTING_METHOD);
    final ValueRequirement surfacesRequirement = getCubeRequirement(cubeName, currency, fittingMethod);
    final Object surfacesObject = inputs.getValue(surfacesRequirement);
    if (surfacesObject == null) {
      throw new OpenGammaRuntimeException("Could not get " + surfacesRequirement);
    }
    final SABRFittedSurfaces surfaces = (SABRFittedSurfaces) surfacesObject;
    final InterpolatedDoublesSurface alphaSurface = surfaces.getAlphaSurface();
    final InterpolatedDoublesSurface betaSurface = surfaces.getBetaSurface();
    final InterpolatedDoublesSurface nuSurface = surfaces.getNuSurface();
    final InterpolatedDoublesSurface rhoSurface = surfaces.getRhoSurface();
    final SABRInterestRateParameters modelParameters = new SABRInterestRateParameters(alphaSurface, betaSurface, rhoSurface, nuSurface, dayCount, VolatilityFunctionFactory.HAGAN_FORMULA);
    return new SABRInterestRateDataBundle(modelParameters, yieldCurves);
  }

  @Override
  protected ValueProperties getResultProperties(final Currency currency) {
    return createValueProperties()
        .with(ValuePropertyNames.CURRENCY, currency.getCode())
        .with(ValuePropertyNames.CURVE_CURRENCY, currency.getCode())
        .withAny(ValuePropertyNames.CURVE_CALCULATION_METHOD)
        .withAny(ValuePropertyNames.CURVE)
        .withAny(YieldCurveFunction.PROPERTY_FORWARD_CURVE)
        .withAny(YieldCurveFunction.PROPERTY_FUNDING_CURVE)
        .withAny(ValuePropertyNames.CUBE)
        .withAny(SmileFittingProperties.PROPERTY_FITTING_METHOD)
        .with(SmileFittingProperties.PROPERTY_VOLATILITY_MODEL, SmileFittingProperties.SABR)
        .with(ValuePropertyNames.CALCULATION_METHOD, SABRFunctionDeprecated.SABR_NO_EXTRAPOLATION).get();
  }

  @Override
  protected ValueProperties getResultProperties(final ComputationTarget target, final ValueRequirement desiredValue) {
    final String currency = FinancialSecurityUtils.getCurrency(target.getSecurity()).getCode();
    final String curveCalculationMethod = desiredValue.getConstraint(ValuePropertyNames.CURVE_CALCULATION_METHOD);
    final String curveName = desiredValue.getConstraint(ValuePropertyNames.CURVE);
    final String fittingMethod = desiredValue.getConstraint(SmileFittingProperties.PROPERTY_FITTING_METHOD);
    final String forwardCurveName = desiredValue.getConstraint(YieldCurveFunction.PROPERTY_FORWARD_CURVE);
    final String fundingCurveName = desiredValue.getConstraint(YieldCurveFunction.PROPERTY_FUNDING_CURVE);
    final String cubeName = desiredValue.getConstraint(ValuePropertyNames.CUBE);
    return createValueProperties()
        .with(ValuePropertyNames.CURRENCY, currency)
        .with(ValuePropertyNames.CURVE_CURRENCY, currency)
        .with(ValuePropertyNames.CURVE_CALCULATION_METHOD, curveCalculationMethod)
        .with(ValuePropertyNames.CURVE, curveName)
        .with(YieldCurveFunction.PROPERTY_FORWARD_CURVE, forwardCurveName)
        .with(YieldCurveFunction.PROPERTY_FUNDING_CURVE, fundingCurveName)
        .with(ValuePropertyNames.CUBE, cubeName)
        .with(SmileFittingProperties.PROPERTY_FITTING_METHOD, fittingMethod)
        .with(SmileFittingProperties.PROPERTY_VOLATILITY_MODEL, SmileFittingProperties.SABR)
        .with(ValuePropertyNames.CALCULATION_METHOD, SABRFunctionDeprecated.SABR_NO_EXTRAPOLATION).get();
  }

  @Override
  protected PresentValueNodeSensitivityCalculator getNodeSensitivityCalculator(final ValueRequirement desiredValue) {
    return NSC;
  }

}
