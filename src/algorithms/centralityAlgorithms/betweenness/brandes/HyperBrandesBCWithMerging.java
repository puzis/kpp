package algorithms.centralityAlgorithms.betweenness.brandes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.SourceVersion;

import javolution.util.FastSet;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphFactory;
import topology.GraphUtils;
import topology.HyperGraphInterface;
import topology.SerializableGraphRepresentation;
import algorithms.centralityAlgorithms.betweenness.CentralityAlgorithmInterface;
import algorithms.centralityAlgorithms.tm.MultiplicityTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 * Computes Betweenness Centrality of all vertices of a hypergraph.
 * All vertices should have multiplicity 1 initially.
 * Merges vertices of having hyperdegree 1
 * @author manish
 *
 */
public class HyperBrandesBCWithMerging implements BrandesInterface{
	HyperGraphInterface<Index, BasicVertexInfo> hgraph;
	HashMap<Index,ArrayList<Index>> map;
	HashMap<Index, Index> m_oldToNewMap;
	BrandesInterface hbrandes;
	MultiplicityTrafficMatrix tm;
	double[] m_BC = null;
	boolean usingBack;
	Index[] m_sourceVertices = null;
	boolean merged = false;
	boolean m_BCMapped = false;
	/**
	 * Constructor should be followed by a call to merge()
	 * @param hgraph2
	 * @param usingBack
	 * @param sources
	 */
	public HyperBrandesBCWithMerging(
			HyperGraphInterface<Index, BasicVertexInfo> hgraph2, boolean usingBack, Index[] sources) {
		super();
		SerializableGraphRepresentation sgraph = new SerializableGraphRepresentation(hgraph2);
		this.hgraph = GraphFactory.copy(sgraph);
		this.usingBack = usingBack;
		if(sources != null)
			m_sourceVertices = Arrays.copyOf(sources, sources.length);
		merged = false;
	}
	
	/**
	 * Constructor should be followed by a call to merge()
	 * @param hgraph2
	 * @param usingBack
	 */
	public HyperBrandesBCWithMerging(
			HyperGraphInterface<Index, BasicVertexInfo> hgraph2, boolean usingBack) {
		super();
		SerializableGraphRepresentation sgraph = new SerializableGraphRepresentation(hgraph2);
		this.hgraph = GraphFactory.copy(sgraph);
		this.usingBack = usingBack;
		merged = false;
	}

	public void merge() {
		map = GraphUtils.mergeEquivalentVertices(hgraph);
		
		// Maintain the back map
		m_oldToNewMap = new HashMap<Index, Index>();
		for(Entry<Index,ArrayList<Index>> e : map.entrySet()) {
			Index newVertex = e.getKey();
			for(Index v : e.getValue()) {
				m_oldToNewMap.put(v, newVertex);
			}
		}
		
		tm = new MultiplicityTrafficMatrix(hgraph);
		if(usingBack)
			hbrandes = new HyperBrandesBCWithBackPhase(hgraph, tm, m_sourceVertices);
		else
			hbrandes = new HyperBrandesBC(hgraph, tm, m_sourceVertices);
		merged = true;
	}
	
	/**
	 * run() should be preceded by a call to merge() 
	 */
	@Override
	public void run() {
		if(!merged) {
			throw new IllegalStateException("Tried to run without merging");			
		}		
		hbrandes.run();
		m_BCMapped = false;
	}
	
	public void setSources(Index[] sources) {
		if(!merged) {
			throw new IllegalStateException("Tried to run without merging");			
		}
		
		Set<Index> existingSources = new FastSet<Index>();
		for (Index s : sources){
			if (m_oldToNewMap.containsKey(s))
				existingSources.add(m_oldToNewMap.get(s));
			else
				existingSources.add(s);
		}
		
		hbrandes.setSources(existingSources.toArray(new Index[existingSources.size()]));
	}

	private void mapCentralityValues() {
		m_BC = new double[hgraph.getNumberOfVertices()];
		for(int i=0;i<hgraph.getNumberOfVertices();i++) {
			m_BC[i] += hbrandes.getCentrality(i);			
		}
		for(Entry<Index,ArrayList<Index>> e : map.entrySet()) {
			int s = e.getValue().size();
			double val = (m_BC[e.getKey().intValue()])/s;
			for(Index v: e.getValue()) {
				m_BC[v.intValue()] = val;
			}			
		}
	}
	@Override
	public double getCentrality(int v) {
		if(!m_BCMapped) {
			mapCentralityValues();
			m_BCMapped = true;
		}
		return m_BC[v];		
	}

	@Override
	public double[] getCentralitites() {
		if(!m_BCMapped) {
			mapCentralityValues();
			m_BCMapped = true;
		}
		return m_BC;
	}
	
	public int getBackCount() {
		if(usingBack)
			return ((HyperBrandesBCWithBackPhase)hbrandes).getBackCount();
		else
			return ((HyperBrandesBC)hbrandes).getBackCount();
	}
	
	public int getForwardCount() {
		if(usingBack)
			return ((HyperBrandesBCWithBackPhase)hbrandes).getForwardCount();
		else
			return ((HyperBrandesBC)hbrandes).getForwardCount();		
	}

	@Override
	public HyperGraphInterface<Index, ? extends BasicVertexInfo> getGraph() {
		return hgraph;
	}
	@Override
	public ShortestPathAlgorithmInterface getShortestPathAlgorithm() {
		return hbrandes.getShortestPathAlgorithm();
	}
	
}
