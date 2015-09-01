package gov.va.semoss.ui.helpers;

import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.query.util.QueryExecutorAdapter;
import gov.va.semoss.rdf.query.util.impl.OneVarListQueryAdapter;
import gov.va.semoss.ui.components.ParamComboBox;
import gov.va.semoss.util.Constants;
import gov.va.semoss.util.DIHelper;

import gov.va.semoss.util.GuiUtility;
import gov.va.semoss.util.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

/**
 * Contains a static method to build selected parameter values into a non-legacy
 * Insight query. And contains instance methods to populate Insight Parameter
 * combo-boxes.
 *
 * @author Thomas
 *
 */
public class NonLegacyQueryBuilder {

	private static final Logger logger = Logger.getLogger( NonLegacyQueryBuilder.class );
	//External Parameter Query:
	private String extQuery;

	/**
	 * Builds user-selected parameter values into a non-legacy query, and returns
	 * the filled query. If the "paramHash" is empty, then the passed-in query
	 * will be returned unaltered.
	 *
	 * @param query -- (String) Sparql query in the non-legacy format, where
	 * certain variables may have external bindings (in "paramHash"), and contains
	 * no "<@...@>" expressionns.
	 *
	 * @param paramHash (Map<String, String>) Key/value pairs where the key is a
	 * variable name occurring in "query", and the value is a String URI.
	 *
	 * @return buildNonLegacyQuery -- (String) The "query" with certain variables
	 * replaced by URI's from "paramHash".
	 *
	 * WARNING: A run-time exception may occur if a key from "paramHash" occurs in
	 * the SELECT clause. Wherever that key occurs, it will be replaced by a URI.
	 */
	public static String buildNonLegacyQuery( String query, Map<String, String> paramHash ) {
		if ( query.trim().equalsIgnoreCase( "null" ) || query.trim().isEmpty() ) {
			return "null";
		}

		QueryExecutorAdapter<String> queryExer = new QueryExecutorAdapter<String>() {
			@Override
			public void handleTuple( BindingSet set, ValueFactory fac ) {
			}
		};
		queryExer.setSparql( query );
		Map<String, String> map = new HashMap<>( paramHash );
		for ( Map.Entry<String, String> e : map.entrySet() ) {
			String key = e.getKey();
			String value = e.getValue();
			//We must prevent the creation of "VALUES" clauses 
			//when none of the variables are used by the query:
			Pattern pattern = Pattern.compile( "\\?" + key + "\\b" );
			Matcher matcher = pattern.matcher( query );
			if ( matcher.find() == true ) {
				queryExer.bindURI( key, value );
			}
		}
		return queryExer.bindAndGetSparql();
	}

	/**
	 * Fills a Parameter combo-box with labels (for display) and associated URIs.
	 *
	 * @param box -- (JComboBox) An Insight Parameter combo-box.
	 *
	 * @param engine -- (IEngine) The selected engine.
	 *
	 * @param type -- (URI-String) Value Type associated with the Parameter.
	 */
	public void fill( ParamComboBox box, IEngine engine, String type ) {
		Map<URI, String> mapURI_Labels = getData( engine, type );
		Collection<URI> colURIs = new ArrayList<>();
		//Try to fill a collection of URIs that don't have labels
		//from the Parameter query:
		for ( Map.Entry<URI, String> entry : mapURI_Labels.entrySet() ) {
			URI key = entry.getKey();
			String value = entry.getValue();

			if ( value.equals( "" ) ) {
				colURIs.add( key );
			}
		}
		//If the query did not fetch labels for URIs 
		//then fetch the labels via a utility method:
		if ( colURIs.size() == mapURI_Labels.size() ) {
			Map<URI, String> labellkp = GuiUtility.getInstanceLabels( colURIs, engine );
			box.setData( labellkp );

			//If labels were fetched with URIs, then use them:
		}
		else {
			box.setData( mapURI_Labels );
		}
		box.setEditable( false );
	}

	/**
	 * Sets the external query to the given SPARQL query.
	 *
	 * @param query -- (String) An external SPARQL query in string form.
	 */
	public void setExternalQuery( String query ) {
		this.extQuery = query;
	}

	/**
	 * Fetches data for an Insight Parameter combo-box, either from the Insight's
	 * BIND statement (legacy), from an externally defined Type, or from an
	 * externally defined Sparql query.
	 *
	 * @param eng -- (IEngine) The selected engine.
	 *
	 * @param type -- (URI-String) Value Type associated with the Parameter.
	 *
	 * @return getData -- (Map<URI, String>) Described above, where "URI" is a URI
	 * and "String" may be an "rdfs:label" value, or "".
	 */
	private Map<URI, String> getData( IEngine eng, String type ) {
		logger.debug( "NonLegacyQueryBuilder using engine: " + eng );
		Map<URI, String> mapReturnValue = new HashMap<>();

		if ( null != type ) {
			//If options for the Parameter have been explicitly defined 
			//on the question sheet, parse and use just them:
			String typeprop = type + "_" + Constants.OPTION;
			if ( DIHelper.getInstance().getProperty( typeprop ) != null ) {
				//Try to pick this from DBCM Properties table.
				//This will typically be of the format:
				String options = DIHelper.getInstance().getProperty( typeprop );

				//This is a string with ; delimited values:
				for ( String s : Arrays.asList( options.split( ";" ) ) ) {
					mapReturnValue.put( new URIImpl( s ), "" );
				}
			}
			else if ( DIHelper.getInstance().getLocalProp( type ) == null ) {
	      //The the param options have not been explicitly defined and 
				//the combo box has not been cached, time for the main processing:

				//Check if URI is used in param filler:
				if ( type.startsWith( "http://" ) ) {
					//Use the type query defined on RDF Map, unless an external query has been defined:
					String sparqlQuery 
							= DIHelper.getInstance().getProperty( "TYPE_" + Constants.QUERY );

					Map<String, String> paramTable = new HashMap<>();
					paramTable.put( Constants.ENTITY, type );
					sparqlQuery = extQuery == null
							? Utility.fillParam( sparqlQuery, paramTable ) : extQuery;

					Map<URI, String> mapURI_Labels = new HashMap<>();
					try {
						List<URI> uris = eng.query( OneVarListQueryAdapter.getUriList( sparqlQuery ) );
						mapURI_Labels.putAll(GuiUtility.getInstanceLabels( uris, eng ) );
						logger.debug( "URIs: " + mapURI_Labels );
					}
					catch ( RepositoryException | MalformedQueryException | QueryEvaluationException e ) {
						logger.error( e, e );
					}

					//Set return value:
					if ( mapURI_Labels.isEmpty() ) {
						mapReturnValue.put( ParamComboBox.MISSING_CONCEPT, "" );
					}
					else {
						mapReturnValue.putAll( mapURI_Labels );
					}
				}
				else {
					mapReturnValue.put( ParamComboBox.BAD_FILL, "" );
				}
			}
		}
		return mapReturnValue;
	}
}//End class, "NonLegacyQueryBuilder".