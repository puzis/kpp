package topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

import common.markerInterfaces.SerializableIterator;

public class GraphUtils
{
	public static <VertexType,VertexInfoStructure> GraphInterface<VertexType,VertexInfoStructure> reduceVertices(AbstractUndirectedGraph<VertexType,VertexInfoStructure> graph, final VertexType... vertices)
	{	
		return reduceVertices(graph,new SerializableIterator<VertexType>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			int i=0;
			@Override
			public boolean hasNext() {return i<vertices.length;}

			@Override
			public VertexType next() {return vertices[i++];}

			@Override
			public void remove() {}});
	}

	/**
	 * This method receives a graph and a set of vertices. 
	 * It returns a graph which is the intersection of the given graph and the given set of vertices. 
	 * @param graph
	 * @param _S
	 * @return
	 */
	public static <VertexType,VertexInfoStructure> GraphInterface<VertexType,VertexInfoStructure> reduceVertices(AbstractUndirectedGraph<VertexType,VertexInfoStructure> graph, Iterator<VertexType> _S)
	{
		GraphInterface<VertexType,VertexInfoStructure> reduced = new GraphAsHashMap<VertexType,VertexInfoStructure>();

		while (_S.hasNext())
		{
			VertexType s = _S.next();
			if (graph.isVertex(s))
			{
				reduced.addVertex(s);
				Iterable<? extends AbstractSimpleEdge<VertexType,VertexInfoStructure>> neighbors = graph.getOutgoingEdges(s);
				for (AbstractSimpleEdge<VertexType,VertexInfoStructure> e: neighbors )
				{
					VertexType n = e.getNeighbor(s);
					if (reduced.getVertex(n) != null)
					{
						if (graph.isEdge(s, n))
							reduced.addEdge(s, n, graph.getEdgeWeight(s, n));
						else if (graph.isEdge(n, s))
							reduced.addEdge(n, s, graph.getEdgeWeight(n, s));
					}
				}
			}
		}
		return reduced;
	}

	/**
	 * This method receives a graph and a set of edges. 
	 * It returns a graph which is the intersection of the given graph and the given set of edges. 
	 * NOTE: was not tested with directed graphs !
	 * @param graph
	 * @param _S
	 * @return
	 */
	public static <VertexType,VertexInfoStructure> GraphInterface<VertexType,VertexInfoStructure> reduceEdges(GraphInterface<VertexType,VertexInfoStructure> graph, Iterator<AbstractSimpleEdge<VertexType,VertexInfoStructure>> _S)
	{
		GraphInterface<VertexType,VertexInfoStructure> reduced = new GraphAsHashMap<VertexType,VertexInfoStructure>();

		while (_S.hasNext())
		{
			AbstractSimpleEdge<VertexType,VertexInfoStructure> e = _S.next();
			if (graph.isEdge(e))
			{
				VertexType v0 = e.getV0();
				VertexType v1 = e.getV1();

				if (reduced.getVertex(v0) == null)
					reduced.addVertex(v0, graph.getVertex(v0));
				if (reduced.getVertex(v1) == null)
					reduced.addVertex(v1, graph.getVertex(v1));

				reduced.addEdge(e, graph.getEdgeWeight(e));
			}
		}
		return reduced;
	}

	public static FastList<FastList<Object>> getEdgeComponents(GraphInterface<Object,Object> proxy)
	{
		FastList<FastList<Object>> connectedComponents = new FastList<FastList<Object>>();
		FastList<Object> seen = new FastList<Object>();
		FastList<Object> component = null;

		for(Object v :proxy.getVertices())
		{
			if (!seen.contains(v))
			{
				seen.add(v);
				component = new FastList<Object>();
				component.add(v);
				markComponent(proxy, v, seen, component);
				connectedComponents.add(component);
			}

		}
		return connectedComponents;
	}

	private static void markComponent(GraphInterface<Object,Object> proxy, Object v, FastList<Object> seen, FastList<Object> component)
	{
		Iterable<? extends AbstractSimpleEdge<Object,Object>> neighbors = proxy.getOutgoingEdges(v);
		for (AbstractSimpleEdge<Object,Object> e : neighbors )
		{
			Object w = e.getNeighbor(v);

			if (!seen.contains(w))
			{
				seen.add(w);
				component.add(w);
				markComponent(proxy, w, seen, component);
			}
		}
	}

	public static GraphInterface<Index,BasicVertexInfo> mapGraph(GraphInterface<Index,BasicVertexInfo> original, int maximalVertexIndex, int[] old_2_new, int[] new_2_old)
	{
		BasicVertexInfo vInfo; 
		GraphInterface<Index,BasicVertexInfo> map = new GraphAsHashMap<Index,BasicVertexInfo>();
		for (int new_i=0; new_i<new_2_old.length; new_i++){
			int old = new_2_old[new_i];
			if (VertexFactory.isVertexInfo(original.getVertex(Index.valueOf(old))))
				vInfo = new VertexInfo(new_i, Integer.toString(old), ((VertexInfo)original.getVertex(Index.valueOf(old))).getMultiplicity());
			else 
				vInfo = new BasicVertexInfo(new_i, Integer.toString(old));
			map.addVertex(Index.valueOf(new_i), vInfo);
		}

		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : original.getEdges()) {
			Index v0 = Index.valueOf(old_2_new[e.getV0().intValue()]);
			Index v1 = Index.valueOf(old_2_new[e.getV1().intValue()]);
			map.addEdge(v0, v1);
		}
		return map;
	}

	public static GraphInterface<Index,BasicVertexInfo> mapGraph(GraphInterface<Index,BasicVertexInfo> original, int maximalVertexIndex){
		int[] old_2_new = new int[maximalVertexIndex + 1];
		int[] new_2_old = new int[original.getNumberOfVertices()];
		int index = 0;
		for (Index v : original.getVertices()){
			old_2_new[v.intValue()] = index;
			new_2_old[index] = v.intValue();
			index++;
		}
		return mapGraph(original, maximalVertexIndex, old_2_new, new_2_old);
	}

	public static <VertexType,VertexInfoStructure> boolean compare(GraphInterface<VertexType,VertexInfoStructure> a, GraphInterface<VertexType,VertexInfoStructure> b){
		if (a.getNumberOfVertices() != b.getNumberOfVertices())
			return false;
		if (a.getNumberOfEdges() != b.getNumberOfEdges())
			return false;

		FastList<Object> edges_a = new FastList<Object>();
		for(Object e : a.getEdges() )
			edges_a.add(e);

		for(Object e: b.getEdges()){
			if (!edges_a.contains(e))
				return false;
		}
		return true;
	}

	public static FastList<Index> topologicalSort(AbstractDirectedGraph<Index,BasicVertexInfo> dag) throws Exception{

		FastList<Index> sList = new FastList<Index>();	/** Empty list that will contain the sorted elements. */
		FastList<Index> _Q = new FastList<Index>();		/** Queue of all nodes with no incoming edges */

		for (Index v : dag.getVertices()){
			if (dag.getInDegree(v)==0)
				_Q.addLast(v);
		}

		while (_Q.size()>0){
			Index v = _Q.removeFirst();
			sList.addLast(v);
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: dag.getOutgoingEdges(v)){
				dag.removeVertex(v);
				Index u = e.getNeighbor(v);
				if (dag.getInDegree(u)==0)
					_Q.addLast(u);
			}
		}
		if (dag.getNumberOfEdges()>0) throw new Exception("graph has at least one cycle");
		return sList;
	}

	public static AbstractDirectedGraph<Index,BasicVertexInfo> getDAG(GraphInterface<Index,BasicVertexInfo> original, Index t, AbsRoutingFunction rf){
		AbstractDirectedGraph<Index,BasicVertexInfo> diGraph = new DiGraphAsHashMap<Index,BasicVertexInfo>();
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : original.getEdges()) {
			Index u = e.getV0();
			Index v = e.getV1();
			/** consider routing direction for farmost vertices to appear first on the topological order */
			if (rf.routingProbability(u.intValue(), u.intValue(), v.intValue(), t.intValue()) > 0){
				if (!diGraph.isVertex(v)) diGraph.addVertex(v);
				if (!diGraph.isVertex(u)) diGraph.addVertex(u);
				diGraph.addEdge(u, v);
			}
			if (rf.routingProbability(v.intValue(), v.intValue(), u.intValue(), t.intValue()) > 0){
				if (!diGraph.isVertex(v)) diGraph.addVertex(v);
				if (!diGraph.isVertex(u)) diGraph.addVertex(u);
				diGraph.addEdge(v, u);
			}
		}

		return diGraph;
	}

	public static AbstractDirectedGraph<Index,BasicVertexInfo> copy(AbstractDirectedGraph<Index,BasicVertexInfo> g){
		AbstractDirectedGraph<Index,BasicVertexInfo> copyDag = new DiGraphAsHashMap<Index,BasicVertexInfo>();
		for (Index v : g.getVertices()){
			copyDag.addVertex(v);
		}
		for(AbstractSimpleEdge<Index,BasicVertexInfo> e : g.getEdges()) {
			copyDag.addEdge(e);
		}
		return copyDag;
	}

	/**
	 * A clique cannot contain vertices with multiplicity<>1. 
	 * @param graph
	 * @return
	 */
	public static <VertexType,VertexInfoStructure> boolean isClique(GraphInterface<VertexType,VertexInfoStructure> graph){
		boolean isClique = false;

		int m = graph.getNumberOfEdges();
		int n = graph.getNumberOfVertices();
		//		/** m = (n * (n-1))/2 */
		//		/** 1 = (2 * (2-1))/2 */
		//		/** 3 = (3 * (3-1))/2 */
		//		/** 6 = (4 * (4-1))/2 */
		//		/** 10 = (5 * (5-1))/2 */
		if (m == (n * (n-1))/2){
			if (graph.getSize()==n) 
				isClique = true;
		}
		return isClique;
	}

	/**
	 * Removes self loops from the graph
	 * Works only with GraphInterface<Index>
	 * @param graph
	 */
	public static void removeLoops(GraphInterface<Index,BasicVertexInfo> graph) {
		for(Index v : graph.getVertices()) {
			LinkedList<AbstractSimpleEdge<Index,BasicVertexInfo>> edges = new LinkedList<AbstractSimpleEdge<Index,BasicVertexInfo>>();
			edges.addAll((LinkedList<AbstractSimpleEdge<Index,BasicVertexInfo>>)graph.getOutgoingEdges(v));
			for(AbstractSimpleEdge<Index,BasicVertexInfo> e: edges) {
				if(e.getV0().equals(e.getV1())) {
					graph.removeEdge(e);
				}
			}
		}
	}
	/**
	 * If a hyperedge contains two or more vertices which participate
	 *  in only one hyperedge, these vertices are merged into a new
	 *  vertex with updated multiplicities
	 */
	public static <VertexType, VertexInfoStructure> HashMap<VertexType, ArrayList<VertexType>> mergeEquivalentVertices(HyperGraphInterface<VertexType, VertexInfoStructure> hgraph) {
		HashMap<VertexType, ArrayList<VertexType>> map = new HashMap<VertexType, ArrayList<VertexType>>();
		for(AbstractHyperEdge<VertexType, VertexInfoStructure> e : hgraph.getEdges()) {
			ArrayList<VertexType> singles = new ArrayList<VertexType>();
			int size = 0;
			for(VertexType v :e.getVertices()) {
				if(hgraph.getDegree(v)==1) {
					singles.add(v);
					size += hgraph.getVertex(v).getMultiplicity();
				}
			}
			//Now merge these singles into one vertex
			if(singles.size()>=2) {
				BasicVertexInfo vertex = hgraph.getVertex(singles.get(0));
				vertex.setMultiplicity(size);
				for(int i=1;i<singles.size();i++){
					BasicVertexInfo vInfo = hgraph.getVertex(singles.get(i));
					hgraph.removeVertex(singles.get(i));
					hgraph.addVertex(singles.get(i),vInfo);
					hgraph.getVertex(singles.get(i)).setMultiplicity(0);
				}
				
				//Enter into the map
				map.put(singles.get(0), singles);
			}
		}
		return map;
	}
}