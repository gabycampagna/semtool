/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.engine.impl;

import gov.va.semoss.rdf.engine.api.InsightManager;
import gov.va.semoss.rdf.engine.util.EngineManagementException;
import gov.va.semoss.rdf.engine.util.EngineManagementException.ErrorCode;
import gov.va.semoss.rdf.engine.util.StatementSorter;
import gov.va.semoss.user.Security;
import gov.va.semoss.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.openrdf.http.protocol.UnauthorizedException;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 *
 * @author ryan
 */
public class SesameEngine extends AbstractSesameEngine {

	private static final Logger log = Logger.getLogger( SesameEngine.class );
	private Repository insights;
	private RepositoryConnection data;

	public SesameEngine( Properties initProps ) {
		super( initProps );
		this.openDB( initProps );
	}

	@Override
	protected void createRc( Properties props ) throws RepositoryException {
		String url = props.getProperty( REPOSITORY_KEY );
		String ins = props.getProperty( INSIGHTS_KEY );

		Pattern pat = Pattern.compile( "^(.*)/repositories/(.*)" );
		Matcher m = pat.matcher( url );
		if ( m.find() ) {
			for ( int i = 0; i < m.groupCount(); i++ ) {
				log.debug( m.group( i ) );
			}
		}

		String username = props.getProperty( "username", "" );
		String password = props.getProperty( "password", "" );

		HTTPRepository repo = ( m.find()
				? new HTTPRepository( m.group( 1 ), m.group( 2 ) )
				: new HTTPRepository( url ) );
		if ( !username.isEmpty() ) {
			repo.setUsernameAndPassword( username, password );
		}
		repo.initialize();

		data = repo.getConnection();

		m.reset( ins );
		HTTPRepository tmp = ( m.find()
				? new HTTPRepository( m.group( 1 ), m.group( 2 ) )
				: new HTTPRepository( url ) );
		if ( !username.isEmpty() ) {
			tmp.setUsernameAndPassword( username, password );
		}
		insights = tmp;
	}

	@Override
	protected RepositoryConnection getRawConnection() {
		return data;
	}

	@Override
	protected InsightManager createInsightManager() {
		return InsightManagerImpl.createFromRepository( insights );
	}

	@Override
	public void updateInsights( InsightManager im ) throws EngineManagementException {
		List<Statement> stmts = new ArrayList<>();
		User user = Security.getSecurity().getAssociatedUser( this );
		stmts.addAll( InsightManagerImpl.getStatements( im, user ) );
		Collections.sort( stmts, new StatementSorter() );

		RepositoryConnection rc = null;
		try {
			rc = insights.getConnection();
			rc.begin();
			rc.clear();
			rc.add( stmts );
			rc.commit();
		}
		catch( UnauthorizedException ue ){
			throw new EngineManagementException( ErrorCode.ACCESS_DENIED, ue );
		}
		catch ( RepositoryException re ) {
			log.error( re, re );
			if ( null != rc ) {
				try {
					rc.rollback();
				}
				catch ( RepositoryException re2 ) {
					log.error( re2, re2 );
				}
			}
			throw new EngineManagementException( ErrorCode.UNKNOWN, re );
		}
		finally {
			if ( null != rc ) {
				try {
					rc.close();
				}
				catch ( RepositoryException re ) {
					log.warn( re, re );
				}
			}
		}
	}
}