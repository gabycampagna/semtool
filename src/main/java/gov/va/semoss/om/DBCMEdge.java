/*******************************************************************************
 * Copyright 2013 SEMOSS.ORG
 * 
 * This file is part of SEMOSS.
 * 
 * SEMOSS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SEMOSS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SEMOSS.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package gov.va.semoss.om;

import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openrdf.model.Literal;

import gov.va.semoss.util.Constants;
import gov.va.semoss.util.Utility;

/**
 * 
 * @author pkapaleeswaran
 * Something that expresses the edge
 * @version $Revision: 1.0 $
 */
public class DBCMEdge {
	
	transient public DBCMVertex inVertex = null;
	transient public DBCMVertex outVertex = null;
	String uri = null;

	transient Hashtable uriHash = new Hashtable();
	public Hashtable <String, Object> propHash = new Hashtable();
	transient Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 	
	 * @param inVertex
	 * @param outVertex
	 * @param uri
	 *  Vertex1 (OutVertex) -------> Vertex2 (InVertex)
	 *  (OutEdge)					(InEdge) 
	 */
	public DBCMEdge(DBCMVertex outVertex, DBCMVertex inVertex, String uri)
	{
		this.uri = uri;
		putProperty(Constants.URI_KEY, uri);
		String className = Utility.getClassName(uri);
		String edgeName = Utility.getInstanceName(uri);
		putProperty(Constants.EDGE_TYPE, className);
		putProperty(Constants.EDGE_NAME, edgeName);
		this.inVertex = inVertex;
		this.outVertex = outVertex;
		inVertex.addInEdge(this);
		
		outVertex.addOutEdge(this);
		
	}
	
	/**
	 * Method getURI.
	
	 * @return String */
	public String getURI()
	{
		return uri;
	}

	/**
	 * Method getProperty.
	 * @param arg0 String
	
	 * @return Object */
	public Object getProperty(String arg0) {
		return propHash.get(arg0);
	}
	
	/**
	 * Method getProperty.
	
	 * @return Hashtable<String,Object> */
	public Hashtable <String,Object> getProperty()
	{
		return propHash;
	}

	/**
	 * Method getPropertyKeys.
	
	 * @return Set<String> */
	public Set<String> getPropertyKeys() {
		// TODO: Don't return null
		return null;
	}

	/**
	 * Method removeProperty.
	 * @param arg0 String
	
	 * @return Object */
	public Object removeProperty(String arg0) {
		return propHash.remove(arg0);
	}

	/**
	 * Method setProperty.
	 * @param propNameURI String
	 * @param propValue Object
	 */
	public void setProperty(String propNameURI, Object propValue) {
		// one is a p
		StringTokenizer tokens = new StringTokenizer(propNameURI + "", "/");
		int totalTok = tokens.countTokens();
		String className = Utility.getClassName(propNameURI);
		String instanceName = Utility.getInstanceName(propNameURI);

		for (int tokIndex = 0; tokIndex <= totalTok && tokens.hasMoreElements(); tokIndex++) {
			if (tokIndex + 2 == totalTok)
				className = tokens.nextToken();
			else if (tokIndex + 1 == totalTok)
				instanceName = tokens.nextToken();
			else
				tokens.nextToken();
		}
		uriHash.put(instanceName, propNameURI);
		// I need to convert these decimals and other BS into a proper value
		// awesome !!
		// will come to this in a bit
		// only 2 possibilities for us at this point
		// double value or a string
		// TODO incorporate the same for jena too
		
		boolean converted = false;
		try
		{
			if(propValue instanceof Literal)
			{
				//logger.info("This is a literal impl >>>>>> "  + ((Literal)propValue).doubleValue());
				propHash.put(instanceName, ((Literal)propValue).doubleValue());
				converted = true;
			}
		}catch(Exception ex)
		{
			logger.debug(ex);
		}
		try
		{
			if(propValue instanceof com.hp.hpl.jena.rdf.model.Literal)
			{
				logger.info("Class is " + propValue.getClass());
				String prop = (String) ((com.hp.hpl.jena.rdf.model.Literal)propValue).getValue();

				if(prop.contains("XMLSchema#double"))
				{
					String[] split = prop.split("\""); 
					Double val = Double.parseDouble(split[1]);
					propHash.put(instanceName, val);
					converted = true;
				}
				
				
			}
		}catch(Exception ex)
		{
			logger.debug(ex);
		}
		if(!converted)
			propHash.put(instanceName, propValue);
		logger.debug(uri + "<>" + instanceName + "<>" + propValue);
	}

	/**
	 * Method putProperty.
	 * @param propName String
	 * @param propValue String
	 */
	public void putProperty(String propName, String propValue)
	{
		propHash.put(propName, propValue);
	}


}