package com.ostrichemulators.semtool.rdf.engine.util;

import com.ostrichemulators.semtool.model.vocabulary.SEMTOOL;
import com.ostrichemulators.semtool.rdf.engine.api.IEngine;
import com.ostrichemulators.semtool.rdf.query.util.MetadataQuery;
import com.ostrichemulators.semtool.rdf.query.util.impl.ListQueryAdapter;
import com.ostrichemulators.semtool.rdf.query.util.impl.MapQueryAdapter;
import com.ostrichemulators.semtool.rdf.query.util.impl.OneValueQueryAdapter;
import com.ostrichemulators.semtool.rdf.query.util.impl.OneVarListQueryAdapter;
import com.ostrichemulators.semtool.util.Constants;
import com.ostrichemulators.semtool.util.Utility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.query.BindingSet;

/**
 * This class is responsible for providing a number of utility methods for the
 * SEMOSS system, specifically in the area of producing concept collections from
 * gross RDF content, and deriving predicates.
 *
 * @author Wayne Warren
 *
 */
public class NodeDerivationTools {

	/**
	 * The logger for this class
	 */
	private static final Logger log = Logger.getLogger( NodeDerivationTools.class );

	/**
	 * Private singleton default constructor
	 */
	private NodeDerivationTools() {

	}

	public static String getConceptQuery( IEngine engine ) {
		MetadataQuery mq = new MetadataQuery( SEMTOOL.ReificationModel );
		engine.queryNoEx( mq );
		URI reif = URI.class.cast( mq.getOne() );

		if ( SEMTOOL.Custom_Reification.equals( reif ) ) {
			OneValueQueryAdapter<String> qq = OneValueQueryAdapter.
					getString( "SELECT ?val WHERE { ?base ?pred ?val }" );
			qq.bind( "base", engine.getBaseUri() );
			qq.bind( "pred", SEMTOOL.ConceptsSparql );

			return engine.queryNoEx( qq );
		}
		else {
			String query = "SELECT ?concept WHERE "
					+ "{ "
					+ "  ?concept rdfs:subClassOf+ ?top ."
					+ "  FILTER( ?concept != ?top && \n"
					+ "    ?concept != <http://www.w3.org/2004/02/skos/core#Concept> ) ."
					+ "  VALUES ?top {<replace-with-binding>} "
					+ " }";

			return query.replaceAll( "replace-with-binding",
					engine.getSchemaBuilder().getConceptUri().build().stringValue() );
		}
	}

	/**
	 * Produces a list of concepts based on a given engine that has digested a RDF
	 * knowledgebase.
	 *
	 * @param engine The RDF knowledgebase
	 * @return A list of concepts in URI form
	 * @deprecated use {@link StructureManager#getTopLevelConcepts() }
	 */
	@Deprecated
	public static List<URI> createConceptList( IEngine engine ) {
		OneVarListQueryAdapter<URI> qe
				= OneVarListQueryAdapter.getUriList( getConceptQuery( engine ) );
		final List<URI> conceptList = engine.queryNoEx( qe );
		return conceptList;
	}

	public static List<URI> createInstanceList( URI concept, IEngine engine ) {
		String query = "SELECT DISTINCT ?instance WHERE { ?instance rdf:type ?concept }";
		ListQueryAdapter<URI> qa = OneVarListQueryAdapter.getUriList( query );
		qa.bind( "concept", concept );

		return engine.queryNoEx( qa );
	}

	/**
	 * Derives a query adapter capable of pulling out the predicates that connect
	 * all subject nodes of a given type and all object nodes of a given type. The
	 * results will contain *all* types, so they will generally be run through {@link
	 * #getTopLevelRelations(java.util.Collection,
	 * com.ostrichemulators.semtool.rdf.engine.api.IEngine) } to get only the
	 * top-level relationships
	 *
	 * @param subjectNodeType The type (in URI form) of the subject node
	 * @param objectNodeType The type (in URI form) of the object node
	 * @param engine
	 * @return A proper query adapter capable of querying a knowledgebase for the
	 * desired predicates
	 */
	public static ListQueryAdapter<URI> getPredicatesBetweenQA( URI subjectNodeType,
			URI objectNodeType, IEngine engine ) {
		String q
				= "SELECT DISTINCT ?relationship WHERE {\n"
				+ "  ?in  a ?stype . \n"
				+ "  ?out a ?otype . \n"
				+ "  ?in ?relationship ?out .\n"
				+ "  FILTER( ?relationship != ?semrel )\n"
				+ "}";
		OneVarListQueryAdapter<URI> varq = OneVarListQueryAdapter.getUriList( q );
		varq.useInferred( false );
		varq.bind( "semrel", engine.getSchemaBuilder().getRelationUri().build() );
		varq.bind( "stype", subjectNodeType );
		if ( !objectNodeType.equals( Constants.ANYNODE ) ) {
			varq.bind( "otype", objectNodeType );
		}

		log.debug( varq.bindAndGetSparql() );
		return varq;
	}

