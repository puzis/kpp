/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils;

import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

/**
 *
 * @author bennyl
 */
public class TrafficMatrixUtils {
    public static void normalize(AbsTrafficMatrix tm){
        double sum = 0;
        for (int i=0; i<tm.getDimensions(); i++){
            for (int j=0; j<tm.getDimensions(); j++){
                sum += tm.getWeight(i, j);
            }
        }
        
        for (int i=0; i<tm.getDimensions(); i++){
            for (int j=0; j<tm.getDimensions(); j++){
                tm.setWeight(i, j, tm.getWeight(i, j)/sum);
            }
        }
    }
}
