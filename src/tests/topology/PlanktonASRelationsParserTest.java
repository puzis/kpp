package tests.topology;

import org.junit.Before;
import topology.graphParsers.PlanktonASRelationsParser;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 13/09/12
 * Time: 17:27
 */
public class PlanktonASRelationsParserTest extends ParserTest {

    @Before
    public void setUp(){
        content = "d 1997 12 23\n" +
                "t 2\n" +
                "T 1\n" +
                "N 0 sj.cache.nlanr.net  r\n" +
                "N 1 sv.cache.nlanr.net  r\n" +
                "l 0 1 0\n";
        progress = 0.25;
        fileName = "19971223.fvl" ;
        ext = "fvl";
        expectedVerticesFromFile = 779;
        expectedEdgesFromFile = 1087;
        expectedVerticesFromString = 2;
        expectedEdgesFromString = 1;
        networkGraphParser = new PlanktonASRelationsParser();

        super.setUp();
    }
}