	/**
	 * Get a list of predicates that connect subjects and objects of given types
	 *
	 * @param subjectNodeType The type of subject node
	 * @param objectNodeType The type of object node
	 * @param engine The engine, which contains a digested knowledgebase, which
	 * will run the query designed to derive the various predicates between the
	 * node types
	 * @return A list of predicates, in URI
	 * @deprecated use {@link StructureManager#getLinksBetween(org.openrdf.model.URI,
	 * org.openrdf.model.URI) } instead
	 */
	@Deprecated
	public static List<URI> getPredicatesBetween( URI subjectNodeType, URI objectNodeType,
			IEngine engine ) {
		List<URI> values = engine.queryNoEx( getPredicatesBetweenQA( subjectNodeType,
				objectNodeType, engine ) );
		return new ArrayList<>( getTopLevelRelations( values, engine ) );
	}

	public static URI getType( URI instance, IEngine engine ) {
		String query = "SELECT ?object "
				+ "WHERE { "
				+ "  ?subject a ?object "
				+ "  FILTER NOT EXISTS { ?subject a ?subtype . ?subtype rdfs:subClassOf ?object }"
				+ "}";

		return engine.queryNoEx( OneValueQueryAdapter.getUri( query ).bind( "subject", instance ) );
	}

	/**
	 *
	 * @param instance
	 * @param engine
	 * @param instanceIsSubject
	 * @return
	 * @deprecated use {@link StructureManager#getConnectedConceptTypes(java.util.Collection)
	 * } instead
	 */
	@Deprecated
	public static List<URI> getConnectedConceptTypes( URI instance, IEngine engine,
			boolean instanceIsSubject ) {
		String query = "SELECT DISTINCT ?subtype ?objtype \n"
				+ "WHERE { \n"
				+ "  ?subject ?predicate ?object .\n"
				+ "  ?subject a ?subtype .\n"
				+ "  ?subtype rdfs:subClassOf ?concept . FILTER( ?subtype != ?concept && ?subtype != ?skos ) .\n"
				+ "  ?object a ?objtype .\n"
				+ "  ?objtype rdfs:subClassOf ?concept . FILTER( ?objtype != ?concept && ?objtype != ?skos ) .\n"
				+ "}";

		OneVarListQueryAdapter<URI> lqa = OneVarListQueryAdapter.getUriList( query );
		lqa.bind( "concept", engine.getSchemaBuilder().getConceptUri().build() );
		lqa.bind( "skos", SKOS.CONCEPT );

		if ( instanceIsSubject ) {
			lqa.setVariableName( "objtype" );
			lqa.bind( "subject", instance );
	}
		else {
			lqa.setVariableName( "subtype" );
			lqa.bind( "object", instance );
		}

		log.debug( "connected types (one instance) is: " + lqa.bindAndGetSparql() );

		return engine.queryNoEx( lqa );
	}

	/**
	 * Gets the top-level connections between the give node types
	 *
	 * @param subtype
	 * @param objtype
	 * @param engine
	 * @return
	 * @deprecated use {@link StructureManager#getLinksBetween(org.openrdf.model.URI,
	 * org.openrdf.model.URI) } instead
	 */
	@Deprecated
	public static Collection<URI> getConnections( URI subtype, URI objtype, IEngine engine ) {
		String query = "SELECT DISTINCT ?rel {\n"
				+ "  ?s a ?subtype .\n"
				+ "  ?o a ?objtype .\n"
				+ "  ?s ?rel ?o .\n"
				+ "  ?rel rdfs:subPropertyOf ?semrel .\n"
				+ "  FILTER( ?rel != ?semrel ) .\n"
				+ "  FILTER( ?subtype != ?concept && ?subtype != ?skos ) .\n"
				+ "  FILTER( ?objtype != ?concept && ?objtype != ?skos ) .\n"
				+ "}";
		ListQueryAdapter<URI> lqa = OneVarListQueryAdapter.getUriList( query );
		lqa.bind( "semrel", engine.getSchemaBuilder().getRelationUri().build() );
		lqa.bind( "concept", engine.getSchemaBuilder().getConceptUri().build() );
		lqa.bind( "skos", SKOS.CONCEPT );
		lqa.bind( "subtype", subtype );
		lqa.bind( "objtype", objtype );
		Set<URI> edges = getTopLevelRelations( engine.queryNoEx( lqa ), engine );

		return edges;
	}

