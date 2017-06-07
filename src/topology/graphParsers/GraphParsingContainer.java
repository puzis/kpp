package topology.graphParsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

import javolution.util.Index;

import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import topology.BasicVertexInfo;
import topology.GraphDataInterface;
import topology.GraphFactory;
import topology.SerializableGraphRepresentation;
import topology.graphParsers.common.FileLister;
import topology.graphParsers.common.FilenameExtentionFilter;

/**
 * @author Omer Zohar
 * general graph parsing container
 * accepts classes that implements NetworkGraphParser for the parsing part
 */
public class GraphParsingContainer {
	/**
	 * list of files to parse
	 */
	private String [] m_saFileList;
	private NetworkGraphParser m_netparser;
	public final static String PARSEALL="PARSEALL";
	private static final String DOT = ".";
	private String ext;
	
	/**
	 * 	 * Constructor
	 * @param parser the parser...
	 * @param dir directory to parse the files in
	 * @param ext extension of files to parse
	 */
	public GraphParsingContainer(NetworkGraphParser parser,String dir) {
		ext = parser.getextension();
		FileLister f=new FileLister(dir,new FilenameExtentionFilter(ext));
		m_saFileList=f.getfilesfromdir();
		m_netparser=parser;
//		System.out.println("Trying to read files from : "+dir);
//		System.out.println("Reading files with extension : "+ext);
//		System.out.println("# Files:"+m_saFileList.length);
		//System.out.println(m_saFileList[1]);
		//System.out.println(m_saFileList[2]);
	}

	
	private int lineCount (String filename){
		int count=0;
		try
        {
         	RandomAccessFile randFile = new RandomAccessFile(filename,"r");
        	long lastRec=randFile.length();
        	randFile.close();
        	FileReader fileRead = new FileReader(filename);
        	LineNumberReader lineRead = new LineNumberReader(fileRead);
        	lineRead.skip(lastRec);
        	count=lineRead.getLineNumber()-1;
        	fileRead.close();
        	lineRead.close();
        	randFile.close();
        }
		catch(IOException ex){
				LoggingManager.getInstance().writeSystem("Couldn't Read from " + filename + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "LineCount", ex);		
		}
		return count;
		
	}
	
	
	
	/**
	 * loads the files one by one, parse'em and save the data to a graph
	 * @param parse a specific file from the directory. use "PARSEALL" to parse the entire dir
	 * @return a graph consists of the merged files' data
	 */
	public void execute (String filename, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {		
		String filename_with_ext = null;
		
		if (!filename.contains(DOT))
			filename_with_ext=ServerConstants.DATA_DIR+filename+"."+ext;
		else 
			filename_with_ext=ServerConstants.DATA_DIR+filename;
		
		FileInputStream fis = null;
		try{
			boolean filefound = false;
			for (int i=0;i<m_saFileList.length;i++)
				if (filename.equals(PARSEALL)||m_saFileList[i].equals(filename_with_ext)){
					filefound=true;
					fis=loadFile(this.m_saFileList[i]);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
					m_netparser.analyzeFile(reader, new DummyProgress(), 1, graph,vertexInfoType);//filename does not matter for this parser!
					fis.close();
					reader.close();					
				}
			
			if (!filefound) {
				System.out.println("File not found : "+filename_with_ext);
				throw new FileNotFoundException("The file: " + filename + " does not exist at the data folder" );
			}
		}
		catch (IOException e){
			LoggingManager.getInstance().writeSystem("Error while parsing graphs"+"\n" + e.getMessage() + "\n" + e.getStackTrace() , "GraphParsingContainer", "execute", e);
		}
	}
	
	/** Updates the Progress of the operation according to the percentage
	 * @param progress
	 * @param percentage
	 */

	
	/** Loads a Network file, analyze it and builds a graph of the network
	 * @param filename source filename
	 * @param progress the progress indicator
	 * @param percentage percentage indicator
	 * @return Graph - an analyzed graph of the net
	 */
	public FileInputStream loadFile(String filename)
    {
      	File file = new File(filename);
        FileInputStream fis = null;
        try {          
           fis = new FileInputStream(file);
        }
        catch(FileNotFoundException ex) {
        	LoggingManager.getInstance().writeSystem("Couldn't find the network file: " + filename + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "GraphParsingContainer", "loadFile", ex);
        	try {
            	if (fis != null)
            		fis.close();
            }
            catch(IOException exio){
            	LoggingManager.getInstance().writeSystem("Couldn't close FileInputStream to: " + file.getAbsoluteFile() + "\n" + exio.getMessage() + "\n" + exio.getStackTrace() , "GraphParsingContainer", "loadFile", exio); 
            }
            fis=null;
        }
        return fis;
    }
	
	
	/**
	 * @param args
	 */
/*	public static void main(String[] args) {
		PlanktonASRelationsParser parser=new PlanktonASRelationsParser();
		GraphParsingContainer n=new GraphParsingContainer(parser,"D:\\Java\\Projects\\betweness\\res\\plankton\\199802");
		Graph g=n.execute(GraphParsingContainer.PARSEALL);
		System.out.println(g.getNumberOfVertices());
		System.out.println(g.getNumberOfEdges());
	}*/
//	public static void main(String[] args) {
//		CaidaAsGraphParser parser=new CaidaAsGraphParser();
//		GraphParsingContainer n=new GraphParsingContainer(parser,"D:\\Java\\Projects\\betweness\\Betweeness\\res\\");
//		GraphInterface<Index> g = n.execute("cutted_as-rel", false);
//		System.out.println(g.getNumberOfVertices());
//		System.out.println(g.getNumberOfEdges());
//	}

	public void executeContent(String fileContent, GraphDataInterface<Index,BasicVertexInfo> graph, GraphFactory.VertexInfoType vertexInfoType) {
		InputStream is = new ByteArrayInputStream(fileContent.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		try{
			m_netparser.analyzeFile(reader,new DummyProgress(), 1,graph,vertexInfoType);
		}
		catch (Exception e){
			LoggingManager.getInstance().writeSystem("Error while parsing graphs"+"\n" + e.getMessage() + "\n" + e.getStackTrace() , "GraphParsingContainer", "execute", e);
		}
		finally {
			try {
				is.close();
				reader.close();
			} catch (IOException e){
				// do nothing
			}
		}
	}
}
