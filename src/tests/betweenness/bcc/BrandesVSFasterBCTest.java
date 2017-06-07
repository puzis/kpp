package tests.betweenness.bcc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javolution.util.Index;
import junit.framework.TestCase;
import server.common.DummyProgress;
import topology.AbstractGraph;
import topology.AbstractUndirectedGraph;
import topology.BANetworkGenerator;
import topology.BasicVertexInfo;
import topology.VertexFactory;
import topology.VertexInfo;
import algorithms.bcc.BCCAlgorithm;
import algorithms.centralityAlgorithms.betweenness.bcc.BetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.bcc.EvenFasterBetweenness;
import algorithms.centralityAlgorithms.betweenness.bcc.TMBetweennessCalculator;
import algorithms.centralityAlgorithms.betweenness.brandes.BrandesBC;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;
import algorithms.structuralEquivalence.StructuralEquivalenceUnifier;

public class BrandesVSFasterBCTest extends TestCase
{
	public BrandesVSFasterBCTest(String arg0){	super(arg0);	}
	
	public void testOnBANetworks() throws Exception
	{
		BANetworkGenerator baGenerator = null;
		for (int i = 0; i < 10; i++)
		{
			baGenerator = new BANetworkGenerator(1);
			testOnNetworks(baGenerator.generate(5));
		}
		
		baGenerator = null;
		for (int i = 0; i < 10; i++)
		{
			baGenerator = new BANetworkGenerator(1.4);
			testOnNetworks(baGenerator.generate(5));
		}
		
		baGenerator = null;
		for (int i = 0; i < 10; i++)
		{
			baGenerator = new BANetworkGenerator(2);
			testOnNetworks(baGenerator.generate(5));
		}
		for (int i=0; i<10; i++){
			baGenerator = new BANetworkGenerator(5);
			testOnNetworks(baGenerator.generate(15));
			baGenerator = new BANetworkGenerator(5);
			testOnNetworks(baGenerator.generate(20));
			baGenerator = new BANetworkGenerator(5);
			testOnNetworks(baGenerator.generate(25));
			
			baGenerator = new BANetworkGenerator(6);
			testOnNetworks(baGenerator.generate(10));
			baGenerator = new BANetworkGenerator(6);
			testOnNetworks(baGenerator.generate(15));
			baGenerator = new BANetworkGenerator(6);
			testOnNetworks(baGenerator.generate(20));
			baGenerator = new BANetworkGenerator(6);
			testOnNetworks(baGenerator.generate(25));
			
			baGenerator = new BANetworkGenerator(3);
			testOnNetworks(baGenerator.generate(10));
			baGenerator = new BANetworkGenerator(3);
			testOnNetworks(baGenerator.generate(15));
			baGenerator = new BANetworkGenerator(3);
			testOnNetworks(baGenerator.generate(20));
			baGenerator = new BANetworkGenerator(3);
			testOnNetworks(baGenerator.generate(25));
			
			baGenerator = new BANetworkGenerator(4);
			testOnNetworks(baGenerator.generate(10));
			baGenerator = new BANetworkGenerator(4);
			testOnNetworks(baGenerator.generate(15));
			baGenerator = new BANetworkGenerator(4);
			testOnNetworks(baGenerator.generate(20));
			baGenerator = new BANetworkGenerator(4);
			testOnNetworks(baGenerator.generate(25));
		}
	}
	
	public void testOnNetworks(AbstractUndirectedGraph<Index,BasicVertexInfo> graph) throws Exception
	{
		NumberFormat formatter = new DecimalFormat("0.000");
    	
    	BetweennessCalculator bcCalc = new BetweennessCalculator(new BCCAlgorithm(graph));
    	EvenFasterBetweenness eagerbc = new EvenFasterBetweenness(graph, bcCalc);	eagerbc.run();
    	
    	// SE+BCC
    	StructuralEquivalenceUnifier sed = new StructuralEquivalenceUnifier(graph);
		sed.run();
		AbsTrafficMatrix unifiedCW = sed.getUnifiedCW();
		AbstractGraph<Index,BasicVertexInfo> unifiedGraph = (AbstractGraph<Index,BasicVertexInfo>)sed.getUnifiedGraph();
		BetweennessCalculator unifiedBcCalc = new BetweennessCalculator(new BCCAlgorithm(unifiedGraph), unifiedCW);
		EvenFasterBetweenness unified_eagerbc = new EvenFasterBetweenness(graph, unifiedBcCalc); unified_eagerbc.run();

    	TMBetweennessCalculator tmBcCalc = new TMBetweennessCalculator(new BCCAlgorithm(graph), new DefaultTrafficMatrix(graph.getNumberOfVertices()));
    	EvenFasterBetweenness tmbccbc = new EvenFasterBetweenness(graph, tmBcCalc);	tmbccbc.run();

    	BrandesBC brandes = new BrandesBC(ShortestPathAlgorithmInterface.DEFAULT, graph, new DummyProgress(), 1);
		brandes.run();
		
		DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, graph, true, new DummyProgress(), 1);
		
        for (int i = 0; i < graph.getNumberOfVertices(); i++){
        	assertEquals(formatter.format(dw.getBetweenness(i)),         formatter.format(brandes.getCentrality(i)));
        	assertEquals(formatter.format(eagerbc.getCentrality(i)),    formatter.format(brandes.getCentrality(i)));
        	assertEquals(formatter.format(tmbccbc.getCentrality(i)),    formatter.format(brandes.getCentrality(i)));
        	
        	int unifiedVertex = sed.getContainingUnifiedVertex(i).intValue();
        	int multiplicity=1;
        	if (VertexFactory.isVertexInfo(sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))))
        		multiplicity = ((VertexInfo)sed.getUnifiedGraph().getVertex(Index.valueOf(unifiedVertex))).getMultiplicity();
        	double centrality = unified_eagerbc.getCentrality(unifiedVertex);
        	if (!formatter.format(centrality/(double)multiplicity).equals(formatter.format(brandes.getCentrality(i)))){
        		System.out.print(graph);
        	}
        	assertEquals(formatter.format(centrality/(double)multiplicity), formatter.format(brandes.getCentrality(i)));

         }
	}
}