package topology;

import topology.GraphFactory.GraphDataStructure;

public abstract class AbstractUndirectedGraph<VertexType, VertexInfoStructure> extends AbstractGraph<VertexType, VertexInfoStructure> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AbstractUndirectedGraph(GraphDataStructure gds) {
        super(gds);
    }
}