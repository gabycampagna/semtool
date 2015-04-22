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
package gov.va.semoss.ui.main.listener.impl;

import java.util.ArrayList;
import java.util.Hashtable;

import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectStatement;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectWrapper;

import com.google.gson.Gson;
import com.teamdev.jxbrowser.chromium.JSValue;

/**
 */
public class SPARQLExecuteFilterBaseFunction extends AbstractBrowserSPARQLFunction {

	Hashtable filterHash;
	static final String concept = "http://semoss.org/ontologies/Concept";
	static final String relation = "http://semoss.org/ontologies/Relation";
	static final String resource = "http://www.w3.org/2000/01/rdf-schema#Resource";
	
	
	// This class is just like SPARQLExecuteFunction but will filter so that all results are in base filter data
	// Only will return things that are in the filter hash but not concept or resource or what not
	
	/**
	 * Method invoke.
	 * @param arg0 Object[]
	
	 * @return Object */
	@Override
	public JSValue invoke(JSValue... arg0) {
		logger.info("Arguments are " + arg0);
		
		//get the query from the args
		String query = (arg0[0].getString()).trim(); 

		Hashtable retHash = process(query);

		Gson gson = new Gson();
        
		return JSValue.create(gson.toJson(retHash));
	}
	
	public Hashtable process(String query){
		Hashtable retHash = new Hashtable();
		ArrayList ret = new ArrayList();
		boolean success = true;
		
		//run the query against the set repository
		try {
			logger.info("Using repository " + engine);
			
			if(query.toUpperCase().startsWith("INSERT") || query.toUpperCase().startsWith("DELETE")){
				logger.info("Update query incorrectly passed to FilterBaseFunction");
			}

			else if(query.startsWith("SELECT") ){
				logger.info("running select : " + query);
				ret = processSelect(query, engine);
			}
				
			else { 
				logger.error("UNKNOWN QUERY TYPE SENT TO JAVA FOR PROCESSING");
			}
		} catch (Exception e) {
			logger.fatal(e);
			success = false;
		}

		Object[] retArray = ret.toArray();
		retHash.put("results", retArray);
		retHash.put("success", success);
		
		return retHash;
	}
	
	/**
	 * Method processSelect.
	 * @param query String
	 * @param selectedEngine IEngine
	
	 * @return ArrayList<Object[]> */
	private ArrayList<Object[]> processSelect(String query, IEngine selectedEngine){
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		
		//create the update wrapper, set the variables, and let it run
		SesameJenaSelectWrapper wrapper = new SesameJenaSelectWrapper();
		wrapper.setEngine(selectedEngine);
		wrapper.setQuery(query);
		wrapper.executeQuery();

		// get the bindings from it
		String [] names = wrapper.getVariables();
		int count = 0;
		// now get the bindings and generate the data
		while(wrapper.hasNext())
		{
			SesameJenaSelectStatement sjss = wrapper.next();
			// only will return things that are in the base data but not concept or resource or what not
			Object [] values = new Object[names.length];
			boolean addRow = true;
			for(int colIndex = 0;colIndex < names.length;colIndex++)
			{
				String var = sjss.getRawVar(names[colIndex])+"";
				//just check subject verb and object because obviously everything else will be contained in the filterhash
				if( !filterHash.containsKey(var) || this.concept.equals(var) || this.relation.equals(var))
				{
					addRow = false;
					break;
				}
				else
				{
					values[colIndex] = var;
				}
					
			}
			if(addRow)
			{
				list.add(count, values);
				count++;
			}
		}
		return list;
	}
	
	/**
	 * Method setFilterHash.
	 * @param filterHash Hashtable
	 */
	public void setFilterHash(Hashtable filterHash) {
		this.filterHash = filterHash;
	}

	
}