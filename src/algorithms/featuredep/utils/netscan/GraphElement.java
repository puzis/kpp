/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public interface GraphElement {
    void visit(GraphElementVisitor visitor);
    
    public static interface GraphElementVisitor{
        void visit(VertexElement e);
        void visit(EdgeElement e);
    }
    
    public static class VertexElement implements GraphElement{
        private String name;
        private int id;

        public VertexElement(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public void visit(GraphElementVisitor visitor) {
            visitor.visit(this);
        }
    }
    
    public static class EdgeElement implements GraphElement{
        private int vertexFromId;
        private int vertexToId;
        private String name;
        private double weight;
        private boolean directed;
        private Map<String, String> additionalProperties = new HashMap<String, String>();

        public EdgeElement(int vertexFromId, int vertexToId, String name, double weight, boolean directed, String... additionalProperties) {
            this.vertexFromId = vertexFromId;
            this.vertexToId = vertexToId;
            this.name = name;
            this.weight = weight;
            this.directed = directed;
            for (int i=0; i<additionalProperties.length; i+=2){
                this.additionalProperties.put(additionalProperties[i], additionalProperties[i+1]);
            }
        }

        public boolean isDirected() {
            return directed;
        }

        public Map<String, String> getAdditionalProperties() {
            return additionalProperties;
        }
        
        public int getVertexFromId() {
            return vertexFromId;
        }

        public int getVertexToId() {
            return vertexToId;
        }

        public String getName() {
            return name;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public void visit(GraphElementVisitor visitor) {
            visitor.visit(this);
        }
    }
}
