package gov.va.semoss.rdf.engine.impl;

import gov.va.semoss.model.vocabulary.OLO;
import gov.va.semoss.model.vocabulary.SP;
import gov.va.semoss.model.vocabulary.SPIN;
import gov.va.semoss.model.vocabulary.UI;
import gov.va.semoss.model.vocabulary.VAS;
import gov.va.semoss.om.Insight;
import gov.va.semoss.om.Perspective;
import gov.va.semoss.rdf.engine.api.MetadataConstants;
import gov.va.semoss.rdf.engine.api.WriteableInsightManager;
import gov.va.semoss.rdf.engine.api.WriteablePerspectiveTab;
import gov.va.semoss.rdf.engine.util.EngineUtil;
import gov.va.semoss.rdf.query.util.impl.ListQueryAdapter;

import gov.va.semoss.util.GuiUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class WriteablePerspectiveTabImpl implements WriteablePerspectiveTab {

	private final WriteableInsightManager wim;
	private final RepositoryConnection rc;
	private static final Logger log = Logger.getLogger( WriteablePerspectiveTab.class );

	public WriteablePerspectiveTabImpl( WriteableInsightManagerImpl wim ) {
		this.wim = wim;
		this.rc = wim.getRawConnection();
	}

	/**
	 * Adds a new dummy Insight to the current Perspective and to the triple-store
	 * on disk.
	 *
	 * @param newOrder -- (int) Order value for the new Insight.
	 *
	 * @param perspective -- (Perspective) Perspective to which the dummy Insight
	 * must be added.
	 *
	 * @return addPerspective -- (boolean) Whether the save to disk succeeded.
	 */
	@Override
	public boolean addInsight( int newOrder, Perspective perspective ) {
		boolean boolReturnValue = false;
		ValueFactory insightVF = rc.getValueFactory();
		Literal now = insightVF.createLiteral( new Date() );
		String strCreator = wim.userInfoFromToolPreferences( "Created By Insight Manager, " + System.getProperty( "release.nameVersion", "VA SEMOSS" ) );
		Literal creator = insightVF.createLiteral( strCreator );
		String strUniqueIdentifier = String.valueOf( System.currentTimeMillis() );

		try {
			rc.begin();
			URI perspectiveURI = perspective.getUri();
			String slotUriName = perspective.getLabel() + "-slot-" + strUniqueIdentifier;
			URI slot = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, slotUriName );
			rc.add( perspectiveURI, OLO.slot, slot );

			String insightUriName = perspective.getLabel() + "-insight-" + strUniqueIdentifier;
			URI insightURI = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, insightUriName );

			rc.add( slot, OLO.item, insightURI );
			rc.add( slot, OLO.index, insightVF.createLiteral( 999 ) );

			String dataViewName = "gov.va.semoss.ui.components.playsheets.GridPlaySheet";
			URI dataViewURI = insightVF.createURI( VAS.NAMESPACE, dataViewName );
			rc.add( dataViewURI, RDFS.LABEL, insightVF.createLiteral( dataViewName ) );

			String sparql = "SELECT * WHERE{?s ?p ?o .}";

			String spinBodyUriName = perspective.getLabel() + "-insight-body-" + strUniqueIdentifier;
			URI spinBody = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, spinBodyUriName );
			rc.add( spinBody, RDF.TYPE, SP.Select );
			rc.add( spinBody, SP.text, insightVF.createLiteral( sparql ) );

			rc.add( insightURI, RDF.TYPE, SPIN.Function );
			rc.add( insightURI, RDFS.SUBCLASSOF, VAS.InsightProperties );
			rc.add( insightURI, RDFS.LABEL, insightVF.createLiteral( "New (" + perspective.getLabel() + ") Insight" ) );
			rc.add( insightURI, SPIN.body, spinBody );
			rc.add( insightURI, UI.dataView, dataViewURI );
			rc.add( insightURI, VAS.isLegacy, insightVF.createLiteral( false ) );
			rc.add( insightURI, DCTERMS.CREATED, now );
			rc.add( insightURI, DCTERMS.MODIFIED, now );
			rc.add( insightURI, DCTERMS.CREATOR, creator );

			rc.commit();

			//Reorder Insights, inserting the new Insight at the new order value:
			if ( setInsightOrders( newOrder, perspective ) == false ) {
				GuiUtility.showWarningOkCancel( "Insight could not be placed at desired position." );
			};

			//Import Insights into the repository:
			boolReturnValue = EngineUtil.getInstance().importInsights( wim );
			//Give the left-pane drop-downs enough time to refresh from the import:
			Thread.sleep( 2000 );

		}
		catch ( Exception e ) {
			e.printStackTrace();
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Reorders Insights under the passed-in Perspective, placing the Insight with
	 * order, 999, at the "newOrder" (passed-in) position.
	 *
	 * Note: This method is designed to be called by "addInsight(...)", which
	 * gives the added Insight an order of 999.
	 *
	 * @param newOrder -- (int) New order for the added Insight.
	 *
	 * @param perspective -- (Perspective) The Perspective to which the added
	 * Insight belongs.
	 *
	 * @return setInsightOrders -- (boolean) Whether the Insight reordering
	 * succeeded.
	 */
	private boolean setInsightOrders( int newOrder, Perspective perspective ) {
		boolean boolReturnValue = false;
		ValueFactory insightVF = rc.getValueFactory();
		List<Insight> arylInsights = wim.getInsights( perspective );

		try {
			rc.begin();
			for ( Insight insight : arylInsights ) {
				int currentOrder = insight.getOrder( perspective.getUri() );

				if ( currentOrder >= newOrder && currentOrder < 999 ) {
					Literal order = insightVF.createLiteral( currentOrder + 1 );
					String query_1 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
							+ "DELETE{ ?slot olo:index ?o .} "
							+ "INSERT{ ?slot olo:index " + order + " .} "
							+ "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
							+ "?slot olo:index ?o . "
							+ "?slot olo:item <" + insight.getId() + ">} ";
					Update uq_1 = rc.prepareUpdate( QueryLanguage.SPARQL, query_1 );
					uq_1.execute();

				}
				else if ( currentOrder == 999 ) {
					Literal order = insightVF.createLiteral( newOrder );
					String query_2 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
							+ "DELETE{ ?slot olo:index ?o .} "
							+ "INSERT{ ?slot olo:index " + order + " .} "
							+ "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
							+ "?slot olo:index ?o . "
							+ "?slot olo:item <" + insight.getId() + ">} ";
					Update uq_2 = rc.prepareUpdate( QueryLanguage.SPARQL, query_2 );
					uq_2.execute();
				}
			}
			rc.commit();
			boolReturnValue = true;

		}
		catch ( Exception e ) {
			e.printStackTrace();
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Reorders Insights under the passed-in Perspective, using the new orders
	 * within the passed-in Insight array-list.
	 *
	 * @param perspectiveURI -- (URI) The URI of the Perspective containing the
	 * passed-in Insights.
	 *
	 * @param arylInsights -- (ArrayList<Insight>) An array list of Insights that
	 * needs all Insight Orders persisted
	 *
	 * @return setInsightOrders -- (boolean) Whether the Insight reordering
	 * succeeded.
	 */
	public boolean reorderInsights( URI perspectiveURI, List<Insight> arylInsights ) {
		boolean boolReturnValue = false;
		ValueFactory insightVF = rc.getValueFactory();

		//If no Insights have been passed in, return true:
		if ( arylInsights == null ) {
			return true;
		}

		try {
			rc.begin();
			for ( Insight insight : arylInsights ) {
				Literal order = insightVF.createLiteral( insight.getOrder( perspectiveURI ) );
				String query = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
						+ "DELETE{ ?slot olo:index ?o .} "
						+ "INSERT{ ?slot olo:index " + order + " .} "
						+ "WHERE{ <" + perspectiveURI + "> olo:slot ?slot . "
						+ "?slot olo:index ?o . "
						+ "?slot olo:item <" + insight.getId() + "> . } ";
				Update uq = rc.prepareUpdate( QueryLanguage.SPARQL, query );
				uq.execute();
			}
			rc.commit();
			boolReturnValue = true;

		}
		catch ( Exception e ) {
			e.printStackTrace();
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Removes an Insight from a Perspective in the triple-store on disk.
	 *
	 * @param arylInsights - (ArrayList<Insight>) All Insights under the current
	 * Perspective.
	 *
	 * @param insight -- (Insight) Insight to remove from the Perspective.
	 *
	 * @param perspective -- (Perspective) Perspective containing the above
	 * Insight.
	 *
	 * @param doImport -- (boolean) Whether to import memory database to disk.
	 */
	@Override
	public boolean removeInsight( List<Insight> arylInsights, Insight insight,
			Perspective perspective, boolean doImport ) {
		boolean boolReturnValue = false;

		  //Adjust orders of all Insights under the current Perspective
		//as if this Insight has been removed:
		arylInsights.remove( insight );
		for ( int i = 0; i < arylInsights.size(); i++ ) {
			arylInsights.get( i ).setOrder( perspective.getUri().toString(), i + 1 );
		}

		String query_1 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
				+ "DELETE{ ?perspective olo:slot ?slot . "
				+ "?slot ?p ?o .} "
				+ "WHERE{ ?slot olo:item <" + insight.getIdStr() + "> . "
				+ "BIND( <" + perspective.getUri() + "> AS ?perspective) . "
				+ "?perspective olo:slot ?slot . "
				+ "?slot ?p ?o .}";

		try {
			rc.begin();

			Update uq_1 = rc.prepareUpdate( QueryLanguage.SPARQL, query_1 );
			uq_1.execute();

			rc.commit();

			//Reorder Insights under the current Perspective:
			boolReturnValue = reorderInsights( perspective.getUri(), arylInsights );

			if ( doImport == true ) {
	            //Import Insights into the repository if deletion succeeded
				//and Insights have been reordered:
				if ( boolReturnValue == true ) {
					boolReturnValue = EngineUtil.getInstance().importInsights( wim );
				}
				//Give the left-pane drop-downs enough time to refresh from the import:
				Thread.sleep( 2000 );
			}
		}
		catch ( Exception e ) {
			log.error( e, e );
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Adds a new Perspective to the triple-store on disk.
	 *
	 * @param strTitle -- (String) Title of Perspective (rdfs:label).
	 *
	 * @param strUriTitle -- (String) Title of Perspective suitable for a URI (no
	 * whitespace).
	 *
	 * @param strDescription -- (String) Description of Perspective
	 * (dcterms:description).
	 *
	 * @param addDummyInsight -- (boolean) Whether to add a dummy Insight to this
	 * Perspective.
	 *
	 * @return addPerspective -- (boolean) Whether the save to disk succeeded.
	 */
	@Override
	public boolean addPerspective( String strTitle, String strUriTitle, String strDescription, boolean addDummyInsight ) {
		boolean boolReturnValue = false;
		ValueFactory insightVF = rc.getValueFactory();
		Literal now = insightVF.createLiteral( new Date() );
		String strCreator = wim.userInfoFromToolPreferences( "Created By Insight Manager, " + System.getProperty( "release.nameVersion", "VA SEMOSS" ) );
		Literal creator = insightVF.createLiteral( strCreator );
		Literal title = insightVF.createLiteral( strTitle );
		Literal description = insightVF.createLiteral( strDescription );

		String strUniqueIdentifier = String.valueOf( System.currentTimeMillis() );

		try {
			rc.begin();

			URI perspectiveURI = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, strUriTitle );
			rc.add( perspectiveURI, RDF.TYPE, VAS.Perspective );
			rc.add( perspectiveURI, RDFS.LABEL, title );
			rc.add( perspectiveURI, DCTERMS.DESCRIPTION, description );
			rc.add( perspectiveURI, DCTERMS.CREATED, now );
			rc.add( perspectiveURI, DCTERMS.MODIFIED, now );
			rc.add( perspectiveURI, DCTERMS.CREATOR, creator );

			if ( addDummyInsight == true ) {
				String slotUriName = strUriTitle + "-slot-" + strUniqueIdentifier;
				URI slot = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, slotUriName );
				rc.add( perspectiveURI, OLO.slot, slot );

				String insightUriName = strUriTitle + "-insight-" + strUniqueIdentifier;
				URI insightURI = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, insightUriName );

				rc.add( slot, OLO.item, insightURI );
				rc.add( slot, OLO.index, insightVF.createLiteral( 1 ) );

				String dataViewName = "gov.va.semoss.ui.components.playsheets.GridPlaySheet";
				URI dataViewURI = insightVF.createURI( VAS.NAMESPACE, dataViewName );
				rc.add( dataViewURI, RDFS.LABEL, insightVF.createLiteral( dataViewName ) );

				String sparql = "";

				String spinBodyUriName = strUriTitle + "-insight-body-" + strUniqueIdentifier;
				URI spinBody = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, spinBodyUriName );
				rc.add( spinBody, RDF.TYPE, SP.Construct );
				rc.add( spinBody, SP.text, insightVF.createLiteral( sparql ) );

				rc.add( insightURI, RDF.TYPE, SPIN.Function );
				rc.add( insightURI, RDFS.SUBCLASSOF, VAS.InsightProperties );
				rc.add( insightURI, RDFS.LABEL, insightVF.createLiteral( "New (" + strTitle + ") Insight" ) );
				rc.add( insightURI, SPIN.body, spinBody );
				rc.add( insightURI, UI.dataView, dataViewURI );
				rc.add( insightURI, VAS.isLegacy, insightVF.createLiteral( true ) );
				rc.add( insightURI, DCTERMS.CREATED, now );
				rc.add( insightURI, DCTERMS.MODIFIED, now );
				rc.add( insightURI, DCTERMS.CREATOR, creator );
			}
			rc.commit();

			//Import Insights into the repository:
			boolReturnValue = EngineUtil.getInstance().importInsights( wim );
			//Give the left-pane drop-downs enough time to refresh from the import:
			Thread.sleep( 2000 );

		}
		catch ( Exception e ) {
			e.printStackTrace();
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Deletes a Perspective, and associated Insights, from the triple-store on
	 * disk.
	 *
	 * @param perspective -- (Perspective) Perspective to remove from database.
	 */
	@Override
	public boolean deletePerspective( Perspective perspective ) {
		boolean boolReturnValue = false;

//	      String query_1 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
//	       	   + "PREFIX " + SPIN.PREFIX + ": <" + SPIN.NAMESPACE + "> "
//	    	   + "DELETE{ ?constraint ?p ?o .} "
//	   	       + "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
//	   	       + "?slot olo:item ?item . "
//	   	       + "?item spin:constraint ?constraint . "
//	   	       + "?constraint ?p ?o .} ";
//	      String query_2 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
//	    	   + "PREFIX " + SPIN.PREFIX + ": <" + SPIN.NAMESPACE + "> "
//	 	       + "DELETE{ ?body ?p ?o .} "
//		       + "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
//		       + "?slot olo:item ?item . "
//		       + "?item spin:body ?body . "
//		       + "?body ?p ?o .} ";
//		  String query_3 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
//				  + "DELETE{ ?item ?p ?o .} "
//	          + "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
//	          + "?slot olo:item ?item . "
//	          + "?item ?p ?o .} ";
		String query_4 = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
				+ "DELETE{ ?slot ?p ?o .} "
				+ "WHERE{ <" + perspective.getUri() + "> olo:slot ?slot . "
				+ "?slot ?p ?o .} ";

		String query_5 = "DELETE{ <" + perspective.getUri() + "> ?p ?o .} "
				+ "WHERE{ <" + perspective.getUri() + "> ?p ?o . }";

		try {
			rc.begin();

//	         Update uq_1 = rc.prepareUpdate(QueryLanguage.SPARQL, query_1);
//	         Update uq_2 = rc.prepareUpdate(QueryLanguage.SPARQL, query_2);
//	         Update uq_3 = rc.prepareUpdate(QueryLanguage.SPARQL, query_3);
			Update uq_4 = rc.prepareUpdate( QueryLanguage.SPARQL, query_4 );
			Update uq_5 = rc.prepareUpdate( QueryLanguage.SPARQL, query_5 );
//	         uq_1.execute();
//	         uq_2.execute();
//	         uq_3.execute();
			uq_4.execute();
			uq_5.execute();

			rc.commit();

			//Import Insights into the repository:
			boolReturnValue = EngineUtil.getInstance().importInsights( wim );
			//Give the left-pane drop-downs enough time to refresh from the import:
			Thread.sleep( 2000 );

		}
		catch ( Exception e ) {
			log.error( e, e );
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Saves a Perspective's Title and Description into the triple-store on disk,
	 * where the passed-in URI is the subject.
	 *
	 * @param arylInsights - (ArrayList<Insight>) All Insights under the current
	 * Perspective.
	 *
	 * @param uri -- (String) URI of Perspective.
	 *
	 * @param strTitle -- (String) Title of Perspective (rdfs:label).
	 *
	 * @param strDescription (String) Description of Perspective
	 * (dcterms:description).
	 *
	 * @return savePerspective -- (boolean) Whether the save to disk succeeded.
	 */
	@Override
	public boolean savePerspective( List<Insight> arylInsights, String uri,
			String strTitle, String strDescription ) {
		boolean boolReturnValue = false;
		ValueFactory insightVF = rc.getValueFactory();

		//Make sure that embedded new-line characters can be persisted:
		strDescription = strDescription.replace( "\n", "\\n" );

		String query = "PREFIX " + DCTERMS.PREFIX + ": <" + DCTERMS.NAMESPACE + "> "
				+ "DELETE{ ?uri rdfs:label ?label .  "
				+ "?uri dcterms:description ?description . } "
				+ "INSERT{ ?uri rdfs:label \"" + strTitle + "\". "
				+ "?uri dcterms:description \"" + strDescription + "\".} "
				+ "WHERE{ BIND(<" + uri + "> AS ?uri) . "
				+ "?uri rdfs:label ?label . "
				+ "OPTIONAL{ ?uri dcterms:description ?description .} } ";
		try {
			rc.begin();
			Update uq = rc.prepareUpdate( QueryLanguage.SPARQL, query );
			uq.execute();
			rc.commit();

			//Reorder Insights under the current Perspective:
			boolReturnValue = reorderInsights( insightVF.createURI( uri ), arylInsights );

	         //Import Insights into the repository if save succeeded
			//and Insights have been reordered:
			if ( boolReturnValue == true ) {
				boolReturnValue = EngineUtil.getInstance().importInsights( wim );
			}
			//Give the left-pane drop-downs enough time to refresh from the import:
			Thread.sleep( 2000 );

		}
		catch ( Exception e ) {
			e.printStackTrace();
			try {
				rc.rollback();
			}
			catch ( Exception ee ) {
				log.warn( ee, ee );
			}
		}
		return boolReturnValue;
	}

	/**
	 * Searches the database for Insights that are not associated with any
	 * Perspective, and places them under the "Detached-Insight-Perspective". If
	 * no "Detached-Insight-Perspective" exists, then this method creates one.
	 *
	 * @return saveDetachedInsights -- (boolean) Whether the operations described
	 * above succeeded. Note: Should there be no detached Insights in the
	 * database, then true will be returned.
	 */
	@Override
	public boolean saveDetachedInsights() {
		boolean boolReturnValue = true;
		ValueFactory insightVF = rc.getValueFactory();
		Collection<String> colDanglingInsights = new ArrayList<String>();

		String perspectiveUriName = "ZZZ-Detached-Insight-Perspective";
		URI perspectiveURI = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, perspectiveUriName );

		//Fetch a collection of dangling Insight URI's:
		colDanglingInsights.addAll( getDanglingInsights() );

		if ( doesPerspectiveExist( perspectiveURI ) == false && colDanglingInsights.size() > 0 ) {
			boolReturnValue = addPerspective( "ZZZ-Detached-Insight-Perspective", "ZZZ-Detached-Insight-Perspective",
					"Perspective for dangling Insights", true );
		}

		if ( boolReturnValue == true && colDanglingInsights.size() > 0 ) {
			try {
				rc.begin();

				for ( String insightURI : colDanglingInsights ) {
					String strUniqueIdentifier = String.valueOf( System.currentTimeMillis() );
					String slotUriName = "ZZZ-Detached-Insight-Perspective-slot-" + strUniqueIdentifier;
					URI slot = insightVF.createURI( MetadataConstants.VA_INSIGHTS_NS, slotUriName );
					rc.add( perspectiveURI, OLO.slot, slot );
					rc.add( slot, OLO.item, insightVF.createURI( insightURI ) );
					int intIndex = getNextInsightIndex( perspectiveURI );
					rc.add( slot, OLO.index, insightVF.createLiteral( intIndex ) );
				}
				rc.commit();

				//Import Insights into the repository:
				boolReturnValue = EngineUtil.getInstance().importInsights( wim );
				//Give the left-pane drop-downs enough time to refresh from the import:
				Thread.sleep( 2000 );

			}
			catch ( Exception e ) {
				e.printStackTrace();
				try {
					rc.rollback();
				}
				catch ( Exception ee ) {
					log.warn( ee, ee );
				}
				boolReturnValue = false;
			}
		}
		return boolReturnValue;
	}

	/**
	 * Determines if the Perspective URI passed-in exists within the database
	 *
	 * @param perspectiveURI -- The URI of the Perspective to find.
	 *
	 * @return doesPerspectiveExist -- (boolean) Whether the Perspective exists.
	 */
	private boolean doesPerspectiveExist( URI perspectiveURI ) {
		boolean boolReturnValue = false;

		List<String> uris = new ArrayList<>();
		try {
			String q = "PREFIX " + VAS.PREFIX + ": <" + VAS.NAMESPACE + "> "
					+ "SELECT DISTINCT ?perspectiveURI WHERE { "
					+ "BIND(<" + perspectiveURI + "> AS ?perspectiveURI) . "
					+ "?perspectiveURI a <" + VAS.Perspective + "> .}";

			ListQueryAdapter<String> lqa = new ListQueryAdapter<String>( q ) {
				@Override
				public void handleTuple( BindingSet set, ValueFactory fac ) {
					add( set.getValue( "perspectiveURI" ).stringValue() );
				}
			};
			Collection<String> r = AbstractSesameEngine.getSelect( lqa, rc, true );
			uris.addAll( r );

			if ( uris.isEmpty() == false ) {
				boolReturnValue = true;
			}

		}
		catch ( RepositoryException | MalformedQueryException | QueryEvaluationException e ) {
			log.error( e, e );
		}
		return boolReturnValue;
	}

	/**
	 * Obtains the next available Insight ordering index from the passed-in
	 * Perspective.
	 *
	 * @param perspectiveURI -- (URI) URI of a Perspective.
	 *
	 * @return getNextInsightIndex -- (int) Described above.
	 */
	@Override
	public int getNextInsightIndex( URI perspectiveURI ) {
		int intReturnValue = 1;

		List<Integer> indices = new ArrayList<>();
		try {
			String q = "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
					+ "SELECT ?index WHERE { "
					+ "BIND(<" + perspectiveURI + "> AS ?perspectiveURI) "
					+ "?perspectiveURI olo:slot [olo:index ?index ] .} "
					+ "ORDER BY DESC(?index) LIMIT 1";

			ListQueryAdapter<Integer> lqa = new ListQueryAdapter<Integer>( q ) {
				@Override
				public void handleTuple( BindingSet set, ValueFactory fac ) {
					add( Integer.valueOf( set.getValue( "index" ).stringValue() ) );
				}
			};
			Collection<Integer> r = AbstractSesameEngine.getSelect( lqa, rc, true );
			indices.addAll( r );

			if ( indices.isEmpty() == false ) {
				intReturnValue = indices.get( 0 ) + 1;
			}

		}
		catch ( RepositoryException | MalformedQueryException | QueryEvaluationException e ) {
			log.error( e, e );
		}
		return intReturnValue;
	}

	/**
	 * Returns a list of URIs of Insights that have no associated Perspective in
	 * the database.
	 *
	 * @return getDanglingInsights -- (Collection<String>) Described above.
	 */
	private Collection<String> getDanglingInsights() {
		List<String> uris = new ArrayList<>();
		try {
			String q = "PREFIX " + SPIN.PREFIX + ": <" + SPIN.NAMESPACE + "> "
					+ "PREFIX " + OLO.PREFIX + ": <" + OLO.NAMESPACE + "> "
					+ "SELECT ?insightURI WHERE{ "
					+ "?insightURI spin:body ?body . "
					+ "MINUS{ ?perspective olo:slot [ olo:item ?insightURI ]  } }";

			ListQueryAdapter<String> lqa = new ListQueryAdapter<String>( q ) {
				@Override
				public void handleTuple( BindingSet set, ValueFactory fac ) {
					add( set.getValue( "insightURI" ).stringValue() );
				}
			};
			Collection<String> r = AbstractSesameEngine.getSelect( lqa, rc, true );
			uris.addAll( r );

		}
		catch ( RepositoryException | MalformedQueryException | QueryEvaluationException e ) {
			log.error( e, e );
		}
		return uris;
	}

}//End class, WriteablePerspectiveTabImpl.