/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.query.util;

import gov.va.semoss.rdf.engine.api.ModificationExecutor;

/**
 *
 * @author ryan
 */
public abstract class ModificationExecutorAdapter implements ModificationExecutor {

  private final boolean intrans;

  public ModificationExecutorAdapter() {
    this( false );
  }

  public ModificationExecutorAdapter( boolean doInTrans ) {
    intrans = doInTrans;
  }

  @Override
  public boolean execInTransaction() {
    return intrans;
  }
}
