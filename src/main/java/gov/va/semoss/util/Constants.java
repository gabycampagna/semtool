/**
 * *****************************************************************************
 * Copyright 2013 SEMOSS.ORG
 *
 * This file is part of SEMOSS.
 *
 * SEMOSS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SEMOSS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SEMOSS. If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */
package gov.va.semoss.util;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 * This class contains all of the constants referenced elsewhere in the code.
 */
public class Constants {

	public static final String PDF = "PDF";
	public static final String PNG = "PNG";

	public static final int INITIAL_GRAPH_FONT_SIZE = 10;
	public static final String TBD = "TBD";
	public static final String NA = "n/a";

	public static final String SUBJECT = "SUBJECT";
	public static final String OBJECT = "OBJECT";
	public static final String TRANSPARENT = "TRANSPARENT";
	public static final String ENTER_TEXT = "Enter your search text here";
	public static final String ENTER_SEARCH_DISABLED_TEXT = "Search is disabled for faster processing";
	public static final String DB_NAME_FIELD = "dbSelectorField";
	public static final String IMPORT_FILE_FIELD = "importFileNameField";
	public static final String BASE_URI_TEXT_FIELD = "customBaseURItextField";

	public static final String IMPORT_COMBOBOX = "dbImportTypeComboBox";
	public static final String IMPORT_TYPE_COMBOBOX = "loadingFormatComboBox";
	public static final String IMPORT_CSV_IMPORT_LBL = "csvPropLbl";
	public static final String IMPORT_CSV_FILE_BUTTON = "csvPropBrowseBtn";
	public static final String IMPORT_CSV_FILE_FIELD = "csvPropFilenameField";
	public static final String IMPORT_TYPE_LABEL = "lblDataInputFormat";
	public static final String IMPORT_PANEL = "dbImportPanel";
	public static final String IMPORT_ENTERDB_LABEL = "dbNameLbl";
	public static final String IMPORT_FILE_LABEL = "selectionFileLbl";
	public static final String IMPORT_BUTTON_BROWSE = "fileBrowseBtn";
	public static final String IMPORT_BUTTON = "importButton";

	public static final String MAP_BROWSE_BUTTON = "mapBrowseBtn";
	public static final String DB_PROP_BROWSE_BUTTON = "dbPropBrowseButton";
	public static final String QUESTION_BROWSE_BUTTON = "questionBrowseButton";
	public static final String MAP_TEXT_FIELD = "importMapFileNameField";
	public static final String DB_PROP_TEXT_FIELD = "dbPropFileNameField";
	public static final String QUESTION_TEXT_FIELD = "questionFileNameField";
	public static final String ADVANCED_IMPORT_OPTIONS_PANEL = "advancedImportOptionsPanel";
	public static final String ADVANCED_IMPORT_OPTIONS_BUTTON = "btnShowAdvancedImportFeatures";
	public static final String IMPORT_RDBMS_URL_LABEL = "lblDBImportURL";
	public static final String IMPORT_RDBMS_URL_FIELD = "dbImportURLField";
	public static final String IMPORT_RDBMS_DRIVER_LABEL = "lblDBImportDriverType";
	public static final String IMPORT_RDBMS_DRIVER_COMBOBOX = "dbImportRDBMSDriverComboBox";
	public static final String IMPORT_RDBMS_USERNAME_LABEL = "lblDBImportUsername";
	public static final String IMPORT_RDBMS_USERNAME_FIELD = "dbImportUsernameField";
	public static final String IMPORT_RDBMS_PW_LABEL = "lblDBImportPW";
	public static final String IMPORT_RDBMS_PW_FIELD = "dbImportPWField";
	public static final String TEST_RDBMS_CONNECTION = "btnTestRDBMSConnection";
	public static final String GET_RDBMS_SCHEMA = "btnGetRDBMSSchema";
	public static final String IMPORT_MAP_LABEL = "lblselectCustomMap";
	public static final String DBCM_Prop = "DBCM_Prop";
	public static final String EMPTY = "@@";
	public static final String LAYOUT = "LAYOUT";
	public static final String VERTEX_NAME = "VERTEX_LABEL_PROPERTY";
	public static final String VERTEX_TYPE = "VERTEX_TYPE_PROPERTY";
	public static final String VERTEX_COLOR = "VERTEX_COLOR_PROPERTY";
	public static final String GENERIC_IMAGE = "GENERIC";
	public static final String PERSPECTIVE = "PERSPECTIVE";
	public static final String INEDGE_COUNT = "Inputs";
	public static final String OUTEDGE_COUNT = "Outputs";
	public static final String PROCESS_CURRENT_DATE = "PROCESS_CURRENT_DATE";
	public static final String PROCESS_CURRENT_USER = "PROCESS_CURRENT_USER";
	public static final String CURRENT_PLAYSHEET = "layoutValue";
	public static final String BASE_FOLDER = "BaseFolder";

