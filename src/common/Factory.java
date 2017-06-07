/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author bennyl
 */
public interface Factory<T, A> {
    T construct(A argument);
}
