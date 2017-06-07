package algorithms.clustering;


import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;
import topology.GraphInterface;


/***
 * This algorithm can be used to detect the natural community structure of a network 
 * in near linear time. Basic implementation is provided of the algorithm described in 
 *    Usha Nandini Raghavan, Reka Albert, and Soundar Kumara, 
 *    Near linear time algorithm to detect community structures in large-scale networks,
 *    PHYSICAL REVIEW E 76, 036106 (2007)
 *   
 * @author Rami Puzis
 */
public class LabelPropagation {
	
	protected int m_maxIter;

	protected int[] m_labels;
	protected int[] m_newLabels;
	protected GraphInterface<Index,BasicVertexInfo> m_graph;
	protected Index[] m_vertices;
	
	public LabelPropagation(GraphInterface<Index,BasicVertexInfo> graph){
		m_graph = graph;
		m_labels = new int[graph.getNumberOfVertices()];
		//m_newLabels = new int[graph.getNumberOfVertices()];
		m_newLabels = m_labels; //avoid oscillations and speedup conversion
		m_vertices = new Index[graph.getNumberOfVertices()];
		m_maxIter = 1000;

		//Initialize by assigning unique integer labels to all nodes.
		initLables();
	}

	/**
	 * Assigns unique integer labels to all vertices and shuffles the vertices 
	 * Vertex index is used as a label.
	 */
	public void initLables() {
		for (int i=0;i<m_labels.length;i++){
			m_labels[i] = i;
			m_vertices[i] = Index.valueOf(i);
		}
	}
	
	/**
	 * Repeats the label propagation process until the stop condition is met. 
	 * Default stop condition is met when no labels are changed in an iteration. 
	 * In order to avoid infinite oscillations, the number of iterations is limited by
	 * using setMaxIterations(..) method. Initially the max number of iterations 
	 * defaults to 1000.  
	 */
	public void run(){		
		boolean wasChange = true;
		
		for(int i =0;i<m_maxIter & wasChange;i++){
			//go through all vertices in random order.
			common.ArrayUtils.shuffle(m_vertices);

			wasChange = false;
			for (int k=0;k<m_vertices.length;k++){
				Index v = m_vertices[k];
				
				if (m_graph.getDegree(v)==0){
					m_newLabels[v.intValue()]=m_labels[v.intValue()];
					continue;
				}
				
				//determine the most frequent label on v's neighbors
				int [][] labelCounts = new int[m_graph.getNumberOfVertices()][];
				int maxCount =-1;
				FastList<int[]>[] radixArray = new FastList[m_graph.getInDegree(v)];
				for (int j=0;j<radixArray.length;j++)
					radixArray[j] = new FastList<int[]>();
				for (AbstractSimpleEdge<Index,BasicVertexInfo> e :m_graph.getIncomingEdges(v)){
					int w = e.getNeighbor(v).intValue();
					int labelW = m_labels[w];
					if (labelCounts[labelW]==null)
						//add a new label counter
						//start from zero for convenience of radix indexing
						labelCounts[labelW]=new int[]{labelW,0}; 
					else
						//increase the count.
						labelCounts[labelW][1]+=1;
					if (labelCounts[labelW][1]>0){
						//label exists more than once
						radixArray[labelCounts[labelW][1]-1].remove(labelCounts[labelW]);
					}
					radixArray[labelCounts[labelW][1]].add(labelCounts[labelW]);
					if (labelCounts[labelW][1]>maxCount)
						maxCount = labelCounts[labelW][1];
				}
				
				//ties are broken uniformly at random
				int rnd = (int)(Math.random()* radixArray[maxCount].size());
				int randomMostFrequentLabel = radixArray[maxCount].get(rnd)[0];
				if (randomMostFrequentLabel!=m_labels[v.intValue()])
					wasChange=true;
				m_newLabels[v.intValue()]=randomMostFrequentLabel;
			}
			
			//swap label arrays to reduce the number of allocations
			int[] tmplabels = m_labels;
			m_labels = m_newLabels;
			m_newLabels = tmplabels;
		}
	}
	
	public FastMap<Integer, FastSet<Index>> getClusters(){
		FastMap<Integer, FastSet<Index>> result = new FastMap<Integer, FastSet<Index>>();
		for (int i=0;i<m_labels.length;i++){
			Integer label = m_labels[i];
			if (!result.containsKey(label)){
				result.put(label,new FastSet<Index>());
			}
			result.get(label).add(Index.valueOf(i));	
		}
		return result;
	}
	public int getMaxIterations() {
		return m_maxIter;
	}

	public void setMaxIterations(int iter) {
		m_maxIter = iter;
	}

}