	//graphplaysheet option constants
	// layouts
	public static final String GPSSudowl = "GPS_SUDOWL_DEFAULT";
	public static final String GPSSearch = "GPS_SEARCH_DEFAULT";
	public static final String GPSProp = "GPS_PROPERTIES_DEFAULT";
	public static final String highQualityExport = "GPSHighQualityExport";
	public static final String sudowlCheck = "sudowlCheck";
	public static final String searchCheck = "searchCheck";
	public static final String propertyCheck = "propertyCheck";
	public static final String highQualityExportCheck = "highQualityExportCheck";

	//used by ExportSpecificRelationshipsToLoadingSheetAction
	public static final URI ANYNODE = new URIImpl( "semoss://any" );
	public static final URI NONODE = new URIImpl( "semoss://none" );

	//Used by POIReader
	public static final String RELATION_URI_CONCATENATOR = "_x_"; //used in between the in node and out node for relation instance uris.
	public static final String RELATION_LABEL_CONCATENATOR = ":"; //used in between the in node and out node for relation instance uris.
	public static final String SLASH = "/";
	public static final String DEFAULT_NODE_CLASS = "Concept";
	public static final String DEFAULT_RELATION_CLASS = "Relation";
	public static final String CONTAINS = "Contains";

	public static final String SEMOSS_URI = "SEMOSS_URI";
	public static final String CLASS = "_CLASS";
	public static final String DEFAULT_SEMOSS_URI = "http://semoss.org/ontologies";
	/**
	 * @deprecated use {@link OwlUriBuilder#getRelationUri(java.lang.String) }
	 * instead
	 */
	@Deprecated
	public static final String RELATION_URI = DEFAULT_SEMOSS_URI + SLASH + DEFAULT_RELATION_CLASS + SLASH;
	/**
	 * @deprecated use {@link OwlUriBuilder#getContainsUri() } instead
	 */
	@Deprecated
	public static final String CONTAINS_URI = RELATION_URI + CONTAINS;
	/**
	 * @deprecated use {@link OwlUriBuilder#getConceptUri() } instead
	 */
	@Deprecated
	public static final String CONCEPT_URI = DEFAULT_SEMOSS_URI + SLASH + DEFAULT_NODE_CLASS;

	public static final String SUBPROPERTY_URI = RDFS.SUBPROPERTYOF.toString();
	public static final String SUBCLASS_URI = RDFS.SUBCLASSOF.toString();
	public static final String CLASS_URI = RDFS.CLASS.toString();
	public static final String DEFAULT_PROPERTY_URI = RDF.PROPERTY.toString();
	public static final String RDFTYPE_URI = RDF.TYPE.toString();
	public static final String DATE_URI = XMLSchema.DATETIME.toString();

	// layouts
	public static final String FR = "Fruchterman-Reingold";
	public static final String KK = "Kamada-Kawai";
	public static final String SPRING = "Spring-Layout";
	public static final String SPRING2 = "Spring-Layout2";
	public static final String CIRCLE_LAYOUT = "Circle-Layout";
	public static final String ISO = "ISO-Layout";
	public static final String TREE_LAYOUT = "Tree-Layout";
	public static final String RADIAL_TREE_LAYOUT = "Radial-Tree-Layout";
	public static final String BALLOON_LAYOUT = "Balloon Layout";

	public static final String LEGEND = "_LEGEND";

