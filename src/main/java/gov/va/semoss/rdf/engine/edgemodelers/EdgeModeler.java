/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.semoss.rdf.engine.edgemodelers;

import gov.va.semoss.poi.main.ImportData;
import gov.va.semoss.poi.main.ImportMetadata;
import gov.va.semoss.poi.main.LoadingSheetData;
import gov.va.semoss.poi.main.LoadingSheetData.LoadingNodeAndPropertyValues;
import gov.va.semoss.rdf.engine.util.QaChecker;
import java.util.Map;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author ryan
 */
public interface EdgeModeler {

	/**
	 * Adds a new relationship node to the repository
	 *
	 * @param nap
	 * @param namespaces
	 * @param sheet
	 * @param metas Metadata to use when loading. This argument MUST pass the
	 * {@link AbstractEdgeModeler#isValidMetadata(gov.va.semoss.poi.main.ImportMetadata) }
	 * check
	 * @param rc
	 * @return the newly-created relationship node
	 * @throws RepositoryException
	 */
	public URI addRel( LoadingNodeAndPropertyValues nap,
			Map<String, String> namespaces, LoadingSheetData sheet,
			ImportMetadata metas, RepositoryConnection rc ) throws RepositoryException;

	/**
	 * Adds a new node to the repository
	 *
	 * @param nap
	 * @param namespaces
	 * @param sheet
	 * @param metas Metadata to use when loading. This argument MUST pass the
	 * {@link AbstractEdgeModeler#isValidMetadata(gov.va.semoss.poi.main.ImportMetadata) }
	 * check
	 * @param rc
	 * @return the newly-create node
	 * @throws RepositoryException
	 */
	public URI addNode( LoadingNodeAndPropertyValues nap,
			Map<String, String> namespaces, LoadingSheetData sheet,
			ImportMetadata metas, RepositoryConnection rc ) throws RepositoryException;

	/**
	 * Create statements for all of the properties of the instanceURI
	 *
	 * @param subject URI containing the subject instance URI
	 * @param properties Map<String, Object> that contains all properties
	 * @param namespaces
	 * @param sheet
	 * @param metas Metadata to use when loading. This argument MUST pass the
	 * {@link AbstractEdgeModeler#isValidMetadata(gov.va.semoss.poi.main.ImportMetadata) }
	 * check
	 * @param rc
	 *
	 * @throws RepositoryException
	 */
	public void addProperties( URI subject, Map<String, Value> properties,
			Map<String, String> namespaces, LoadingSheetData sheet,
			ImportMetadata metas, RepositoryConnection rc ) throws RepositoryException;

	public void setQaChecker( QaChecker qaer );

	/**
	 * Creates the metamodel for the given data using the given namespaces
	 *
	 * @param alldata the data that contains the model nodes. This
	 * {@link ImportData#getMetadata()} must pass the 
	 * {@link AbstractEdgeModeler#isValidMetadata(gov.va.semoss.poi.main.ImportMetadata) }
	 * check
	 * @param namespaces the namespaces to use for resolving metamodel elements
	 * @param rc where to put the
	 * @throws RepositoryException
	 */
	public void createMetamodel( ImportData alldata, Map<String, String> namespaces,
			RepositoryConnection rc ) throws RepositoryException;
}
