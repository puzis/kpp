package algorithms.dfbnb.samples;

import server.common.Network;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.InfNodeUpdate;

/**
 * 
 * @deprecated use BiModalGroup insted 
 */
public class DynamicSet_GBC_NodeUpdate<E> implements InfNodeUpdate<InfNode<E>> {

	private Network m_network;
	private DataWorkshop m_dw;
	
	public DynamicSet_GBC_NodeUpdate(Network network, DataWorkshop dw){
		m_network = network;
		m_dw = dw;
	}
	
	@Override
	public void update(InfNode<E> node) {
		InfGroup<E> group = node.getGroup();
		if (group instanceof DynamicSet_GBC_Size){
			((DynamicSet_GBC_Size)group).setNetwork(m_network, false);
			((DynamicSet_GBC_Size)group).setDataWorkshop(m_dw);
		}
	}

}
