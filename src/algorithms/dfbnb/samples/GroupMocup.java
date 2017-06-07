/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.dfbnb.samples;

import algorithms.dfbnb.AbsGroup;



/**
 *
 * @author Ishay Peled
 */
public class GroupMocup extends AbsGroup<Integer>{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GroupMocup(){
        super();
    }
    
    public GroupMocup(GroupMocup toCopy){
        super(toCopy);
    }

    public Double getUtility() {
        return ((double)m_groupMembers.size()+1) * 5;
    }

    public Double getCost() {
        return ((double)m_groupMembers.size());
    }

    public Double getUtilityOf(Integer member) {
        return 5.0;
    }

    public Double getCostOf(Integer member) {
        return 6.0;
    }

	@Override
	public GroupMocup clone() {
		return new GroupMocup(this);
	}
}
