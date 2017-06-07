package server.common;

public class ServerConstants
{
	
	public static final String SERVER_PROPS_XML = "serverProps.xml";
	public static final String PORT = "port";
	public static final String UTF_ENCODING = "UTF-8";
	private static int port = 8080;
	public static final String SHORTEST_PATH_ALG = "shortest_path_alg";	
	public static final String GRID_ENABLED = "grid_enabled";
	
	public static final String DATA_DIR = System.getProperty("data.dir");
	public static final String LOGS_DIR = "logs";
	public static final String MATHKERNEL_ABS_PATH = "math_kernal_path";
	public static String mathKernelPath = "";
	
	public static final String NETWORK = "Network";
	public static final String SUM_GROUP = "getSumCentrality";
	public static final String EVALUATION_RESULT = "evaluationResult";
	public static final String EVALUATE_SUCCESS = "evaluateSuccess";
	public static final String EVALUATE_GROUP = "evaluateGroup";
	public static final String EVALUATE_LINKS = "evaluateLinks";
	public static final String CAN_EVALUATE = "canEvaluate";
	public static final String SEARCH_RESULT = "searchResult";
	public static final String SEARCH_SUCCESS = "searchSuccess";
	public static final String FIND_LINKS = "findLinks";
	public static final String FIND_VERTICES = "findVertices";
	public static final String CAN_SEARCH = "canSearch";
	public static final String ANALYSIS_SUCCESS = "analysisSuccess";
	public static final String ANALYZE = "analyze";
	public static final String ANALYZE_RESULT = "analyzeResult";
	public static final String STORE_ANALYSIS = "storeAnalysis";
	public static final String STORE_GRAPH = "storeGraph";
	public static final String CANCEL_ANALYSIS = "cancelAnalysis";
	public static final String CAN_ANALYZE = "canAnalyze";
	public static final String LOAD_SUCCESS = "loadSuccess";
	public static final String IMPORT_NETWORK = "importNetwork";
	public static final String LOAD_NETWORK = "loadNetwork";
	public static final String LOAD_PROGRESS = "loadProgress";
	public static final String ANALYSIS_PROGRESS = "analysisProgress";
	public static final String SEARCH_PROGRESS = "searchProgress";
	public static final String EVALUATE_PROGRESS = "evaluateProgress";
	public static final String SAVING_ANALYSIS_PROGRESS = "savingAnalysisProgress";
	public static final String SAVING_ANALYSIS_SUCCESS = "savingAnalysisSuccess";
	public static final String CLOSE_NETWORK = "closeNetwork";
	public static final String CREATE_GROUP = "createGroup";
	public static final String ADD = "add";
	public static final String GET_MEMBERS = "getMembers";
	public static final String GET_BETWEENNESS = "getBetweenness";
	public static final String GET_TOP_K_FROM_GROUP = "getTopKFromGroup";
	public static final String CLOSE_GROUP = "closeGroup";
	public static final String ALGORITHMS_SERVER = "CentralityAlgorithmsServer";
	public static final String NETWORK_INDEX_DOESN_T_EXIST = "Network index doesn't exist: ";
	public static final String GROUP_INDEX_DOESN_T_EXIST = "Group index doesn't exist: ";
	public static final String RETURNING_TO_CLIENT = "Returning to client..";
	
	/** Centrality types */
	public static enum Centrality {Betweeness, Degree, Closeness, RandomWalk, FasterBC}

	/** Algorithm types */
	public static enum Algorithm {TopK, Contribution}
	
	/** Bound types */
	public static enum Bound {GroupSize, Centrality}
	
	/** BCC algorithm types */
	public static enum BiConnectedCAlgorithm{
		EAGER_BCC, ULTRA_EAGER_BCC, PROXY_BCC;
		
		public static BiConnectedCAlgorithm make(int algIndex) {
			 return BiConnectedCAlgorithm.values()[algIndex];
		}
	}

	/** BC calculator types */
	public static enum BCCalculatorAlgorithm{
		DEFAULT, TRAFIC_MATRIX_BC;
		
		public static BCCalculatorAlgorithm make(int algIndex) {
			 return BCCalculatorAlgorithm.values()[algIndex];
		}
	}
	
	/**
	 * @return the port
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public static void setPort(int port) {
		ServerConstants.port = port;
	}
	public static String getMathKernelPath() {
		return mathKernelPath;
	}
	public static void setMathKernelPath(String mathKernelPath) {
		ServerConstants.mathKernelPath = mathKernelPath;
	}
}