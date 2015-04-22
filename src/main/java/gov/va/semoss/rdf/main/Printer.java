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
package gov.va.semoss.rdf.main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.ByteArrayOutputStream;
import org.apache.log4j.Logger;

/**
 */
public class Printer {
  private static final Logger logger = Logger.getLogger( Printer.class );
	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String [] args) throws Exception
	{
		String q2 = "SELECT ?Subject ?Predicate ?Object WHERE " +
				"{" +
				"{?Predicate <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation>;} " +
				"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
				"{?Object  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
				"{?Subject ?Predicate ?Object}" +
				"} BINDINGS ?Subject {(<http://semoss.org/ontologies/Concept/Capability/Anesthesia_Documentation>)} " +
				""; // working query

		String conceptHierarchyForSubject = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE " +
		"{" +
		"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
		//"{?Object <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://semoss.org/ontologies/Concept>;}"+
		"{?Subject ?Predicate ?Object}" + 
		"} BINDINGS ?Subject {(<http://semoss.org/ontologies/Relation/Fulfill>)} " +
		"";// working one


		String relationHierarchy = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE " +
		"{" +
		"{?Subject ?Predicate ?Object}" + 
		"} BINDINGS ?Subject {(<http://semoss.org/ontologies/Relation/Fulfill>)} " +
		"";// relation hierarcy

		String predicateSelectQuery = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE {" +
				//"BIND (<http://semoss.org/ontologies/Concept> AS ?Subject)" +
		  "{?Predicate " +"<http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation>;}" +
		  "{?Subject " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
		  //"{?Object " + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
		  //"{?Object " + "<http://semoss.org/ontologies/Relation>  " +  " <http://semoss.org/ontologies/Concept>;}" +
		  //"{?Subject ?Predicate <http://semoss.org/ontologies/Concept>}" +
		  //"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
		  "{?Subject ?Predicate ?Object}"+
		  "}";
		//String propertyHierarchy = 
		
		String q4 = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE " +
		"{" +
		"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
		//"{?Object  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://semoss.org/ontologies/Concept>;}" +
		//"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://semoss.org/ontologies/Concept>}" +
		"{?Subject ?Predicate ?Object}" +
		"} BINDINGS ?Subject {(<http://semoss.org/ontologies/Concept/Capability/Anesthesia_Documentation>)} " +
		"?Predicate {(<http://semoss.org/ontologies/Relation/Fulfill>)}" + 
		"";
		
		String findPropertyRelationQuery = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE " +
		"{" +
		"{?Subject ?Predicate ?Object}" + 
		"{?Object <http://www.w3.org/2000/01/rdf-schema#subPropertyOf> <http://semoss.org/ontologies/Relation/Contains>}" +
		//"} BINDINGS ?Subject { " + predicates + " } " +
		"}";// relation hierarcy


		String q = findPropertyRelationQuery;
		
		
		// +"{?Subject ?Predicate ?Object} } BINDINGS ?Predicate {(<http://semoss.org/ontologies/Relation/Fulfill>)}";
		
		logger.debug("Query: "+q);
		Printer printer = new Printer();
		//printer.tryRDF();
		printer.tryRDFSesame();
		printer.tryPropTest();
	}
	
	/**
	 * Method tryPropTest.
	 */
	private void tryPropTest() throws Exception{
		Properties prop = new Properties();
		prop.load(new FileInputStream("Sample.prop"));
		prop.put("Doh", "hello");
		prop.store(new FileOutputStream("Sample.prop"), "Hello");
		
	}

	/**
	 * Method tryRDF.
	 */
	public void tryRDF()
	{
		logger.debug("Jena Start" + System.currentTimeMillis());

			//jenaModel = ModelFactory.createDefaultModel(ReificationStyle.Standard);
			Model baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			//ReasonerRegistry.
			Model jenaModel = ModelFactory.createInfModel(ReasonerRegistry.getTransitiveReasoner(),baseModel);
			
			com.hp.hpl.jena.rdf.model.Resource beijing = baseModel.createResource("http://example.org/things/Beijing");
			com.hp.hpl.jena.rdf.model.Resource city = baseModel.createResource("http://example.org/terms/city");
			com.hp.hpl.jena.rdf.model.Resource place = baseModel.createResource("http://example.org/terms/place");
			
			com.hp.hpl.jena.rdf.model.Resource doctor123 = baseModel.createResource("http://example.org/Doctor/123");
			com.hp.hpl.jena.rdf.model.Resource doctor = baseModel.createResource("http://example.org/terms/Doctor");
			com.hp.hpl.jena.rdf.model.Resource concept = baseModel.createResource("http://example.org/terms/Concept");

			
			Resource beijing2 = baseModel.createResource("http://example.org/things/Beijing");

			// Sail reasoner = new ForwardChainingRDFSInferencer(new GraphSail(new TinkerGraph()));
			// reasoner.initialize();

			try {
				// SailConnection c = reasoner.getConnection();
					Statement stmt = jenaModel.createStatement(city, RDFS.subClassOf, place);
					Statement stmt2 = jenaModel.createStatement(beijing, RDF.type, city);
					jenaModel.add(stmt);
					jenaModel.add(stmt2);
					/*stmt2 = jenaModel.createStatement(doctor123, RDF.type, doctor);
					stmt = jenaModel.createStatement(doctor, RDFS.subClassOf, concept);
					jenaModel.add(stmt);
					jenaModel.add(stmt2);
					*/
					
					//jenaModel.commit();

					String query3 = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE {" +
							//"{?Subject ?Predicate ?Object}" +
							//"{?Subject <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?Object}" +
							//"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
							"{?Subject ?Predicate <http://example.org/terms/Concept>}" +
							"}";

					String query2 = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE {" +
					"VALUES ?Object {<http://example.org/terms/city>}" +
					"VALUES ?Subject {<http://example.org/terms/place> }" +
										//"{?Subject ?Predicate ?Object}" +
					//"{?Subject <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?Object}" +
					//"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
					"{?Subject ?Predicate ?Object.}" +
					"}";
					
					String query = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE {" +
					//"{?Subject ?Predicate ?Object}" +
					//"{?Subject <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?Object}" +
					//"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
					"{?Subject ?Predicate <http://example.org/terms/place>}" +
					"{?Subject ?Predicate ?Object}" +
					"}";


					logger.debug("Query is " + query);
					
					com.hp.hpl.jena.query.Query q2 = QueryFactory.create(query); 
					QueryExecution qex = QueryExecutionFactory.create(q2, jenaModel);
					ResultSet rs = qex.execSelect();
					
					String [] var = getVariables(rs);
					FileOutputStream fox = new FileOutputStream("output.txt");
				  ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ResultSetFormatter.out(baos, rs); 
          logger.debug( baos.toString());
				    //ResultSetFormatter.out(fox, rs); 
					
					/*while(rs.hasNext())
					{
					    QuerySolution row = rs.nextSolution();
					    logger.debug(row.get(var[0])+"");
					    logger.debug(row.get(var[1])+"");
					    logger.debug(row.get(var[2]));

					}*/
			}catch (Exception ex)
			{
				logger.error( ex );
			}
			logger.debug("Jena End" + System.currentTimeMillis());

	}
	
	/**
	 * Method getVariables.
	 * @param rs ResultSet
	
	 * @return String[] */
	public String[] getVariables(ResultSet rs)
	{
		String [] var = new String[rs.getResultVars().size()];
				var = new String[rs.getResultVars().size()];
				List <String> names = rs.getResultVars();
				for(int colIndex = 0;colIndex < names.size();var[colIndex] = names.get(colIndex), colIndex++);
		return var;
	}
	
	/**
	 * Method tryRDFSesame.
	 */
	public void tryRDFSesame() throws Exception
	{
		logger.debug("Sesame Start" + System.currentTimeMillis());
		Repository myRepository = new SailRepository(
                new ForwardChainingRDFSInferencer(
                new MemoryStore()));
		myRepository.initialize();
		
		
		
		RepositoryConnection rc = myRepository.getConnection();
			
		org.openrdf.model.Resource beijing = new URIImpl("http://example.org/things/Beijing");
		org.openrdf.model.Resource city = new URIImpl("http://example.org/terms/city");
		org.openrdf.model.Resource place = new URIImpl("http://example.org/terms/place");

		org.openrdf.model.Resource beijing2 = new URIImpl("http://example.org/things/Beijing");

		// Sail reasoner = new ForwardChainingRDFSInferencer(new GraphSail(new TinkerGraph()));
		// reasoner.initialize();

		try {
			// SailConnection c = reasoner.getConnection();
				rc.add(city, org.openrdf.model.vocabulary.RDFS.SUBCLASSOF, place);
				rc.add(beijing, org.openrdf.model.vocabulary.RDF.TYPE, city);

				//InferenceEngine ie = bdSail.getInferenceEngine();
				//ie.computeClosure(null);
				//rc.commit();
				
				String query = "SELECT DISTINCT ?Subject ?Predicate ?Object WHERE {" +
				//"{?Subject ?Predicate ?Object}" +
				//"{?Subject <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?Object}" +
				//"{?Subject <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object}" +
				"{?Subject ?Predicate <http://example.org/terms/city>;}" +
				"{?Subject ?Predicate ?Object}" +
				"}";
				
				String deleteQuery2 = "DELETE WHERE {?Subject <" +  RDF.type + "> <http://example.org/terms/city>}";

				String deleteQuery3 = "DELETE  {?Subject ?Predicate ?Object} WHERE {" +
						"{<http://example.org/things/city> <" +  org.openrdf.model.vocabulary.RDFS.SUBCLASSOF + "> <http://example.org/terms/place>;}" +
						"{<http://example.org/things/Beijing> ?Predicate <http://example.org/terms/place>;}" +
						"{?Subject ?Predicate ?Object}" +
						"}";

				String deleteQuery4 = "DELETE  {?Subject ?Predicate ?Object} WHERE {" +
				"BIND (<http://example.org/things/Beijing> AS ?Subject)" +
				"{?Subject ?Predicate ?Object}" +
				"}";

				String deleteQuery5 = "DELETE  {?Subject ?Predicate ?Object} WHERE {" +
				"{?Subject ?Predicate ?Object}" +
				"}BINDINGS ?Subject {<http://example.org/things/Beijing> }" ;


				String deleteQuery = "DELETE  WHERE {" +
						//"BIND (<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> AS ?Predicate)" +
						//"{<http://example.org/things/Beijing> " + "<" +  org.openrdf.model.vocabulary.RDFS.SUBCLASSOF + "> <http://example.org/terms/city>;} " +
						"<http://example.org/things/Beijing>" + "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?Object; " +
				//"<http://example.org/things/Beijing>" + "<http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://example.org/terms/city>; " +
								//"{?Subject ?Predicate ?Object;}" +
						"}";

				logger.debug("Deleting query " + deleteQuery);
				
				TupleQueryResult tqr = execSelectQuery(query,rc);
				while(tqr.hasNext())
				{
					BindingSet bs = tqr.next();
					logger.debug(bs.getBinding("Subject") + "<>" + bs.getBinding("Predicate")
              + "<>" + bs.getBinding("Object"));
				}
				
				logger.debug("Deleting now " + deleteQuery + System.currentTimeMillis());
				Update update = rc.prepareUpdate(QueryLanguage.SPARQL, deleteQuery);
				
				update.execute();
				//rc.commit();
				logger.debug("Deleting now " + deleteQuery + System.currentTimeMillis());

				tqr = execSelectQuery(query,rc);
				while(tqr.hasNext())
				{
					BindingSet bs = tqr.next();
					logger.debug(bs.getBinding("Subject") + "<>" + bs.getBinding("Predicate")
              + "<>" + bs.getBinding("Object"));
				}

				// re-adding... 
				rc.add(beijing, org.openrdf.model.vocabulary.RDF.TYPE, city);
				
				rc.export(new RDFXMLPrettyWriter(new FileWriter("Sample.rdf")));
			
				tqr = execSelectQuery(query,rc);
				while(tqr.hasNext())
				{
					BindingSet bs = tqr.next();
					logger.debug(bs.getBinding("Subject") + "<>" + bs.getBinding("Predicate") 
              + "<>" + bs.getBinding("Object"));
				}				
				logger.debug("Sesame end" + System.currentTimeMillis());
				
		}catch(Exception ex)
		{
			// nothings
			logger.error( ex );
		}

		
	}
	
	/**
	 * Method execSelectQuery.
	 * @param query String
	 * @param rc RepositoryConnection
	
	 * @return TupleQueryResult */
	public TupleQueryResult execSelectQuery(String query, RepositoryConnection rc) {
		
		TupleQueryResult sparqlResults = null;
		
		try {
			TupleQuery tq = rc.prepareTupleQuery(QueryLanguage.SPARQL, query);
			logger.debug("SPARQL: " + query);
			tq.setIncludeInferred(true /* includeInferred */);
			sparqlResults = tq.evaluate();
		} catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
			logger.error( e );
		}
		return sparqlResults;
	}

	
	/**
	 * Method execGraphQuery.
	 * @param query String
	 * @param rc RepositoryConnection
	
	 * @return GraphQueryResult */
	public GraphQueryResult execGraphQuery(String query, RepositoryConnection rc) {
		 GraphQueryResult res = null;
		try {
			GraphQuery sagq = rc.prepareGraphQuery(QueryLanguage.SPARQL,
						query);
				res = sagq.evaluate();
		} catch (RepositoryException e) {
			logger.error( e );
		} catch (MalformedQueryException e) {
			logger.error( e );
		} catch (QueryEvaluationException e) {
			logger.error( e );
		}
		return res;	
	}

	
}