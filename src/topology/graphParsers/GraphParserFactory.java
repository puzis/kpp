package topology.graphParsers;

import server.execution.AbstractExecution;
import topology.GraphDataInterface;
import topology.GraphFactory;
import topology.SerializableGraphRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 * EXTENSIONS_MAP will be map from extension's string to parser's class
 * At getGraph method there is usage at Java reflection in order to create graph object
 */
public class GraphParserFactory
{
    private static final String DOT = ".";

    private static final Map<String, Class<? extends NetworkGraphParser>> EXTENSIONS_MAP =
            new HashMap<String, Class<? extends NetworkGraphParser>>();

    static {
        EXTENSIONS_MAP.put("net", NetFileParser.class);
        EXTENSIONS_MAP.put("net0", InnerReprParser.class);
        EXTENSIONS_MAP.put("txt", CaidaAsGraphParser.class);
        EXTENSIONS_MAP.put("fvl", PlanktonASRelationsParser.class);
        EXTENSIONS_MAP.put("sel", SimpleEdgeListParser.class);
        EXTENSIONS_MAP.put("oregon", ASOregonParser.class);
        EXTENSIONS_MAP.put("onet", OmnetNetworkParser.class);
        EXTENSIONS_MAP.put("e.csv", CSVEdgeListParser.class);
    }

    public static void getGraph(String dataDir, String fileName, String fileContent, AbstractExecution progress, double percentage, SerializableGraphRepresentation rawGraph) {
        String ext = fileName.substring(fileName.lastIndexOf(DOT)+1);
        getGraph(dataDir, fileName, fileContent, ext, progress, percentage, rawGraph);
    }

    public static void getGraph(String dataDir, String fileName, String fileContent, String ext, AbstractExecution progress, double percentage, SerializableGraphRepresentation rawGraph){
        getGraph(dataDir, fileName, fileContent, ext,progress,percentage,rawGraph,GraphFactory.DEFAULT_VERTEX_INFO_TYPE);
    }

    public static void getGraph(String dataDir, String fileName, String fileContent, String ext, AbstractExecution progress, double percentage, GraphDataInterface rawGraph, GraphFactory.VertexInfoType vertexInfoType)
    {
        Class<? extends NetworkGraphParser> clazz = EXTENSIONS_MAP.get(ext);
        if (clazz == null){
            throw new IllegalStateException("Can't initiate graph with extension " + ext);
        }

        NetworkGraphParser parser;
        try {
            parser = clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Can't initiate graph with extension " + ext);
        }

        GraphParsingContainer parsingContainer = new GraphParsingContainer(parser, dataDir);//initiating the container

        if (fileContent == null || fileContent.isEmpty()) {
            parsingContainer.execute(fileName, rawGraph, vertexInfoType);
        } else {
            parsingContainer.executeContent(fileContent,rawGraph,vertexInfoType);
        }
    }

}
