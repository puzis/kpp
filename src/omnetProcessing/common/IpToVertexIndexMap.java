package omnetProcessing.common;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.Index;

public class IpToVertexIndexMap {

	public static FastMap<IPArray, Index> IP_INDEX_MAP = new FastMap<IPArray, Index>();
	public static FastMap<Index, IPArray> INDEX_IP_MAP = new FastMap<Index, IPArray>();
	public static FastMap<Index, IPArray> HOSTS = new FastMap<Index, IPArray>();
	
	public static boolean IS_SET = false;
	
	public static void markInitialized(){
		IS_SET = true;
	}
	
	public static void put(IPArray ip, int vertexNum){
		IP_INDEX_MAP.put(ip, Index.valueOf(vertexNum));
		INDEX_IP_MAP.put(Index.valueOf(vertexNum), ip);
	}
	
	public static Index getVertexNum(IPArray ip) throws Exception{
		if (!IS_SET)
			throw new Exception("IP to Index mapping has not been initialized yet. Please load router config files first (R*.irt files).");
		return IP_INDEX_MAP.get(ip);
	}
	
	public static IPArray getIP(Index vertexNum) throws Exception{
		if (!IS_SET)
			throw new Exception("IP to Index mapping has not been initialized yet. Please load router config files first (R*.irt files).");
		return INDEX_IP_MAP.get(vertexNum);
	}
	
	public static void addHost(int vertexNum, IPArray ip){
		HOSTS.put(Index.valueOf(vertexNum), ip);
	}
	
	public static boolean isHost(IPArray ip) throws Exception{
		if (!IS_SET)
			throw new Exception("IP to Index mapping has not been initialized yet. Please load router config files first (R*.irt files).");
		return HOSTS.containsValue(ip);
	}
	
	public static IPArray getHost(Index vertexNum) throws Exception{
		if (!IS_SET)
			throw new Exception("IP to Index mapping has not been initialized yet. Please load router config files first (R*.irt files).");
		return HOSTS.get(vertexNum);
	}
}
