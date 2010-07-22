/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

/**
 * Visitor for the OptionSecurity subclasses.
 * 
 * @param <T> return type of the visit methods
 */
public interface OptionSecurityVisitor<T> {

  T visitBondOptionSecurity(BondOptionSecurity security);

  T visitEquityOptionSecurity(EquityOptionSecurity security);

  T visitFutureOptionSecurity(FutureOptionSecurity security);

  T visitFXOptionSecurity(FXOptionSecurity security);

  T visitOptionOptionSecurity(OptionOptionSecurity security);

  T visitSwapOptionSecurity(SwapOptionSecurity security);

}
