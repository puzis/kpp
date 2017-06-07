package topology.graphParsers;

import java.io.BufferedReader;

import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphDataInterface;
import topology.GraphFactory;


/**
 * @author Omer Zohar
 * This interface define the abstract for parsing a network file 
 */
public abstract class  NetworkGraphParser {

    final static private double PERCENTAGE_PROGRESS=0.25;


    /**
	 * function which parses one content of a file of data and adding its vertex and edges information to the given structure
	 * the function must add to the given structure and not delete it's existing data!
	 * @param reader stream to be parsed
	 * @param progress progress indicator
	 * @param percentage percentage done indicator
     * @param graph represent the graph
	 * @param vertexInfoType - the vertex structure
	 */
	
	public abstract void analyzeFile(BufferedReader reader, AbstractExecution progress, double percentage, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType);
	
	/**
	 * the parser's progress management 
	 * @param progress progress indicator
	 * @param percentage percentage done indicator
	 */
	public  void updateLoadProgress(AbstractExecution progress, double percentage)
    {
        double p = progress.getProgress();
        p += PERCENTAGE_PROGRESS * percentage;
        progress.setProgress(p);
    }
	
	/**
	 * getting the extension file type of the parser 
	 * @return the extension string (without the .)
	 */
	public abstract String getextension();

}