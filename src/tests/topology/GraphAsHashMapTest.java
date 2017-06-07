package tests.topology;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.GraphUtils;
import topology.VertexInfo;

public class GraphAsHashMapTest extends TestCase 
{
	AbstractUndirectedGraph<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2, defaultnet;
	
    public void setUp(){
        g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }

        g_clique = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
                if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }

        g_7_1 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());

        g_7_2 = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++)
        {
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>()); g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
    }
    
    public void testReduction()
    {
    	FastList<Index> _S = new FastList<Index>();
    	
    	_S.add(Index.valueOf(0));
    	_S.add(Index.valueOf(2));
    	_S.add(Index.valueOf(4));
    	
    	GraphInterface<Index,BasicVertexInfo> r_line = GraphUtils.reduceVertices(g_line, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_clique = GraphUtils.reduceVertices(g_clique, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_7_1 = GraphUtils.reduceVertices(g_7_1, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_7_2 = GraphUtils.reduceVertices(g_7_2, _S.iterator());
    	
    	testVerticesReduction(r_line);
    	testVerticesReduction(r_clique);
    	testVerticesReduction(r_7_1);
    	testVerticesReduction(r_7_2);
    	
    	/********************** Test Edges **********************/
    	
    	assertEquals(r_line.getEdges().iterator().hasNext(), false);
    	assertEquals(r_clique.getNumberOfEdges(), 3);
    	assertEquals(r_clique.getEdgeWeight(Index.valueOf(0), Index.valueOf(2)), g_clique.getEdgeWeight(Index.valueOf(0), Index.valueOf(2)));
    	assertEquals(r_clique.getEdgeWeight(Index.valueOf(0), Index.valueOf(4)), g_clique.getEdgeWeight(Index.valueOf(0), Index.valueOf(4)));
    	assertEquals(r_clique.getEdgeWeight(Index.valueOf(2), Index.valueOf(4)), g_clique.getEdgeWeight(Index.valueOf(2), Index.valueOf(4)));
    	
    	assertEquals(r_7_1.getNumberOfEdges(), 1);
    	assertEquals(r_7_1.getEdgeWeight(Index.valueOf(0), Index.valueOf(2)), g_7_1.getEdgeWeight(Index.valueOf(0), Index.valueOf(2)));
    	
    	assertEquals(r_7_2.getNumberOfEdges(), 0);
    }
    
    public void testVerticesReduction(GraphInterface<Index,BasicVertexInfo> reduced)
    {
    	assertTrue(reduced.getVertex(Index.valueOf(0)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(2)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(4)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(1)) == null);
    	assertTrue(reduced.getVertex(Index.valueOf(3)) == null);
    	assertTrue(reduced.getVertex(Index.valueOf(5)) == null);
    }
    
    public void testReductionByEdges()
    {
    	FastList<AbstractSimpleEdge<Index,BasicVertexInfo>> _S = new FastList<AbstractSimpleEdge<Index,BasicVertexInfo>>();    	
    	_S.add(g_line.getEdge(Index.valueOf(0), Index.valueOf(1)));
    	_S.add(g_line.getEdge(Index.valueOf(2), Index.valueOf(3)));
    	_S.add(g_line.getEdge(Index.valueOf(4), Index.valueOf(5)));
    	
    	GraphInterface<Index,BasicVertexInfo> r_line = GraphUtils.reduceEdges(g_line, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_clique = GraphUtils.reduceEdges(g_clique, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_7_1 = GraphUtils.reduceEdges(g_7_1, _S.iterator());
    	GraphInterface<Index,BasicVertexInfo> r_7_2 = GraphUtils.reduceEdges(g_7_2, _S.iterator());
    	
    	testReductionByEdges(r_line);
    	testReductionByEdges(r_clique);
    	testReductionByEdges(r_7_1);
    	testReductionByEdges(r_7_2);
    	
    	assertEquals(r_line.getNumberOfEdges(), 3);
    	assertEquals(r_clique.getNumberOfEdges(), 3);
    	assertEquals(r_7_1.getNumberOfEdges(), 3);
    	assertEquals(r_7_2.getNumberOfEdges(), 3);
    }
    
    private void testReductionByEdges(GraphInterface<Index,BasicVertexInfo> reduced)
    {
    	assertTrue(reduced.getVertex(Index.valueOf(0)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(2)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(4)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(1)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(3)) != null);
    	assertTrue(reduced.getVertex(Index.valueOf(5)) != null);
    }
    
    public void testDegree()
    {
    	assertEquals(1,this.g_line.getDegree(Index.valueOf(0)));
    	assertEquals(2,this.g_line.getDegree(Index.valueOf(1)));
    	assertEquals(1,this.g_line.getDegree(Index.valueOf(6)));

    	assertEquals(1,this.g_line.getInDegree(Index.valueOf(0)));
    	assertEquals(2,this.g_line.getInDegree(Index.valueOf(1)));
    	assertEquals(1,this.g_line.getInDegree(Index.valueOf(6)));
    	
    	assertEquals(1,this.g_line.getOutDegree(Index.valueOf(0)));
    	assertEquals(2,this.g_line.getOutDegree(Index.valueOf(1)));
    	assertEquals(1,this.g_line.getOutDegree(Index.valueOf(6)));

    	assertEquals(6,this.g_clique.getDegree(Index.valueOf(2)));
    	assertEquals(6,this.g_clique.getInDegree(Index.valueOf(2)));
    	assertEquals(6,this.g_clique.getOutDegree(Index.valueOf(2)));
    }
    
}