	public static final String DESCR = "DESCRIPTION";
	public static final String QUERY = "QUERY";
	//public static final String SUBMIT_SPARQL_QUERY = "btnSubmitSparqlQuery";
	//public static final String APPEND_SPARQL_QUERY = "appendSparqlQueryChkBox";
	//public static final String GET_CURRENT_SPARQL = "btnGetQuestionSparql";
	//public static final String SHOW_HINT = "btnShowHint";
	public static final String SHOW_PLAYSHEETS_LIST = "btnShowPlaySheets";
	public static final String ENGINE_NAME = "ENGINE";
	public static final String ENGINES = "ENGINES";
	public static final String ENGINE_PROPERTIES_FILE = "ENGINE_PROP";
	public static final String ENGINE_IMPL = "ENGINE_TYPE";
	public static final String LISTENERS = "LISTENERS";
	public static final String MODEL = "MODEL";
	public static final String CONTROL = "CONTROLLER";
	public static final String VIEW = "VIEW";
	public static final String PARENT_VIEW = "PARENT_VIEW";
	public static final String RIGHT_VIEW = "gov.va.semoss.ui.components.RightView";
	public static final String SPARQL_AREA_FIELD = "sparqlArea";
	public static final String PARAM_PANEL_FIELD = "paramPanel";
	public static final String QUESTION_LIST_FIELD = "questionSelector";
	public static final String RIGHT_VIEW_FIELD = "rightView";
	public static final String MAIN_FRAME = "playPane";
	public static final String DESKTOP_PANE = "desktopPane";

	//colors
	public static final String BROWN = "BROWN";
	public static final String RED = "RED";
	public static final String GREEN = "GREEN";
	public static final String BLUE = "BLUE";
	public static final String ORANGE = "ORANGE";
	public static final String YELLOW = "YELLOW";
	public static final String PURPLE = "PURPLE";
	public static final String AQUA = "AQUA";
	public static final String MAGENTA = "MAGENTA";

	//shapes
	public static final String SQUARE = "SQUARE";
	public static final String TRIANGLE = "TRIANGLE";
	public static final String DIAMOND = "DIAMOND";
	public static final String STAR = "STAR";
	public static final String CIRCLE = "CIRCLE";
	public static final String HEXAGON = "HEXAGON";
	public static final String PENTAGON = "PENTAGON";

