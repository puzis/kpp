package topology.graphParsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.common.ServerConstants;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphAsHashMap;
import topology.GraphInterface;
import topology.GraphRegularExpressions;
import topology.VertexInfo;
import Opus5.BinarySearchTree;
import Opus5.Comparable;
import Opus5.Enumeration;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.shortestPath.ShortestPathAlgorithmInterface;

/**
 * @author Omer Zohar
 *
 */
public class AsRelationshipParser {
	
	public AsRelationshipParser(){
		
	}
	private class AS_Links implements Comparable{//this is a node in the BST
		private int m_iASvertice=0;
		private FastList<Index> m_llLinks=new FastList<Index>();;
		
		public AS_Links (int vertice){
			m_iASvertice=vertice;
			m_llLinks=new FastList<Index>();
		}
		
		public int compare (Comparable obj){
			if (obj instanceof AS_Links) {
				AS_Links tmp=(AS_Links)obj;
				if (this.m_iASvertice<tmp.getASvertex())
					return -1;
				else if (this.m_iASvertice>tmp.getASvertex())
					return 1;
				else return 0;			
			}
			else return -2;
		}
		public void addLink(int AStag){					
			this.m_llLinks.addFirst(Index.valueOf(AStag));
		}
		public int getASvertex(){return m_iASvertice;}
		public int getLinkedAS(){return this.m_llLinks.removeFirst().intValue();}
		public boolean isLinkedASEmpty(){return this.m_llLinks.isEmpty();}
		
		public boolean 	isEQ(Comparable object){return false;}
		public boolean 	isGE(Comparable object){return false;}
		public boolean 	isGT(Comparable object){return false;}
		public boolean 	isLE(Comparable object){return false;}
		public boolean 	isLT(Comparable object){return false;}
		public boolean 	isNE(Comparable object){return false;}
	}
//END OF private class AS_Links
	
	private class ASRelationShipStructure {// this is a temporary structure to order the verices befor entering the matrix
		private int m_iCount;
		private BinarySearchTree m_bstVertices;	
		public ASRelationShipStructure (){
			m_iCount=0;
			m_bstVertices=new BinarySearchTree();		
		}
		
		//adds a vertex with no adjactencies
		public int addASVertex (int vertex){//returns 1 if succeed, -1 if vertex already exists.
			AS_Links as=new AS_Links(vertex);
			try {//try to enter the new vertex			
				m_bstVertices.insert(as);
				m_iCount++;
			}
			catch (IllegalArgumentException ex){//if vertex exists, do nothing.
				return -1; }	
			return 1;
		}
		public void addAsLink(AS_Links as, int dest){
			AS_Links asadd=(AS_Links)m_bstVertices.find(as);
			if (asadd!=null){
				asadd.addLink(dest);	
			}
			else {
				as.addLink(dest);
				m_bstVertices.insert(as);
				m_iCount++;
			}
		}
		
		public  Enumeration getEnumerator(){//return an Enumerator for the tree.
			return m_bstVertices.getEnumeration();
		}
	}
//END OF private class ASRelationShipStructure
	
