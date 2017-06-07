/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.centralityAlgorithms.rbc;

import java.util.Arrays;


import javolution.util.FastList;
import javolution.util.Index;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.rbc.routingFunction.AbsRoutingFunction;

/**
 *
 * @author Ishay Peled
 */
public class SRBCAlgorithm extends AbsBetweenessAlgorithm<FastList<Index>>{
    private VRBCAlgorithm m_vrbc;

    public SRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw) {
        super(G,cw);
        m_vrbc = new StatefullVRBCAlgorithm(G, cw);
    }
    
    public SRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw,int cachetype) {
        super(G,cw);
        m_vrbc = new StatefullVRBCAlgorithm(G, cw,cachetype);
    }
    
    public SRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsRoutingFunction routingFunction, AbsTrafficMatrix cw){
        super(G, routingFunction, cw);
        m_vrbc = new StatefullVRBCAlgorithm(G, routingFunction, cw, false, false);
    }
    
    public SRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G, AbsTrafficMatrix cw, VRBCAlgorithm vrbcAlg) {
        super(G,cw);
        m_vrbc=vrbcAlg;
    }
    public SRBCAlgorithm(GraphInterface<Index,BasicVertexInfo> G,AbsRoutingFunction routingFunction, AbsTrafficMatrix cw, VRBCAlgorithm vrbcAlg) {
        super(G,routingFunction,cw);
        m_vrbc=vrbcAlg;
    }
    
    
    public double getDelta(Index s, FastList<Index> sequence, Index t){
        double result=1.0;
        if (sequence.isEmpty()) return result;
        if (sequence.size()==1) 
        	result = m_vrbc.getDelta(s, sequence.get(0), t);
        else {
	        double [] cached = new double [sequence.size()];
	        cached[0] = m_vrbc.getDelta(s, sequence.get(0), t);
	        
	        for (int i=0;i<sequence.size()-1;i++){
	        	cached[i+1]=m_vrbc.getDelta(sequence.get(i), sequence.get(i+1), t);
	        }
	        Arrays.sort(cached);
	        for (int i=0;i<sequence.size();i++){
	        	result*=cached[i];
	        }
        }
        return result;
    }

	public VRBCAlgorithm getM_vrbc() {
		return m_vrbc;
	}
}