	public static final String TYPE_QUERY = "TYPE_QUERY";
	public static final String TCCALC_PROGRESS_BAR = "calcTCprogressBar";
	public static final String TMCALC_PROGRESS_BAR = "calcTMprogressBar";
	public static final String ENTITY = "entity";
	public static final String REPO_LIST = "repoList";
	public static final String FILTER_TABLE = "filterTable";
	public static final String EDGE_TABLE = "edgeTable";
	public static final String PROP_TABLE = "propertyTable";
	public static final String FILTER_PANEL = "filterPanel";
	public static final String PLAYSHEETS = "playsheetList";
	public static final String APPEND = "appendChkBox";
	public static final String PROP_URI = "PROP_URI";
	public static final String PREDICATE_URI = "PREDICATE_URI";
	public static final String EDGE_NAME = "EDGE_NAME";
	public static final String EDGE_TYPE = "EDGE_TYPE";
	public static final String OPTION = "OPTION"; // used by entity filler
	public static final String FILTER = "FILTER_NAME";
	public static final String EDGE_ADJUSTER_TABLE = "edgeAdjusterTable";
	public static final String LABEL_TABLE = "labelTable";
	public static final String TOOLTIP_TABLE = "tooltipTable";
	public static final String BUSINESS_VALUE = "System/Business_Value";
	public static final String CAPABILITY_BUSINESS_VALUE = "Capability/Business_Value";
	public static final String BUSINESS_PROCESS_BUSINESS_VALUE = "BusinessProcess/Business_Value";
	public static final String CALC_MATRIX = "_Matrix";
	public static final String CALC_MATRIX_EXT_STAB = "_Matrix_Ext_Stab";
	public static final String CALC_MATRIX_TECH_STD = "_Matrix_Tech_Std";
	public static final String CALC_PROPERTY_LABEL = "_PropLabels";
	public static final String CALC_COLUMN_LABELS = "_ColLabels";
	public static final String CALC_ROW_LABELS = "_RowLabels";
	public static final String CALC_EXAMPLE_EDGE = "_ExEdge";
	public static final String URI_KEY = "URI";
	public static final String CALC_NAMES_LIST = "_Names";
	public static final String CALC_NAMES_TECH_STD_LIST = "_Names_Tech_Std";
	public static final String GRID_VIEW = "gov.va.semoss.ui.components.playsheets.GridPlaySheet";
	public static final String PROP_HASH = "_PropHash";
	public static final String TRAVERSE_JENA_MODEL = "traverseJenaModel";
	public static final String UNDO_BOOLEAN = "undoBoolean";
	public static final String UNDOBTN = "undoBtn";
	public static final String TECH_MATURITY = "System/Tech_Maturity";
	public static final String TRANSITION_COSTS = "TRANSITION_COSTS";
	public static final String TM_LIFECYCLE = "_Tech_Maturity_Lifecycle";
	public static final String TM_CATEGORY = "_Tech_Maturity_Category";
	public static final String TC_OVERHEAD_ARRAY = "_TC_Overhead_Array";
	public static final String TC_CORE_MATRIX = "_TC_Core_Matrix";
	public static final String TC_SDLC_CORE_MATRIX = "_TC_SDLC_Core_Matrix";
	public static final String RDF_FILE_NAME = "RDF_FILE_NAME";
	public static final String RDF_FILE_TYPE = "RDF_FILE_TYPE";
	public static final String RDF_FILE_BASE_URI = "RDF_FILE_BASE_URI";
	public static final String OBJECT_PROP_TABLE = "objectPropertiesTable";
	public static final String DATA_PROP_TABLE = "dataPropertiesTable";
	public static final String OBJECT_PROP_STRING = "objectPropertiesString";
	public static final String DATA_PROP_STRING = "dataPropertiesString";
	public static final String COLOR_SHAPE_TABLE = "colorShapeTable";
	public static final String SIZE_TABLE = "sizeTable";
	public static final String EXTEND_TABLE = "extendTable";
	public static final String SUBMIT_BUTTON = "submitButton";
	public static final String TRANS_ALL_FRAME = "transAllFrame";
	public static final String TRANS_ALL_SYSTEM_LABEL = "sysNoLabel";
	public static final String TRANS_ALL_DATA_LABEL = "dataNoLabel";
	public static final String TRANS_ALL_ICD_LABEL = "icdNoLabel";
	public static final String TRANS_ALL_SYSTEM_AREA = "transAllSysArea";
	public static final String TRANS_ALL_DATA_AREA = "transAllDataArea";
	public static final String TRANS_ALL_ICD_AREA = "transAllICDArea";
	public static final String TRANS_ALL_WSPRO_AREA = "transAllWSPArea";
	public static final String TRANS_ALL_WSCON_AREA = "transAllWSCArea";
	public static final String PLAYSHEET_COMBOBOXLIST = "playSheetComboBox";
	public static final String SPARQLLABEL = "sparqlLbl";
	public static final String BLANK_URL = "http://bornhere.com/noparent/blank/";
	public static final String PPT_TRAINING_BUTTON = "pptTrainingBtn";
	public static final String HTML_TRAINING_BUTTON = "htmlTrainingBtn";

	//Traverse Freely Queries
	public static final String NEIGHBORHOOD_TYPE_QUERY = "NEIGHBORHOOD_TYPE_QUERY";
	public static final String NEIGHBORHOOD_TYPE_QUERY_JENA = "NEIGHBORHOOD_TYPE_QUERY_JENA";
	public static final String NEIGHBORHOOD_OBJECT_QUERY = "NEIGHBORHOOD_OBJECT_QUERY";
	public static final String NEIGHBORHOOD_PREDICATE_FINDER_QUERY = "NEIGHBORHOOD_PREDICATE_FINDER_QUERY";
	public static final String NEIGHBORHOOD_PREDICATE_ALT2_FINDER_QUERY = "NEIGHBORHOOD_PREDICATE_ALT2_FINDER_QUERY";
	public static final String NEIGHBORHOOD_PREDICATE_ALT3_FINDER_QUERY = "NEIGHBORHOOD_PREDICATE_ALT3_FINDER_QUERY";
	public static final String TRAVERSE_FREELY_QUERY = "TRAVERSE_FREELY_QUERY";
	public static final String TRAVERSE_FREELY_QUERY_JENA = "TRAVERSE_FREELY_QUERY_JENA";
	public static final String TRAVERSE_INSTANCE_FREELY_QUERY = "TRAVERSE_INSTANCE_FREELY_QUERY";
	public static final String SUBJECT_TYPE_QUERY = "SUBJECT_TYPE_QUERY";
	public static final String PREDICATE = "PREDICATE";
	public static final String IGNORE_URI = "IGNORE_URI";

