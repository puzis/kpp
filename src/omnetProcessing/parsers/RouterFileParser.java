package omnetProcessing.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import omnetProcessing.common.IPArray;
import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.common.Utils;


import server.common.LoggingManager;
import javolution.util.FastMap;
import javolution.util.Index;

public class RouterFileParser {
	
	private int m_vertexNum = -1;
	private int[] m_ipAddress = null;
	private int _base = 1;
	private FastMap<Index, Index> m_nextHops = null;
	
	public RouterFileParser(String filename){
		m_ipAddress = new int[4];
		Arrays.fill(m_ipAddress, 0);
		m_vertexNum = Utils.readVertexNum(filename, true) - _base;
		readInterfaces(filename);
	}
	
	private void readInterfaces(String filename){
		m_nextHops = new FastMap<Index, Index>();
		try{
			FileInputStream fis = new FileInputStream(new File(filename));
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String line = null;
			int previousInterfaceIdx = -1;
			while ((line = reader.readLine())!=null){
				
				// Map Ethernet card to the neighbor router
				if (line.startsWith("#")){
					// Line structure: 
					// # ethernet card 1 of R1 - connected to R2:
					String[] tokens = line.split("[\\s\\t]");
					int interfaceIdx = Integer.parseInt(tokens[3]);
					String neighborStr = tokens[tokens.length-1];
					
					if (!neighborStr.startsWith("H")){ 
						// If the Ethernet card is not connected to an end host then 
						// map the card to the (next-hop) router.  
						neighborStr = neighborStr.substring(1, neighborStr.length()-1);
						int neighbor = Integer.parseInt(neighborStr) - _base;
						m_nextHops.put(Index.valueOf(interfaceIdx), Index.valueOf(neighbor));
					}
					else{ // If the Ethernet card is connected to an end-host, 
						  // then the packet arrived at its destination and stops 
						  // (the next-hop is the current router).
						m_nextHops.put(Index.valueOf(interfaceIdx), Index.valueOf(m_vertexNum));
					}
					
					previousInterfaceIdx = interfaceIdx;
				}
				else if (line.indexOf("inet_addr")!=-1){
					
					int pos = line.indexOf(":")+1;
					int[] ip = Utils.parseIP(line.substring(pos).trim());
					if (Utils.compareIPAddresses(ip, m_ipAddress)==1){
						m_ipAddress = ip;
					}
					IpToVertexIndexMap.put(new IPArray(ip), m_vertexNum);
					
					if (previousInterfaceIdx==0){
						// If the current IP address belongs to an end-host
						// then apply the mask 255.255.255.0 to the IP and
						// add it to the Ip to vertex map and to the hosts list. 
						int[] ip0 = Arrays.copyOf(ip, ip.length);
						ip0[3] = 0;
						IpToVertexIndexMap.put(new IPArray(ip0), m_vertexNum);
						IpToVertexIndexMap.addHost(m_vertexNum, new IPArray(ip0));
						
						int[] ip2 = Arrays.copyOf(ip, ip.length);
						ip2[3] = 2;
						IpToVertexIndexMap.put(new IPArray(ip2), m_vertexNum);
						IpToVertexIndexMap.addHost(m_vertexNum, new IPArray(ip2));
					}
				}
				else if (line.trim().equals("ifconfigend.")){
					break;
				}
			}
			
			fis.close();
			isr.close();
			reader.close();
		}
		catch(IOException ex){
			LoggingManager.getInstance().writeSystem(ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex) , "RouterFileParser", "readInterfaces", ex);
		}
	}
	
	
	/**
	 * @return the index of the vertex in base=0.
	 */
	public int getVertexNum(){
		return m_vertexNum;
	}
	
	/**
	 * @return mapping of the interfaces (ethernet cards) to the neighboring routers. 
	 */
	public FastMap<Index, Index> getInterfacesMapping(){
		return m_nextHops;
	}
	
	public int[] getIP(){
		return m_ipAddress;
	}
}
