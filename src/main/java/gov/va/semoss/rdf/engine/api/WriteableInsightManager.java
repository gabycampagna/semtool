/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.engine.api;

import gov.va.semoss.om.Insight;
import gov.va.semoss.om.Perspective;

import java.util.Collection;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

/**
 * A first pass at CRUD operations we might want for
 * {@link Perspective Perspectives} and {@link Insight Insights}
 *
 * @author ryan
 */
public interface WriteableInsightManager extends InsightManager {

  /**
   * Commits any changes to the engine. If no changes were made, does nothing.
   * Note that this function may do strange things with the {@link IEngine} that
   * creates it, so care must be taken when calling it. For example, the
   * {@link BigDataEngine} flips read-only and read-write handles, which will
   * cause a hang if the connections aren't closed from the same thread that
   * opened them.
   */
  public void commit();

  /**
   * Do we have any changes we need to commit?
   *
   * @return
   */
  public boolean hasCommittableChanges();

  /**
   * Discards any changes that have not been committed
   */
  public void dispose();

  /**
   * Adds a completely-new Insight
   *
   * @param ins the insight to add
   *
   * @return the URI of the new insight
   */
  public URI add( Insight ins );

  public void remove( Insight ins );

  public void update( Insight ins );

  /**
   * Adds a completely-new Perspective
   *
   * @param p the perspective to add
   *
   * @return the URI of the new perspective
   */
  public URI add( Perspective p );

  public void remove( Perspective p );

  public void update( Perspective p );

  /**
   * Sets the insights (in order) for this Perspective
   *
   * @param p        the perspective
   * @param insights the insights, in order, to set for this perspective
   */
  public void setInsights( Perspective p, List<Insight> insights );

  public void addRawStatements( Collection<Statement> stmts ) throws RepositoryException;

  /**
   * Removes all perspectives and insights
   */
  public void clear();
  
  /**   Provides access to methods that persist changes to "Perspective" tab data.
   * 
   * @return getWriteablePerspectiveTab -- (WriteablePerspectiveTab)
   *    Methods described above.
   */
  public WriteablePerspectiveTab getWriteablePerspectiveTab(); 
  
  /**   Provides access to methods that persist changes to "Insight" tab data.
   * 
   * @return getWriteableInsightTab -- (WriteableInsightTab)
   *    Methods described above.
   */
  public WriteableInsightTab getWriteableInsightTab();

  /**   Provides access to methods that persist changes to "Parameter" tab data.
   * 
   * @return getWriteableParameterTab -- (WriteableParameterTab)
   *    Methods described above.
   */
  public WriteableParameterTab getWriteableParameterTab(); 
  
}//End "WriteableInsightManager" interface.