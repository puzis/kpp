/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.dfbnb;


import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ishay Peled
 */
public abstract class AbsGroup<E> implements InfGroup<E>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector<E> m_groupMembers;

    public AbsGroup(){
        m_groupMembers  = new Vector<E>();
    }
    
    public AbsGroup(AbsGroup<E> toCopy){
        m_groupMembers  = new Vector<E>();
        for (int i=0;i<toCopy.getGroupSize();i++){
                m_groupMembers.add(toCopy.getElementAt(i));
        }
    }
    
    public void add(E member) {
        m_groupMembers.add(member);
    }

    public abstract Double getUtility();
    public abstract Double getCost();
    public abstract Double getUtilityOf(E member);
    public abstract Double getCostOf(E member);

    @Override
    public abstract InfGroup<E> clone();
    
    
    public E getElementAt(int position){
        return m_groupMembers.elementAt(position);
    }

    public int getGroupSize() {
        return m_groupMembers.size();
    }
    
    public boolean equals(InfGroup<E> another){
        boolean equal = true;
        if (m_groupMembers.size() != another.getGroupSize())
            return false;
        for (int i=0;i<m_groupMembers.size() && equal;i++){
            try {
                if (m_groupMembers.elementAt(i) != another.getElementAt(i)){
                    equal = false;
                }
            } 
            catch (Exception ex) {
                Logger.getLogger(AbsGroup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return equal;
    }
    
    @Override
    public String toString(){
    	return m_groupMembers.toString();
    }
    
}
