package tests.topology;
import org.junit.Before;
import topology.graphParsers.ASOregonParser;


/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 11/09/12
 * Time: 20:32
 createNetworkFromString */
public class ASOregonParserTest extends ParserTest{


    @Before
    public void setUp(){
        progress = 0.25;
        fileName = "peer.oregon.010331";
        ext = "oregon";
        content =  "1:2\n" + "1:3\n";
        expectedVerticesFromFile = 10670;
        expectedEdgesFromFile = 44004;
        expectedVerticesFromString = 3;
        expectedEdgesFromString =4;
        networkGraphParser = new ASOregonParser();

        super.setUp();
    }
}
