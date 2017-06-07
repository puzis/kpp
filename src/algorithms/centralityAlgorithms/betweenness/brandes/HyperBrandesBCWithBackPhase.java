package algorithms.centralityAlgorithms.betweenness.brandes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.DummyProgress;
import server.execution.AbstractExecution;
import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import topology.HyperGraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.GraphTraversalListener;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;
import algorithms.shortestPath.ShortestPathFactory;

public class HyperBrandesBCWithBackPhase implements GraphTraversalListener, BrandesInterface{
	private int BACK_COUNT = 0;
	private int FORWARD_COUNT = 0;
	private final int THRESHOLD = 6;
	protected HyperGraphInterface<Index,BasicVertexInfo> m_hgraph;
	protected int m_numOfVertices;
	protected ShortestPathAlgorithmInterface m_shortestPathAlg = null;
	protected AbstractExecution m_progress = new DummyProgress();
	protected double[] m_BC = null;
	protected AbsTrafficMatrix m_communicationWeights;


	/**
	 * Stack to store vertices in increasing distance from source
	 */
	protected FastList<Index> m_S;
	/**
	 * Predecessor list - Stores vertex, and edge multiplicity
	 * m_P.get(w) will store predecessors of w in a HashMap
	 * HashMap maps predecessor v to multiplicity of edge(v,w)
	 */
	protected ArrayList<HashSet<AbstractHyperEdge<Index, BasicVertexInfo>>> m_P;
	protected double [] m_sigma;
	protected double [] m_delta;
	protected double [] m_d;
	protected int m_currentSource;    
	Index[] m_sourceVertices = null;

	protected HashMap<AbstractHyperEdge<Index,BasicVertexInfo>
	,ArrayList<Index>> sources;
	protected HashMap<AbstractHyperEdge<Index, BasicVertexInfo>, Double> edgeDelta;

	public HyperBrandesBCWithBackPhase(HyperGraphInterface<Index,BasicVertexInfo> hgraph, AbsTrafficMatrix communicationWeights, Index[] sources) {
		m_hgraph = hgraph;
		// HyperBFS is always used as the SSSP Algorithm
		m_shortestPathAlg = ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlg.HYPERBFS, m_hgraph);
		m_shortestPathAlg.addListener(this);

		m_d = m_shortestPathAlg.getDistanceArray(); //Assume reference escaping !!! 
		m_numOfVertices = hgraph.getNumberOfVertices();

		setSources(sources);
		
