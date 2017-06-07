/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.cache;

/**
 *
 * @author bennyl
 */
public interface CachePath {

    public abstract int length();

    public abstract boolean equals(Object other, int length);

    public abstract int hashCode(int length);
    
}
