package com.ostrichemulators.semtool.rdf.main;

/**
 * RPB: 2015-08-08
 * A class to interface with RDBMSes. We don't use it, but I thought it might be
 * good to jog our memory when we tackle RDBMSes again later.
 * @author ryan
 */
public class ImportRDBMSProcessor {
//
//	private static final Logger logger = Logger.getLogger( ImportRDBMSProcessor.class );
//	
//	private StringBuilder tableMapping = new StringBuilder();
//	private StringBuilder propertyTypeMapping = new StringBuilder();
//	private StringBuilder relationshipTypeMapping = new StringBuilder();
//	private StringBuilder relationshipMapping = new StringBuilder();
//	private String dbConnection = new String();
//	
//	// public PropFileWriter propWriter;
//	
//	private String customBaseURI = "";
//	private String baseRelURI = "";
//	private final String semossURI = "http://semoss.org/ontologies" + "/" + Constants.DEFAULT_NODE_CLASS + "/";
//	private final String semossRelURI = "http://semoss.org/ontologies" + "/" + Constants.DEFAULT_RELATION_CLASS + "/";
//	private final String propURI = "http://semoss.org/ontologies/" + Constants.DEFAULT_RELATION_CLASS + "/Contains/";
//	private String filePath = "";
//	private String dbName = "";
//	private String type = "";
//	private String url = "";
//	private String username = "";
//	private char[] password;
//	
//	private String owlPath = "";
//	
//	private final static String spacer = " \n\t";
//	
//	private Set<String> propertyList = new HashSet<>();
//	private Set<String> relationshipList = new HashSet<>();
//	private Set<String> baseConcepts = new HashSet<>();
//	private Set<String> baseRels = new HashSet<>();
//	private Map<String, List<String>> baseRelationships = new HashMap<>();
//	
//	private final String MYSQL = "MySQL";
//	private final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
//	private final String ORACLE = "Oracle";
//	private final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
//	private final String SQLSERVER = "MS SQL Server";
//	private final String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//	private final String ASTER = "Aster Database";
//	private final String ASTER_DRIVER = "com.asterdata.ncluster.jdbc.core.NClusterJDBCDriver";
//	
//	public ImportRDBMSProcessor() {
//		
//	}
//	
//	public ImportRDBMSProcessor( String customBaseURI, Collection<File> files,
//			String repoName, String type, String url, String username, char[] password ) {
//		this.customBaseURI = customBaseURI + "/" + Constants.DEFAULT_NODE_CLASS + "/";
//		this.baseRelURI = customBaseURI + "/" + Constants.DEFAULT_RELATION_CLASS + "/";		
//		this.dbName = repoName;
//		this.type = type;
//		this.url = url;
//		this.username = username;
//		this.password = password;
//		
//		StringBuilder sb = new StringBuilder();
//		for ( File f : files ) {
//			if ( 0 == sb.length() ) {
//				sb.append( ";" );
//			}
//			sb.append( f.getAbsoluteFile() );
//		}
//		this.filePath = sb.toString();
//	}
//	
//	public File setUpRDBMS() {
//		processExcel( this.filePath );
//
//		//Change path for where the template file is
//		String templatePath = DIHelper.getInstance().getProperty( Constants.BASE_FOLDER )
//				+ File.separator + "rdbms" + File.separator + "MappingTemplate.ttl";
//		String requiredMapping = readRequiredMappings( templatePath );
//
//		// Write the file
//		String outputDir = "db" + File.separator + this.dbName;
//		File mappingFileDir = new File( outputDir );
//		try {
//			if ( !mappingFileDir.getCanonicalFile().isDirectory() ) {
//				if ( !mappingFileDir.mkdirs() ) {
//					return null;
//				}
//			}
//		}
//		catch ( IOException e ) {
//			logger.error( e );
//			return null;
//		}
//		
//		File mappingFile = new File( outputDir + File.separator + this.dbName + "_Mapping.ttl" );
//		try ( FileWriter writer = new FileWriter( mappingFile.getAbsolutePath() ) ) {
//			writer.write( "@prefix map: <#> . \n" );
//			writer.write( "@prefix d2rq: <http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#> . \n" );
//			writer.write( "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n" );
//			writer.write( "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n" );
//			writer.write( "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . \n" );
//			writer.write( "@prefix jdbc: <http://d2rq.org/terms/jdbc/> . \n" );
//			writer.write( "\n" );
//			writer.write( createDatabase( this.url, this.username, new String( this.password ) ) );
//			writer.write( tableMapping.toString() + "\n"
//					+ propertyTypeMapping.toString() + "\n"
//					+ relationshipMapping.toString() + "\n"
//					+ relationshipTypeMapping.toString() + "\n"
//					+ requiredMapping );
//		}
//		catch ( IOException e ) {
//			GuiUtility.showError( "Could not create mapping file!" );
//			logger.error( e );
//		}
//		
//		this.owlPath = outputDir + File.separator + this.dbName + "_OWL.OWL";
//		
//		
//		logger.error( "this function has been refactored, and probably doesn't work" );
//		Map<String, File> files = new HashMap<>();
//		//Map<String, File> files = writeSMSS(outputDir);
//		return ( files.containsKey( Constants.PROPS ) ? files.get( Constants.PROPS )
//				: null );
//	}
//	
//	private void processExcel( String wb ) {
//		String[] files = wb.split( ";" );
//		for ( String file : files ) {
//			XSSFWorkbook workbook = null;
//			try {
//				workbook = new XSSFWorkbook( new FileInputStream( file ) );
//			}
//			catch ( Exception e ) {
//				logger.error( e );
//				GuiUtility.showError( "Couldn't Find Workbook" );
//			}
//
//			// process properties
//			XSSFSheet propSheet = workbook.getSheet( "Nodes" );
//
//			// check rows in correct order
//			XSSFRow headerPropRow = propSheet.getRow( 0 );
//			if ( !headerPropRow.getCell( 0 ).toString().equals( "Table" ) && !headerPropRow.getCell( 0 ).toString().equals( "Subject" ) && !headerPropRow.getCell( 0 ).toString().equals( "Property" ) && !headerPropRow.getCell( 0 ).toString().equals( "DataType" ) ) {
//				logger.error( "Headers are incorrect in property sheet! \nPlease correct your workbook format" );
//			}
//			
//			String tableInput = "";
//			String tableInstanceColumn = "";
//			String propertyName = "";
//			String dataType = "";
//			String nodeType = "";
//			
//			int propRows = propSheet.getLastRowNum();
//			for ( int i = 1; i <= propRows; i++ ) {
//				XSSFRow dataRow = propSheet.getRow( i );
//				tableInput = dataRow.getCell( 0 ).toString();
//				
//				if ( tableInput.isEmpty() ) {
//					continue;
//				}
//				
//				tableInstanceColumn = dataRow.getCell( 1 ).toString();
//				
//				if ( dataRow.getCell( 2 ) != null ) {
//					propertyName = dataRow.getCell( 2 ).toString();
//					dataType = dataRow.getCell( 3 ).toString();
//					
//					if ( dataType.equalsIgnoreCase( "int" ) || dataType.equalsIgnoreCase( "Integer" ) ) {
//						dataType = "integer";
//					}
//					else if ( dataType.equalsIgnoreCase( "varchar" ) || dataType.equalsIgnoreCase( "String" ) ) {
//						dataType = "string";
//					}
//					else if ( dataType.equalsIgnoreCase( "DateTime" ) || dataType.equalsIgnoreCase( "Date" ) ) {
//						dataType = "dateTime";
//					}
//					else if ( dataType.equalsIgnoreCase( "Double" ) || dataType.equalsIgnoreCase( "Decimal" ) ) {
//						dataType = "double";
//					}
//					else if ( dataType.equalsIgnoreCase( "Float" ) ) {
//						dataType = "float";
//					}
//					else {
//						dataType = "string";
//					}
//				}
//				
//				nodeType = dataRow.getCell( 4 ).toString();
//				
//				if ( !baseConcepts.contains( nodeType ) ) {
//					baseConcepts.add( nodeType );
//					processTable( tableInput, tableInstanceColumn, nodeType );
//				}
//				
//				if ( propertyName != null && !propertyName.equals( "" ) && dataType != null && !dataType.equals( "" ) ) {
//					processTableProperty( tableInput, propertyName, dataType );					
//				}
//			}
//			
//			processProperties( propertyList );
//
//			// process relationships
//			XSSFSheet relSheet = workbook.getSheet( "Relationships" );
//
//			//TODO: add check that columns are in correct order
//			String relTable = "";
//			String relSubjectColumn = "";
//			String relObjectColumn = "";
//			String subjectTable = "";
//			String objectTable = "";
//			String relation = "";
//			String subjectInstance = "";
//			String objectInstance = "";
//			String subjectID = "";
//			String objectID = "";
//			String subjectNodeType = "";
//			String objectNodeType = "";
//			
//			int relRows = relSheet.getLastRowNum();
//			for ( int i = 1; i <= relRows; i++ ) {
//				XSSFRow dataRow = relSheet.getRow( i );
//				relTable = dataRow.getCell( 0 ).toString();
//				relSubjectColumn = dataRow.getCell( 1 ).toString();
//				relObjectColumn = dataRow.getCell( 2 ).toString();
//				subjectTable = dataRow.getCell( 3 ).toString();
//				subjectInstance = dataRow.getCell( 4 ).toString();
//				subjectID = dataRow.getCell( 5 ).toString();
//				
//				objectTable = dataRow.getCell( 6 ).toString();
//				objectInstance = dataRow.getCell( 7 ).toString();
//				objectID = dataRow.getCell( 8 ).toString();
//				
//				relation = dataRow.getCell( 9 ).toString();
//				subjectNodeType = dataRow.getCell( 10 ).toString();
//				objectNodeType = dataRow.getCell( 11 ).toString();
//				baseConcepts.add( subjectNodeType );
//				baseConcepts.add( objectNodeType );
//				baseRels.add( relation );
//				ArrayList<String> baseRel = new ArrayList<String>();
//				baseRel.add( subjectNodeType );
//				baseRel.add( relation );
//				baseRel.add( objectNodeType );
//				baseRelationships.put( String.valueOf( i ), baseRel );
//				
//				processRelationships( relTable, relSubjectColumn, relObjectColumn, subjectTable, objectTable, relation,
//						subjectInstance, objectInstance, subjectID, objectID, subjectNodeType, objectNodeType );
//			}
//			
//			processRelationshipType( relationshipList );
//		}
//	}
//	
//	private String createDatabase( String url, String username, String password ) {
//		//Account for the single backslash in a SQL Server URL and escape it when writing D2RQ mapping
//		if ( this.type.equalsIgnoreCase( this.SQLSERVER ) ) {
//			url = url.replace( "\\", "\\\\" );
//		}
//		
//		String dbConnection = "map:database a d2rq:Database;" + spacer
//				+ "d2rq:jdbcDSN \"" + url + "\";" + spacer
//				+ "d2rq:jdbcDriver \"";
//		
//		if ( this.type.equalsIgnoreCase( this.MYSQL ) ) {
//			dbConnection += this.MYSQL_DRIVER;
//		}
//		else if ( this.type.equalsIgnoreCase( this.ORACLE ) ) {
//			dbConnection += this.ORACLE_DRIVER;
//		}
//		else if ( this.type.equalsIgnoreCase( this.SQLSERVER ) ) {
//			dbConnection += this.SQLSERVER_DRIVER;
//		}
//		else if ( this.type.equalsIgnoreCase( this.ASTER ) ) {
//			dbConnection += this.ASTER_DRIVER;
//		}
//		
//		dbConnection += "\";" + spacer
//				+ "d2rq:username \"" + username + "\";" + spacer
//				+ "d2rq:password \"" + password + "\";" + spacer
//				+ "jdbc:keepAlive \"3600\";" + spacer
//				+ ".\n";
//		
//		return dbConnection;
//	}
//	
//	private void processTable( String tableName, String tableInstance, String nodeType ) {
//		tableMapping.append( "#####Table " ).append( tableName ).append( "\n" ).append(
//				"#Create the instanceNode typeOf baseNode triple \n" ).append(
//						"map:Instance" ).append( tableName ).append( "_TypeOf_Base" ).append( tableName ).append( " a d2rq:ClassMap;" ).append( spacer ).append(
//						"d2rq:dataStorage map:database;" ).append( spacer ).append(
//						"d2rq:uriPattern \"" ).append( customBaseURI ).append( nodeType ).append( "/@@" ).append( tableName ).append( "." ).append( tableInstance ).append( "@@\";" ).append( spacer ).append(
//						"d2rq:class " ).append( "<" ).append( semossURI ).append( nodeType ).append( ">;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:TypeOf_Concept;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:SubClassOf_Resource; " ).append( spacer ).append(
//						".\n" ).append(
//						"#Create the baseNode subclassOf Concept triple \n" ).append(
//						"map:Base" ).append( tableName ).append( "_SubClassOf_Concept a d2rq:ClassMap;" ).append( spacer ).append(
//						"d2rq:dataStorage map:database;" ).append( spacer ).append(
//						"d2rq:constantValue <" ).append( semossURI ).append( nodeType ).append( ">;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:SubClassOf_Concept;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:SubClassOf_Resource; " ).append( spacer ).append(
//						".\n" ).append(
//						"#####Property Label for Table " ).append( tableName ).append( "\n" ).append(
//						"#Create the rdfs:label for the concept" ).append( tableName ).append( spacer ).append(
//						"map:Instance" ).append( tableName ).append( "_Label a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:Instance" ).append( tableName ).append( "_TypeOf_Base" ).append( tableName ).append( ";" ).append( spacer ).append(
//						"d2rq:property rdfs:label;" ).append( spacer ).append(
//						"d2rq:column \"" ).append( tableName ).append( "." ).append( tableInstance ).append( "\";" ).append( spacer ).append(
//						".\n" );
//	}
//	
//	private void processTableProperty( String tableName, String propertyName, String dataType ) {
//		//add property to total list of unique properties
//		propertyList.add( propertyName );
//		tableMapping.append( "#####Property " ).append( propertyName ).append( " for Table " ).append( tableName ).append( "\n" ).append(
//				"#Create the instanceNode contains/prop propValue triple \n" ).append(
//						"map:Instance" ).append( tableName ).append( "_BaseProp_" ).append( propertyName ).append( " a d2rq:PropertyBridge; \n" ).append(
//						"d2rq:belongsToClassMap map:Instance" ).append( tableName ).append( "_TypeOf_Base" ).append( tableName ).append( ";" ).append( spacer ).append(
//						"d2rq:property " ).append( "<" ).append( propURI ).append( propertyName ).append( ">;" ).append( spacer ).append(
//						"d2rq:column " ).append( "\"" ).append( tableName ).append( "." ).append( propertyName ).append( "\";" ).append( spacer ).append(
//						"d2rq:datatype xsd:" ).append( dataType ).append( ";" ).append( spacer ).append(
//						".\n" );
//	}
//	
//	private void processProperties( Set<String> propertyList ) {
//		Iterator<String> propIterator = propertyList.iterator();
//		while ( propIterator.hasNext() ) {
//			String propertyName = propIterator.next();
//			propertyTypeMapping.append( "#####Property " ).append( propertyName ).append( "\n" ).append(
//					"#Create the Necessary Definitions for the property " ).append( propertyName ).append( "\n" ).append(
//							"map:Base" ).append( propertyName ).append( " a d2rq:ClassMap;" ).append( spacer ).append(
//							"d2rq:dataStorage map:database;" ).append( spacer ).append(
//							"d2rq:constantValue <" ).append( propURI ).append( propertyName ).append( ">;" ).append( spacer ).append(
//							"d2rq:additionalProperty map:TypeOf_Property;" ).append( spacer ).append(
//							"d2rq:additionalProperty map:TypeOf_Contains;" ).append( spacer ).append(
//							"d2rq:additionalProperty map:Base" ).append( propertyName ).append( "_SubPropertyOf_Base" ).append( propertyName ).append( ";" ).append( spacer ).append(
//							".\n" ).append(
//							"map:Base" ).append( propertyName ).append( "_SubPropertyOf_Base" ).append( propertyName ).append( " a d2rq:AdditionalProperty;" ).append( spacer ).append(
//							"d2rq:propertyName rdfs:subPropertyOf;" ).append( spacer ).append(
//							"d2rq:propertyValue <" ).append( propURI ).append( propertyName ).append( ">;" ).append( spacer ).append(
//							".\n" );
//		}
//	}
//	
//	private void processRelationships( String relTable, String relSubjectColumn, String relObjectColumn, String subjectTable,
//			String objectTable, String relation, String subjectInstance, String objectInstance, String subjectID, String objectID,
//			String subjectNodeType, String objectNodeType ) {
//		//add relationship to total list of unique relationships
//		relationshipList.add( relation );
//		relationshipMapping.append( "#####Defining Relationship: " ).append( subjectTable ).append( " " ).append( relation ).append( " " ).append( objectTable ).append( "\n" ).append(
//				"#Create the instance " ).append( subjectTable ).append( " " ).append( relation ).append( " " ).append( objectTable ).append( "\n" ).append(
//						"map:Instance" ).append( subjectTable ).append( "_InstanceRel_Instance" ).append( objectTable ).append( " a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:Instance" ).append( subjectTable ).append( "_TypeOf_Base" ).append( subjectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:refersToClassMap map:Instance" ).append( objectTable ).append( "_TypeOf_Base" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:dynamicProperty \"" ).append( baseRelURI ).append( relation ).append( "/@@" ).append( subjectTable ).append( "." ).append( subjectInstance ).append( "@@:@@" ).append( objectTable ).append( "." ).append( objectInstance ).append( "@@\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relSubjectColumn ).append( " = " ).append( subjectTable ).append( "." ).append( subjectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relObjectColumn ).append( " = " ).append( objectTable ).append( "." ).append( objectID ).append( "\";" ).append( spacer ).append(
//						".\n" ).append(
//						"#Create the higher level triples for the relationship \n" ).append(
//						"map:InstanceRel_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( " a d2rq:ClassMap;" ).append( spacer ).append(
//						"d2rq:dataStorage map:database;" ).append( spacer ).append(
//						"d2rq:uriPattern \"" ).append( baseRelURI ).append( relation ).append( "/@@" ).append( subjectTable ).append( "." ).append( subjectInstance ).append( "@@:@@" ).append( objectTable ).append( "." ).append( objectInstance ).append( "@@\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relSubjectColumn ).append( " = " ).append( subjectTable ).append( "." ).append( subjectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relObjectColumn ).append( " = " ).append( objectTable ).append( "." ).append( objectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:additionalProperty map:TypeOf_Property;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:SubPropertyOf_Relation;" ).append( spacer ).append(
//						"d2rq:additionalProperty map:SubPropertyOf_" ).append( relation ).append( "_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						".\n" ).append(
//						"map:SubPropertyOf_" ).append( relation ).append( "_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( " a d2rq:AdditionalProperty;" ).append( spacer ).append(
//						"d2rq:propertyName rdfs:subPropertyOf;" ).append( spacer ).append(
//						"d2rq:propertyValue <" ).append( semossRelURI ).append( relation ).append( ">;" ).append( spacer ).append(
//						".\n" ).append(
//						"map:Label_" ).append( relation ).append( "_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( " a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:InstanceRel_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:property rdfs:label;" ).append( spacer ).append(
//						"d2rq:pattern \"@@" ).append( subjectTable ).append( "." ).append( subjectInstance ).append( "@@:@@" ).append( objectTable ).append( "." ).append( objectInstance ).append( "@@\";" ).append( spacer ).append(
//						".\n" ).append(
//						"map:" ).append( subjectTable ).append( "_" ).append( objectTable ).append( "_SubPropertyOf_Self a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:InstanceRel_" ).append( subjectTable ).append( "_" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:property rdfs:subPropertyOf;" ).append( spacer ).append(
//						"d2rq:uriPattern \"" ).append( baseRelURI ).append( relation ).append( "/@@" ).append( subjectTable ).append( "." ).append( subjectInstance ).append( "@@:@@" ).append( objectTable ).append( "." ).append( objectInstance ).append( "@@\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relSubjectColumn ).append( " = " ).append( subjectTable ).append( "." ).append( subjectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relObjectColumn ).append( " = " ).append( objectTable ).append( "." ).append( objectID ).append( "\";" ).append( spacer ).append(
//						".\n" ).append(
//						"map:Instance" ).append( subjectTable ).append( "_Rel_Instance_" ).append( objectTable ).append( " a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:Instance" ).append( subjectTable ).append( "_TypeOf_Base" ).append( subjectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:refersToClassMap map:Instance" ).append( objectTable ).append( "_TypeOf_Base" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:property <http://semoss.org/ontologies/Relation>;" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relSubjectColumn ).append( " = " ).append( subjectTable ).append( "." ).append( subjectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relObjectColumn ).append( " = " ).append( objectTable ).append( "." ).append( objectID ).append( "\";" ).append( spacer ).append(
//						".\n" ).append(
//						"map:Instance" ).append( subjectTable ).append( "_Rel_" ).append( objectTable ).append( " a d2rq:PropertyBridge;" ).append( spacer ).append(
//						"d2rq:belongsToClassMap map:Instance" ).append( subjectTable ).append( "_TypeOf_Base" ).append( subjectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:refersToClassMap map:Instance" ).append( objectTable ).append( "_TypeOf_Base" ).append( objectTable ).append( ";" ).append( spacer ).append(
//						"d2rq:property <" ).append( semossRelURI ).append( relation ).append( ">;" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relSubjectColumn ).append( " = " ).append( subjectTable ).append( "." ).append( subjectID ).append( "\";" ).append( spacer ).append(
//						"d2rq:join \"" ).append( relTable ).append( "." ).append( relObjectColumn ).append( " = " ).append( objectTable ).append( "." ).append( objectID ).append( "\";" ).append( spacer ).append(
//						".\n" );
//	}
//	
//	private void processRelationshipType( Set<String> relationshipList ) {
//		Iterator<String> relIterator = relationshipList.iterator();
//		while ( relIterator.hasNext() ) {
//			String relationshipName = relIterator.next();
//			relationshipTypeMapping.append( "#####Relationship " ).append( relationshipName ).append( "\n" ).append(
//					"#Create the rel/relName subPropertyOf rel triple \n" ).append(
//							"map:" ).append( relationshipName ).append( " a d2rq:ClassMap;" ).append( spacer ).append(
//							"d2rq:dataStorage map:database;" ).append( spacer ).append(
//							"d2rq:constantValue <" ).append( semossRelURI ).append( relationshipName ).append( ">;" ).append( spacer ).append(
//							"d2rq:additionalProperty map:SubPropertyOf_Relation;" ).append( spacer ).append(
//							".\n" );
//		}
//	}
//	
//	private String readRequiredMappings( String templatePath ) {
//		String requiredMapping = "";
//		try {
//			requiredMapping = new Scanner( new File( templatePath ) ).useDelimiter( "\\Z" ).next();
//		}
//		catch ( FileNotFoundException e ) {
//			GuiUtility.showError( "Could not find template file!" );
//			logger.error( e );
//		}
//		return requiredMapping;
//	}
//	
//	public boolean processRDBMSSchema( String type, String url, String username, char[] password ) {
//		boolean success = true;
//		if ( !url.contains( "jdbc" ) || url.contains( "<" ) || url.contains( ">" ) || url.contains( "[" ) || url.contains( "]" ) ) {
//			return ( success = false );
//		}
//		
//		Connection con;
//		String dbName = "";
//		String sql = "";
//		Hashtable<String, Hashtable<String, ArrayList<String>>> schemaHash = new Hashtable<String, Hashtable<String, ArrayList<String>>>();
//		ResultSet resultSet = null;
//		
//		if ( type.equals( this.MYSQL ) ) {
//			try {
//				Class.forName( this.MYSQL_DRIVER );
//				con = DriverManager
//						.getConnection( url + "?user=" + username + "&password=" + new String( password ) );
//				//Get DBname from URL
//				dbName = url.substring( url.lastIndexOf( "/" ) + 1 );
//				sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + dbName + "';";
//				logger.info( "SQL Query for all Tables/Columns/DataTypes: " + sql );
//				Statement statement = con.createStatement();
//				resultSet = statement.executeQuery( sql );
//			}
//			catch ( ClassNotFoundException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//			catch ( SQLException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//		}
//		else if ( type.equals( this.ORACLE ) ) {
//			try {
//				Class.forName( this.ORACLE_DRIVER );
//				con = DriverManager
//						.getConnection( url, username, new String( password ) );
//				//Get DBname from URL
//				dbName = url.substring( url.lastIndexOf( "/" ) + 1 );
//				sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM ALL_TAB_COLUMNS";
//				logger.info( "SQL Query for all Tables/Columns/DataTypes: " + sql );
//				Statement statement = con.createStatement();
//				resultSet = statement.executeQuery( sql );
//			}
//			catch ( ClassNotFoundException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//			catch ( SQLException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//		}		
//		else if ( type.equals( this.SQLSERVER ) ) {
//			try {
//				Class.forName( this.SQLSERVER_DRIVER );
//				con = DriverManager
//						.getConnection( url + ";" + "user=" + username + ";" + "password=" + new String( password ) );
//				//Get DBname from URL
//				dbName = url.substring( url.indexOf( "=" ) + 1 );
//				sql = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG = '" + dbName + "';";
//				logger.info( "SQL Query for all Tables/Columns/DataTypes: " + sql );
//				Statement statement = con.createStatement();
//				resultSet = statement.executeQuery( sql );
//			}
//			catch ( ClassNotFoundException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//			catch ( SQLException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//		}
//		else if ( type.equals( this.ASTER ) ) {
//			try {
//				Class.forName( this.ASTER_DRIVER );
//				con = DriverManager.getConnection( url, username, new String( password ) );
//				
//				DatabaseMetaData md = con.getMetaData();
//				resultSet = md.getColumns( null, null, null, null );
//			}
//			catch ( ClassNotFoundException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//			catch ( SQLException e ) {
//				logger.error( e );
//				return ( success = false );
//			}
//		}
//		
//		try {
//			while ( resultSet.next() ) {
//				String tableName = "";
//				String columnName = "";
//				String dataType = "";
//				
//				if ( type.equals( this.ASTER ) ) {
//					tableName = resultSet.getString( "TABLE_NAME" );
//					if ( tableName.startsWith( "nc_" ) ) {
//						continue;
//					}
//					columnName = resultSet.getString( "COLUMN_NAME" );
//					logger.debug( tableName + " " + columnName );
//					dataType = resultSet.getString( "DATA_TYPE" );
//				}
//				else {
//					tableName = resultSet.getString( 1 );
//					columnName = resultSet.getString( 2 );
//					dataType = resultSet.getString( 3 );
//				}
//				
//				logger.debug( "SQL Result:     " + tableName + ">>>>>" + columnName + ">>>>>" + dataType );
//				
//				if ( !schemaHash.containsKey( tableName ) ) {
//					ArrayList<String> columnList = new ArrayList<String>();
//					columnList.add( columnName );
//					ArrayList<String> dataTypeList = new ArrayList<String>();
//					dataTypeList.add( dataType );
//					schemaHash.put( tableName, new Hashtable<String, ArrayList<String>>() );
//					Hashtable<String, ArrayList<String>> innerHash = schemaHash.get( tableName );
//					innerHash.put( "COLUMN", columnList );
//					innerHash.put( "DATATYPE", dataTypeList );
//				}
//				else {
//					Hashtable<String, ArrayList<String>> innerHash = schemaHash.get( tableName );
//					ArrayList<String> columnList = innerHash.get( "COLUMN" );
//					columnList.add( columnName );
//					ArrayList<String> dataTypeList = innerHash.get( "DATATYPE" );
//					dataTypeList.add( dataType );
//				}
//			}
//		}
//		catch ( SQLException e1 ) {
//			logger.error( e1 );
//			return ( success = false );
//		}
//		
//		String path = DIHelper.getInstance().getProperty( Constants.BASE_FOLDER ) + "/rdbms/";
//		String excelLoc = path + "RDBMS_Import_Sheet.xlsx";
//		
//		XSSFWorkbook wb = null;
//		try {
//			wb = new XSSFWorkbook( new FileInputStream( excelLoc ) );
//		}
//		catch ( IOException e ) {
//			logger.error( e );
//			return ( success = false );
//		}
//
//		// add schema sheet
//		XSSFSheet schemaSheet = wb.createSheet( dbName + "_Schema" );
//		XSSFRow row = schemaSheet.createRow( 0 );
//		row.createCell( 0 ).setCellValue( "TABLE NAME" );
//		row.createCell( 1 ).setCellValue( "COLUMN NAME" );
//		row.createCell( 2 ).setCellValue( "COLUMN DATA TYPE" );
//		
//		int counter = 1;
//		for ( String tName : schemaHash.keySet() ) {
//			Hashtable<String, ArrayList<String>> innerHash = schemaHash.get( tName );
//			ArrayList<String> columnList = innerHash.get( "COLUMN" );
//			ArrayList<String> dataTypeList = innerHash.get( "DATATYPE" );
//			
//			for ( int i = 0; i < columnList.size(); i++ ) {
//				row = schemaSheet.createRow( counter );				
//				row.createCell( 0 ).setCellValue( tName );
//				row.createCell( 1 ).setCellValue( columnList.get( i ) );
//				row.createCell( 2 ).setCellValue( dataTypeList.get( i ) );
//				counter++;
//			}			
//		}
//		
//		XSSFSheet dataSheet = wb.createSheet( dbName + "_DataSheet_DO_NOT_DELETE" );
//		//buildDataSheeet( dataSheet, schemaHash );
//		dataSheet.getWorkbook().setSheetHidden( 4, true );
//
//		// create drop downs for node and relationship tabs
//		HashSet<String> allColumnNames = new HashSet<String>();
//		HashSet<String> allDataTypes = new HashSet<String>();
//		String[] tableNames = new String[schemaHash.keySet().size()];
//
//		// remove duplicated results
//		counter = 0;
//		for ( String tName : schemaHash.keySet() ) {
//			tableNames[counter] = tName;			
//			
//			Hashtable<String, ArrayList<String>> innerHash = schemaHash.get( tName );
//			ArrayList<String> columnList = innerHash.get( "COLUMN" );
//			ArrayList<String> dataTypeList = innerHash.get( "DATATYPE" );
//			
//			allColumnNames.addAll( columnList );
//			allDataTypes.addAll( dataTypeList );
//			counter++;
//		}
//		allColumnNames.toArray( new String[allColumnNames.size()] );
//		allDataTypes.toArray( new String[allDataTypes.size()] );
//		
//		XSSFSheet nodeSheet = wb.getSheet( "Nodes" );
//		XSSFSheet relationshipSheet = wb.getSheet( "Relationships" );
//
//		// create drop down validation helper
//		XSSFDataValidationHelper nodeSheetValidationHelper = new XSSFDataValidationHelper( nodeSheet );
//		// create all lists
//		DataValidationConstraint nodeSheetTableNameConstraint = nodeSheetValidationHelper.createFormulaListConstraint( "TABLES" );
//		DataValidationConstraint nodeSheetColumnNameConstraint = nodeSheetValidationHelper.createFormulaListConstraint( "INDIRECT(UPPER(A2))" );
//		DataValidationConstraint nodeSheetDataTypeConstraint = nodeSheetValidationHelper.createFormulaListConstraint( "INDIRECT(CONCATENATE(UPPER(A2),\"_\",UPPER(C2)))" );
//		// create all ranges
//		CellRangeAddressList nodeSheetTableNameAddressList = new CellRangeAddressList( 1, 6, 0, 0 );
//		CellRangeAddressList nodeSheetColumnNameAddressList1 = new CellRangeAddressList( 1, 6, 1, 1 );
//		CellRangeAddressList nodeSheetColumnNameAddressList2 = new CellRangeAddressList( 1, 6, 2, 2 );
//		CellRangeAddressList nodeSheetDataTypeAddressList = new CellRangeAddressList( 1, 6, 3, 3 );
//		// create the drop downs
//		DataValidation nodeSheetTableNameDataValidation = nodeSheetValidationHelper.createValidation( nodeSheetTableNameConstraint, nodeSheetTableNameAddressList );
//		DataValidation nodeSheetColumnNameDataValidation1 = nodeSheetValidationHelper.createValidation( nodeSheetColumnNameConstraint, nodeSheetColumnNameAddressList1 );
//		DataValidation nodeSheetColumnNameDataValidation2 = nodeSheetValidationHelper.createValidation( nodeSheetColumnNameConstraint, nodeSheetColumnNameAddressList2 );
//		DataValidation nodeSheetDataTypeDataValidation = nodeSheetValidationHelper.createValidation( nodeSheetDataTypeConstraint, nodeSheetDataTypeAddressList );
//		// create the drop down side btn
//		nodeSheetTableNameDataValidation.setSuppressDropDownArrow( true );
//		nodeSheetColumnNameDataValidation1.setSuppressDropDownArrow( true );
//		nodeSheetColumnNameDataValidation2.setSuppressDropDownArrow( true );
//		nodeSheetDataTypeDataValidation.setSuppressDropDownArrow( true );
//		// add the validation to the node sheet
//		nodeSheet.addValidationData( nodeSheetTableNameDataValidation );
//		nodeSheet.addValidationData( nodeSheetColumnNameDataValidation1 );
//		nodeSheet.addValidationData( nodeSheetColumnNameDataValidation2 );
//		nodeSheet.addValidationData( nodeSheetDataTypeDataValidation );
//
//		// create drop down validation helper
//		XSSFDataValidationHelper relationshipSheetValidationHelper = new XSSFDataValidationHelper( relationshipSheet );
//		// create all lists
//		DataValidationConstraint relationshipSheetTableNameConstraint = relationshipSheetValidationHelper.createFormulaListConstraint( "TABLES" );
//		DataValidationConstraint relationshipSheetColumnNameConstraint1 = relationshipSheetValidationHelper.createFormulaListConstraint( "INDIRECT(UPPER(A2))" );
//		DataValidationConstraint relationshipSheetColumnNameConstraint2 = relationshipSheetValidationHelper.createFormulaListConstraint( "INDIRECT(UPPER(D2))" );
//		DataValidationConstraint relationshipSheetColumnNameConstraint3 = relationshipSheetValidationHelper.createFormulaListConstraint( "INDIRECT(UPPER(G2))" );
//
//		// create all ranges
//		CellRangeAddressList relationshipSheetTableNameAddressList1 = new CellRangeAddressList( 1, 6, 0, 0 );
//		CellRangeAddressList relationshipSheetTableNameAddressList2 = new CellRangeAddressList( 1, 6, 3, 3 );
//		CellRangeAddressList relationshipSheetTableNameAddressList3 = new CellRangeAddressList( 1, 6, 6, 6 );
//		
//		CellRangeAddressList relationshipSheetColumnNameAddressList1 = new CellRangeAddressList( 1, 6, 1, 1 );
//		CellRangeAddressList relationshipSheetColumnNameAddressList2 = new CellRangeAddressList( 1, 6, 2, 2 );
//		CellRangeAddressList relationshipSheetColumnNameAddressList3 = new CellRangeAddressList( 1, 6, 4, 4 );
//		CellRangeAddressList relationshipSheetColumnNameAddressList4 = new CellRangeAddressList( 1, 6, 5, 5 );
//		CellRangeAddressList relationshipSheetColumnNameAddressList5 = new CellRangeAddressList( 1, 6, 7, 7 );
//		CellRangeAddressList relationshipSheetColumnNameAddressList6 = new CellRangeAddressList( 1, 6, 8, 8 );
//
//		// create the drop downs	    
//		DataValidation relationshipSheetTableNameDataValidation1 = relationshipSheetValidationHelper.createValidation( relationshipSheetTableNameConstraint, relationshipSheetTableNameAddressList1 );
//		DataValidation relationshipSheetTableNameDataValidation2 = relationshipSheetValidationHelper.createValidation( relationshipSheetTableNameConstraint, relationshipSheetTableNameAddressList2 );
//		DataValidation relationshipSheetTableNameDataValidation3 = relationshipSheetValidationHelper.createValidation( relationshipSheetTableNameConstraint, relationshipSheetTableNameAddressList3 );
//		DataValidation relationshipSheetColumnNameDataValidation1 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint1, relationshipSheetColumnNameAddressList1 );
//		DataValidation relationshipSheetColumnNameDataValidation2 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint1, relationshipSheetColumnNameAddressList2 );
//		DataValidation relationshipSheetColumnNameDataValidation3 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint2, relationshipSheetColumnNameAddressList3 );
//		DataValidation relationshipSheetColumnNameDataValidation4 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint2, relationshipSheetColumnNameAddressList4 );
//		DataValidation relationshipSheetColumnNameDataValidation5 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint3, relationshipSheetColumnNameAddressList5 );
//		DataValidation relationshipSheetColumnNameDataValidation6 = relationshipSheetValidationHelper.createValidation( relationshipSheetColumnNameConstraint3, relationshipSheetColumnNameAddressList6 );
//		// create the drop down side btn
//		relationshipSheetTableNameDataValidation1.setSuppressDropDownArrow( true );
//		relationshipSheetTableNameDataValidation2.setSuppressDropDownArrow( true );
//		relationshipSheetTableNameDataValidation3.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation1.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation2.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation3.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation4.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation5.setSuppressDropDownArrow( true );
//		relationshipSheetColumnNameDataValidation6.setSuppressDropDownArrow( true );
//		// add the validations to the relationship sheet
//		relationshipSheet.addValidationData( relationshipSheetTableNameDataValidation1 );
//		relationshipSheet.addValidationData( relationshipSheetTableNameDataValidation2 );
//		relationshipSheet.addValidationData( relationshipSheetTableNameDataValidation3 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation1 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation2 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation3 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation4 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation5 );
//		relationshipSheet.addValidationData( relationshipSheetColumnNameDataValidation6 );
//		
//		try {
//			wb.write( new FileOutputStream( path + dbName + "_" + "RDBMS_Import_Sheet.xlsx" ) );
//		}
//		catch ( FileNotFoundException e ) {
//			logger.error( e );			
//			return ( success = false );
//		}
//		catch ( IOException e ) {
//			logger.error( e );
//			return ( success = false );
//		}
//		
//		return success;
//	}
}
