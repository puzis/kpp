package algorithms.dfbnb;

import java.io.Serializable;
import java.util.Vector;


/**
 * @author Ishay Peled
 * @description
 * This interface defines a virtual tree iterator, which is defined as follow:
 * The root node will consist of all candidates (provided as a parameter) and an empty group of members
 * The right child is defined as a node consisting of the candidate list except for one chosen by an internal
 * procedure, and will be added to member group
 * The left child is defined the same as the right one, but without adding to the member group
 */
public interface InfNode<E> extends Serializable, Cloneable{
    void accept(E candidate);                       //Returns the right child of the virtual node
    void reject(E candidate);                       //Returns the left child of the virtual node
    Vector<E> getCandidates();                       //Returns the candidates of the current node
    InfGroup<E> getGroup();                                 //Returns the group members of the current node
    boolean equals(InfNode<E> another);            //Returns normal equility between two instances
    InfNode<E> clone();
    int getID();
    double getH();
    double getG();
    double getF();
    void setH(double hVal);
    void setG(double gVal);
}
