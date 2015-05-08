/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.engine.edgemodelers;

import gov.va.semoss.model.vocabulary.VAS;
import gov.va.semoss.poi.main.ImportData;
import gov.va.semoss.poi.main.LoadingSheetData;
import gov.va.semoss.poi.main.LoadingSheetData.LoadingNodeAndPropertyValues;
import gov.va.semoss.rdf.engine.impl.InMemorySesameEngine;
import gov.va.semoss.rdf.engine.util.EngineLoader;
import gov.va.semoss.rdf.engine.util.QaChecker;
import gov.va.semoss.util.DeterministicSanitizer;
import gov.va.semoss.util.UriBuilder;
import info.aduna.iteration.Iterations;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author ryan
 */
public class SemossEdgeModelerTest {

	private static final Logger log = Logger.getLogger( SemossEdgeModelerTest.class );
	private static final ValueFactory vf = new ValueFactoryImpl();
	private static final Date now;
	private static final String SCHEMA = "http://semoss.org/ontologies/";
	private static final String DATA = "http://va.gov/ontologies/";

	private static final File REL1 = new File( "src/test/resources/semossedge-rel1.ttl" );
	private static final File REL2 = new File( "src/test/resources/semossedge-rel2.ttl" );
	private static final File META = new File( "src/test/resources/semossedge-mm.ttl" );
	private static final File NODE = new File( "src/test/resources/semossedge-node.ttl" );
	private static final File NODE2 = new File( "src/test/resources/semossedge-node2.ttl" );

	private QaChecker qaer;
	private InMemorySesameEngine engine;
	private EngineLoader loader;
	private LoadingSheetData rels;
	private LoadingSheetData nodes;
	private ImportData data;

	static {
		Calendar cal = Calendar.getInstance();
		cal.set( 2031, 9, 22, 6, 58, 59 );
		cal.set( Calendar.MILLISECOND, 15 );
		now = cal.getTime();
	}

	@BeforeClass
	public static void setUpClass() {
		// a deterministic sanitizer ensures we get repeatable results for URIs
		UriBuilder.setDefaultSanitizerClass( DeterministicSanitizer.class );
	}

	@Before
	public void setUp() throws RepositoryException {
		// this basically duplicates the data from test12.xlsx

		engine = new InMemorySesameEngine();
		engine.setBuilders( UriBuilder.getBuilder( DATA ), UriBuilder.getBuilder( SCHEMA ) );
		engine.getRawConnection().setNamespace( "vcamp", DATA );
		engine.getRawConnection().setNamespace( "semoss", SCHEMA );
		engine.getRawConnection().setNamespace( RDFS.PREFIX, RDFS.NAMESPACE );
		engine.getRawConnection().setNamespace( RDF.PREFIX, RDF.NAMESPACE );
		engine.getRawConnection().setNamespace( OWL.PREFIX, OWL.NAMESPACE );
		engine.getRawConnection().setNamespace( XMLSchema.PREFIX, XMLSchema.NAMESPACE );

		loader = new EngineLoader();
		loader.setDefaultBaseUri( new URIImpl( "http://sales.data/purchases/2015" ),
				false );

		qaer = new QaChecker();

		rels = LoadingSheetData.relsheet( "Human Being", "Car", "Purchased" );
		rels.addProperties( Arrays.asList( "Price", "Date" ) );

		nodes = LoadingSheetData.nodesheet( "Human Being" );
		nodes.addProperties( Arrays.asList( "First Name", "Last Name" ) );

		data = ImportData.forEngine( engine );
		data.add( rels );
		data.add( nodes );
	}

	@After
	public void tearDown() {
		engine.closeDB();
		loader.release();
	}

	private static Model getExpectedGraph( File rdf ) {
		SailRepository repo = new SailRepository( new MemoryStore() );
		RepositoryConnection expectedrc = null;
		List<Statement> stmts = new ArrayList<>();
		try {
			repo.initialize();
			expectedrc = repo.getConnection();
			expectedrc.add( rdf, null, RDFFormat.TURTLE );
			stmts.addAll( Iterations.asList( expectedrc.getStatements( null, null,
					null, true ) ) );
		}
		catch ( RepositoryException | IOException | RDFParseException e ) {
		}
		finally {
			if ( null != expectedrc ) {
				try {
					expectedrc.close();
				}
				catch ( Exception ex ) {
					// don't care
				}

				try {
					repo.shutDown();
				}
				catch ( Exception exc ) {
					// don't care
				}
			}
		}

		return new LinkedHashModel( stmts );
	}

