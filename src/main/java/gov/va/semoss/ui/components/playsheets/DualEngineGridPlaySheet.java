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
package gov.va.semoss.ui.components.playsheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JDesktopPane;
import org.apache.log4j.Logger;

import gov.va.semoss.rdf.engine.api.IEngine;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectStatement;
import gov.va.semoss.rdf.engine.impl.SesameJenaSelectWrapper;
import gov.va.semoss.util.DIHelper;
import static java.util.Collections.list;
import java.util.List;

/**
 * This class is a temporary fix for queries to run across multiple databases
 * The query passed through this class must have the format engine1&engine2&engine1query&engine2query
 * The two queries must have exactly one variable name in common--which is how this class will line up the table
 */
public class DualEngineGridPlaySheet extends GridPlaySheet {
  private static final Logger log = Logger.getLogger( DualEngineGridPlaySheet.class );
	String query1;
	String query2;
	String engineName1;
	String engineName2;
	IEngine engine1;
	IEngine engine2;
	Hashtable<Object, ArrayList<Object[]>> dataHash1 = new Hashtable();
	Hashtable<Object, ArrayList<Object[]>> dataHash2 = new Hashtable();
	int names1size;
	int names2size;
	List list;
	
	/**
	 * This is the function that is used to create the first view 
	 * of any play sheet.  It often uses a lot of the variables previously set on the play sheet, such as {@link #setQuery(String)},
	 * {@link #setJDesktopPane(JDesktopPane)}, {@link #setEngine(IEngine)}, and {@link #setTitle(String)} so that the play 
	 * sheet is displayed correctly when the view is first created.  It generally creates the model for visualization from 
	 * the specified engine, then creates the visualization, and finally displays it on the specified desktop pane
	 * 
	 * <p>This is the function called by the PlaysheetCreateRunner.  PlaysheetCreateRunner is the runner used whenever a play 
	 * sheet is to first be created, most notably in ProcessQueryListener.
	 */
	@Override
	public void createData() {
				
		List list = new ArrayList();
		
		//Process query 1
		SesameJenaSelectWrapper wrapper1 = new SesameJenaSelectWrapper();
		if(engine1!= null){
			wrapper1.setQuery(query1);
			//updateProgressBar("10%...Querying RDF Repository", 10);
			wrapper1.setEngine(engine1);
			//updateProgressBar("20%...Querying RDF Repository", 30);
			wrapper1.executeQuery();
			//updateProgressBar("30%...Processing RDF Statements	", 60);
		}
		// get the bindings from it
		String [] names1 = wrapper1.getVariables();
		names1size = names1.length;

		//process query 2
		SesameJenaSelectWrapper wrapper2 = new SesameJenaSelectWrapper();
		if(engine2!= null){
			wrapper2.setQuery(query2);
			//updateProgressBar("40%...Querying RDF Repository", 10);
			wrapper2.setEngine(engine2);
			//updateProgressBar("50%...Querying RDF Repository", 30);
			wrapper2.executeQuery();
			//updateProgressBar("60%...Processing RDF Statements	", 60);
		}
		// get the bindings from it
		String [] names2 = wrapper2.getVariables();
		names2size = names2.length;
		
		//find the common variable in the wrapper names (this will be the hashtable key)
        Set<String> set=new LinkedHashSet<>(Arrays.asList(names1));
        set.retainAll(Arrays.asList(names2));
		String commonVar = set.iterator().next();
		
		processWrapper(commonVar, wrapper1, dataHash1, names1);
		processWrapper(commonVar, wrapper2, dataHash2, names2);
		
		//updateProgressBar("60%...Preparing List", 80);
		
		prepareList(dataHash1, dataHash2);
		
		String[] totalNames = new String [names1.length+names2.length];
		for(int i = 0; i<totalNames.length; i++){
			if(i<names1.length) totalNames[i] = names1[i];
			else totalNames[i] = names2[i-names1.length];
		}			

		//progressComplete( "100%...Data Generation Complete" );
	}
	
