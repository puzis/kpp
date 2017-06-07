package topology;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: puzis
 * Date: Aug 23, 2007
 * Time: 2:43:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphRegularExpressions
{
    public static Pattern VERTICES_BEGINING = Pattern.compile("\\*Vertices\\s+(\\d+)");
    public static Pattern VERTICES_LINE = Pattern.
    		//compile("\\s*(\\d+)\\s+['\"](.*)['\"]\\s+(\\d+\\.\\d*)\\s+(\\d+\\.\\d*)\\s+(\\d+\\.\\d*)(\\s+\\d+\\.?\\d*)?(\\s+\\w+\\s+)?(\'\\w+\')?(\\s+\\w+\\s+)?(\'\\w+\')?"); Dana
    		compile("\\s*(\\d+)\\s+['\"](.*)['\"]\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)\\s+(\\d+\\.?\\d*)");
    
    public static Pattern OPTIONAL_INFO = Pattern.
    		compile("(\\s*\\w+)\\s+(\'?[a-zA-Z_0-9\\.]+\'?)");
    
    public static Pattern VERTICES_LINE_VERSION_2 = Pattern.
			compile("\\s*(\\d+)");
    
    public static Pattern EDGES_BEGINING = Pattern.compile("\\*Edges");
    public static Pattern EDGE_LINE = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s+(\\d*[\\.]?\\d*)");//@Rami: modified
    public static Pattern SIMPLE_EDGE_LINE = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s*");// @Roni: for simple edge format    
    public static Pattern ASRELATIONSHIP = Pattern.compile("(\\d+)\\s+(\\d+)\\s+(-?\\d+)?");
    public static Pattern ASPLANKTON = Pattern.compile("(\\w)\\s(\\w*)(\\s([A-Za-z0-9\\.-]*))?(\\s(\\w*))?");
    public static Pattern ASOREGON = Pattern.compile("(\\d+):(\\d+)");
    public static Pattern ASLEDA = Pattern.compile("(\\d+)\\s(\\d+)\\.*");
    
  
    
    
}
