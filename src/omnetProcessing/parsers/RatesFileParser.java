package omnetProcessing.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import omnetProcessing.common.Utils;


import server.common.LoggingManager;

public class RatesFileParser {
	
	private int m_vertexNum = -1;
	private int _base = 1;
	private double[] m_rates = null; 
	
	public RatesFileParser(String filename, int numberOfVertices){
		m_vertexNum = Utils.readVertexNum(filename, false) - _base;
		m_rates = new double[numberOfVertices];
		m_rates[m_vertexNum] = 0;
		readRates(filename);
	}
	
	private void readRates(String filename){
		try{
			FileInputStream fis = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = null;
			while ((line = reader.readLine())!=null){
				String[] tokens = line.split("[\\s\\t]");
				String destStr = tokens[0].substring(1, tokens[0].length());
				int destNum = Integer.parseInt(destStr) - _base;
				double rate = Double.parseDouble(tokens[2]);
				m_rates[destNum] = rate;
			}
			fis.close();
			isr.close();
			reader.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "RatesFileParser", "readRates", ex);
		}
	}
	
	/**
	 * @return the index of the vertex in base=0.
	 */
	public int getVertexNum(){
		return m_vertexNum;
	}
	
	public double[] getRates(){
		return m_rates;
	}
}
