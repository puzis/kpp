package algorithms.centralityAlgorithms.betweenness.brandes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import server.execution.AbstractExecution;
import topology.AbstractHyperEdge;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphInterface;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.shortestPath.GraphTraversalListener;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;
import algorithms.shortestPath.ShortestPathFactory;

import common.IndexFastList;

/**
 * Algorithm for computing betweenness centrality as proposed by Brandes (2001).
 * Computes betweenness INCLUDING end vertices.
 * The class is implemented as a collection of major computation steps for 
 * easier sub-classing.
 */
public class BrandesBC implements GraphTraversalListener, BrandesBCAlgListener, BrandesInterface
{
	protected GraphInterface<Index,BasicVertexInfo> m_graph = null;
    protected int m_numOfVertices = 0;
    protected ShortestPathAlgorithmInterface m_shortestPathAlg = null;
    protected AbstractExecution m_progress;
    protected double m_percentage;
        
    protected double [] m_BC = null;

    protected FastList<Index> m_S;
	protected ArrayList<IndexFastList> m_P;
	protected double [] m_sigma;
	protected double [] m_delta;
	protected double [] m_d;
	protected int m_currentSource;
    
	Index[] m_sourceVertices = null;
	
	protected Set<Integer> m_group = null;
	protected double m_GBC = 0;
	
	/** When calling the empty constructor, it is mandatory to call init method before running the algorithm. */
	public BrandesBC(){}
	
