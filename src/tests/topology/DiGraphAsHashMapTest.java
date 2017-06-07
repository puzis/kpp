package tests.topology;

import javolution.util.Index;
import junit.framework.TestCase;
import topology.BasicVertexInfo;
import topology.DiGraphAsHashMap;
import topology.EdgeInfo;
import topology.AbstractSimpleEdge;
import topology.GraphInterface;
import topology.VertexInfo;

public class DiGraphAsHashMapTest extends TestCase 
{
    GraphInterface<Index,BasicVertexInfo> g_line, g_clique, g_7_1, g_7_2;
	
    public void setUp(){
        g_line = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }

        g_clique = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++)
            for (int u = 0; u < 7; u++)
                if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }

        g_7_1 = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_7_1.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
        {
            g_7_1.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_1.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); g_7_1.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());

        g_7_2 = new DiGraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 6; v++)
            g_7_2.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 5; v++)
        {
            g_7_2.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        }
        g_7_2.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>()); g_7_2.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
    }
    
    
    public void testIsEdge(){
    	assertTrue(g_line.isEdge(Index.valueOf(0), Index.valueOf(1)));
    	assertFalse(g_line.isEdge(Index.valueOf(1), Index.valueOf(0)));
    	
    	AbstractSimpleEdge<Index,BasicVertexInfo> e;
    	
    	e = g_line.getEdge(Index.valueOf(0), Index.valueOf(1));
    	assertNotNull(e);
    	assertTrue(g_line.isEdge(e));

    	e = g_line.getEdge(Index.valueOf(1), Index.valueOf(0));
    	assertNull(e);
    	assertFalse(g_line.isEdge(e));
    }
    public void testDegree()
    {
    	assertEquals(1,this.g_line.getDegree(Index.valueOf(0)));
    	assertEquals(2,this.g_line.getDegree(Index.valueOf(1)));
    	assertEquals(1,this.g_line.getDegree(Index.valueOf(6)));

    	assertEquals(0,this.g_line.getInDegree(Index.valueOf(0)));
    	assertEquals(1,this.g_line.getInDegree(Index.valueOf(1)));
    	assertEquals(1,this.g_line.getInDegree(Index.valueOf(6)));
    	
    	assertEquals(1,this.g_line.getOutDegree(Index.valueOf(0)));
    	assertEquals(1,this.g_line.getOutDegree(Index.valueOf(1)));
    	assertEquals(0,this.g_line.getOutDegree(Index.valueOf(6)));

    	assertEquals(6,this.g_clique.getDegree(Index.valueOf(2)));
    	assertEquals(6,this.g_clique.getInDegree(Index.valueOf(2)));
    	assertEquals(6,this.g_clique.getOutDegree(Index.valueOf(2)));
    }
    
}
