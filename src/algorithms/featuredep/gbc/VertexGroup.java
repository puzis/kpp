/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.gbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author bennyl
 */
public class VertexGroup implements Cloneable, Iterable<VertexGroup.VertexAndSampleRate> {

    private ArrayList<VertexAndSampleRate> group;
    private boolean orderDirty = true;

    public VertexGroup(int initialCapacity) {
        group = new ArrayList<VertexAndSampleRate>(initialCapacity);
    }

    public void addVertex(int vertix, double sampleRate) {
        group.add(new VertexAndSampleRate(vertix, sampleRate));
        orderDirty = true;
    }

    public void orderBySampleRates() {
        if (orderDirty) {
            Collections.sort(group);
            orderDirty = false;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(group.toArray());
    }

    
    
    /**
     * @return the order of the vertexes in this group, filling the given array
     * or creating a new array if the given array is null.
     */
    public int[] getOrder(int[] order) {
        if (order == null) {
            order = new int[size()];
        }

        int pos = 0;
        for (VertexAndSampleRate a : group) {
            order[pos++] = a.vertex;
        }

        return order;
    }

    public int getVertex(int pos) {
        return group.get(pos).vertex;
    }

    public double getSampleRate(int pos) {
        return group.get(pos).sampleRate;
    }
    
    public void setSampleRate(int pos, double sampleRate){
        for (VertexAndSampleRate g : group){
            if (g.vertex == pos) {g.sampleRate = sampleRate; break;}
        } //check in profiling...
        orderDirty = true;
    }

    public int size() {
        return group.size();
    }

    /**
     * create a vertex group of size end-start and initialize it with vertices:
     * start, start+1, ..., end-1
     * all vertices sample rate will set to be the initialSampleRate
     * @param start
     * @param end
     * @param initialSampleRate
     * @return 
     */
    public static VertexGroup createFromRange(int start, int end, double initialSampleRate){
        VertexGroup ret = new VertexGroup(end-start);
        for (int i=start; i<end; i++){
            ret.addVertex(i, initialSampleRate);
        }
        
        return ret;
    }

    @Override
    public VertexGroup clone() {
        VertexGroup v = new VertexGroup(group.size());
        v.group.addAll(group);
        v.orderDirty = orderDirty;
        
        return v;
    }

    @Override
    public Iterator<VertexAndSampleRate> iterator() {
        return this.group.iterator();
    }
    
    public static final class VertexAndSampleRate implements Comparable {

        int vertex;
        double sampleRate;

        public VertexAndSampleRate(int vertex, double sampleRate) {
            this.vertex = vertex;
            this.sampleRate = sampleRate;
        }

        @Override
        public int compareTo(Object o) {
            VertexAndSampleRate other = (VertexAndSampleRate) o;
            return Double.compare(other.sampleRate, sampleRate);
        }

        @Override
        public String toString() {
            return "{" + "vertex=" + vertex + ", sampleRate=" + sampleRate + '}';
        }

        
    }
}
