/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.dfbnb;


import algorithms.dfbnb.elementOrder.ElementOrderingStrategyInterface;
import algorithms.dfbnb.elementOrder.NoOrder;
import algorithms.dfbnb.heuristics.CostHeuristicFunction;
import algorithms.dfbnb.heuristics.TrivialCostLowerBound;
import algorithms.dfbnb.heuristics.TrivialUtilityUpperBound;
import algorithms.dfbnb.heuristics.UtilityHeuristicFunction;


/**
 *
 * @author Matt
 */
public abstract class AbsSearch<E> implements InfSearch<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected InfNode<E> m_root;
    protected InfGroup<E> m_bestGroup;
    protected int m_countExpandedNodes;
    protected int m_countUnprunedExpandedNodes;  
    protected int m_countAcceptedNodes;
    protected int m_countRejectedNodes;  
    protected ElementOrderingStrategyInterface<E> m_nodeOrdering;    
    protected UtilityHeuristicFunction<E> m_utilityHeuristic;
    protected CostHeuristicFunction<E> m_costHeuristic;


    public AbsSearch(InfNode<E> root) {
    	this(root, new NoOrder<E>(), new TrivialUtilityUpperBound<E>(), new TrivialCostLowerBound<E>());    
    }

    public AbsSearch(InfNode<E> root, UtilityHeuristicFunction<E> utilityh, CostHeuristicFunction<E>  costh) {
    	this(root, new NoOrder<E>(), utilityh,costh);    
    }
    
    public AbsSearch(InfNode<E> root, ElementOrderingStrategyInterface<E> nodeOrdering){
    	this(root, nodeOrdering, new TrivialUtilityUpperBound<E>(), new TrivialCostLowerBound<E>());    
    }

  	public AbsSearch(InfNode<E> root, ElementOrderingStrategyInterface<E> nodeOrdering, UtilityHeuristicFunction<E> utilityh, CostHeuristicFunction<E>  costh) {
        m_root = root;
        m_nodeOrdering = nodeOrdering;
        m_utilityHeuristic = utilityh;
        m_costHeuristic = costh;
        m_bestGroup = m_root.getGroup();
        m_countExpandedNodes = 0;
        m_countUnprunedExpandedNodes=0;  
        m_countAcceptedNodes=0;
        m_countRejectedNodes=0;
    }
  	
  	
  	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#setElementOrderingStrategy(algorithms.dfbnb.elementOrder.ElementOrderingStrategyInterface)
	 */
  	public void setElementOrderingStrategy(ElementOrderingStrategyInterface<E> nodeOrdering){
  		m_nodeOrdering = nodeOrdering;
  	}
  	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#setUtilityHeuristic(algorithms.dfbnb.heuristics.UtilityHeuristicFunction)
	 */
  	public void setUtilityHeuristic(UtilityHeuristicFunction<E> h){
  		m_utilityHeuristic = h;
  	}
  	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#setCostHeuristic(algorithms.dfbnb.heuristics.CostHeuristicFunction)
	 */
  	public void setCostHeuristic(CostHeuristicFunction<E> h){
  		m_costHeuristic = h;
  	}    

    /* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#offerGroup(algorithms.dfbnb.InfGroup)
	 */
    public void offerGroup(InfGroup<E> group) {
    	if (isFeasible(group) & isBest(group)){
    		m_bestGroup = group.clone();
    	}
    }
    
	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#getBestGroup()
	 */
	public InfGroup<E> getBestGroup(){
		return m_bestGroup;		
	}	
	
	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#getNodeCheckCounter()
	 */
	public int getNodeCheckCounter(){
		//FIXME: what is this shit?
		int res = m_countExpandedNodes;
		m_countExpandedNodes = 0;
		return res;
	}
	
    /* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#execute()
	 */
    public InfGroup<E> execute() {
        while (!isSearchDone()) {
            expandNextNode();
        }
        return m_bestGroup;
    }

    /* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#execute(int)
	 */
    public InfGroup<E> execute(int maxNumOfNodes) {
        for (int i = 0; i < maxNumOfNodes && !isSearchDone(); i++) {
            expandNextNode();
        }
        return m_bestGroup;
    }
    
    /* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#expandNextNode()
	 */
    public void expandNextNode() {
    	InfNode<E> currentNode = this.pullNode(); 
    	this.expandNode(currentNode);
    }

    /**
     * Expands the given node
     * @param currentNode The node to expand.
     */
	private void expandNode(InfNode<E> currentNode) {
		m_countExpandedNodes++;    
        //System.out.println(currentNode);
        if (!isPruned(currentNode)){
        	m_countUnprunedExpandedNodes++;

            if (currentNode.getCandidates().size()>0){
            	E best = m_nodeOrdering.findBestCandidate(currentNode);
            	
	            InfNode<E> reject = currentNode.clone();
	            reject.reject(best);
	            if (!isPruned(reject)) {
	                offerNode(reject, false);
	                m_countRejectedNodes++;
	            }
	            InfNode<E> accept = currentNode; //no current node clone is required
	            accept.accept(best);
	            if (!isPruned(accept)) {
	                if (isBest(currentNode.getGroup())) {
	                    offerGroup(currentNode.getGroup());
	                }            
	                offerNode(accept, true);
	                m_countAcceptedNodes++;
	            }
        	}
        }
	}

	
	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#isSearchDone()
	 */
	public abstract boolean isSearchDone();
	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#getCurrentGroup()
	 */
	public abstract InfGroup<E> getCurrentGroup();	
	protected abstract boolean offerNode(InfNode<E> node, boolean accept);
	protected abstract InfNode<E> pullNode();
	/* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#isFeasible(algorithms.dfbnb.InfGroup)
	 */
	public abstract boolean isFeasible(InfGroup<E> group);
    /* (non-Javadoc)
	 * @see algorithms.dfbnb.InfSearch#isBest(algorithms.dfbnb.InfGroup)
	 */
    public abstract boolean isBest(InfGroup<E> group);
    protected abstract boolean isPruned(InfNode<E> treeNode);

	public int getCountUnprunedExpandedNodes() {
		return m_countUnprunedExpandedNodes;
	}

	public int getCountAcceptedNodes() {
		return m_countAcceptedNodes;
	}

	public int getCountRejectedNodes() {
		return m_countRejectedNodes;
	} 
	   

	/**
	 * Clear used memory
	 */
	@Override
	public void clear(){
	    this.m_root=null;
	    this.m_bestGroup=null;
	    this.m_nodeOrdering=null;    
	    this.m_utilityHeuristic=null;
	    this.m_costHeuristic=null;		
	}
}

