package omnetProcessing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javolution.util.FastList;

import javolution.util.FastMap;
import javolution.util.Index;
import omnetProcessing.common.RouterTable;
import omnetProcessing.parsers.RTFileParser;
import omnetProcessing.parsers.RTFileParser.RoutingTableEntry;
import server.common.LoggingManager;

import common.Pair;

public class RoutersTablesLoader {

	private String _dir = "";
	private int _numberOfVertices = 0;
	private FastMap<Index, Index>[] _ifcMaps = null;
	private double _totalTime = 3500.0;
	
	public RoutersTablesLoader(String dir, int numberOfVertices, FastMap<Index, Index>[] ifcMaps){
		_dir = dir;
		_numberOfVertices = numberOfVertices;
		_ifcMaps = ifcMaps;
	}

	public RouterTable[] loadRouterTables(double threshold){
		RouterTable[] rts = new RouterTable[_numberOfVertices];
		
		for (int i=1; i<=_numberOfVertices; i++){
			FastList<Pair<RoutingTableEntry, Double>> rtList = new FastList<Pair<RoutingTableEntry, Double>>(); 
			FastList<Pair<String, Double>> stabilizedTables = selectStabilized(_dir + "R" + i + ".txt", threshold);
			RTFileParser rtParser = new RTFileParser(_ifcMaps[i-1]);
			
			for (Pair<String, Double> weightedTableStr : stabilizedTables){
				
				String tableStr = weightedTableStr.getValue1();
				Double weight = weightedTableStr.getValue2();
				FastList<Pair<RoutingTableEntry, Double>> rt = rtParser.readRoutingTable(new ByteArrayInputStream(tableStr.getBytes()), weight);
				if (rt.size()==_numberOfVertices)
					rtList.addAll(rt);
			}
			rts[i-1] = new RouterTable(i-1, rtList);
		}
		
		return rts;
	}
	
	private FastList<Pair<String, Double>> selectStabilized(String filename, double threshold){
		FastList<Pair<String, Double>> stabilizedRT = new FastList<Pair<String, Double>>();
		
		double lastTimeStabilized = 0.0, lastTime = 0.0, newTime=0.0;
		try{
			File file = new File(filename);
			if (file.exists()){
			
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader reader = new BufferedReader(isr);
			
				StringBuilder buffer = null;
				
				String line = null;
				line = reader.readLine();
				while (line!=null){
					newTime = readTime(line);
					if (newTime-lastTime>threshold){//The previous router table has been a stabilized table.  
						double time = (newTime-lastTimeStabilized)/_totalTime;
						stabilizedRT.add(new Pair<String, Double>(buffer.toString(), time));
						lastTimeStabilized = newTime;
					}
					buffer = new StringBuilder();
					line = reader.readLine();
					// Buffer the current routing table until the next routing table is reached.
					while(line!=null&&!line.startsWith("T:")){
						buffer.append(line).append("\n");
						line = reader.readLine();
					}
					lastTime = newTime;
				}
				// The last routing table is assumed to be stabilized too.
				double time = (_totalTime-lastTimeStabilized)/_totalTime;
				stabilizedRT.add(new Pair<String, Double>(buffer.toString(), time));
				
				fis.close();
				isr.close();
				reader.close();
			}
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "RoutersTablesLoader", "selectStabilized", ex);
		}
		
		return stabilizedRT;
	}
	
	private double readTime(String line){
		String timeStr = line.substring(2);
		return Double.parseDouble(timeStr);
	}
	
	
//	public RouterTable[] loadRouterTables(){
//		RouterTable[] rts = new RouterTable[_numberOfVertices];
//		
//		for (int i=1; i<=_numberOfVertices; i++){
//			FileLister f = new FileLister(_dir, new RTFilenameFilter("txt", Integer.toString(i)));
//			String[] filesList = f.getfilesfromdir();
//			FastList<Pair<Index, Pair<Index, Integer>>> rtList = new FastList<Pair<Index,Pair<Index, Integer>>>(); 
//			for (String filename : filesList){
//				
//				RTFileParser rtParser = new RTFileParser(filename, _ifcMaps[i-1]);
//				rtList.addAll(rtParser.getRoutingTable());
//			}
//			rts[i-1] = new RouterTable(i-1, rtList);
//		}
//		
//		return rts;
//	}
}
