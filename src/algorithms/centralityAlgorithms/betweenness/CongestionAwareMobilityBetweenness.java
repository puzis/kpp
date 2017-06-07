package algorithms.centralityAlgorithms.betweenness;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

public class CongestionAwareMobilityBetweenness {
	
	private AbsTrafficMatrix m_tm;
	private GraphInterface<Index,BasicVertexInfo> m_graph;
	private int m_steps;
	private double m_fearFactor;
	private double[] m_tmf;
	private double[] m_emissions;

	public CongestionAwareMobilityBetweenness(GraphInterface<Index,BasicVertexInfo> graph,
			AbsTrafficMatrix tm, int steps, double fearFactor,
			double cf0, double cf1, double cf2, double cf3, double cf4) {
		m_graph = graph;
		m_tm = tm;
		m_steps = steps;
		m_fearFactor = fearFactor;
		m_tmf = new double[5];
		m_tmf[0] = cf0;
		m_tmf[1] = cf1;
		m_tmf[2] = cf2;
		m_tmf[3] = cf3;
		m_tmf[4] = cf4;
		m_emissions = null;
	}

	public CongestionAwareMobilityBetweenness(GraphInterface<Index,BasicVertexInfo> graph,
			AbsTrafficMatrix tm, int steps, double fearFactor,
			double cf0, double cf1, double cf2, double cf3, double cf4, double[] emissions) {
		this(graph, tm, steps, fearFactor,cf0, cf1, cf2, cf3, cf4);
		
		if (emissions==null) m_emissions = null;
		else if (emissions.length==0) m_emissions = null;
		else {
			m_emissions = new double[emissions.length];
			for (int i = 0; i < emissions.length; i++){
				m_emissions[i] = ((Double)emissions[i]).doubleValue();
			}
		}
	}
	public void run(){
		if (m_graph==null)
			throw new RuntimeException (new CloneNotSupportedException());
		
		//@pre: all links contain EdgeInfo with four parameters: fftime, capacity, flow, latency
		//fftime and capacity are static properties 
		//flow and latency change as the algorithm is executed.
		//initial flow values are taken into account
		//initial latency values are discarded.
		
		System.out.println("vertices#: "+ m_graph.getNumberOfVertices());
		System.out.println("steps: "+ m_steps);
		
		for (int i=0;i<m_steps;i++){
			System.out.println(i);
			//1. set latency
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : m_graph.getEdges()){
				//TODO: optimization: avoid string parsing
				EdgeInfo<Index,BasicVertexInfo> eInfo = m_graph.getEdgeWeight(e);
				double fftime = Double.parseDouble(eInfo.getLabel("fftime"));
				double capacity = Double.parseDouble(eInfo.getLabel("capacity"));
				double flow = Double.parseDouble(eInfo.getLabel("flow"));
				double latency = getTime(fftime, capacity, flow);
				eInfo.setLatency(latency);
			}
			
			//2. compute edge betweenness and update flow
			DataWorkshop dw = new DataWorkshop(ShortestPathAlg.DIJKSTRA, m_graph, m_tm, false, new DummyProgress(),1);
			for (AbstractSimpleEdge<Index,BasicVertexInfo> e : m_graph.getEdges()){
				EdgeInfo<Index,BasicVertexInfo> eInfo = m_graph.getEdgeWeight(e);
				double flow = Double.parseDouble(eInfo.getLabel("flow"));
				double bc = dw.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
				flow += bc/m_steps;
				eInfo.setLabel("flow", String.valueOf(flow));
			}
		}
		for (AbstractSimpleEdge<Index,BasicVertexInfo> e : m_graph.getEdges()){
			//TODO: optimization: avoid string parsing
			EdgeInfo<Index,BasicVertexInfo> eInfo = m_graph.getEdgeWeight(e);
			double fftime = Double.parseDouble(eInfo.getLabel("fftime"));
			double capacity = Double.parseDouble(eInfo.getLabel("capacity"));
			double flow = Double.parseDouble(eInfo.getLabel("flow"));
			double time = getTime(fftime, capacity, flow);
			double latency;
			if (m_emissions==null){
				latency = time;
			}else{
				double length = Double.parseDouble(eInfo.getLabel("length"));
				double speed = length / (time/60+.0000000000001);
				speed = Math.floor(speed/10);
				if (speed<0)  speed=0;
				if (speed>=m_emissions.length) speed=m_emissions.length-1;
				latency = m_emissions[(int)speed]*length;
			}
			eInfo.setLatency(latency);
		}

	}
	
	private double getTime(double fftime, double capacity, double flow){
		double overload = flow/capacity;
		double slowdown = 0; 
		for (int i=0;i<m_tmf.length;i++)
			slowdown+= m_tmf[i]*Math.pow(overload, i);
		double time = fftime*slowdown;
		time = Math.round(time*10)/10.0;
		return time*m_fearFactor;
	}
}