	/**
	 * Method prepareList.  This method essentially combines the results of two separate query results.
	 * Iterates through hash1, gets the list associated with each key, then combines each array in the list with each array in the list of hash2.
	 * @param hash1 Hashtable<Object,ArrayList<Object[]>> - The results from processWrapper() on the first query
	 * @param hash2 Hashtable<Object,ArrayList<Object[]>> - The results from processWrapper() on the second query
	 */
	private void prepareList(Hashtable<Object, ArrayList<Object[]>> hash1, Hashtable<Object, ArrayList<Object[]>> hash2){

		Iterator<Object> hash1it = hash1.keySet().iterator();
		while (hash1it.hasNext()){
			Object key = hash1it.next();
			ArrayList<Object[]> hash1list = hash1.get(key);
			ArrayList<Object[]> hash2list = hash2.remove(key);
			for(Object[] hash1array : hash1list){
				if(hash2list == null){
					Object[] fullRow = new Object[names1size + names2size];
					//combine the two arrays into one row
					for(int i = 0; i<fullRow.length; i++){
						if(i<names1size) fullRow[i] = hash1array[i];
						else fullRow[i] = null;
					}
					// add to the list
					list.add(fullRow);
				}
				else{
					for(Object[] hash2array : hash2list){
						Object[] fullRow = new Object[names1size + names2size];
						
						//combine the two arrays into one row
						for(int i = 0; i<fullRow.length; i++){
							if(i<names1size) fullRow[i] = hash1array[i];
							else fullRow[i] = hash2array[i-names1size];
						}
						// add to the list
						list.add(fullRow);
					}
				}
			}
		}
		// now add any results that were returned from the second query but don't match with the first
		Iterator hash2it = hash2.keySet().iterator();
		while(hash2it.hasNext()){
			Object key = hash2it.next();
			ArrayList<Object[]> hash2list = hash2.get(key);
			for(Object[] hash2array : hash2list){
				Object[] fullRow = new Object[names1size + names2size];
				
				//combine the two arrays into one row
				for(int i = 0; i<fullRow.length; i++){
					if(i<names1size) fullRow[i] = null;
					else fullRow[i] = hash2array[i-names1size];
				}
				// add to the list
				list.add(fullRow);
			}
		}
		
	}
	
	/**
	 * Method processWrapper.  Processes the wrapper for the results of a query to a specific database, and adds the results to a Hashtable.
	 * @param commonVar String - the variable name that the two queries have in common.
	 * @param sjw SesameJenaSelectWrapper - the wrapper for the query
	 * @param hash Hashtable<Object,ArrayList<Object[]>> - The data structure where the data from the query will be stored.
	 * @param names String[] - An array consisting of all the variables from the query.
	 */
	private void processWrapper(String commonVar, SesameJenaSelectWrapper sjw, Hashtable<Object, ArrayList<Object[]>> hash, String[] names){
		// now get the bindings and generate the data
		try {
			while(sjw.hasNext())
			{
				SesameJenaSelectStatement sjss = sjw.next();
				
				Object [] values = new Object[names.length];
				Object commonVal = null;
				for(int colIndex = 0;colIndex < names.length;colIndex++)
				{
					values[colIndex] = sjss.getVar(names[colIndex]);
					if(names[colIndex].equals(commonVar)) commonVal = sjss.getVar(names[colIndex]);
					log.debug("Binding Name " + names[colIndex]);
					log.debug("Binding Value " + values[colIndex]);
				}
				log.debug("Creating new Value " + values);
				ArrayList<Object[]> overallArray = new ArrayList<Object[]>();
				if(hash.containsKey(commonVal))
					overallArray = hash.get(commonVal);
				
				overallArray.add(values);
				hash.put(commonVal, overallArray);
			}
		} catch (Exception e) {
			log.fatal(e);
		}
	}

	/**
	 * Sets the String version of the SPARQL query on the play sheet. <p> The query must be set before creating the model for
	 * visualization.  Thus, this function is called before createView(), extendView(), overlayView()--everything that 
	 * requires the play sheet to pull data through a SPARQL query.
	 * @param query the full SPARQL query to be set on the play sheet
	 * @see	#createView()
	 * @see #extendView()
	 * @see #overlayView()
	 */
	@Override
	public void setQuery(String query) {

		StringTokenizer queryTokens = new StringTokenizer(query, "&");
		for (int queryIdx = 0; queryTokens.hasMoreTokens(); queryIdx++){
			String token = queryTokens.nextToken();
			if (queryIdx == 0){
				this.engineName1 = token;
				this.engine1 = (IEngine) DIHelper.getInstance().getLocalProp(engineName1);
			}
			else if (queryIdx == 1){
				this.engineName2 = token;
				this.engine2 = (IEngine) DIHelper.getInstance().getLocalProp(engineName2);
			}
			else if (queryIdx == 2)
				this.query1 = token;
			else if (queryIdx == 3)
				this.query2 = token;
		}
	}
}