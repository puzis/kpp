package server.dfbnb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import javolution.util.Index;
import server.common.Network;
import server.common.ServerConstants.Centrality;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.dfbnb.AbsGroup;
import algorithms.dfbnb.CertifiedUtilitySearch_ol;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.LDSNode;
import algorithms.dfbnb.Node;
import algorithms.dfbnb.elementOrder.HighUtilityFirst;
import algorithms.dfbnb.elementOrder.HighUtilityPerCostFirst;
import algorithms.dfbnb.elementOrder.LowCostFirst;
import algorithms.dfbnb.elementOrder.NoOrder;
import algorithms.dfbnb.heuristics.CostLowerBound1;
import algorithms.dfbnb.heuristics.CostLowerBound2;
import algorithms.dfbnb.heuristics.TrivialCostLowerBound;
import algorithms.dfbnb.heuristics.TrivialUtilityUpperBound;
import algorithms.dfbnb.heuristics.UtilityUpperBound1;
import algorithms.dfbnb.heuristics.UtilityUpperBound2;
import algorithms.dfbnb.openList.F_OpenList;
import algorithms.dfbnb.openList.Greedy;
import algorithms.dfbnb.openList.LDS_OpenList;
import algorithms.dfbnb.openList.LIFO_OpenList;
import algorithms.dfbnb.openList.OptimisticSearchOpenlist;
import algorithms.dfbnb.openList.Potential_OpenList;
import algorithms.dfbnb.openList.Potential_h_OpenList;
import algorithms.dfbnb.openList.Random_OpenList;
import algorithms.dfbnb.openList.WeightedOpenlist;
import algorithms.dfbnb.samples.BiModalGroup;
import algorithms.dfbnb.samples.DynamicSet_GBC_BC;
import algorithms.dfbnb.samples.DynamicSet_GBC_Size;



/**
 * 
 * @author ishayp
 */