	//TAP Cost Transition Queries 
	public static final String TRANSITION_COST_INSERT_WITH_OVERHEAD = "TRANSITION_COST_INSERT_WITH_OVERHEAD";
	public static final String TRANSITION_COST_INSERT_WITHOUT_OVERHEAD = "TRANSITION_COST_INSERT_WITHOUT_OVERHEAD";
	public static final String TRANSITION_COST_DELETE = "TRANSITION_COST_DELETE";
	public static final String TRANSITION_COST_INSERT_SITEGLITEM = "TRANSITION_COST_INSERT_SITEGLITEM";
	public static final String TRANSITION_QUERY_SEPARATOR = "&";
	public static final String TRANSITION_COST_INSERT_SUSTAINMENT = "TRANSITION_COST_INSERT_SUSTAINMENT";
	public static final String TRANSITION_COST_INSERT_TRAINING = "TRANSITION_COST_INSERT_TRAINING";
	public static final String TRANSITION_COST_INSERT_SEMANTICS = "TRANSITION_COST_INSERT_SEMANTICS";
	public static final String TRANSITION_DATA_FEDERATION_PHASE_INDEPENDENT = "TRANSITION_DATA_FEDERATION_PHASE_INDEPENDENT";
	public static final String TRANSITION_REPORT_COMBO_BOX = "transCostReportSystemcomboBox";
	public static final String TRANSITION_REPORT_TYPE_COMBO_BOX = "TransReportTypecomboBox";
	public static final String TRANSITION_REPORT_FORMAT_COMBO_BOX = "TransReportFormatcomboBox";
	public static final String TRANSITION_APPLY_OVERHEAD_RADIO = "rdbtnApplyTapOverhead";
	public static final String TRANSITION_NOT_APPLY_OVERHEAD_RADIO = "rdbtnDoNotApplyOverhead";
	public static final String TRANSITION_SERVICE_PANEL = "transitionServicePanel";
	public static final String SOURCE_SELECT_PANEL = "sourceSelectPanel";
	public static final String DHMSM_CAPABILITY_SELECT_PANEL = "dhmsmCapabilitySelectPanel";
	public static final String SELECT_RADIO_PANEL = "selectRadioPanel";
	public static final String SELECT_DATA_ACCESS_FILE_JFIELD = "dhmsmDataAccessImportFileNameField";
	public static final String TRANSITION_CHECK_BOX_DATA_FED = "chckbxDataFederationTransReport";
	public static final String TRANSITION_CHECK_BOX_DATA_CONSUMER = "chckbxDataConsumer";
	public static final String TRANSITION_ITEM_GEN_BUTTON = "loadGenBtn";
	public static final String TRANSITION_CHECK_BOX_BLU_PROVIDER = "chckbxBLUprovider";
	public static final String TRANSITION_CHECK_BOX_DATA_GENERIC = "chckbxDataEsbImplementation";
	public static final String TRANSITION_CHECK_BOX_BLU_GENERIC = "chckbxBluEsbImplementation";
	public static final String TRANSITION_SYSTEM_DROP_DOWN_PANEL = "transReportSysDropDownPanel";
	public static final String TRANSITION_GENERIC_BLU = "TRANSITION_GENERIC_BLU";
	public static final String TRANSITION_GENERIC_DATA = "TRANSITION_GENERIC_DATA";
	public static final String TRANSITION_SPECIFIC_DATA_CONSUMER = "TRANSITION_SPECIFIC_DATA_CONSUMER";
	public static final String TRANSITION_DATA_FEDERATION = "TRANSITION_DATA_FEDERATION";
	public static final String TRANSITION_BLU_PROVIDER = "TRANSITION_BLU_PROVIDER";
	public static final String TRANSITION_SPECIFIC_SITE_CONSUMER = "TRANSITION_SPECIFIC_SITE_CONSUMER";
	public static final String ADVANCED_TRANSITION_FUNCTIONS_PANEL = "advancedFunctionsPanel";
	public static final String SERVICE_SELECTION_BUTTON = "serviceSelectionBtn";
	public static final String SERVICE_SELECTION_PANE = "serviceSelectScrollPane";
	public static final String SOURCE_SELECTION_PANE = "sourceSelectScrollPane";
	public static final String TIER1_CHECKBOX = "tierCheck1";
	public static final String TIER2_CHECKBOX = "tierCheck2";
	public static final String TIER3_CHECKBOX = "tierCheck3";