	/**
	 * Gets the subject (or object) types connected
	 *
	 * @param instances
	 * @param engine
	 * @param instanceIsSubject
	 * @return
	 * @deprecated use {@link StructureManager#getConnectedConceptTypes(java.util.Collection)
	 * }
	 * instead
	 */
	@Deprecated
	public static List<URI> getConnectedConceptTypes( Collection<URI> instances,
			IEngine engine, boolean instanceIsSubject ) {

		StringBuilder query = new StringBuilder( "SELECT DISTINCT ?subtype ?objtype " )
				.append( "WHERE { " )
				.append( "  ?subject ?predicate ?object ." )
				.append( "  ?subject a ?subtype . FILTER( ?subtype != ?skos ) ." )
				.append( "  ?object a ?objtype . FILTER isUri( ?object ) ." )
				.append( "  FILTER( ?objtype != ?skos ) .")
				.append( "  MINUS { ?subject a ?object } " )
				.append( "} VALUES ?" );
		query.append( instanceIsSubject ? "subject " : "object" );
		query.append( "{" );
		query.append( Utility.implode( instances ) );
		query.append( "}" );

		OneVarListQueryAdapter<URI> lqa
				= OneVarListQueryAdapter.getUriList( query.toString() );
		lqa.bind( "skos", SKOS.CONCEPT );
		if ( instanceIsSubject ) {
			lqa.setVariableName( "objtype" );
		}
		else {
			lqa.setVariableName( "subtype" );
		}

		log.debug( "connected types (many instances) is: " + lqa.bindAndGetSparql() );

		return engine.queryNoEx( lqa );
	}

	/**
	 * Gets the set of top-level relations for the given set. The input may
	 * contain all "specific" relationships, all "top level", or a mixture of both
	 *
	 * @param rels
	 * @param engine
	 * @return the smallest set of relations that cover the input rels. All the
	 * returned URIs will be <code>rdfs:subClassOf semonto:Relation</code>
	 * @deprecated use
	 * {@link StructureManager#getTopLevelRelations(java.util.Collection)}
	 */
	@Deprecated
	public static Set<URI> getTopLevelRelations( Collection<URI> rels,
			IEngine engine ) {

		Set<URI> todo = new HashSet<>( rels ); // get unique set of input

		if ( todo.isEmpty() ) {
			return todo;
		}

		// this query gets the top level URI for any specific URI
		String query = "SELECT ?rel ?superrel WHERE {\n"
				+ "  ?rel rdfs:subPropertyOf ?superrel .\n"
				+ "  ?superrel rdfs:subPropertyOf ?semrel .\n"
				+ "  FILTER( ?superrel != ?semrel ) .\n"
				+ "  FILTER( ?rel != ?superrel ) .\n"
				+ "  FILTER( ?rel != ?semrel ) ."
				+ "  VALUES ?rel {" + Utility.implode( todo ) + "} ."
				+ "}";
		MapQueryAdapter<URI, URI> mqa = new MapQueryAdapter<URI, URI>( query ) {

			@Override
			public void handleTuple( BindingSet set, ValueFactory fac ) {
				add( URI.class.cast( set.getValue( "rel" ) ),
						URI.class.cast( set.getValue( "superrel" ) ) );
			}

		};
		mqa.bind( "semrel", engine.getSchemaBuilder().getRelationUri().toUri() );

		mqa.useInferred( false );
		Map<URI, URI> inputOutput = engine.queryNoEx( mqa );

		// anything *not* in the inputOutput map wasn't a specific relation,
		// so it must be a top-level relation. Remove the specifics from the todo
		// set, and then add whatever's left to the output
		todo.removeAll( inputOutput.keySet() );
		for ( URI alreadytop : todo ) {
			inputOutput.put( alreadytop, alreadytop );
		}

		return new HashSet<>( inputOutput.values() );
	}
}