public class Dfbnb implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	public static HashMap<Index, Dfbnb> networks = new HashMap<Index, Dfbnb>();
	
	
	private CertifiedUtilitySearch_ol<Index> m_implementation;
	
	/**
	 * @deprecated
	 */
	public Dfbnb(
			Centrality centrality, 
			int setType, 
			int elementOrdering, 
			int utilityHeuristic, 
			int costHeuristic, 
			Index[] candidates,
			Network network, 
			Double budget, 
			int openList) {
				
		Vector<Index> vectorCandidates = new Vector<Index>();
		for (int i = 0; i < candidates.length; i++) {
			vectorCandidates.add(candidates[i]);
		}
		
		AbsGroup<Index> group = ceateGroupImpl(network, centrality, setType);
		InfNode<Index> root = this.createRootNode(vectorCandidates,group,openList);						
		this.m_implementation = this.createImplementation(root,elementOrdering,utilityHeuristic,costHeuristic,budget,openList);
	}
	
	public Dfbnb(
			BasicSetInterface utilityGroup,
			BasicSetInterface costGroup, 
			int elementOrdering,
			int utilityHeuristic, 
			int costHeuristic, 
			Index[] candidates,
			Network network, 
			double budget, 
			int openList) {
		Vector<Index> vectorCandidates = new Vector<Index>();
		for (int i = 0; i < candidates.length; i++) {
			vectorCandidates.add(candidates[i]);
		}		
		AbsGroup<Index> group = ceateGroupImpl(utilityGroup,costGroup);
		InfNode<Index> root = this.createRootNode(vectorCandidates,group,openList);						
		this.m_implementation = this.createImplementation(root,elementOrdering,utilityHeuristic,costHeuristic,budget,openList);
	}



	public void setNetwork(Network network, boolean createDataWorkshop){
		InfGroup<Index> group = m_implementation.getBestGroup();
		if (group instanceof DynamicSet_GBC_Size)
		{
			((DynamicSet_GBC_Size)group).setNetwork(network, createDataWorkshop);
			m_implementation.updateNodes(((DynamicSet_GBC_Size)group).createInfNodeUpdate());
		}
	}
	
	/**
	 * Analyze the heuristics after a complete solution
	 * @param networkIndex
	 *
	public Pair<double[], double[]> analyzeHeuristic(Network network) {
		double bestUtility = this.m_implementation.getBestGroupUtility();
		InfGroup<Index> bestGroup = this.m_implementation.getBestGroup();
		// Can go over all subsets of the best group, but will take time. Better to sample more solutions
		Vector<Index> members = new Vector<Index>();
		Index firstMember = bestGroup.getElementAt(0);
		members.add(firstMember);
		
		Vector<Index> candidates = new Vector<Index>();
		for(int i=0;i<network.getGraph().getNumberOfVertices();i++){
			candidates.add(i, Index.valueOf(i));
		}
		candidates.remove(firstMember);
		
		InfNode<Index> node = this.createNode(network, candidates, members);
		double[] h = new double[bestGroup.getGroupSize()-1];
		double[] optimalH = new double[bestGroup.getGroupSize()-1];
		h[0] = this.m_implementation.getUtilityH(node);
		optimalH[0] = bestUtility - this.m_implementation.getUtilityG(node);
		for(int i=1;i<bestGroup.getGroupSize()-1;i++){
			node.accept(bestGroup.getElementAt(i));
			h[i]=this.m_implementation.getUtilityH(node);
			optimalH[i] = bestUtility - this.m_implementation.getUtilityG(node);
		}
		return new Pair<double[], double[]>(h,optimalH);		
	}
	*/
	
	/**
	 * Create an instance of the search implementation initialized and set for execute()
	 * @param root The root node of the search
	 * @return an instance of the search implementation
	 */
	private CertifiedUtilitySearch_ol<Index> createImplementation(InfNode<Index> root, int elementOrdering, int utilityHeuristic, int costHeuristic, Double initialBudget,int openListType) {		
		CertifiedUtilitySearch_ol<Index> implementation = new CertifiedUtilitySearch_ol<Index>(root, initialBudget);
		switch(elementOrdering){
		case 0: implementation.setElementOrderingStrategy(new NoOrder<Index>()); break;
		case 1: implementation.setElementOrderingStrategy(new HighUtilityFirst<Index>());break;
		case 2: implementation.setElementOrderingStrategy(new HighUtilityPerCostFirst<Index>());break;
		case 3: implementation.setElementOrderingStrategy(new LowCostFirst<Index>());break;
		}
		
		switch(utilityHeuristic){
		case 0: implementation.setUtilityHeuristic(new TrivialUtilityUpperBound<Index>());break;
		case 1: implementation.setUtilityHeuristic(new UtilityUpperBound1<Index>());break;
		case 2: implementation.setUtilityHeuristic(new UtilityUpperBound2<Index>());break;
		}

		switch(costHeuristic){
		case 0: implementation.setCostHeuristic(new TrivialCostLowerBound<Index>());break;
		case 1: implementation.setCostHeuristic(new CostLowerBound1<Index>());break;
		case 2: implementation.setCostHeuristic(new CostLowerBound2<Index>());break;
		}
		
		switch(openListType){
		case 0: implementation.setOpenList(new F_OpenList<Index>(implementation));break;
		case 1: implementation.setOpenList(new LIFO_OpenList<Index>());break;
		case 2: implementation.setOpenList(new Potential_OpenList<Index>(implementation));break;
		case 3: implementation.setOpenList(new LDS_OpenList<Index>(implementation,0));break;
		case 4: implementation.setOpenList(new LDS_OpenList<Index>(implementation,1));break;
		case 5: implementation.setOpenList(new Potential_h_OpenList<Index>(implementation));break;
		case 6: implementation.setOpenList(new Random_OpenList<Index>());break;
		case 7: implementation.setOpenList(new Greedy<Index>(implementation));break;
		case 8: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,1));break;
		case 9: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,1.1));break;
		case 10: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,1.3));break;
		case 11: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,1.5));break;
		case 12: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,1.75));break;
		case 13: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,2));break;
		case 14: implementation.setOpenList(new WeightedOpenlist<Index>(implementation,0.78));break;
		case 15: implementation.setOpenList(new OptimisticSearchOpenlist<Index>(implementation,0.78));break;
		case 16: implementation.setOpenList(new OptimisticSearchOpenlist<Index>(implementation,1.3));break;		
		}
		return implementation;
	}

	/**
	 * Creates a root node for the search
	 * @param network The searched network
	 * @param vectorCandidates A list of possible nodes to select from
	 * @return The root of the search tree
	 */
	private InfNode<Index> createRootNode(Vector<Index> candidates, InfGroup<Index> group, int openListType) {
		Vector<Index> groupMembers = new Vector<Index>();
		for(Index member : groupMembers){
			group.add(member);
		}
		InfNode<Index> node=null;		
		if (openListType>=3)
			node=new LDSNode<Index>(candidates,group, 0, Double.POSITIVE_INFINITY);
		else
			node = new Node<Index>(candidates, group, 0, Double.POSITIVE_INFINITY);
		return node;		
	}

	private AbsGroup<Index> ceateGroupImpl(Network network,
			Centrality centrality, int setType) {
		AbsGroup<Index> group=null;
		switch (setType){
			case 0:	group = new DynamicSet_GBC_Size(network,centrality);break;
			case 1: group = new DynamicSet_GBC_BC(network,centrality);break;			
		}
		return group;
	}
	
	private AbsGroup<Index> ceateGroupImpl(BasicSetInterface utilityGroup, BasicSetInterface costGroup) {
		return new BiModalGroup(utilityGroup,costGroup);
	}
	
	public double getCertificate() {
		return m_implementation.getCertificate();
	}

	public int execute() {
		m_implementation.execute();
		return m_implementation.getNodeCheckCounter();
	}

	public int execute(int numberOfSteps) {
		m_implementation.execute(numberOfSteps);
		return m_implementation.getNodeCheckCounter();
	}

	public boolean isSearchDone() {
		return m_implementation.isSearchDone();
	}

	public Index[] getBestSolution() {
		InfGroup<Index> bestGroup = m_implementation.getBestGroup();
		return groupToArray(bestGroup);
	}

	public Index[] getCurrentSearchNode() {
		InfGroup<Index> currentGroup = m_implementation
				.getCurrentGroup();
		return groupToArray(currentGroup);
	}

	private Index[] groupToArray(InfGroup<Index> group) {
		Index[] groupMembers = new Index[group.getGroupSize()];
		for (int i = 0; i < group.getGroupSize(); i++) {
			try {
				groupMembers[i] = group.getElementAt(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return groupMembers;
	}

	/**
	 * Remove redundant references
	 */
	public void clear(){
		this.m_implementation.clear();
		this.m_implementation=null;
	}
	
	public double getBestUtility() {
		return m_implementation.getBestGroup().getUtility();
	}
	public double getBestCost() {
		return m_implementation.getBestGroup().getCost();
	}

	public double getCurrentUtility() {
		return m_implementation.getCurrentGroup().getUtility();
	}

	public double getCurrentCost() {
		return m_implementation.getCurrentGroup().getCost();
	}
	
	public int getCountUnprunedExpandedNodes() {
		return m_implementation.getCountUnprunedExpandedNodes();
	}

	public int getCountAcceptedNodes() {
		return m_implementation.getCountAcceptedNodes();
	}

	public int getCountRejectedNodes() {
		return m_implementation.getCountRejectedNodes();
	} 
	
	public int getOpenListSize() {
		return m_implementation.getOpenListSize();
	}	
	
	
}