    public BrandesBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, AbstractExecution progress, double percentage){
        init(spAlg, graph, null, progress, percentage, null);
    }
    public BrandesBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, Index[] sources, AbstractExecution progress, double percentage){
        init(spAlg, graph, sources, progress, percentage, null);
    }
    public BrandesBC(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, Index[] sources, AbstractExecution progress, Index[] group, double percentage){
        init(spAlg, graph, sources, progress, percentage, group);
    }

    public BrandesBC(GraphInterface<Index,BasicVertexInfo> g) {
		this(ShortestPathAlgorithmInterface.DEFAULT,g, null, 1);
	}
	public double getCentrality(int v){	
    	return m_BC[v];	
    }

    public double[] getCentralitites(){	return m_BC;	}
    
	public void setSources(Index[] sources) {
        if (sources==null){
            m_sourceVertices = new Index[m_numOfVertices];
	        Iterator<Index> itr = m_graph.getVertices().iterator();
	        for(int i=0;i<m_numOfVertices;i++){        	
	        	m_sourceVertices[i]=itr.next();
	        }
        }else{
        	m_sourceVertices=Arrays.copyOf(sources,sources.length);
        }
		return;
	}
    
    public void init(ShortestPathAlg spAlg, GraphInterface<Index,BasicVertexInfo> graph, Index[] sources, AbstractExecution progress, double percentage, Index[] group) 
    {    	
    	m_shortestPathAlg = ShortestPathFactory.getShortestPathAlgorithm(spAlg, graph);
    	m_shortestPathAlg.addListener(this);
    	if (!progress.isDone())	m_d = m_shortestPathAlg.getDistanceArray(); //Assume reference escaping !!! 
    	m_graph = graph;
        m_numOfVertices = graph.getNumberOfVertices();
        m_progress = progress;
        m_percentage = percentage;
        setSources(sources);
        
    	m_group = new FastSet<Integer>();
        if (group!=null){
        	for (Index idxv : group)
        		m_group.add(idxv.intValue());
        }
        m_GBC = 0;
    }
    
    public void run(){
    	algorithmInitialization(m_graph);    	
        //Iterator<Index> vertices = m_graph.getVertices();
        //while(vertices.hasNext() && !m_progress.isDone())
    	int i=0;
    	while(i<m_sourceVertices.length && !m_progress.isDone())
        {
        	//int s = vertices.next().intValue();
    		int s = m_sourceVertices[i].intValue(); i++;	
        	
        	/** Phase 1 BFS */            
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
            while(m_S.size() > 0 && !m_progress.isDone())
            {
                int w = ((Index) m_S.removeFirst()).intValue();                
                /** update deltaDot */
                beforeSourceDependencyUpdate(s, w);                                
                FastList<Index> predecessors = m_P.get(w);
                for (FastList.Node<Index> vNode = predecessors.head(), end = predecessors.tail(); (vNode = vNode.getNext()) != end && !m_progress.isDone();)
                {
              	   int v = ((Index)vNode.getValue()).intValue();              	   
              	   sourceDependencyUpdate(s, v, w);              	   
                }
                afterSourceDependencyUpdate(s, w);
            }
            afterAccumulation(s);  

            double p = m_progress.getProgress();
            p += (1 / (double) m_numOfVertices) * m_percentage;
            m_progress.setProgress(p);                      		
        }    	
    }
    
    
    private void initP(ArrayList<IndexFastList> _P)
	{
		for (int i = 0; i<m_numOfVertices; i++)
			_P.add(i, new IndexFastList());
	}    
    
    protected void updateSigma(Index v, Index w){
        double eCapacity = ((EdgeInfo<Index,BasicVertexInfo>)m_graph.getEdgeWeight(v, w)).getMultiplicity();
        int wMultiplicity;
        if (VertexFactory.isVertexInfo( m_graph.getVertex(w)))
        	wMultiplicity = ((VertexInfo)m_graph.getVertex(w)).getMultiplicity();
        else 
        	wMultiplicity = 1 ;
        m_sigma[w.intValue()] += wMultiplicity * eCapacity * m_sigma[v.intValue()];
	}
	protected void updatePredecessors(Index v, Index w){
        m_P.get(w.intValue()).add(v);		
	}
	
	@Override
	public void beforeExpand(Index v){
		m_S.addFirst(v);
	}
	
	@Override
	public void vertexDiscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge){
		m_sigma[w.intValue()]=0;
		m_P.get(w.intValue()).clear();
    	updateSigma(v,w);
    	updatePredecessors(v,w);
	}
	
	@Override
	public void vertexRediscovered(Index v, Index w, AbstractHyperEdge<Index, BasicVertexInfo> hedge, double dist){		
        if (m_d[w.intValue()] == dist){
        	updateSigma(v,w);
        	updatePredecessors(v,w);
        }		
	}
	
	@Override
	public void afterExpand(Index v){		
	}
        

	@Override
	public void afterSingleSourceShortestPaths(int s) {				
	}

	@Override
	public void beforeSingleSourceShortestPaths(int s) {
	}

	@Override
	public void afterAccumulation(int s) {
        for (int i=0;i<m_BC.length && !m_progress.isDone();i++){
        	m_BC[i] += m_delta[i];
        }
	}

	@Override
	public void beforeAccumulation(int s) {
	}

	@Override
	public void singleSourceInitialization(int s) {
    	/** S is a list of all vertices in a non increasing order from s. */
        m_S = new FastList<Index>();

        /** P is an array of sets. P[w] is a set of predecessors of w on a way from s. */
        /** NOTE: not to forget to initialize each FastList when it is needed. */
        m_P = new ArrayList<IndexFastList> (m_numOfVertices); 
        initP(m_P);            
        m_sigma = new double[m_numOfVertices]; 	
        Arrays.fill(m_sigma, 0); 
        m_sigma[s] = 1;
        m_delta = new double [m_numOfVertices];	
        Arrays.fill(m_delta, 0.0);					
	}

	@Override
	public void algorithmInitialization(GraphInterface<Index,BasicVertexInfo>  graph) {
        m_numOfVertices = graph.getNumberOfVertices();
        m_BC = new double[m_numOfVertices];		
	}

	@Override
	public void beforeSourceDependencyUpdate(int s, int w) {
        m_delta[w] += (s==w)? 0 : 1;		
	}

	@Override
	public void afterSourceDependencyUpdate(int s, int w) {
        //Group calculations    
        if (m_group.contains(w))
            //Dana's group betweenness
            m_GBC+=m_delta[w];
	}

	@Override
	public void sourceDependencyUpdate(int s, int v, int w) {
		double  eCapacity = ((EdgeInfo<Index,BasicVertexInfo>)m_graph.getEdgeWeight(Index.valueOf(v), Index.valueOf(w))).getMultiplicity();
		int wMultiplicity; 
		if (VertexFactory.isVertexInfo( m_graph.getVertex(Index.valueOf(w))))
			wMultiplicity = ((VertexInfo)m_graph.getVertex(Index.valueOf(w))).getMultiplicity();
		else
			wMultiplicity = 1;

        if (!m_group.contains(w))
    		m_delta[v] += m_delta[w] * ((m_sigma[v] * wMultiplicity * eCapacity) / m_sigma[w]);
        else                             
            m_delta[v] += 0;  //customization point
	}

	public GraphInterface<Index,BasicVertexInfo> getGraph() {
		return m_graph;
	}
	
	
	@Override
	public boolean isExpandable(Index v) {
		return true;
	}

	@Override
	public ShortestPathAlgorithmInterface getShortestPathAlgorithm() {
		return m_shortestPathAlg;
	}
	
	
	public double getGBC(){
		return m_GBC;
	}
}