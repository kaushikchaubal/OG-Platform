/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.conversion;

import java.util.ArrayList;

import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.instrument.InstrumentDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponCMSDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponFixedDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponIborDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityCouponIborSpreadDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityDefinition;
import com.opengamma.analytics.financial.instrument.annuity.AnnuityDefinitionBuilder;
import com.opengamma.analytics.financial.instrument.index.GeneratorSwapFixedON;
import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.instrument.index.IndexON;
import com.opengamma.analytics.financial.instrument.index.IndexSwap;
import com.opengamma.analytics.financial.instrument.payment.CouponFloatingDefinition;
import com.opengamma.analytics.financial.instrument.payment.PaymentDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedIborSpreadDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedONDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapFixedONSimplifiedDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapIborIborDefinition;
import com.opengamma.analytics.financial.instrument.swap.SwapXCcyDefinition;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.region.RegionSource;
import com.opengamma.financial.analytics.fixedincome.InterestRateInstrumentType;
import com.opengamma.financial.convention.ConventionBundle;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.frequency.Frequency;
import com.opengamma.financial.convention.frequency.PeriodFrequency;
import com.opengamma.financial.convention.frequency.SimpleFrequency;
import com.opengamma.financial.security.FinancialSecurityVisitorAdapter;
import com.opengamma.financial.security.swap.FixedInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingInterestRateLeg;
import com.opengamma.financial.security.swap.FloatingSpreadIRLeg;
import com.opengamma.financial.security.swap.ForwardSwapSecurity;
import com.opengamma.financial.security.swap.InterestRateNotional;
import com.opengamma.financial.security.swap.SwapLeg;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * Convert the swaps from their Security version to the Definition version.
 */
public class SwapSecurityConverter extends FinancialSecurityVisitorAdapter<InstrumentDefinition<?>> {
  /** A holiday source */
  private final HolidaySource _holidaySource;
  /** A convention bundle source */
  private final ConventionBundleSource _conventionSource;
  /** A region source */
  private final RegionSource _regionSource;
  /** Is this converter being used in curve construction code */
  private final boolean _forCurves;

  /**
   * @param holidaySource The holiday source, not null
   * @param conventionSource The convention source, not null
   * @param regionSource The region source, not null
   * @param forCurves true if the converter is used in curve construction code
   */
  public SwapSecurityConverter(final HolidaySource holidaySource, final ConventionBundleSource conventionSource, final RegionSource regionSource, final boolean forCurves) {
    ArgumentChecker.notNull(holidaySource, "holiday source");
    ArgumentChecker.notNull(conventionSource, "convention source");
    ArgumentChecker.notNull(regionSource, "region source");
    _holidaySource = holidaySource;
    _conventionSource = conventionSource;
    _regionSource = regionSource;
    _forCurves = forCurves;
  }

  @Override
  public InstrumentDefinition<?> visitForwardSwapSecurity(final ForwardSwapSecurity security) {
    return visitSwapSecurity(security);
  }

  @Override
  public InstrumentDefinition<?> visitSwapSecurity(final SwapSecurity security) {
    ArgumentChecker.notNull(security, "swap security");
    final InterestRateInstrumentType swapType = SwapSecurityUtils.getSwapType(security);
    switch (swapType) {
      case SWAP_FIXED_IBOR:
        return getFixedIborSwapDefinition(security, SwapSecurityUtils.payFixed(security), false);
      case SWAP_FIXED_IBOR_WITH_SPREAD:
        return getFixedIborSwapDefinition(security, SwapSecurityUtils.payFixed(security), true);
      case SWAP_IBOR_IBOR:
        return getIborIborSwapDefinition(security);
      case SWAP_CMS_CMS:
        return getCMSCMSSwapDefinition(security);
      case SWAP_FIXED_CMS:
        return SwapSecurityUtils.payFixed(security) ? getFixedCMSSwapDefinition(security, true) : getFixedCMSSwapDefinition(security, false);
      case SWAP_IBOR_CMS:
        return getIborCMSSwapDefinition(security);
      case SWAP_FIXED_OIS:
        return getFixedOISSwapDefinition(security, SwapSecurityUtils.payFixed(security), _forCurves);
      case SWAP_CROSS_CURRENCY:
        return getCrossCurrencySwapDefinition(security);
      default:
        throw new OpenGammaRuntimeException("Cannot handle swapType " + swapType);
    }
  }

