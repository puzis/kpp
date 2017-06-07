/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

/**
 *
 * @author bennyl
 */
public interface TrafficEstimationElement {
    void visit(TrafficEstimationVisitor visitor);
    
    public static interface TrafficEstimationVisitor{
        void visit(EstimatedValueElement e);
    }
    
    public static class EstimatedValueElement implements TrafficEstimationElement{
        private int fromIndex;
        private int toIndex;
        private double estimation;

        public EstimatedValueElement(int fromIndex, int toIndex, double estimation) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.estimation = estimation;
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }

        public double getEstimation() {
            return estimation;
        }

        @Override
        public void visit(TrafficEstimationVisitor visitor) {
            visitor.visit(this);
        }
    }
}
