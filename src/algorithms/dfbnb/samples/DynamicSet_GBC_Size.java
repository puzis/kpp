/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.dfbnb.samples;

import java.io.File;

import java.util.Arrays;

import javolution.util.Index;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.common.ServerConstants;
import server.common.ServerConstants.Centrality;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.CandidatesBasedAlgorithm;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.closeness.ClosenessAlgorithm;
import algorithms.centralityAlgorithms.closeness.IClosenessAlgorithm;
import algorithms.centralityAlgorithms.degree.GroupDegreeAlgorithm;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.dfbnb.AbsGroup;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.InfNodeUpdate;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 *
 * @author Ishay Peled, revised by Rami Puzis, Emily Rozenshine
 * @author moved to Algorithms project by Polina Zilberman, revised by Rami Puzis,
 * @deprecated use BiModalGroup insted 
 */
 public class DynamicSet_GBC_Size extends AbsGroup<Index>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient Network m_network;
    protected Centrality m_centrality; 
    protected transient DataWorkshop m_dw;    
    protected int m_groupVersion;
    protected int m_centralityVersion;
    protected double[] m_contributions;
    protected double m_centralityValue;
    
    
    protected DynamicSet_GBC_Size(DynamicSet_GBC_Size anotherInstance){
        super(anotherInstance);
        m_network = anotherInstance.m_network;
        m_centrality = anotherInstance.m_centrality;
        m_dw = anotherInstance.m_dw;
        m_centralityValue = anotherInstance.m_centralityValue;
        m_contributions = new double[anotherInstance.m_contributions.length];
        System.arraycopy(anotherInstance.m_contributions, 0, m_contributions, 0, m_contributions.length);
        m_groupVersion=anotherInstance.m_groupVersion;
        m_centralityVersion=anotherInstance.m_centralityVersion;
    }
    
    public DynamicSet_GBC_Size(Network network, Centrality centrality){
        super();
        //sets m_netowrk and m_contributions
        m_centrality = centrality;
        setNetwork(network, true);
        m_centralityValue = getUtility();
        m_groupVersion=1;
        m_centralityVersion=0;
    }
    
    public void setNetwork(Network network, boolean recreateDataWorkshop){
    	m_network = network;
        m_contributions = new double[network.getGraphSimple().getNumberOfVertices()];
        Arrays.fill(m_contributions, -1);
        if (recreateDataWorkshop && m_centrality==Centrality.Betweeness)
        	initDataWorkshop();
    }
    
    public void setDataWorkshop(DataWorkshop dw){
    	m_dw = dw;
    }
    
    public InfNodeUpdate<InfNode<Index>> createInfNodeUpdate(){
    	return new DynamicSet_GBC_NodeUpdate<Index>(m_network, m_dw);
    }
    
    @Override
    public void add(Index member) {
    	m_groupVersion++;
        m_groupMembers.add(member);
    }    

    public Double getUtility() {
    	if (m_groupVersion==m_centralityVersion){
    		return m_centralityValue;
    	}else{
//	        m_centralityValue = calculateUtility(m_groupMembers.toArray(new Object[0]));
    		m_centralityValue = calculateUtility(m_groupMembers.toArray());
	        m_centralityVersion = m_groupVersion;
	        Arrays.fill(m_contributions, -1);
	        return m_centralityValue;
    	}
    }

    
    public Double getCost() {
        return Double.valueOf(m_groupMembers.size());
    }

    public Double getUtilityOf(Index member) {    	
    	Double currentEvaluation=getUtility();
    	if(m_contributions[member.intValue()]==-1){
	        Object[] newVerticeArray = new Object[m_groupMembers.size()+1];
	        newVerticeArray = m_groupMembers.toArray(newVerticeArray);
	        newVerticeArray[newVerticeArray.length-1] = member;
	        Double newEvaluation = calculateUtility(newVerticeArray);
	        m_contributions[member.intValue()] = newEvaluation-currentEvaluation; 
    	}
		return m_contributions[member.intValue()];    	
    }

    public Double getCostOf(Index member) {
        return 1.0;
    }
    

	@Override
	public InfGroup<Index> clone() {
		return new DynamicSet_GBC_Size(this);
	}

    private void initDataWorkshop(){
    	try{
			File dwFile = new File(ServerConstants.DATA_DIR + m_network.getName() + ".dw");
			if (dwFile.exists()){
				m_dw = new DataWorkshop();
				try{
					m_dw.loadFromDisk(dwFile, new DummyProgress(), 1);
				}
				catch(Exception ex){
					LoggingManager.getInstance().writeSystem("Couldn't load " + m_network.getName() + ".dw.", "DynamicSet_GBC_Size", "DynamicSet_GBC_Size", ex);
				}
			}else{
				GraphInterface<Index,BasicVertexInfo> graph = m_network.getGraphSimple();
				AbsTrafficMatrix communicationWeights = null;
				if (graph != null){
					communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices());// MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
				
					try{
						m_dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, communicationWeights, true, new DummyProgress(), 1);
					}
					catch(Exception ex){
						LoggingManager.getInstance().writeSystem("An exception has occured while creating dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "DynamicSet_GBC_Size", "DynamicSet_GBC_Size", ex);
					}
				}
			}
		}
		catch(RuntimeException ex){
			LoggingManager.getInstance().writeSystem("The file " +  ServerConstants.DATA_DIR + m_network.getName() + ".dw doesn't exist.", "DynamicSet_GBC_Size", "DynamicSet_GBC_Size", null);
		}
    }

    private double calculateUtility(Object[] group){		
		switch(m_centrality){
		case Betweeness:
			return CandidatesBasedAlgorithm.calculateGB(m_dw, group, new DummyProgress(), 0.9999);
		case Degree:
			return GroupDegreeAlgorithm.calculateMixedGroupDegree(group, m_network.getGraphSimple(), new DummyProgress(), 0.9999);
		case Closeness:
			IClosenessAlgorithm cAlg = new ClosenessAlgorithm(m_network.getGraphSimple(), new DummyProgress(), 1);
			return cAlg.calculateMixedGroupCloseness(group, new DummyProgress(), 0.9999);
		default:
			throw new IllegalArgumentException("Invalid centrality type: " + m_centrality);
		}
    }
}

