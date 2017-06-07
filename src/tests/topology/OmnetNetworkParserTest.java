package tests.topology;


import topology.graphParsers.OmnetNetworkParser;

public class OmnetNetworkParserTest extends ParserTest {
	public void setUp(){

        progress = 0.25;
        fileName = "3257.onet" ;
        ext = "onet";
        content = "*Vertices 9\n" +
                "1 \"R1\"\n" +
                "2 \"R2\"\n" +
                "3 \"R3\"\n" +
                "4 \"R4\"\n" +
                "5 \"R5\"\n" +
                "6 \"R6\"\n" +
                "7 \"R7\"\n" +
                "8 \"R8\"\n" +
                "9 \"R9\"\n" +
                "*Arcs\n" +
                "1 2 1\n" +
                "2 1 1\n" +
                "2 3 3\n" +
                "3 2 3\n" +
                "3 4 2\n" +
                "3 5 3\n" +
                "3 6 1\n" +
                "3 7 2\n" +
                "4 3 2\n" +
                "5 3 3\n" +
                "6 3 1\n" +
                "6 9 3\n" +
                "7 3 2\n" +
                "8 9 1\n" +
                "9 6 3\n" +
                "9 8 1\n" +
                "*Edges";

        expectedVerticesFromFile = 9;
        expectedEdgesFromFile = 16;
        expectedVerticesFromString = 9;
        expectedEdgesFromString = 16;

        networkGraphParser = new OmnetNetworkParser();

        super.setUp();
			
	}

}
