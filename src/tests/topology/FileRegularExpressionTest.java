package tests.topology;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import topology.graphParsers.common.FileRegularExpression;

import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 23/09/12
 * Time: 13:06
 */
public class FileRegularExpressionTest {

    String fileExtension;
    Matcher m;

    @Before
    public void setUp(){
        fileExtension= "";
    }

    private  void createMatcher ( String fileName){
        m = FileRegularExpression.FILE_NAME.matcher(fileName);
    }

    @Test
    public void oregonExtensionTest(){
        fileExtension = "oregon";
        createMatcher("peer.oregon.010331");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(1));
    }

    @Test
    public void oregonPlusExtensionTest(){
        fileExtension = "oregon";
        createMatcher("peer.oregon+.010331");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(1));
    }

    @Test
    public void txtExtensionTest(){
        fileExtension = "txt";
        createMatcher("as-rel.20040202.a0.01000.txt");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(2));
    }

    @Test
    public void csvExtensionTest(){
        fileExtension = "csv";
        createMatcher("bgumail_011107_071107_allweek.csv");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(2));
    }

    @Test
    public void netExtensionTest(){
        fileExtension = "net";
        createMatcher("bgumail_011107_071107_allweek.net");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(2));
    }

    @Test
    public void planktonExtensionTest(){
        fileExtension = "fvl";
        createMatcher("19971223.fvl");
        Assert.assertTrue(m.find());
        Assert.assertEquals(fileExtension,m.group(2));
    }
}