		if(communicationWeights==null) {
			m_communicationWeights = new DefaultTrafficMatrix(m_numOfVertices);
		}
		else {
			m_communicationWeights = communicationWeights;
		}
	}
	public HyperBrandesBCWithBackPhase(HyperGraphInterface<Index,BasicVertexInfo> hgraph, AbsTrafficMatrix communicationWeights) {
		m_hgraph = hgraph;
		// HyperBFS is always used as the SSSP Algorithm
		m_shortestPathAlg = ShortestPathFactory.getShortestPathAlgorithm(ShortestPathAlg.HYPERBFS, m_hgraph);
		m_shortestPathAlg.addListener(this);

		m_d = m_shortestPathAlg.getDistanceArray(); //Assume reference escaping !!! 
		m_numOfVertices = hgraph.getNumberOfVertices();

		setSources(null);

		if(communicationWeights==null) {
			m_communicationWeights = new DefaultTrafficMatrix(m_numOfVertices);
		}
		else {
			m_communicationWeights = communicationWeights;
		}
	}

	@Override
	public void setSources(Index[] sources) {
		if(sources != null) {
    		m_sourceVertices = Arrays.copyOf(sources, sources.length);
    	}
    	else {
    		m_sourceVertices = new Index[m_numOfVertices];
        	Iterator<Index> itr = m_hgraph.getVertices().iterator();
        	for(int i=0;i<m_numOfVertices;i++){        	
        		m_sourceVertices[i]=itr.next();
    	    }
    	}		
	}
	
	@Override
	public void run() {
		algorithmInitialization(m_hgraph);    	
		int i=0;
		while(i<m_sourceVertices.length && !m_progress.isDone())
		{
			int s = m_sourceVertices[i].intValue(); i++;	

			/** Phase 1 HyperBFS */            
			beforeSingleSourceShortestPaths(s);        	
			singleSourceInitialization(s);
			m_currentSource = s;
			m_shortestPathAlg.run(s);
			afterSingleSourceShortestPaths(s);

			/**
			 * Phase 2 The algorithm heart
			 * S returns vertices in order of non-increasing distance from s
			 */
			beforeAccumulation(s);
			double dist_old = Double.NEGATIVE_INFINITY, dist;
			while(m_S.size() > 0 && !m_progress.isDone())
			{
				int w = ((Index) m_S.removeFirst()).intValue();
				dist = m_shortestPathAlg.getDistance(w);
				if(dist<dist_old) {
					for(Entry<AbstractHyperEdge<Index, BasicVertexInfo>, Double> e: edgeDelta.entrySet()) {
						for(Index v : sources.get(e.getKey())) {
							m_delta[v.intValue()] += m_sigma[v.intValue()]*e.getValue();
//							updateBackCount();
						}
					}
					edgeDelta.clear();
				}                
				dist_old = dist;
				/** update deltaDot */
				beforeSourceDependencyUpdate(s, w);
				for(AbstractHyperEdge<Index, BasicVertexInfo> hedge : m_P.get(w)) {
					int wMultiplicity; 
					if (VertexFactory.isVertexInfo(m_hgraph.getVertex(Index.valueOf(w))))
						wMultiplicity = ((VertexInfo)m_hgraph.getVertex(Index.valueOf(w))).getMultiplicity();
					else
						wMultiplicity = 1;
					if(hedge.getNumberOfVertices() > THRESHOLD && sources.get(hedge).size()>=2) {
						double old = 0.0;
						if(edgeDelta.containsKey(hedge))
							old = edgeDelta.get(hedge);						
						edgeDelta.put(hedge, old + m_delta[w]*((wMultiplicity)/m_sigma[w]));
//						updateBackCount();
					}
					else {
						for(Index v : sources.get(hedge)) {
							m_delta[v.intValue()] += m_delta[w] * ((m_sigma[v.intValue()] * wMultiplicity) / m_sigma[w]);
//							updateBackCount();
						}
					}					
				}				
				afterSourceDependencyUpdate(s, w);
			}
			afterAccumulation(s);  

			double p = m_progress.getProgress();
			p += (1 / (double) m_numOfVertices);
			m_progress.setProgress(p);                      		
		}    	
	}

	@Override
	public double getCentrality(int v) {
		return m_BC[v];
	}

	@Override
	public double[] getCentralitites() {
		return m_BC;
	}


	public void algorithmInitialization(HyperGraphInterface<Index,BasicVertexInfo> hgraph) {
		m_numOfVertices = hgraph.getNumberOfVertices();
		m_BC = new double[m_numOfVertices];		
	}


	public void beforeSingleSourceShortestPaths(int s) {
		// Do nothing		
	}


	public void singleSourceInitialization(int s) {
		/** S is a stack of all vertices in a non increasing order from s. */
		m_S = new FastList<Index>();

		m_P = new ArrayList<HashSet<AbstractHyperEdge<Index, BasicVertexInfo>>> (m_numOfVertices); 
		initP(m_P);            
		edgeDelta = new HashMap<AbstractHyperEdge<Index, BasicVertexInfo>, Double>();
		sources = new HashMap<AbstractHyperEdge<Index, BasicVertexInfo>, ArrayList<Index>> ();
		m_sigma = new double[m_numOfVertices]; 	
		Arrays.fill(m_sigma, 0); 
		m_sigma[s] = 1;
		m_delta = new double [m_numOfVertices];	
		Arrays.fill(m_delta, 0.0);
	}

	private void initP(ArrayList<HashSet<AbstractHyperEdge<Index, BasicVertexInfo>>> _P)
	{
		for (int i = 0; i<m_numOfVertices; i++) {
			_P.add(i, new HashSet<AbstractHyperEdge<Index, BasicVertexInfo>>());
		}
	} 
	public void afterSingleSourceShortestPaths(int s) {
		//Do  nothing
	}


	public void beforeAccumulation(int s) {
		// Do Nothing		
	}


	public void beforeSourceDependencyUpdate(int s, int w) {
		m_delta[w] += m_communicationWeights.getWeight(s, w);	
	}


	public void sourceDependencyUpdate(int s, int v, int w, int mult) {
		// wmultiplicity = multiplicity of the vertex w

		//double  eCapacity = ((EdgeInfo<Index>)m_graph.getEdgeWeight(Index.valueOf(v), Index.valueOf(w))).getMultiplicity();
		int wMultiplicity; 
		if (VertexFactory.isVertexInfo(m_hgraph.getVertex(Index.valueOf(w))))
			wMultiplicity = ((VertexInfo)m_hgraph.getVertex(Index.valueOf(w))).getMultiplicity();
		else
			wMultiplicity = 1;

		m_delta[v] += m_delta[w] * ((m_sigma[v] * wMultiplicity * mult) / m_sigma[w]);
	}


	public void afterSourceDependencyUpdate(int s, int w) {
		// Do nothing		
	}


	public void afterAccumulation(int s) {
		for (int i=0;i<m_BC.length && !m_progress.isDone();i++){
			m_BC[i] += m_delta[i];
		}		
	}

	@Override
	public void beforeExpand(Index v) {
		m_S.addFirst(v);
	}

	@Override
	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge) {
		m_sigma[w.intValue()]=0;
		m_P.get(w.intValue()).clear();
		updateSigma(v,w);
		updatePredecessors(v,w,hedge);
//		updateForwardCount();
	}

	private void updatePredecessors(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge) {
		m_P.get(w.intValue()).add(hedge);

		if(sources.get(hedge)==null) {
			ArrayList<Index> s = new ArrayList<Index>();
			s.add(v);
			sources.put(hedge, s);
		}
		else {
			if(!sources.get(hedge).contains(v))
				sources.get(hedge).add(v);
		}


		//		HashMap<Index,Integer> predecessors = m_P.get(w.intValue());
		//		if(predecessors.containsKey(v)) {
		//			Integer cur = predecessors.get(v);
		//			predecessors.put(v, cur+1); //TODO multiplicity of hyperedge
		//		}
		//		else {
		//			predecessors.put(v, 1);
		//		}
	}


	private void updateSigma(Index v, Index w) {
		//double eCapacity = ((EdgeInfo<Index>)m_graph.getEdgeWeight(v, w)).getMultiplicity();
		/**
		 * If there are multiple edges between two vertices, they will be traversed separately
		 */
		double eCapacity = 1;
		int wMultiplicity;
		if (VertexFactory.isVertexInfo(m_hgraph.getVertex(w)))
			wMultiplicity = ((VertexInfo)m_hgraph.getVertex(w)).getMultiplicity();
		else
			wMultiplicity = 1;

		m_sigma[w.intValue()] += wMultiplicity * eCapacity * m_sigma[v.intValue()];
	}


	@Override
	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist) {
//		updateForwardCount();
		if (m_d[w.intValue()] == dist){
			updateSigma(v,w);
			updatePredecessors(v,w, hedge);
			
		}		
	}

	@Override
	public void afterExpand(Index v) {
		// Do nothing

	}

	@Override
	public boolean isExpandable(Index v) {
		return true;
	}
	
	private void updateBackCount() {
		BACK_COUNT++;
	}
	private void updateForwardCount(){
		FORWARD_COUNT++;
	}
	public int getBackCount() {
		return BACK_COUNT;
	}
	public int getForwardCount() {
		return FORWARD_COUNT;
	}
	
	
	@Override
	public HyperGraphInterface<Index, ? extends BasicVertexInfo> getGraph() {
		return m_hgraph;
	}
	@Override
	public ShortestPathAlgorithmInterface getShortestPathAlgorithm() {
		return m_shortestPathAlg;
	}
	
}
