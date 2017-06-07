package omnetProcessing.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import omnetProcessing.common.IPArray;
import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.common.Utils;

import javolution.util.Index;
import server.common.LoggingManager;

public class TrafficMatrixFileParser {

	private double[][] _tm = null;
	private String _dirName;
	private String[] _filenames = new String[0];
	
	public TrafficMatrixFileParser(String dirName, int numberOfVertices){
		_dirName = dirName;
		_tm = new double[numberOfVertices][numberOfVertices];
		listFiles();
	}
	
	private void listFiles(){
		File dir = null;
		try {
			dir = new File (_dirName).getCanonicalFile();
		} catch (IOException ex) {
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "TrafficMatrixFileParser", "listFiles", ex);
		}
		
		if (dir.isDirectory()){
			_filenames = dir.list();
			for (int i=0; i<_filenames.length; i++)
				_filenames[i] = _dirName + _filenames[i];
		}
		else {
			System.out.println(_dirName + "is not a directory.");
		}
	}
	
	public double[][] parse(){
		for (String filename : _filenames){
			readFile(filename);
		}
		
		return _tm;
	}
	
	private void readFile(String filename){
		try{
			FileInputStream fis = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = null;
			// [192.168.0.15, 192.150.7.2]= 174153480
			while ((line = reader.readLine())!=null){
				line = line.replace("[", "");
				String[] tokens = line.split("[\\[\\]=:,<>()\\s\\t]+");
				
				IPArray srcIP = new IPArray(Utils.parseIP(tokens[0]));
				IPArray destIP = new IPArray(Utils.parseIP(tokens[1]));
				long bits = Long.parseLong(tokens[2]);
				
				Index s = IpToVertexIndexMap.getVertexNum(srcIP);
				Index t = IpToVertexIndexMap.getVertexNum(destIP);
				
				_tm[s.intValue()][t.intValue()] += bits;
				
			}
			fis.close();
			isr.close();
			reader.close();
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "TrafficMatrixFileParser", "readFile", ex);
		}
	}
}