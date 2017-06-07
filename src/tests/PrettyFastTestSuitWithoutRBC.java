package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	tests.bcc.EBiConnComponentsTreeTest.class,

	tests.betweenness.SE_BCC_Test.class,
	tests.betweenness.bcc.BetweennessTest.class,
	tests.betweenness.brandes.BetweennessTest.class,
	tests.betweenness.brandes.BetweennessTestDijkstra.class,
	tests.betweenness.brandes.BetweennessTestHyperBFS.class,
	tests.betweenness.brandes.BetweennessTestFullBFSOnHyper.class,
	tests.betweenness.brandes.HyperBrandesBCTest.class,
	tests.betweenness.brandes.CapacityTest.class,
	tests.betweenness.brandes.DirectedBetweennessTest.class,
	tests.betweenness.brandes.LatencyTest.class,
	tests.betweenness.brandes.OptimizedDynamicSetTest.class,
	tests.betweenness.trast.SatoGraphBuilderTest.class,
	tests.betweenness.trast.TrastUpperBoundBCTest.class, 

	tests.structuralEquivalence.BetweennessTest.class,
	tests.structuralEquivalence.StructuralEquivalenceExtractorTest.class,
	tests.structuralEquivalence.StructuralEquivalenceUnifierTest.class,
	
	tests.randomWalkBetweeness.EquationsTest.class,
	tests.randomWalkBetweeness.RandomWalkBetweenessTest.class, 
	tests.randomWalkBetweeness.GRWBTest.class,
	tests.randomWalkBetweeness.SearchTest.class,
	
	//tests.closeness.AbstrractClosenessTest.class, not a test case
	tests.closeness.ClosenessEvaluationTest.class,
	tests.closeness.ClosenessFormulaTMTest.class,
	tests.closeness.ClosenessImportanceVectorTest.class,
	tests.closeness.ClosenessSearchTest.class,
	tests.closeness.GroupClosenessAlgorithmMSBFSTest.class,
	tests.closeness.OptimizedGroupClosenessTest.class, 
	
	tests.degree.GroupDegreeTest.class,

	tests.clustering.LabelPropagationTest.class,
	tests.clustering.BudgetedGreedyClusteringTest.class,

	
	tests.common.HeapTest.class,
	//tests.common.MathLinkTest.class requires user action
	tests.common.RationalGCDTest.class,
	//tests.common.RationalPerformanceTest.class no performance tests in this suit
	tests.common.RationalWholeTest.class, 
	
	tests.dfbnb.DfbnbTest.class, 
	
	tests.omnet.RatesFileParserTest.class,
	tests.omnet.RouterFileParserTest.class,
	tests.omnet.RouterTableTest.class,
	tests.omnet.RTFileParserTest.class, 
	
	tests.topology.CaidaAsGraphParserTest.class,
	tests.topology.DiGraphAsHashMapTest.class,
	tests.topology.GraphAsHashMapTest.class,
	tests.topology.GraphDataStructure1Test.class,
	tests.topology.GraphFactoryTest.class,
	tests.topology.GraphLoaderTest.class,
	tests.topology.NetFileParserTest.class,
	tests.topology.CaidaAsGraphParserTest.class,
	tests.topology.OmnetNetworkParserTest.class,
	tests.topology.CSVHyperEdgesFileParserTest.class
	
	})
public class PrettyFastTestSuitWithoutRBC {

}
