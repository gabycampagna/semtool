/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.web.io;

import gov.va.semoss.user.User;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author ryan
 */
public interface ServiceClient {

	/**
	 * Gets the available databases from the given url
	 *
	 * @param svc the service to hit
	 * @return a (possibly empty) array of database objects
	 * @throws RestClientException if there is a network/password problem
	 */
	public DbInfo[] getDbs( SemossService svc ) throws RestClientException;

	/**
	 * Gets details about the currently-logged-in user from the given url
	 *
	 * @param svc
	 * @return
	 * @throws RestClientException
	 */
	public User getUser( SemossService svc ) throws RestClientException;

	/**
	 * Sets the username/password pair for authenticating against the given service
	 *
	 * @param svc
	 * @param username
	 * @param pass
	 */
	public void setAuthentication( SemossService svc, String username, char[] pass );
}
