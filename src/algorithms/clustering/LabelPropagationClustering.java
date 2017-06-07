package algorithms.clustering;

import javolution.util.FastList;
import javolution.util.Index;
import topology.AbstractUndirectedGraph;
import topology.AbstractSimpleEdge;
import topology.BasicVertexInfo;

//TODO This code is not tested
public class LabelPropagationClustering extends EdgeCutClustering<Index,BasicVertexInfo> {

	protected int m_maxIter;

	protected int[] m_labels;
	protected int[] m_newLabels;
	protected Index[] m_vertices;

	public LabelPropagationClustering(AbstractUndirectedGraph<Index,BasicVertexInfo> graph) {
		super(graph);
		m_labels = new int[graph.getNumberOfVertices()];
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
	protected void initLables() {
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
	protected void doClustering(){		
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
		
		for(Index i: m_vertices){
			applyLabel(i, m_labels[i.intValue()]);
		}
	}

	
	public int getMaxIterations() {
		return m_maxIter;
	}

	public void setMaxIterations(int iter) {
		m_maxIter = iter;
	}
}
