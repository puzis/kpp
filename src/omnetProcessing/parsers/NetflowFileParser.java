package omnetProcessing.parsers;

import java.io.BufferedReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import omnetProcessing.common.IPArray;
import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.common.Utils;


import algorithms.tmEstimation.NetFlow;

import javolution.util.Index;

import server.common.LoggingManager;
import topology.graphParsers.common.FileLister;
import topology.graphParsers.common.FilenameExtentionFilter;

public class NetflowFileParser {

	private NetFlow _flows = null;
	public NetflowFileParser(String dir) {
		_flows = new NetFlow();
		
		FileLister f = new FileLister(dir, new  FilenameExtentionFilter("txt"));
		String[] filesList = f.getfilesfromdir();

		for (String filename : filesList){
			readFile(filename);
		}
	}
	
	private void readFile(String filename){
		try{
			FileInputStream fis = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = null;
			while ((line = reader.readLine())!=null){
				String[] tokens = line.split("[:,<>()\\s\\t]+");
				IPArray vIP = new IPArray(Utils.parseIP(tokens[0]));
				IPArray srcIP = new IPArray(Utils.parseIP(tokens[1]));
				IPArray destIP = new IPArray(Utils.parseIP(tokens[2]));
				
				// Ignore if the source or the target are not end hosts.
				if (!IpToVertexIndexMap.isHost(srcIP)|| !IpToVertexIndexMap.isHost(destIP))
					continue;
				
				long bits = Long.parseLong(tokens[3]);
				
				Index v = IpToVertexIndexMap.getVertexNum(vIP);
				Index s = IpToVertexIndexMap.getVertexNum(srcIP);
				Index t = IpToVertexIndexMap.getVertexNum(destIP);
				
				_flows.add(v, s, t, bits);
			}
			fis.close();
			isr.close();
			reader.close();
		}
		catch(Exception ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "NetflowFileParser", "readFile", ex);
		}
	}
	
	public NetFlow getNetFlow(){
		return _flows;
	}
}
