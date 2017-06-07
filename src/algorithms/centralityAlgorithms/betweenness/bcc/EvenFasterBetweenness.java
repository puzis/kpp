package algorithms.centralityAlgorithms.betweenness.bcc;

import java.util.Iterator;

import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.HyperGraphInterface;
import algorithms.bcc.BiConnectedComponent;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class EvenFasterBetweenness implements CentralityAlgorithmInterface{
	private int m_numOfVertices = 0;
    private double[] m_BC = null;
    
    /** Statistics */
//    private int m_maxComponentSize = 0;
//    private double m_avgComponentSize = 0;
//    private int m_componentCounter = 0;
    
    private BCCalculatorInterface m_bc;
    
    /**
     * betweenness of v is composed of two components:
	 * @1 inter-component communications
	 * @2 intra-component communications
	 * 
	 * @1 should be computed for each v as soon as all it weights are known \sum_{C: v\in C} w(C,v)*(|V| - w(C,v) - 1)
	 *
	 * If C is a degenerated bcc then its contribution to @2 is 2*w(C,v).
	 * For example, if all bccs of v are degenerated @2=\sum_{C|v\in C} 2*w(C,v)
	 * 
	 * If C is a complex bcc then @2 should be computed by calculating BC inside the component where traffic matrix values are as following (v!=u are cutoffs, x!=y are not):
	 * T[x,x]=0
     * T[x,y]=1
	 * T[v,x]= |V| - w(C,v)
	 * T[v,v]=0
	 * T[v,u]=(|V| - w(C,v))(|V| - w(C,u))
	 * 
     * @param spAlg
     * @param graph
     */
//    public EvenFasterBetweenness(GraphInterface<Index> graph, BCCTreeBuilder bccTree){
//        init(graph, bccTree);
//        run();
//    }
    
    public EvenFasterBetweenness(GraphInterface<Index,BasicVertexInfo> graph, BCCalculatorInterface bc){
    	init(graph, bc);
    }
    
    public void init(GraphInterface<Index,BasicVertexInfo> graph, BCCalculatorInterface bc){
        m_numOfVertices = graph.getNumberOfVertices();
        m_BC = new double[m_numOfVertices];
        m_bc = bc;
    }
    
    public void run(){
        m_bc.runBCCAlgorithm();
		m_BC = m_bc.getCommunications();
    }
    
    public double getCentrality(int v){	
    	return m_BC[v];	
    }
    
    public double[] getCentralitites(){	return m_BC;	}
    
    public static double calculateSumGroup(BCCalculatorInterface bc, GraphInterface<Index,BasicVertexInfo> graph, Object[] group, AbstractExecution progress, double percentage) 
    {
    	double p = progress.getProgress();
    	
    	EvenFasterBetweenness fasterAlg = new EvenFasterBetweenness(graph, bc);
    	fasterAlg.run();
		double result = 0;
		for (Object member : group){
			if (member instanceof Index){
				result += fasterAlg.getCentrality(((Index)member).intValue());
			}
			else{
				LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "FasterBCAlgorithm", "calculateSumGroup", null);
				throw new IllegalArgumentException ("The given member is of invalid type: " + member.toString());
			}
    			
    		p += (1 / (double) group.length) * percentage;	
    		progress.setProgress(p);
    	}

   		return result;
    }   
    
    public int getNumberOfVertices(){	return m_numOfVertices;	}
    public int getComponentsCounter(){	
    	return 	m_bc.getComponents().length;
	}
    
    public int getMaxComponentSize(){
    	int maxSize = 0;
    	
    	for (Index[] component : m_bc.getComponents()) {
			int size = component.length;
			if (size > maxSize){
				maxSize = size;
			}
		}
    	return maxSize;	
    }
    
    public double getAvgComponentSize(){	
    	int sumSize = 0;
    	int counter = 0;
    	
    	for (Index[] component : m_bc.getComponents()) {
			int size = component.length;
			sumSize += size;
			counter ++;
		}
    	return sumSize/(double)counter;	
    }
    
    //@Rami
    public Index[][] getComponents(){ return m_bc.getComponents();}
    
    public Iterator<BiConnectedComponent> getSubGraphs(){
    	return m_bc.getSubGraphs();    	
    }

	@Override
	public GraphInterface<Index, ? extends BasicVertexInfo> getGraph() {
		return m_bc.getGraph();
	}

}