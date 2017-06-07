package algorithms.dfbnb;

import java.util.Iterator;

import algorithms.dfbnb.openList.InfOpenList;
import algorithms.dfbnb.openList.LIFO_OpenList;

/**
 * Assumptions: cost and utility are in range: [0,Inf) 
 * 
 * @param <E> the type of elements in the evaluated groups
 */
public class BudgetedUtilitySearch_ol<E>
		extends AbsSearch<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Double m_budget;
	protected double m_bestGroupCost;
	protected double m_bestGroupUtility;
	protected InfOpenList<InfNode<E>> m_openList;

	public BudgetedUtilitySearch_ol(InfNode<E> root, double budget) {
		super(root);
		m_budget=budget;
		m_bestGroupCost = Double.POSITIVE_INFINITY;
		m_bestGroupUtility = 0;
		m_openList = new LIFO_OpenList<E>();
	}
	
	public void setOpenList(InfOpenList<InfNode<E>> openList){
		m_openList=openList;
		m_openList.insert(m_root, true);
	}


	@Override
	public boolean isFeasible(InfGroup<E> group) {		
		return (group.getCost() <= m_budget);
	}

	@Override
	public boolean isBest(InfGroup<E> group) {
		InfGroup<E> currentGroup = group;
		Double currentCost = currentGroup.getCost();
		Double currentUtility = currentGroup.getUtility();
		if ((currentUtility > m_bestGroupUtility)
				|| ((currentUtility == m_bestGroupUtility) && (currentCost < m_bestGroupCost))) {
			return true;
		}
		return false;
	}

	@Override
	public void offerGroup(InfGroup<E> group) {
		if (isFeasible(group) & isBest(group)){
			m_bestGroup = group.clone();
			Double currentCost = group.getCost();
			Double currentUtility = group.getUtility();
			m_bestGroupUtility = currentUtility;
			m_bestGroupCost = currentCost; 
			
			//revise the open list
			m_openList.updateBestUtility(m_bestGroupUtility);
		}
	}

	public double getBestGroupUtility(){
		return m_bestGroupUtility;
	}

	@Override
	protected boolean isPruned(InfNode<E> treeNode) {
		boolean result = (treeNode.getGroup().getCost() > m_budget);
		result |= (m_bestGroupUtility > getUtilityF(treeNode));
		result |= (m_budget < getCostF(treeNode));
		return result;
	}
	public double getUtilityG(InfNode<E> node) {
		return node.getGroup().getUtility();
	}

	public double getUtilityF(InfNode<E> node) {
		return getUtilityG(node) + getUtilityH(node);
	}

	public double getUtilityH(InfNode<E> node){
		return m_utilityHeuristic.h(m_budget, node);
	}
	
	public double getCostG(InfNode<E> node){
		return node.getGroup().getCost();
	}
	
	public double getCostF(InfNode<E> node){
		return getCostG(node) + getCostH(node);		
	}
	
	public double getCostH(InfNode<E> node){
		return m_costHeuristic.h(m_bestGroupUtility, node);		
	}

	@Override
	protected boolean offerNode(InfNode<E> node, boolean accept) {
		m_openList.insert(node, accept);
		return true;
	}

	@Override
	protected InfNode<E> pullNode() {
		return m_openList.getNext();
	}

	@Override
	public boolean isSearchDone() {
		return m_openList.isEmpty();
//		double bestUtilInOpen=Double.NEGATIVE_INFINITY;
//		for (InfNode<E> node : m_openList)
//		{
//			double currUtility=getUtilityF(node);
//			if (currUtility>bestUtilInOpen)
//				bestUtilInOpen=currUtility;
//		}
//		return (m_openList.isEmpty() | m_bestGroupUtility>bestUtilInOpen);
	}

	@Override
	public InfGroup<E> getCurrentGroup() {
		InfNode<E> m_currentNode=m_openList.peek();
		return m_currentNode.getGroup();
	}

	public int getOpenListSize() {
		return m_openList.size();
	}
	
	public void updateNodes(InfNodeUpdate<InfNode<E>> update){
		Iterator<InfNode<E>> iterator = m_openList.iterator();
		while (iterator.hasNext())
			update.update(iterator.next());
	}
	/**
	 * Clear used memory
	 */
	@Override
	public void clear(){
	    super.clear();
	    this.m_openList.clear();
	}	
}
