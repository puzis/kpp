/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.dfbnb.samples;

import javolution.util.Index;

import server.common.DummyProgress;
import server.common.Network;
import server.common.ServerConstants.Centrality;
import algorithms.bcc.BCCAlgorithm;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.degree.GroupDegreeAlgorithm;
import algorithms.centralityAlgorithms.randomWalkBetweeness.GroupRandomWalkBetweeness;
import algorithms.dfbnb.InfGroup;

/**
 * @deprecated use BiModalGroup insted 
 * @author Ishay Peled, revised by Rami Puzis, Emily Rozenshine
 */
public class DynamicSet_GBC_BC extends DynamicSet_GBC_Size{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected DynamicSet_GBC_BC(DynamicSet_GBC_BC anotherInstance){
        super(anotherInstance);
    }
    
    public DynamicSet_GBC_BC(Network network, Centrality centrality){
        super(network,centrality);
    }

    public Double getCost() {
    	Index[] verticeArray = new Index[m_groupMembers.size()];
    	m_groupMembers.toArray(verticeArray);    	
        return getSumCentrality(m_centrality, verticeArray);
    }


    public Double getCostOf(Index member) {
    	Object[] verticeArray = new Object[1];
    	verticeArray[0] = member;
        return getSumCentrality(m_centrality, verticeArray);
    }
    
    public Network getNetwork(){
        return m_network;
    }

	@Override
	public InfGroup<Index> clone() {
		return new DynamicSet_GBC_BC(this);
	}
        
	private double getSumCentrality(Centrality centrality, Object [] group){
		switch(centrality)
		{
		case Betweeness:
			return CandidatesBasedAlgorithm.calculateSumGroup(m_dw, group, new DummyProgress(), 1);
		case Degree:
			return GroupDegreeAlgorithm.calculateSumGroup(group, m_network.getGraphSimple(), new DummyProgress(), 1);
		case Closeness:
			double res = 0;
			IClosenessAlgorithm closenessAlgorithm = new ClosenessAlgorithm(m_network.getGraphSimple(), new DummyProgress(), 1);
			for (int i = 0; i < group.length; i++){
				res += closenessAlgorithm.getCloseness(((Index)group[i]).intValue());
			}
			return res;
		case RandomWalk:
			return GroupRandomWalkBetweeness.calculateSumGroup(m_network.getGraphSimple(), group, new DummyProgress(), 1);
		case FasterBC:
			BCCAlgorithm bccAlg = new BCCAlgorithm(m_network.getGraphSimple());
			BetweennessCalculator ebcc = new BetweennessCalculator(bccAlg);
			return EvenFasterBetweenness.calculateSumGroup(ebcc, m_network.getGraphSimple(), group, new DummyProgress(), 1);
		default:
			throw new IllegalArgumentException("Invalid centrality type: " + centrality);
		}
	}
}
