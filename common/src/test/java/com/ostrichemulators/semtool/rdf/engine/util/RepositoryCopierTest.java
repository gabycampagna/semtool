/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.rdf.engine.util;

import info.aduna.iteration.Iterations;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author ryan
 */
public class RepositoryCopierTest {

	private RepositoryConnection from;
	private RepositoryConnection to;

	public RepositoryCopierTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() throws Exception {
		Repository fromrepo = new SailRepository( new MemoryStore() );
		Repository torepo = new SailRepository( new MemoryStore() );

		fromrepo.initialize();
		torepo.initialize();

		from = fromrepo.getConnection();
		to = torepo.getConnection();

		from.add( new StatementImpl( RDFS.DOMAIN, RDFS.LABEL,
				new LiteralImpl( "test" ) ) );
		from.add( new StatementImpl( RDFS.DOMAIN, RDFS.LABEL,
				new LiteralImpl( "test2" ) ) );
		from.setNamespace( OWL.PREFIX, OWL.NAMESPACE );
	}

	@After
	public void tearDown() throws Exception {
		from.close();
		from.getRepository().shutDown();

		to.close();
		to.getRepository().shutDown();
	}

	@Test
	public void testCopy() throws Exception {
		RepositoryCopier rc = new RepositoryCopier( to );
		rc.setCommitLimit( 1 );

		from.export( rc );

		assertEquals( 2, to.size() );

		Statement s = Iterations.asList( to.getStatements( RDFS.DOMAIN, null, null,
				true ) ).get( 0 );
		assertEquals( RDFS.LABEL, s.getPredicate() );
	}

}
