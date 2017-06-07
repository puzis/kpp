package algorithms.dfbnb;

import java.util.Vector;


public class Node<E> implements InfNode<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int instanceCount = 0;
	
    protected Vector<E> m_candidates;
    protected InfGroup<E> m_group;   
    protected int m_instanceID;
    
    protected double m_h = 0;
    protected double m_g = 0;
    
    
    public Node(Vector<E> candidates, InfGroup<E> groupMember) {
    	m_instanceID = instanceCount++;
        m_candidates = candidates;
        m_group = groupMember;
    }
    
    public Node(Vector<E> candidates, InfGroup<E> groupMember, double m_g2, double m_h2) {
    	m_instanceID = instanceCount++;
        m_candidates = candidates;
        m_group = groupMember;
        m_g = m_g2;
        m_h = m_h2;
    }
    
     

    @Override
    public Vector<E> getCandidates() {
        return m_candidates;
    }

    @Override
    public InfGroup<E> getGroup() {
        return m_group;
    }
    
    @Override
    public void accept(E candidate) {
        m_candidates.remove(candidate);
        m_group.add(candidate);
    }
    
    
    @Override
    public boolean equals(InfNode<E> another){
        return ((m_group.equals(another.getGroup())) && (m_candidates.equals(another.getCandidates())));
    }
    
    @Override
    public String toString(){
		return "<" + m_group.toString() + "," + m_candidates.toString() + ">";    	
    }
    
    @SuppressWarnings("unchecked")
	public Node<E> clone(){
    	Vector<E> candidates = (Vector<E>)m_candidates.clone();
    	InfGroup<E> group = m_group.clone();
    	return new Node<E>(candidates,group,m_g,m_h);
    }

	@Override
	public void reject(E candidate) {
	    m_candidates.remove(candidate);
	}

	@Override
	public int getID() {
		return m_instanceID;
	}

	@Override
	public double getF()
	{
		return m_h+m_g;
	}

	@Override
	public double getG()
	{
		return m_g;
	}

	@Override
	public double getH()
	{
		return m_h;
	}

	@Override
	public void setG(double val)
	{
		m_g = val;
	}

	@Override
	public void setH(double val)
	{
		m_h = val;
	}
}