package topology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import server.common.LoggingManager;

public class WeightsLoader 
{
	public static double[][] loadWeightsFromFile(String commWeightsFileName, int numOfVertices)
    {
    	double [][] communicationWeights = new double[numOfVertices][numOfVertices];
    	File file = new File(commWeightsFileName);;
        ObjectInputStream in = null;
        try
        {
        	in = new ObjectInputStream(new FileInputStream(file));
        	communicationWeights = (double[][])in.readObject();
        }
        catch(ClassNotFoundException ex)
		{
        	LoggingManager.getInstance().writeSystem("A ClassNotFoundException has occured while trying to read the weights from file " 
					+ file.getName() + ":\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "analyzeFile", ex);
		}
        catch(FileNotFoundException ex)
        {
        	LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "loadWeights", ex);
        }
        catch(IOException ex)
        {
        	LoggingManager.getInstance().writeSystem("An exception has occured while reading the weights. Check correctness of the .wc file format.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "loadWeights", ex);
        }
        finally
        {
            try{
            	if (in != null)
            		in.close();
            }
            catch(IOException ex)
            {
            	LoggingManager.getInstance().writeSystem("An exception has occured while closing FileInputStream to: " + file.getAbsoluteFile() +
                        "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphLoader", "loadWeights", ex);
            }
        }
        return communicationWeights;
    }
    
    public static double[][] loadWeightsFromString(String communicationWeights, int numOfVertices)
    {
    	String [] weightsTokens = communicationWeights.split("[\\n\\t\\s\\r]+");
    	double [][] commWeights = new double[numOfVertices][numOfVertices];

    	int k = 0;
    	for (int i = 0; i < numOfVertices; i++)
    	{
    		for (int j = 0; j < numOfVertices; j++)
    			commWeights[i][j] = Double.parseDouble(weightsTokens[k++]);
    	}
    	return commWeights;
    }
    
    public static Map<Integer, Map<Integer, Double>> loadWeightsFromString(int numOfVertices, String communicationWeights)
    {
    	String [] weightsTokens = communicationWeights.split("[\\n\\t\\s\\r]+");
    	Map<Integer, Map<Integer, Double>> cw = new HashMap<Integer, Map<Integer, Double>>();
    	
    	int k = 0;
    	for (int i = 0; i < numOfVertices; i++)
    	{
    		for (int j = 0; j < numOfVertices; j++){
    			Map<Integer, Double> iMap = cw.get(i);
    			if (iMap == null)
    				iMap = new HashMap<Integer, Double>();
    			iMap.put(j, Double.parseDouble(weightsTokens[k++]));
    		}	
    	}
    	return cw;
    }
}