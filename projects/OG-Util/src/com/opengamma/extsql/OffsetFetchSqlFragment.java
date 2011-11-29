/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.extsql;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Representation of OFFSETFETCH.
 * <p>
 * This outputs an OFFSET-FETCH type clauses.
 */
public final class OffsetFetchSqlFragment extends ContainerSqlFragment {

  /**
   * The offset variable.
   */
  private final String _offsetVariable;
  /**
   * The fetch variable.
   */
  private final String _fetchVariable;

  /**
   * Creates an instance.
   * 
   * @param fetchVariable  the fetch variable, not null
   */
  public OffsetFetchSqlFragment(String fetchVariable) {
    _offsetVariable = null;
    _fetchVariable = fetchVariable;
  }

  /**
   * Creates an instance.
   * 
   * @param offsetVariable  the offset variable, not null
   * @param fetchVariable  the fetch variable, not null
   */
  public OffsetFetchSqlFragment(String offsetVariable, String fetchVariable) {
    _offsetVariable = offsetVariable;
    _fetchVariable = fetchVariable;
  }

  //-------------------------------------------------------------------------
  @Override
  protected void toSQL(StringBuilder buf, ExtSqlBundle bundle, SqlParameterSource paramSource) {
    int offset = 0;
    int fetchLimit = 0;
    if (_offsetVariable != null && paramSource.hasValue(_offsetVariable)) {
      offset = ((Number) paramSource.getValue(_offsetVariable)).intValue();
    }
    if (paramSource.hasValue(_fetchVariable)) {
      fetchLimit = ((Number) paramSource.getValue(_fetchVariable)).intValue();
    }
    buf.append(bundle.getConfig().getPaging(offset, fetchLimit));
  }

  //-------------------------------------------------------------------------
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getFragments();
  }

}
