package tests.dfbnb;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.FastList;
import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.Network;
import server.dfbnb.Dfbnb;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.VertexInfo;
import algorithms.centralityAlgorithms.BasicSet;
import algorithms.centralityAlgorithms.BasicSetInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.betweenness.brandes.sets.OptimizedDynamicBetweennessSet;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

public class DfbnbTest  extends TestCase{

	protected FastList<GraphInterface<Index,BasicVertexInfo>> graphs;
	protected GraphInterface<Index,BasicVertexInfo> g_line, g_clique, g_middleV, g_crossedCircle;
//	protected double[] line_betweenness, clique_betweenness, middleV_betweenness, crossedCircle_betweenness;
//	protected double[][] line_GB_overhead, clique_GB_overhead, middleV_GB_overhead, crossedCircle_GB_overhead;
//	protected int[][] testSets;
	protected NumberFormat formatter = new DecimalFormat("0.000");
	
    public void setUp(){
    	graphs = new FastList<GraphInterface<Index,BasicVertexInfo>>();
        g_line = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_line.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 6; v++)
            g_line.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
        graphs.add(g_line);
     
//        line_betweenness = new double[]{0.00, 10.00, 16.00, 18.00, 16.00, 10.00, 0.00}; 
//        line_GB_overhead = new double[][]{{9,0},{8,0},{6,5},{8,4},{3,7}}; 

        g_clique = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
            g_clique.addVertex(Index.valueOf(v), new VertexInfo());
        for (int v = 0; v < 7; v++){
            for (int u = 0; u < 7; u++){
                if (u != v)
                {
                	if (!g_clique.isEdge(Index.valueOf(u), Index.valueOf(v)))
                		g_clique.addEdge(Index.valueOf(u), Index.valueOf(v), new EdgeInfo<Index,BasicVertexInfo>());
                }
            }
        }
        graphs.add(g_clique);   
//        clique_betweenness = new double[]{0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00};
//        clique_GB_overhead = new double[][]{{0,0},{0,0},{0,0},{0,0},{0,0}}; 
                                      
        
        g_middleV = new GraphAsHashMap<Index,BasicVertexInfo>();
        for (int v = 0; v < 7; v++)
	    	g_middleV.addVertex(Index.valueOf(v), new VertexInfo());
	    for (int v = 0; v < 6; v++){
	    	g_middleV.addEdge(Index.valueOf(v), Index.valueOf(v+1), new EdgeInfo<Index,BasicVertexInfo>());
	    }
	    g_middleV.addEdge(Index.valueOf(0), Index.valueOf(2), new EdgeInfo<Index,BasicVertexInfo>()); 
	    g_middleV.addEdge(Index.valueOf(4), Index.valueOf(6), new EdgeInfo<Index,BasicVertexInfo>());
	    graphs.add(g_middleV);
//	    middleV_betweenness = new double[]{0.00, 0.00, 16.00, 18.00, 16.00, 0.00, 0.00};
//	    middleV_GB_overhead = new double[][]{{9,0},{8,0},{6,5},{8,4},{3,7}};
	    
