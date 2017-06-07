
package topology.graphParsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.EdgeInfo;
import topology.GraphFactory;
import topology.GraphDataInterface;
import topology.GraphRegularExpressions;
import topology.VertexFactory;

/**
 * @author Omer Zohar
 * Assumption for this parser:
 * nodes must be in the plankton format which dictates:
 * a) number of nodes and links specified in the t/T tags at the beginning of the file
 * b) nodes declared before links and numbered from 0 running up, increasing by one each time!
 * c) after N tag follows r tag.
 * d) after l tag follows R tag.
 * e) once first l showed, N or r cannot appear.
 * f) G tag is ignored.
 */
public class PlanktonASRelationsParser extends NetworkGraphParser {


    public  static enum FILE_PARAMETER{
        NODES_NUMBER('t'),
        LINKS_NUMBER('T'),
        DATE('d'),
        NODE('n'),
         NODE_CAP('N'),
        LINK('L'),
        LINK_CAP('l'),
        TEST ('G'),
        ROOT_NODE ('r'),
        ROOT_NODE_CAP('R');

        private  Character letter;
        private static final Map<Character,FILE_PARAMETER> lookup = new HashMap<Character,FILE_PARAMETER>();

        static {
            for (FILE_PARAMETER fp: EnumSet.allOf(FILE_PARAMETER.class)/*FILE_PARAMETER.values()*/)
                lookup.put(fp.getChar(),fp) ;
        }
        private FILE_PARAMETER(Character ch){
            this.letter = ch;
        }
        public  char getChar(){
            return letter;
        }

        public static FILE_PARAMETER getParameter(Character letter){
            return lookup.get(letter);
        }
    }

    private boolean datefound=false;
    private boolean verticenumfound=false;
    private boolean linknumfound=false;
    private boolean verticesparsed=false;
    private boolean linksparced=false;
    private boolean doneparsing=false;
    private int verticenum=0;
    private int linksnum=0;
    private int verticesfound=0;
    private int linksfound=0;


    final String EXP_IGNORE="unknown";
	final static private String EXTENSION="fvl";


    public PlanktonASRelationsParser(){}

	/**
	 *  filename for this implementation filename does not matter
	 */

	@Override
	public void analyzeFile(BufferedReader reader,
			AbstractExecution progress, double percentage,
			GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
		{
			String line;
			try {
				while ( (line=reader.readLine()) !=null && !doneparsing )
				{
					while (line.charAt(0)=='#'){line=reader.readLine();}//skipping notes
					Matcher	verticesLine = GraphRegularExpressions.ASPLANKTON.matcher(line);
					if (verticesLine.find())//parsing data coloumns
					{
						char arg1=verticesLine.group(1).charAt(0);
						String arg2=verticesLine.group(2);//!=""?Integer.parseInt(verticesLine.group(2)):null;
						String arg3=verticesLine.group(4);
						String arg4=verticesLine.group(6);//!=""?Integer.parseInt(verticesLine.group(4)):null;

                        FILE_PARAMETER parameter = FILE_PARAMETER.getParameter(arg1);
                        switch (parameter) {
						case DATE:{
							datefound=!datefound;
							break;
                        }
						case NODES_NUMBER:{
							if (datefound&&!verticenumfound&&!linknumfound&&!verticesparsed&&!linksparced){
							    readNodesNum(arg2);
							}
							else throw new Exception ("File does not complie with the plankton format");
							break;
                        }
						case LINKS_NUMBER:{
							if (datefound&&verticenumfound&&!linknumfound&&!verticesparsed&&!linksparced){
                                readLinksNum( arg2,progress, percentage);
							}
							else throw new Exception ("File does not complie with the plankton format");
							break;
                        }
						case NODE://do as N
						case NODE_CAP:{
							if (datefound&&verticenumfound&&linknumfound&&!verticesparsed&&!linksparced){
                                 ParseNode(graph,  arg3, arg2,  progress, percentage, vertexInfoType);
							}
							else throw new Exception ("File does not complie with the plankton format");
							break;
                        }
						case LINK:
						case LINK_CAP:{
							if (datefound&&verticenumfound&&linknumfound&&verticesparsed&&!linksparced){
                                ParseLink( graph,arg3, arg4,  progress,  percentage);
                            }
							else throw new Exception ("File does not complie with the plankton format");
							break;
                        }
						case ROOT_NODE:{break;}
						case ROOT_NODE_CAP:{break;}
						case TEST:{
							if (datefound&&verticenumfound&&linknumfound&&verticesparsed&&linksparced){
								test(progress,  percentage);
							}
							else throw new Exception ("File does not complie with the plankton format");
							break;}
						default:{break;}
						}
					}
				}
				if (reader != null)
					reader.close();
			}
			catch(IOException ex){

				LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "PlanktonASRelationsParser", "analyzeFile", ex);
			}
			catch (Exception e){
				LoggingManager.getInstance().writeSystem(e.getMessage() + "\n" + e , "PlanktonASRelationsParser", "analyzeFile", e);
			}
		}
	}

    private void ParseNode(GraphDataInterface<Index,BasicVertexInfo> graph,
                             String arg3,String arg2,
                             AbstractExecution progress,
                             double percentage,
                             GraphFactory.VertexInfoType vertexInfoType) throws Exception
    {
            if (!arg2.equalsIgnoreCase(EXP_IGNORE)){
                Index idx = Index.valueOf(Integer.valueOf(arg2));
                graph.addVertex(idx, VertexFactory.createVertexStructure(vertexInfoType, graph.getNumberOfVertices(), arg3));
            }
            verticesfound++;
            if (verticesfound>verticenum)
                throw new Exception ("Number of actual vertices differs from declaration");
            else if (verticesfound==verticenum){
                verticesparsed=true;
                updateLoadProgress(progress, percentage);
            }
    }

    private void ParseLink(GraphDataInterface<Index,BasicVertexInfo> graph,
                            String arg3,String arg4,
                           AbstractExecution progress,
                           double percentage) throws Exception
    {
        Index[] edge =  new Index[]{  Index.valueOf(Integer.valueOf(arg3)), Index.valueOf(Integer.valueOf(arg4))};
        graph.addEdge(Arrays.asList(edge),new EdgeInfo<Index,BasicVertexInfo>());
        linksfound++;
        if (linksfound>linksnum)
            throw new Exception ("Number of actual links differs from declaration");
        else if (linksfound==linksnum){
            linksparced=true;
            updateLoadProgress(progress, percentage);
        }
    }

    private void readLinksNum (String arg2,AbstractExecution progress,double percentage) throws Exception
    {
        if (!arg2.equals("")){
            linksnum=Integer.parseInt(arg2);
            linknumfound=!linknumfound;
            updateLoadProgress(progress, percentage);
        }
        else if (arg2.equals("")||verticenum<0)
            throw new Exception("Unable to parse number of links correctly");
    }

    private void readNodesNum(String arg2) throws  Exception{
        if (!arg2.equals("")){
            verticenum=Integer.parseInt(arg2);
            verticenumfound=!verticenumfound;
        }
        else if (arg2.equals("")||verticenum<0)
            throw new Exception("Unable to parse number of vertices correctly");
    }

    private void test(AbstractExecution progress, double percentage) throws Exception{
        if (linksfound<linksnum)
            throw new Exception ("Number of actual vertices differs from declaration");
        doneparsing=true;// there's no info we need in the G's
        updateLoadProgress(progress, percentage);
    }



    @Override
	public String getextension() {
		return EXTENSION;
	}





}