	private static void compare( InMemorySesameEngine engine, File expected )
			throws IOException, RepositoryException, RDFHandlerException {

		// get rid of the random database id
		engine.getRawConnection().remove( (Resource) null, RDF.TYPE, VAS.Database );

		if ( log.isTraceEnabled() ) {
			File tmpdir = FileUtils.getTempDirectory();
			try ( Writer w = new BufferedWriter( new FileWriter( new File( tmpdir,
					expected.getName() ) ) ) ) {
				engine.getRawConnection().export( new TurtleWriter( w ) );
			}
		}

		Model model = getExpectedGraph( expected );
		List<Statement> stmts = Iterations.asList( engine.getRawConnection()
				.getStatements( null, null, null, false ) );

		assertEquals( model.size(), stmts.size() );

		for ( Statement s : stmts ) {
			assertTrue( model.contains( s ) );
		}
	}

	@Test
	public void testAddRel1() throws Exception {
		Map<String, Value> props = new HashMap<>();
		props.put( "Price", vf.createLiteral( "3000 USD" ) );
		props.put( "Date", vf.createLiteral( now ) );
		LoadingNodeAndPropertyValues rel = rels.add( "Yuri", "Yugo", props );

		SemossEdgeModeler instance = new SemossEdgeModeler( qaer );
		instance.createMetamodel( data, new HashMap<>(), engine.getRawConnection() );

		instance.addRel( rel, new HashMap<>(), rels, data.getMetadata(),
				engine.getRawConnection() );

		compare( engine, REL1 );
	}

	@Test
	public void testAddRel2() throws Exception {
		LoadingNodeAndPropertyValues rel = rels.add( "Alan", "Cadillac" );

		SemossEdgeModeler instance = new SemossEdgeModeler( qaer );
		instance.createMetamodel( data, new HashMap<>(), engine.getRawConnection() );

		instance.addRel( rel, new HashMap<>(), rels, data.getMetadata(),
				engine.getRawConnection() );

		compare( engine, REL2 );
	}

	@Test
	public void testCreateMetamodel() throws Exception {
		SemossEdgeModeler instance = new SemossEdgeModeler( qaer );
		instance.createMetamodel( data, new HashMap<>(), engine.getRawConnection() );
		compare( engine, META );
	}

	@Test
	public void testAddNode() throws Exception {
		Map<String, Value> props = new HashMap<>();
		props.put( "First Name", vf.createLiteral( "Yuri" ) );
		props.put( "Last Name", vf.createLiteral( "Gagarin" ) );
		LoadingNodeAndPropertyValues node = nodes.add( "Yuri", props );

		SemossEdgeModeler instance = new SemossEdgeModeler( qaer );
		instance.createMetamodel( data, new HashMap<>(), engine.getRawConnection() );

		instance.addNode( node, new HashMap<>(), rels, data.getMetadata(),
				engine.getRawConnection() );

		compare( engine, NODE );
	}

	@Test
	public void testAdd2Nodes() throws Exception {
		Map<String, Value> props = new HashMap<>();
		props.put( "First Name", vf.createLiteral( "Yuri" ) );
		props.put( "Last Name", vf.createLiteral( "Gagarin" ) );
		
		// adding two nodes with identical properties
		// testing to make sure we get two different URIs back
		LoadingNodeAndPropertyValues node = nodes.add( "Yuri", props );
		LoadingNodeAndPropertyValues node2 = nodes.add( "Yuri", props );

		SemossEdgeModeler instance = new SemossEdgeModeler( qaer );
		instance.createMetamodel( data, new HashMap<>(), engine.getRawConnection() );

		URI one = instance.addNode( node, new HashMap<>(), rels, data.getMetadata(),
				engine.getRawConnection() );
		URI two = instance.addNode( node2, new HashMap<>(), rels, data.getMetadata(),
				engine.getRawConnection() );

		assertNotEquals( one, two );
	}
}
