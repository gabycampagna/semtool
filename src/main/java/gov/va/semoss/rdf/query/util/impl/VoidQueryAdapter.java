/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.query.util.impl;

import gov.va.semoss.rdf.query.util.QueryExecutorAdapter;

/**
 *
 * @author ryan
 */
public abstract class VoidQueryAdapter extends QueryExecutorAdapter<Void> {

  public VoidQueryAdapter() {
  }

  public VoidQueryAdapter( String sparq ) {
    super( sparq );
  }

  @Override
  public Void getResults() {
    return null;
  }

}