  private SwapDefinition getFixedIborSwapDefinition(final SwapSecurity swapSecurity, final boolean payFixed, final boolean hasSpread) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FixedInterestRateLeg fixedLeg = (FixedInterestRateLeg) (payFixed ? payLeg : receiveLeg);
    final FloatingInterestRateLeg iborLeg = (FloatingInterestRateLeg) (payFixed ? receiveLeg : payLeg);
    final ExternalId regionId = payLeg.getRegionId();
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId);
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    final ConventionBundle iborIndexConvention = _conventionSource.getConventionBundle(iborLeg.getFloatingReferenceRateId());
    if (iborIndexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get Ibor index convention for " + currency + " using " + iborLeg.getFloatingReferenceRateId() + " from swap " +
          swapSecurity.getExternalIdBundle());
    }
    final Frequency freqIbor = iborLeg.getFrequency();
    final Period tenorIbor = getTenor(freqIbor);
    final IborIndex indexIbor = new IborIndex(currency, tenorIbor, iborIndexConvention.getSettlementDays(), iborIndexConvention.getDayCount(),
        iborIndexConvention.getBusinessDayConvention(), iborIndexConvention.isEOMConvention());
    final Frequency freqFixed = fixedLeg.getFrequency();
    final Period tenorFixed = getTenor(freqFixed);
    final double fixedLegNotional = ((InterestRateNotional) fixedLeg.getNotional()).getAmount();
    final double iborLegNotional = ((InterestRateNotional) iborLeg.getNotional()).getAmount();
    if (hasSpread) {
      final double spread = ((FloatingSpreadIRLeg) iborLeg).getSpread();
      return SwapFixedIborSpreadDefinition.from(effectiveDate, maturityDate, tenorFixed, fixedLeg.getDayCount(), fixedLeg.getBusinessDayConvention(), fixedLeg.isEom(), fixedLegNotional,
          fixedLeg.getRate(), tenorIbor, iborLeg.getDayCount(), iborLeg.getBusinessDayConvention(), iborLeg.isEom(), iborLegNotional, indexIbor, spread, payFixed, calendar);
    }
    final SwapFixedIborDefinition swap = SwapFixedIborDefinition.from(effectiveDate, maturityDate, tenorFixed, fixedLeg.getDayCount(), fixedLeg.getBusinessDayConvention(), fixedLeg.isEom(),
        fixedLegNotional, fixedLeg.getRate(), tenorIbor, iborLeg.getDayCount(), iborLeg.getBusinessDayConvention(), iborLeg.isEom(), iborLegNotional, indexIbor, payFixed, calendar);
    return swap;
  }

  private SwapDefinition getFixedOISSwapDefinition(final SwapSecurity swapSecurity, final boolean payFixed, final boolean forCurve) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FixedInterestRateLeg fixedLeg = (FixedInterestRateLeg) (payFixed ? payLeg : receiveLeg);
    final FloatingInterestRateLeg floatLeg = (FloatingInterestRateLeg) (payFixed ? receiveLeg : payLeg);
    final ConventionBundle indexConvention = _conventionSource.getConventionBundle(floatLeg.getFloatingReferenceRateId());
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    if (indexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get OIS index convention for " + currency + " using " + floatLeg.getFloatingReferenceRateId());
    }
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, indexConvention.getRegion());
    final String currencyString = currency.getCode();
    Integer publicationLag = indexConvention.getOvernightIndexSwapPublicationLag();
    if (publicationLag == null) {
      publicationLag = 0;
      //throw new OpenGammaRuntimeException("Could not get ON Index publication lag for " + indexConvention.getIdentifiers());
    }
    final Period paymentFrequency = getTenor(floatLeg.getFrequency());
    final IndexON index = new IndexON(floatLeg.getFloatingReferenceRateId().getValue(), currency, indexConvention.getDayCount(), publicationLag);
    final GeneratorSwapFixedON generator = new GeneratorSwapFixedON(currencyString + "_OIS_Convention", index, paymentFrequency, fixedLeg.getDayCount(), fixedLeg.getBusinessDayConvention(),
        fixedLeg.isEom(), 0, 1 - publicationLag, calendar); // TODO: The payment lag is not available at the security level!
    final double notionalFixed = ((InterestRateNotional) fixedLeg.getNotional()).getAmount();
    final double notionalOIS = ((InterestRateNotional) floatLeg.getNotional()).getAmount();
    if (forCurve) {
      return SwapFixedONSimplifiedDefinition.from(effectiveDate, maturityDate, notionalFixed, notionalOIS, generator, fixedLeg.getRate(), payFixed);
    }
    return SwapFixedONDefinition.from(effectiveDate, maturityDate, notionalFixed, notionalOIS, generator, fixedLeg.getRate(), payFixed);
  }

  private SwapIborIborDefinition getIborIborSwapDefinition(final SwapSecurity swapSecurity) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FloatingInterestRateLeg floatPayLeg = (FloatingInterestRateLeg) payLeg;
    final FloatingInterestRateLeg floatReceiveLeg = (FloatingInterestRateLeg) receiveLeg;
    final ExternalId regionId = payLeg.getRegionId();
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId);
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    if (floatPayLeg instanceof FloatingSpreadIRLeg) {
      final AnnuityCouponIborSpreadDefinition payLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, (FloatingSpreadIRLeg) floatPayLeg, calendar, currency, true);
      if (floatReceiveLeg instanceof FloatingSpreadIRLeg) {
        final AnnuityCouponIborSpreadDefinition receiveLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, (FloatingSpreadIRLeg) floatReceiveLeg, calendar, currency, false);
        return SwapIborIborDefinition.from(payLegDefinition, receiveLegDefinition);
      }
      final AnnuityCouponIborDefinition receiveLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, floatReceiveLeg, calendar, currency, false);
      return SwapIborIborDefinition.from(payLegDefinition, receiveLegDefinition);
    }
    final AnnuityCouponIborDefinition payLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, floatPayLeg, calendar, currency, true);
    if (floatReceiveLeg instanceof FloatingSpreadIRLeg) {
      final AnnuityCouponIborSpreadDefinition receiveLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, (FloatingSpreadIRLeg) floatReceiveLeg, calendar, currency, false);
      return SwapIborIborDefinition.from(payLegDefinition, receiveLegDefinition);
    }
    final AnnuityCouponIborDefinition receiveLegDefinition = getIborSwapLegDefinition(effectiveDate, maturityDate, floatReceiveLeg, calendar, currency, false);
    return SwapIborIborDefinition.from(payLegDefinition, receiveLegDefinition);
  }

  private SwapDefinition getCMSCMSSwapDefinition(final SwapSecurity swapSecurity) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FloatingInterestRateLeg floatPayLeg = (FloatingInterestRateLeg) payLeg;
    final FloatingInterestRateLeg floatReceiveLeg = (FloatingInterestRateLeg) receiveLeg;
    final ExternalId regionId = payLeg.getRegionId();
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId);
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    final AnnuityCouponCMSDefinition cmsPayLeg = getCMSwapLegDefinition(effectiveDate, maturityDate, floatPayLeg, calendar, currency, true);
    final AnnuityCouponCMSDefinition cmsReceiveLeg = getCMSwapLegDefinition(effectiveDate, maturityDate, floatReceiveLeg, calendar, currency, false);
    return new SwapDefinition(cmsPayLeg, cmsReceiveLeg);
  }

  private SwapDefinition getFixedCMSSwapDefinition(final SwapSecurity swapSecurity, final boolean payFixed) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FixedInterestRateLeg fixedLeg = (FixedInterestRateLeg) (payFixed ? payLeg : receiveLeg);
    final FloatingInterestRateLeg floatingLeg = (FloatingInterestRateLeg) (payFixed ? receiveLeg : payLeg);
    final ExternalId regionId = payLeg.getRegionId();
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId);
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    final AnnuityCouponFixedDefinition fixedAnnuity = getFixedSwapLegDefinition(effectiveDate, maturityDate, fixedLeg, calendar, payFixed);
    final AnnuityCouponCMSDefinition cmsAnnuity = getCMSwapLegDefinition(effectiveDate, maturityDate, floatingLeg, calendar, currency, !payFixed);
    return payFixed ? new SwapDefinition(fixedAnnuity, cmsAnnuity) : new SwapDefinition(cmsAnnuity, fixedAnnuity);
  }

  private SwapDefinition getIborCMSSwapDefinition(final SwapSecurity swapSecurity) {
    final ZonedDateTime effectiveDate = swapSecurity.getEffectiveDate();
    final ZonedDateTime maturityDate = swapSecurity.getMaturityDate();
    final SwapLeg payLeg = swapSecurity.getPayLeg();
    final SwapLeg receiveLeg = swapSecurity.getReceiveLeg();
    final FloatingInterestRateLeg floatPayLeg = (FloatingInterestRateLeg) payLeg;
    final FloatingInterestRateLeg floatReceiveLeg = (FloatingInterestRateLeg) receiveLeg;
    final boolean payIbor = floatPayLeg.getFloatingRateType().isIbor();
    final boolean receiveIbor = floatReceiveLeg.getFloatingRateType().isIbor();
    if (receiveIbor == payIbor) {
      throw new OpenGammaRuntimeException("This should never happen");
    }
    final FloatingInterestRateLeg iborLeg = payIbor ? floatPayLeg : floatReceiveLeg;
    final FloatingInterestRateLeg cmsLeg = payIbor ? floatReceiveLeg : floatPayLeg;
    final ExternalId regionId = payLeg.getRegionId();
    final Calendar calendar = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId);
    final Currency currency = ((InterestRateNotional) payLeg.getNotional()).getCurrency();
    final AnnuityDefinition<? extends CouponFloatingDefinition> iborAnnuity = getIborSwapLegDefinition(effectiveDate, maturityDate, iborLeg, calendar, currency, payIbor);
    final AnnuityCouponCMSDefinition cmsAnnuity = getCMSwapLegDefinition(effectiveDate, maturityDate, cmsLeg, calendar, currency, !payIbor);
    return payIbor ? new SwapDefinition(iborAnnuity, cmsAnnuity) : new SwapDefinition(cmsAnnuity, iborAnnuity);
    // Implementation note: In the converter, the pay leg is expected to be first.
  }

  private AnnuityCouponFixedDefinition getFixedSwapLegDefinition(final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate, final FixedInterestRateLeg fixedLeg, final Calendar calendar,
      final boolean isPayer) {
    final double notional = ((InterestRateNotional) fixedLeg.getNotional()).getAmount();
    final BusinessDayConvention businessDay = fixedLeg.getBusinessDayConvention();
    if (businessDay == null) {
      throw new OpenGammaRuntimeException("Could not get Business Day for " + fixedLeg);
    }
    final boolean isEOM = fixedLeg.isEom();
    final Frequency freqFixed = fixedLeg.getFrequency();
    final Period tenorFixed = getTenor(freqFixed);
    return AnnuityCouponFixedDefinition.from(((InterestRateNotional) fixedLeg.getNotional()).getCurrency(), effectiveDate, maturityDate, tenorFixed, calendar, fixedLeg.getDayCount(), businessDay,
        isEOM, notional, fixedLeg.getRate(), isPayer);
  }

  private AnnuityCouponIborSpreadDefinition getIborSwapLegDefinition(final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate, final FloatingSpreadIRLeg iborLeg,
      final Calendar calendar, final Currency currency, final boolean isPayer) {
    final ConventionBundle iborIndexConvention = _conventionSource.getConventionBundle(iborLeg.getFloatingReferenceRateId());
    if (iborIndexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get Ibor index convention for " + currency + " using " + iborLeg.getFloatingReferenceRateId());
    }
    final Frequency freqIbor = iborLeg.getFrequency();
    final Period tenorIbor = getTenor(freqIbor);
    final IborIndex iborIndex = new IborIndex(currency, tenorIbor, iborIndexConvention.getSettlementDays(), iborIndexConvention.getDayCount(),
        iborIndexConvention.getBusinessDayConvention(), iborIndexConvention.isEOMConvention());
    final double iborLegNotional = ((InterestRateNotional) iborLeg.getNotional()).getAmount();
    final double spread = iborLeg.getSpread();
    return AnnuityCouponIborSpreadDefinition.from(effectiveDate, maturityDate, tenorIbor, iborLegNotional, iborIndex, isPayer, iborLeg.getBusinessDayConvention(), iborLeg.isEom(),
        iborLeg.getDayCount(), spread, calendar);
  }

  private AnnuityCouponIborDefinition getIborSwapLegDefinition(final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate, final FloatingInterestRateLeg iborLeg,
      final Calendar calendar, final Currency currency, final boolean isPayer) {
    final ConventionBundle iborIndexConvention = _conventionSource.getConventionBundle(iborLeg.getFloatingReferenceRateId());
    if (iborIndexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get Ibor index convention for " + currency + " using " + iborLeg.getFloatingReferenceRateId());
    }
    final Frequency freqIbor = iborLeg.getFrequency();
    final Period tenorIbor = getTenor(freqIbor);
    final IborIndex iborIndex = new IborIndex(currency, tenorIbor, iborIndexConvention.getSettlementDays(), iborIndexConvention.getDayCount(),
        iborIndexConvention.getBusinessDayConvention(), iborIndexConvention.isEOMConvention());
    final double iborLegNotional = ((InterestRateNotional) iborLeg.getNotional()).getAmount();
    return AnnuityCouponIborDefinition.from(effectiveDate, maturityDate, tenorIbor, iborLegNotional, iborIndex, isPayer, iborLeg.getBusinessDayConvention(), iborLeg.isEom(), iborLeg.getDayCount(),
        calendar);
  }

  private AnnuityCouponCMSDefinition getCMSwapLegDefinition(final ZonedDateTime effectiveDate, final ZonedDateTime maturityDate, final FloatingInterestRateLeg floatLeg, final Calendar calendar,
      final Currency currency, final boolean isPayer) {
    final double notional = ((InterestRateNotional) floatLeg.getNotional()).getAmount();
    final Frequency freq = floatLeg.getFrequency();
    // FIXME: convert frequency to period in a better way
    final Period tenorPayment = getTenor(freq);
    final ConventionBundle swapIndexConvention = _conventionSource.getConventionBundle(floatLeg.getFloatingReferenceRateId());
    if (swapIndexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get swap index convention for " + floatLeg.getFloatingReferenceRateId().toString());
    }
    final ConventionBundle iborIndexConvention = _conventionSource.getConventionBundle(swapIndexConvention.getSwapFloatingLegInitialRate());
    if (iborIndexConvention == null) {
      throw new OpenGammaRuntimeException("Could not get ibor index convention for " + swapIndexConvention.getSwapFloatingLegInitialRate());
    }
    final IborIndex iborIndex = new IborIndex(currency, tenorPayment, iborIndexConvention.getSettlementDays(), iborIndexConvention.getDayCount(),
        iborIndexConvention.getBusinessDayConvention(), iborIndexConvention.isEOMConvention());
    final Period fixedLegPaymentPeriod = getTenor(swapIndexConvention.getSwapFixedLegFrequency());
    final IndexSwap swapIndex = new IndexSwap(fixedLegPaymentPeriod, swapIndexConvention.getSwapFixedLegDayCount(), iborIndex, swapIndexConvention.getPeriod(), calendar);
    return AnnuityCouponCMSDefinition.from(effectiveDate, maturityDate, notional, swapIndex, tenorPayment, floatLeg.getDayCount(), isPayer, calendar);
  }

  private SwapDefinition getCrossCurrencySwapDefinition(final SwapSecurity security) {
    final ZonedDateTime settlementDate = security.getEffectiveDate();
    final ZonedDateTime maturityDate = security.getMaturityDate();
    final SwapLeg[] swapLeg = new SwapLeg[2];
    swapLeg[0] = security.getPayLeg();
    swapLeg[1] = security.getReceiveLeg();
    final boolean[] payer = {true, false };
    final double[] notional = new double[2];
    final Currency[] currency = new Currency[2];
    final ExternalId[] regionId = new ExternalId[2];
    final Calendar[] calendar = new Calendar[2];
    // TODO: Calendar need to be merged to have common payment dates
    for (int loopleg = 0; loopleg < 2; loopleg++) {
      notional[loopleg] = ((InterestRateNotional) swapLeg[loopleg].getNotional()).getAmount();
      currency[loopleg] = ((InterestRateNotional) swapLeg[loopleg].getNotional()).getCurrency();
      regionId[loopleg] = swapLeg[loopleg].getRegionId();
      calendar[loopleg] = CalendarUtils.getCalendar(_regionSource, _holidaySource, regionId[0]);
    }
    final ArrayList<AnnuityDefinition<PaymentDefinition>> legDefinition = new ArrayList<AnnuityDefinition<PaymentDefinition>>();
    for (int loopleg = 0; loopleg < 2; loopleg++) {
      if (swapLeg[loopleg] instanceof FloatingInterestRateLeg) { // Leg is Ibor
        double spread = 0.0;
        if (swapLeg[loopleg] instanceof FloatingSpreadIRLeg) {
          spread = ((FloatingSpreadIRLeg) swapLeg[loopleg]).getSpread();
        }
        final FloatingInterestRateLeg legFloat = (FloatingInterestRateLeg) swapLeg[loopleg];
        final ConventionBundle iborIndexConvention = _conventionSource.getConventionBundle(legFloat.getFloatingReferenceRateId());
        if (iborIndexConvention == null) {
          throw new OpenGammaRuntimeException("Could not get Ibor index convention for " + currency[0] + " using " + legFloat.getFloatingReferenceRateId());
        }
        final Period tenorIbor = iborIndexConvention.getPeriod();
        final IborIndex iborIndex = new IborIndex(currency[loopleg], tenorIbor, iborIndexConvention.getSettlementDays(), iborIndexConvention.getDayCount(),
            iborIndexConvention.getBusinessDayConvention(), iborIndexConvention.isEOMConvention());
        legDefinition.add(AnnuityDefinitionBuilder.annuityIborSpreadWithNotionalFrom(settlementDate, maturityDate, notional[loopleg], iborIndex, spread, payer[loopleg], calendar[loopleg]));
      } else {
        if (swapLeg[loopleg] instanceof FixedInterestRateLeg) { // Leg is Fixed
          final FixedInterestRateLeg legFixed = (FixedInterestRateLeg) swapLeg[loopleg];
          final BusinessDayConvention businessDay = legFixed.getBusinessDayConvention();
          if (businessDay == null) {
            throw new OpenGammaRuntimeException("Could not get Business Day for " + legFixed);
          }
          final boolean isEOM = legFixed.isEom();
          final Frequency freqFixed = legFixed.getFrequency();
          final Period tenorFixed = getTenor(freqFixed);
          legDefinition.add(AnnuityDefinitionBuilder.annuityCouponFixedWithNotional(currency[loopleg], settlementDate, maturityDate, tenorFixed,
              calendar[loopleg], legFixed.getDayCount(), businessDay, isEOM, notional[loopleg], legFixed.getRate(), payer[loopleg]));
        } else {
          throw new OpenGammaRuntimeException("X Ccy Swap legs should be Fixed or Floating legs");
        }
      }
    }
    return new SwapXCcyDefinition(legDefinition.get(0), legDefinition.get(1));
  }

  private Period getTenor(final Frequency freq) {
    if (freq instanceof PeriodFrequency) {
      return ((PeriodFrequency) freq).getPeriod();
    } else if (freq instanceof SimpleFrequency) {
      return ((SimpleFrequency) freq).toPeriodFrequency().getPeriod();
    }
    throw new OpenGammaRuntimeException("Can only PeriodFrequency or SimpleFrequency; have " + freq.getClass());
  }

}
