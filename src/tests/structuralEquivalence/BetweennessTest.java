package tests.structuralEquivalence;

import javolution.util.Index;
import server.common.DummyProgress;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.TrafficMatrixBC;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

import common.FastListNG;

public class BetweennessTest extends StructuralEquivalenceUnifierTest {
	
	public void testBetweenness() throws Exception{
		testBetweenness(graph_1);
		testBetweenness(graph_2);
		testBetweenness(graph_3);
		testBetweenness(graph_4);
		testBetweenness(graph_5);
		testBetweenness(g_consistency_check);
		//testBetweenness(g_test_p);
	}
	
	private void testBetweenness(GraphInterface<Index,BasicVertexInfo> graph) throws Exception{
		AbsTrafficMatrix commWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices());// MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
		DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, commWeights, true, new DummyProgress(), 1);
		
		StructuralEquivalenceUnifier sed = new StructuralEquivalenceUnifier(graph);
		sed.run();
		AbsTrafficMatrix unifiedCW = sed.getUnifiedCW();
		GraphInterface<Index,BasicVertexInfo> unifiedGraph = sed.getUnifiedGraph();
		FastListNG<FastListNG<Index>> equivalenceClasses = sed.getEquivalenceClasses();
		
		TrafficMatrixBC tmBC = new TrafficMatrixBC(ShortestPathAlgorithmInterface.DEFAULT, unifiedGraph, unifiedCW, new DummyProgress(), 1);
    	tmBC.run();
    	
		DataWorkshop unifiedDW = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, unifiedGraph, unifiedCW, true, new DummyProgress(), 1);
		
		double[] bc_tm = new double[graph.getNumberOfVertices()];
		double[] bc_dw = new double[graph.getNumberOfVertices()];
		
		// Assumption: the equivalence classes order corresponds to the matching indexes in he unified graph.
		// Example: 
		// Mapping of indexes to the equivalence classes is: 0-{0}, 1-{2,3}, 2-{5}, 3-{4,6}
		// Then the order of the equivalence classes in the list is: {0}, {2,3}, {5}, {4,6} 
		int uv = 0;
		for(FastListNG.Node<FastListNG<Index>> eqNode=equivalenceClasses.head(), end=equivalenceClasses.tail(); (eqNode = eqNode.getNext())!=end;){
			FastListNG<Index> eqClass = eqNode.getValue();
			
			for(FastListNG.Node<Index> cNode=eqClass.head(), endClass=eqClass.tail(); (cNode = cNode.getNext())!=endClass;){
				int v = cNode.getValue().intValue();
				bc_tm[v] = tmBC.getCentrality(uv)/eqClass.size();
				bc_dw[v] = unifiedDW.getBetweenness(uv)/eqClass.size(); 
				System.out.println(v + ": " + dw.getBetweenness(v) + ", " + unifiedDW.getBetweenness(uv));
			}
			uv++;
		}
		
		for (int i = 0; i < graph.getNumberOfVertices(); i++){
            assertEquals(formatter.format(dw.getBetweenness(i)), formatter.format(bc_tm[i]));
            assertEquals(formatter.format(dw.getBetweenness(i)), formatter.format(bc_dw[i]));
        }
	}
}
