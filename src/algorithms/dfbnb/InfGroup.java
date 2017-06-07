package algorithms.dfbnb;

import java.io.Serializable;


public interface InfGroup<E> extends Cloneable, Serializable{
    void add(E member);
    boolean equals(InfGroup<E> another);
    Double getUtility();
    Double getCost();
    Double getUtilityOf(E member);
    Double getCostOf(E member);
    E getElementAt(int position);
    int getGroupSize();
    InfGroup<E> clone();   
}
