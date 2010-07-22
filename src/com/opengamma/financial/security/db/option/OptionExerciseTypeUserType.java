/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db.option;

import com.opengamma.financial.security.db.EnumUserType;
import com.opengamma.financial.security.option.AmericanExerciseType;
import com.opengamma.financial.security.option.AsianExerciseType;
import com.opengamma.financial.security.option.BermudanExerciseType;
import com.opengamma.financial.security.option.EuropeanExerciseType;
import com.opengamma.financial.security.option.ExerciseTypeVisitor;

/**
 * Custom Hibernate usertype for the OptionExerciseType enum
 */
public class OptionExerciseTypeUserType extends EnumUserType<OptionExerciseType> {

  private static final String AMERICAN = "American";
  private static final String ASIAN = "Asian";
  private static final String BERMUDAN = "Bermudan";
  private static final String EUROPEAN = "European";

  public OptionExerciseTypeUserType() {
    super(OptionExerciseType.class, OptionExerciseType.values());
  }

  @Override
  protected String enumToStringNoCache(OptionExerciseType value) {
    return value.accept(new ExerciseTypeVisitor<String>() {

      @Override
      public String visitAmericanExerciseType(AmericanExerciseType exerciseType) {
        return AMERICAN;
      }

      @Override
      public String visitAsianExerciseType(AsianExerciseType exerciseType) {
        return ASIAN;
      }

      @Override
      public String visitBermudanExerciseType(BermudanExerciseType exerciseType) {
        return BERMUDAN;
      }

      @Override
      public String visitEuropeanExerciseType(EuropeanExerciseType exerciseType) {
        return EUROPEAN;
      }

    });
  }

}
