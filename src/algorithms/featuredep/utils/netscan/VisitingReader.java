/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author bennyl
 */
public interface VisitingReader<T> {
    void read(T visitor) throws IOException, ParseException;
}
