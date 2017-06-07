package tests.topology;

import javolution.util.Index;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import topology.graphParsers.InnerReprParser;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 06/09/12
 * Time: 00:08
 */
public class InnerReprParserTest extends ParserTest{

    @Before
    public void setUp(){
         content= "*Vertices      10\n" +
                "       1 \"LSR\"                                    0.4811    0.6681    0.5000 NSP 'DT' RC 'core'\n" +
                "       2 \"LER\"                                    0.6260    0.7800    0.5000 NSP 'DT' RC 'edge'\n" +
                "       3 \"Access\"                                 0.7825    0.6710    0.5000 NSP 'DT' RC 'access'\n" +
                "       4 \"Access\"                                 0.5595    0.9586    0.5000\n" +
                "       5 \"Access\"                                 0.7684    0.8898    0.5000\n" +
                "       6 \"LSR\"                                    0.3426    0.5595    0.5000 NSP 'WORLD' RC 'core'\n" +
                "       7 \"LER\"                                    0.1997    0.4451    0.5000 NSP 'WORLD' RC 'edge'\n" +
                "       8 \"Access\"                                 0.2684    0.2673    0.5000 NSP 'WORLD' RC 'access'\n" +
                "       9 \"Access\"                                 0.0596    0.3324    0.5000\n" +
                "      10 \"Access\"                                 0.0414    0.5512    0.5000\n" +
                "*Edges\n" +
                "       1        6 1\n" +
                "       1        2 1 \n" +
                "       2        3 1 \n" +
                "       2        4 1 \n" +
                "       2        5 1 \n" +
                "       6        7 1 \n" +
                "       7        8 1 \n" +
                "       7        9 1 \n" +
                "       7       10 1";
        progress = 0.25;
        fileName = "bgumail_051101_1100_1200.net0";
        ext = "net0";
        expectedVerticesFromFile = 1035;
        expectedEdgesFromFile = 1079;
        expectedVerticesFromString = 10;
        expectedEdgesFromString = 9;
        networkGraphParser = new InnerReprParser();

        super.setUp();
    }
}
