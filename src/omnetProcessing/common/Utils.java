package omnetProcessing.common;

public class Utils {

	public static int readVertexNum(String filename, boolean router){
		String entity = "";
		if (router)
			entity = "R";
		else
			entity = "H";
		int dotPos = filename.indexOf(".");
		int _entity_Pos = filename.indexOf("/" + entity)+1;
		String numStr = filename.substring(_entity_Pos+1, dotPos);
		return Integer.parseInt(numStr);
	}
	
	/**
	 * @param ip0 - IP address of the form x.y.z.k
	 * @param ip1 - IP address of the form x.y.z.k
	 * @return 0 if the IPs are equal, 1 if ip0 is "higher", and -1 if ip1 is "higher".
	 */
	public static int compareIPAddresses(int[] ip0, int[] ip1){
		int result = 0;
		for (int i=0; i<4; i++){
			if (ip0[i]>ip1[i]){
				result = 1;
				break;
			}
			if (ip0[i]<ip1[i]){
				result = -1;
				break;
			}
		}
		
		return result;
	}
	
	public static int[] parseIP(String ipStr){
		String[] ipTokens = ipStr.split("\\.");
		int[] ip = new int[4];
		for (int i=0; i<4; i++){
			ip[i] = Integer.parseInt(ipTokens[i]);
		}
		return ip;
	}
}