	//Fact Sheet Report Queries
	public static final String FACT_SHEET_REPORT_TYPE_COMBO_BOX = "FactSheetReportTypecomboBox";
	public static final String FACT_SHEET_SYSTEM_DROP_DOWN_PANEL = "factSheetReportSysDropDownPanel";
	public static final String FACT_SHEET_SYSTEM_SELECT_COMBO_BOX = "factSheetReportSyscomboBox";

	//Used by optimization organizer
	public static final String TRANSITION_GENERIC_COSTS = "TRANSITION_GENERIC_COSTS";
	public static final String TRANSITION_CONSUMER_COSTS = "TRANSITION_CONSUMER_COSTS";
	public static final String TRANSITION_PROVIDER_COSTS = "TRANSITION_PROVIDER_COSTS";

	public static final String UPDATE_SPARQL_AREA = "customUpdateTextPane";
	public static final String INSERT_SYS_SUSTAINMENT_BUDGET_BUTTON = "btnInsertBudgetProperty";
	public static final String SYSTEM_SUSTAINMENT_BUDGET_INSERT_QUERY = "SYSTEM_SUSTAINMENT_BUDGET_INSERT_QUERY";

	//Distance Downstream
	public static final String INSERT_DOWNSTREAM_BUTTON = "btnInsertDownstream";
	public static final String DISTANCE_DOWNSTREAM_QUERY = "DISTANCE_DOWNSTREAM_QUERY";
	public static final String SOA_ALPHA_VALUE_TEXT_BOX = "soaAlphaValueTextField";
	public static final String APPRECIATION_TEXT_BOX = "appreciationValueTextField";
	public static final String DEPRECIATION_TEXT_BOX = "depreciationValueTextField";

	//Central System Sys-BP Sys-Activity Aggregation Thresholds
	public static final String DATA_OBJECT_THRESHOLD_VALUE_TEXT_BOX = "dataObjectThresholdValueTextField";
	public static final String BLU_THRESHOLD_VALUE_TEXT_BOX = "bluThresholdValueTextField";

	//SOA Transition All
	public static final String SOA_TRANSITION_ALL_DATA_QUERY = "SOA_TRANSITION_ALL_DATA_QUERY";
	public static final String SOA_TRANSITION_ALL_GENERIC_DATA_QUERY = "SOA_TRANSITION_ALL_GENERIC_DATA_QUERY";
	public static final String SOA_TRANSITION_ALL_GENERIC_BLU_QUERY = "SOA_TRANSITION_ALL_GENERIC_BLU_QUERY";
	public static final String SOA_TRANSITION_ALL_BLU_QUERY = "SOA_TRANSITION_ALL_BLU_QUERY";

	public static final String DREAMER = "DREAMER";
	public static final String ONTOLOGY = "ONTOLOGY";

	public static final String PERSPECTIVE_SELECTOR = "perspectiveSelector";

	public static final String BROWSER_TYPE = "BROWSER_TYPE";

	public static final String SPARQL_QUERY_ENDPOINT = "SPARQL_QUERY_ENDPOINT";
	public static final String SPARQL_UPDATE_ENDPOINT = "SPARQL_UPDATE_ENDPOINT";

	public static final String DATA_LATENCY_WEEKS_TEXT = "dataLatencyMonthsTextField";
	public static final String DATA_LATENCY_DAYS_TEXT = "dataLatencyDaysTextField";
	public static final String DATA_LATENCY_HOURS_TEXT = "dataLatencyHoursTextField";

	public static final String HTML = "HTML";
	public static final String PROPERTY = "PROPERTY";

	//Load Sheet Export Panel
	public static final String EXPORT_LOAD_SHEET_SOURCE_COMBOBOX = "exportDataSourceComboBox";
	public static final String EXPORT_LOAD_SHEET_SUBJECT_NODE_TYPE_COMBOBOX = "subjectNodeTypeComboBox";
	public static final String EXPORT_LOAD_SHEET_OBJECT_NODE_TYPE_COMBOBOX = "objectNodeTypeComboBox";
	public static final String EXPORT_LOAD_SHEET_NODE_RELATIONSHIP_COMBOBOX = "nodeRelationshipComboBox";
	public static final String EXPORT_LOAD_SHEET_MAX_LIMIT_MESSAGE = "lblMaxExportLimit";
	public static final String EXPORT_LOAD_SHEET_CLEAR_ALL_BUTTON = "btnClearAll";
	public static final String EXPORT_LOAD_SHEET_ADD_EXPORT_BUTTON = "btnAddExport";
	public static final int MAX_EXPORTS = 9;