    	g_crossedCircle = new GraphAsHashMap<Index,BasicVertexInfo>();
		for(int i = 0; i < 6; i++)
			g_crossedCircle.addVertex(Index.valueOf(i));
		for(int i = 0; i < 5; i++)
			g_crossedCircle.addEdge(Index.valueOf(i), Index.valueOf(i+1), new EdgeInfo<Index,BasicVertexInfo>());
		g_crossedCircle.addEdge(Index.valueOf(0), Index.valueOf(5), new EdgeInfo<Index,BasicVertexInfo>());
		g_crossedCircle.addEdge(Index.valueOf(0), Index.valueOf(3), new EdgeInfo<Index,BasicVertexInfo>());
		graphs.add(g_crossedCircle);
//		clique_betweenness = new double[]{6.67, 1.67, 1.67, 6.67, 1.67, 1.67};
//		clique_GB_overhead = new double[][]{{3.3333,0.0},{0.8333,0.0},{1.1667,2.6667},{1.0,0.6667},{2.0,4.0}};
		
//		testSets = new int[][]{{3}, {2}, {2,3}, {2,4}, {0,2,3}};
    }
    
    public void testInstantiation(){
    	for(GraphInterface<Index,BasicVertexInfo> graph : graphs)
    		testGreedyOL(graph);
    }
    
    
    public void testGreedyOL(GraphInterface<Index,BasicVertexInfo> graph){
    	int c = 0;
    	int uh = 2;
    	int ch = 0;
    	int eo = 2;
    	int ol = 7;
    	double b = 0;
    	b = 3.0;
    	Index[] candidatesArr = new Index[graph.getNumberOfVertices()];
    	FastList<Index> candidates = new FastList<Index>(graph.getNumberOfVertices());
    	for (int i = 0; i < graph.getNumberOfVertices(); i++){
    		candidatesArr[i] = Index.valueOf(i);
    		candidates.add(Index.valueOf(i));
    	}

    	BasicSetInterface uGroup=null;
    	DataWorkshop dw = getDW(graph);
    	uGroup = new OptimizedDynamicBetweennessSet(dw, candidates);
    	Network network = new Network("", graph);
    	Dfbnb instance = new Dfbnb(uGroup, new BasicSet(), eo, uh, ch, candidatesArr, network, b, ol);
    	int countExpandedNodes = instance.execute();
    	assertTrue(countExpandedNodes>0);
    }

    /*
    public void testInstantiation(GraphInterface<Index> graph){
    	for (int c : new int[]{0,2}){
    		for (int st=0;st<2;st++){
                for (int uh=0;uh<3;uh++){
                  for (int ch=0;ch<3;ch++){
                    for (int eo=0;eo<4;eo++){
                    	for (int ol=0;ol<17;ol++){
                    		double b = 0;
                            if (st==0){
                              b = 3.0;
                            }else if (st==1){
                              b = graph.getNumberOfVertices()*5.0;
                            }
                            Index[] candidatesArr = new Index[graph.getNumberOfVertices()];
                            FastList<Index> candidates = new FastList<Index>(graph.getNumberOfVertices());
                    		for (int i = 0; i < graph.getNumberOfVertices(); i++){
                    			candidatesArr[i] = Index.valueOf(i);
                    			candidates.add(Index.valueOf(i));
                    		}
                    		
                    		BasicSetInterface uGroup=null;
                    		Centrality centrality = convertToEnum(c);
                    		switch(centrality){
                    		case Betweeness: // (c==0)
                    			DataWorkshop dw = getDW(graph);
                    			uGroup = new OptimizedDynamicBetweennessSet(dw, candidates);
                    			break;
                    		case Closeness: // (c==2)
                    			uGroup = new OptimizedDynamicClosenessSet(graph);
                    			break;
                    		}
                    		Network network = new Network("", graph);
                    		Dfbnb instance = new Dfbnb(uGroup, new BasicSet(), eo, uh, ch, candidatesArr, network, b, ol);
                    		int countExpandedNodes = instance.execute();
                    		assertTrue(countExpandedNodes>0);
                    	}
                    }
                  }
                }
    		}
    	}
    }
    */
    
    
	/*
	 *  class BetweennessSearchTest(unittest.TestCase):

class JDFBnBTest(unittest.TestCase):

    def setUp(self):
      self.testGraphs={}

    def test_JDFBNB_Instantiation(self):
      for (title,G) in self.testGraphs.items():
        serv = self.server
        net_id = serv.Network.importNetwork(title,getPajekRepr(G), "net")
        for c in [0,1,2]:
          for st in [0,1]:
            for uh in [0,1,2]:
              for ch in [0,1,2]:
                for eo in [0,1,2,3]:
                  sys.stdout.write(title+","+str(c)+","+str(st)+","+str(uh)+","+str(ch)+","+str(eo))
                  for ol in range(17):
                    if st==0:
                      b = 3.0
                    elif st==1:
                      b = G.getNumberOfVertices()*5.0
                    if not withretry(serv.Dfbnb.dfbnbInit)(net_id,c,st,b,eo,uh,ch,ol):
                      raise "analyze fialed"
                    sys.stdout.write(".")
                  sys.stdout.write("\n")
      pass

    def test_JDFBNB_OptimalResult_size(self):
      for (title,G) in self.testGraphs.items():
        serv = self.server
        net_id = withretry(serv.Network.importNetwork)(title,getPajekRepr(G),"net")
        c=0  #only betweenness
        st=0 #cost = size
        for b in [1.0,2.0,3.0]:
          dw = DataWorkshop(G)
          s = StaticSet(dw)
          election = UndoableCandidatesElection(s,int(b),range(dw.getNumberOfVertices()))
          search = OptimalElectionSearch(election,b)
          while(search.hasNext()):search.next()
          pyResult = search.getOptimalValue()

          for uh in [0,1,2]:
            for ch in [0,1,2]:
              for eo in [0,1,2,3]:
                sys.stdout.write(title+","+str(b)+","+str(c)+","+str(st)+","+str(uh)+","+str(ch)+","+str(eo))
                for ol in [0,1,2,3,4,5,6]:
                  dfbnb = self.server.Dfbnb
                  if not withretry(dfbnb.dfbnbInit)(net_id,c,st,b,eo,uh,ch,ol):
                    raise "analyze fialed"
                  n=withretry(dfbnb.dfbnbCalculate)(net_id)
                  jResult = withretry(dfbnb.dfbnbGetBestUtility)(net_id)
                  self.assertAlmostEqual(jResult,pyResult)
                  sys.stdout.write(".")
                  pass
                sys.stdout.write("\n")
        pass
      pass


    def test_JDFBNB_OptimalResult_randomGraphs(self):
      for n in range(5,10,1):
        G = topology.graphBuilder.createRandomBAGraph(n,2)
        title = "test"

        serv = self.server
        net_id = withretry(serv.Network.importNetwork)(title,getPajekRepr(G),"net")
        c=0  #only betweenness
        st=0 #cost = size
        for b in [1.0,2.0,3.0]:
          dw = DataWorkshop(G)
          s = StaticSet(dw)
          election = UndoableCandidatesElection(s,int(b),range(dw.getNumberOfVertices()))
          search = OptimalElectionSearch(election,b)
          while(search.hasNext()):search.next()
          pyResult = search.getOptimalValue()

          for uh in [0,1,2]:
            for ch in [0,1,2]:
              for eo in [0,1,2,3]:
                sys.stdout.write(title+","+str(b)+","+str(c)+","+str(st)+","+str(uh)+","+str(ch)+","+str(eo))
                for ol in [0,1,2,3,4,5,6]:
                  dfbnb = self.server.Dfbnb
                  if not withretry(dfbnb.dfbnbInit)(net_id,c,st,b,eo,uh,ch,ol):
                    raise "analyze fialed"
                  n=withretry(dfbnb.dfbnbCalculate)(net_id)
                  jResult = withretry(dfbnb.dfbnbGetBestUtility)(net_id)
                  self.assertAlmostEqual(jResult,pyResult)
                  sys.stdout.write(".")
                sys.stdout.write("\n")

        pass
      pass


if __name__ == "__main__":
    while(True):
        unittest.main()



	 */
    
    private DataWorkshop getDW(GraphInterface<Index,BasicVertexInfo> graph){
    	DataWorkshop dw = null;
    	AbsTrafficMatrix communicationWeights = null;
		if (graph != null){
			communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices());// MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		
			try{
				dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, communicationWeights, true, new DummyProgress(), 1);
			}
			catch(Exception ex){
				LoggingManager.getInstance().writeSystem("An exception has occured while creating dataWorkshop:\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex), "DynamicSet_GBC_Size", "DynamicSet_GBC_Size", ex);
			}
		}
		return dw;
    }
}
