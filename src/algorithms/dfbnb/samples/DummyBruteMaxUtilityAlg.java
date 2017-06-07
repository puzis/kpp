/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.dfbnb.samples;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import algorithms.dfbnb.BruteForceUtilitySearch;
import algorithms.dfbnb.InfGroup;
import algorithms.dfbnb.InfNode;
import algorithms.dfbnb.Node;

/**
 *
 * @author Matt
 */
public class DummyBruteMaxUtilityAlg {

    public static void main(String[] args) {


        int numOfCannidates = 3;
        GroupMocup groupMember = new GroupMocup();
        Vector<Integer> candidates = new Vector<Integer>();
        for (int i = 0; i < numOfCannidates; i++) {
            candidates.add(i);
        }
        InfNode<Integer> root = new Node<Integer>(candidates, groupMember);
        BruteForceUtilitySearch<Integer> maxSearch = new BruteForceUtilitySearch<Integer>(root);
        InfGroup<Integer> bestGroup = maxSearch.execute();
        System.out.println("Root: " + root);
        System.out.println("Best Node: " + bestGroup);
        System.out.println("Best Utility: " + bestGroup.getUtility());
        System.out.println("Best Node group size: " + bestGroup.getGroupSize());
        System.out.println("Best Node group members: ");
        for (int i = 0; i < bestGroup.getGroupSize(); i++) {
            try {
//                System.out.println("i "+i);
                System.out.print(bestGroup.getElementAt(i) + '\t');
                System.out.println();

            } catch (Exception ex) {
                Logger.getLogger(DummyBruteMaxUtilityAlg.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}