	//Update Cost DB Panel
	public static final String CHANGED_DB_COMBOBOX = "changedDBComboBox";
	public static final String COST_DB_COMBOBOX = "costDBComboBox";

	public static final String COST_DB_BASE_URI_FIELD = "costDBBaseURIField";
	public static final String GLITEM_LOADING_SHEET = "LoadingSheets1.xlsx";
	public static final String WATCHERS = "WATCHERS";
	public static final String ENGINE_WATCHER = "ENGINE_WATCHER";
	public static final String ENGINE_EXT = "ENGINE_EXT";

	public static final String GLITEM_CORE_LOADING_SHEET = "LoadingSheets1.xlsx";

	public static final String GLITEM_SITE_LOADING_SHEET = "Site_HWSW_GLItems.xlsx";
	public static final String OWLFILE = "OWL";
	public static final String URL_PARAM = "URL_PARAM";
	public static final String PROPS = "PROPS";
	//public static final String TYPE_URI = "TYPE_URI";
	public static final String INSIGHT = "INSIGHT";
	public static final String ID = "ID";
	public static final String LABEL = "LABEL";
	public static final String OUTPUT = "OUTPUT";
	public static final String TYPE = "TYPE";
	public static final String SPARQL = "SPARQL";
	public static final String TAG = "TAG";
	/*
	 public static final URI INSIGHT_URI = new URIImpl( INSIGHT + ":" + INSIGHT );
	 public static final URI INSIGHT_ID = new URIImpl( INSIGHT + ":" + ID );
	 public static final URI INSIGHT_TYPE = new URIImpl( INSIGHT + ":" + TYPE );
	 public static final URI INSIGHT_LABEL = new URIImpl( INSIGHT + ":" + LABEL );
	 public static final URI INSIGHT_TAG = new URIImpl( INSIGHT + ":" + TAG );
	 public static final URI INSIGHT_SPARQL = new URIImpl( INSIGHT + ":" + SPARQL );
	 public static final URI INSIGHT_OUTPUT = new URIImpl( INSIGHT + ":" + OUTPUT );
	 public static final URI INSIGHT_PARAM = new URIImpl( INSIGHT + ":PARAM" );
	 public static final URI INSIGHT_PARAM_LABEL = new URIImpl( INSIGHT + ":PARAM:LABEL" );
	 public static final URI PARAM_QUERY = new URIImpl( "PARAM:QUERY" );
	 public static final URI HAS_PARAM_DEPEND = new URIImpl( "HAS:PARAM:DEPEND" );
	 public static final URI PARAM_TYPE = new URIImpl( "PARAM:TYPE" );
	 public static final URI PARAM_DEPEND = new URIImpl( "PARAM:DEPEND" );
	 public static final URI PERSPECTIVE_URI = new URIImpl( PERSPECTIVE + ":" + PERSPECTIVE );
	 public static final URI PERSPECTIVE_LABEL = new URIImpl( PERSPECTIVE + ":" + LABEL );
	 */

	public static final String ENGINE_WEB_WATCHER = "ENGINE_WEB_WATCHER";
	public static final String DEPEND = "DEPEND";

	public static final String SMSS_LOCATION = "SMSS_LOCATION";
	public static final String SMSS_SEARCHPATH = "SMSS_SEARCHPATH";
	public static final String SMSS_VERSION_KEY = "VERSION";
	public static final String SMSS_RWSTORE_KEY = "RWStore";

	public static final String METADATA_SHEET_NAME = "Metadata";
	public static final String HELPURI_KEY = "HelpURI";
	public static final String LATESTRELEASE_KEY = "LatestRelease";
	public static final String EXPERIMENTALRELEASE_KEY = "ExperimentalRelease";
	public static final String LICENSEURI_KEY = "LicenseURI";
	public static final String BASEURI_KEY = "baseuri";
	public static final String PIN_KEY = "pinned";
	public static final String DEFAULTUI_KEY = "DefaultUI";

	public static final String CALC_INFERENCES_PREF = "calculateInferences";
	public static final String INSIGHTKB = "insights";

	public static final String USERPREF_NAME = "userFullName";
	public static final String USERPREF_EMAIL = "userEmail";
	public static final String USERPREF_ORG = "userOrganization";
	public static final String USERPREF_NAMESPACES = "userNamespaces";

}
