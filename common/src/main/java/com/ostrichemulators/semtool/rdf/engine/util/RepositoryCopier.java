/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ostrichemulators.semtool.rdf.engine.util;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author ryan
 */
public class RepositoryCopier implements RDFHandler {

	private static final Logger log = Logger.getLogger( RepositoryCopier.class );
	private final RepositoryConnection conn;
	private int commitlimit = 10000;
	private int adds = 0;
	private int totalAdds = 0;

	/**
	 * Creates an instance that copies data to the given Repository
	 *
	 * @param rc
	 */
	public RepositoryCopier( RepositoryConnection rc ) {
		conn = rc;
	}

	public void setCommitLimit( int lim ) {
		commitlimit = lim;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		adds = 0;
		totalAdds = 0;
		try {
			conn.begin();
		}
		catch ( Exception e ) {
			throw new RDFHandlerException( e );
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
 			log.debug( "committing " + totalAdds + " statements..." );
			conn.commit();
		}
		catch ( Exception e ) {
			throw new RDFHandlerException( e );
		}
	}

	@Override
	public void handleNamespace( String string, String string1 ) throws RDFHandlerException {
		try {
			conn.setNamespace( string, string1 );
		}
		catch ( Exception e ) {
			throw new RDFHandlerException( e );
		}
	}

	@Override
	public void handleStatement( Statement stmt ) throws RDFHandlerException {
		try {
			// RPB: need to rebox these statements because BigData blows up if
			// you try to copy one statement to another repository
			conn.add( EngineLoader.cleanStatement( stmt, conn.getValueFactory() ) );
			adds++;
			totalAdds++;
			if ( adds >= commitlimit ) {
				log.debug( "committed " + totalAdds + " statements..." );
				conn.commit();
				conn.begin();
				adds = 0;
			}
		}
		catch ( Exception e ) {
			try {
				conn.rollback();
			}
			catch ( Exception ex ) {
				throw new RDFHandlerException( ex );
			}
			throw new RDFHandlerException( e );
		}
	}

	@Override
	public void handleComment( String string ) throws RDFHandlerException {
		log.warn( "comments are not yet supported" );
	}
}