	//counts the lines in the file
	public int lineCount (String filename){
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
	
	//loads the file, analize and the return the graph.s
	public GraphInterface<Index,BasicVertexInfo> loadFile(String filename, AbstractExecution progress, double percentage)
    {
        File file = new File(filename);
        int Linesinfile=0;
        FileInputStream fis = null;
        GraphInterface<Index,BasicVertexInfo> graph = null;
        
        Linesinfile=lineCount(filename);
        //loading the file
        try {          
           fis = new FileInputStream(file);
           //analizing phase
           graph = analyzeFile(fis, progress, percentage,Linesinfile);           
        }
        catch(FileNotFoundException ex) {
        	LoggingManager.getInstance().writeSystem("Couldn't find the AS Relationship network file: " + filename + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "loadFile", ex);
        }
        finally {
            try {
            	if (fis != null)
            		fis.close();
            }
            catch(IOException ex){
            	LoggingManager.getInstance().writeSystem("Couldn't close FileInputStream to: " + file.getAbsoluteFile() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "loadFile", ex); 
            }
        }
        return graph;
    }
	
	private void updateLoadProgress(AbstractExecution progress, double percentage)
	{
		double p = progress.getProgress();
		p += 0.5 * percentage;
		progress.setProgress(p);
	}
	
	private void cleanClose(BufferedReader reader, String msg)
    {
    	System.err.println(msg);
    	try{
    		if (reader != null)
    			reader.close();
        }
        catch(IOException ex) {
        	LoggingManager.getInstance().writeSystem("An exception has occured while closing BufferedReader.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "cleanClose", ex);
        }

    }
	
	private GraphInterface<Index,BasicVertexInfo> analyzeFile(InputStream in, AbstractExecution progress, double percentage,int Linesinfile)
    {
    	GraphInterface<Index,BasicVertexInfo> graph = null;
    	
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	ASRelationShipStructure relationstruct=new ASRelationShipStructure();
    	//reading from file in the temp structure
    	int res = fillASRelationsList(relationstruct,reader,Linesinfile);
    	
        if (res != -1){
//        	int i=relationstruct.getVerticesCount();
       		graph = new GraphAsHashMap<Index,BasicVertexInfo>();//building the graph with the found number of verices
        }
        else{
        	cleanClose(reader, "Vertices Could not be read from File or File is empty, exiting the program.");
        	graph = null;
        }
        updateLoadProgress(progress, percentage);
        	
        res = writeVerticesToGraph(relationstruct, graph);//filling the graph
        if (res == -1){
        	cleanClose(reader, "Could not write vertices to graph properly, exiting the program.");
        	graph = null;
        }
        updateLoadProgress(progress, percentage);
        	
           
        try{
        	if (reader != null)
        		reader.close();
        }
        catch(IOException ex)
        {
        	LoggingManager.getInstance().writeSystem("An exception has occured while closing BufferedReader.\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "analyzeFile", ex);
        	graph = null;
        }
        return graph;
    }
	
	
	public int fillASRelationsList(ASRelationShipStructure relationstruct,BufferedReader reader,int Linesinfile)
	{
		String line=null;
		try {
			//while ((line=reader.readLine()).charAt(0)=='#'){}
			for (int i=0;(i< Linesinfile) &&((line=reader.readLine())!=null);i++ )
			{
				while (line.charAt(0)=='#'){line=reader.readLine();}//skipping notes
				Matcher verticesLine = GraphRegularExpressions.ASRELATIONSHIP.matcher(line);
				if (verticesLine.find())//parsing data coloums
				{
					int arg1=Integer.parseInt(verticesLine.group(1));
					int arg2=Integer.parseInt(verticesLine.group(2));
					int direction=Integer.parseInt(verticesLine.group(3));
					relationstruct.addASVertex(arg1);//we're adding vertices in any case,
					relationstruct.addASVertex(arg2);//cause we dont know if we met them before
					switch (direction){ //arcs will be added only when were sure the vertices exist.
						case -1:{relationstruct.addAsLink(new AS_Links(arg2), arg1);break;}
						case 1:{relationstruct.addAsLink(new AS_Links(arg1), arg2);break;}
						default:{
							relationstruct.addAsLink(new AS_Links(arg2), arg1);
							relationstruct.addAsLink(new AS_Links(arg1), arg2);
						}
					}
				}
			}
		}
		catch(IOException ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "AsRelationshipParser", "FillASRelationsList", ex);
			return -1;
		}
		return 1;
	}
	
	
	public int writeVerticesToGraph(ASRelationShipStructure relationstruct,GraphInterface<Index,BasicVertexInfo> graph){
		Enumeration ASenumarator=relationstruct.getEnumerator();
		int serial=0;
		FastMap<Index, Index> AStoserial=new FastMap<Index, Index>();//mapping serial numbers to AS numbers
		while (ASenumarator.hasMoreElements()){//adding vertexes only, and adding the vertices to the graphs, filling the serial no. -> VS num table
			Object AS=ASenumarator.nextElement();
			if (AS instanceof AS_Links){
				int originvertice=((AS_Links)AS).getASvertex();
				if (AStoserial.get(Index.valueOf(originvertice))==null){
					try {
						graph.addVertex(Index.valueOf(serial) ,new VertexInfo(serial,Integer.toString(originvertice)));
						AStoserial.put(Index.valueOf(originvertice),Index.valueOf(serial));
						serial++;
					}
					catch (Exception ex){
						LoggingManager.getInstance().writeSystem("Duplicates in temp structure, this is not suppose to happen" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "WriteVerticesToGraph", "WriteVerticesToGraph", ex);
						graph=null;
						return -1;
					}
				}
			}
			else {LoggingManager.getInstance().writeSystem("Tree Node is not in the right format" + "\n" , "WriteVerticesToGraph", "WriteVerticesToGraph", new Exception());}
		}
		
		//filling the arcs by reading each vertex's adjactories
		ASenumarator=relationstruct.getEnumerator();
		while (ASenumarator.hasMoreElements()){
			Object AS=ASenumarator.nextElement();
			if (AS instanceof AS_Links){
				int originvertice=((AS_Links)AS).getASvertex();
				int originindex=AStoserial.get(Index.valueOf(originvertice)).intValue();
				while (!((AS_Links)AS).isLinkedASEmpty()){
					int destvertice =((AS_Links)AS).getLinkedAS();
					int destindex=AStoserial.get(Index.valueOf(destvertice)).intValue();
					if(!graph.isEdge(Index.valueOf(originindex), Index.valueOf(destindex)))
						graph.addEdge(Index.valueOf(originindex), Index.valueOf(destindex));
				}
			}
			else {LoggingManager.getInstance().writeSystem("Tree Node is not in the right format" + "\n" , "WriteVerticesToGraph", "WriteVerticesToGraph", new Exception());}
		}
		return 1;
	}
	
	public static void main(String[] args){
		AsRelationshipParser n = new AsRelationshipParser();
		GraphInterface<Index,BasicVertexInfo> g = n.loadFile("res/cutted_as-rel.txt", new DummyProgress(), 1);
		System.out.println(g.getNumberOfVertices());
		System.out.println(g.getNumberOfEdges());
		try{
			DataWorkshop dw = new DataWorkshop(ShortestPathAlgorithmInterface.DEFAULT, g, true, new DummyProgress(), 1);
			dw.saveToDisk(ServerConstants.DATA_DIR + "as.dw", new DummyProgress(), 1);
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

}
