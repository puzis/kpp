package omnetProcessing;

import javolution.util.FastMap;
import javolution.util.Index;
import omnetProcessing.common.IpToVertexIndexMap;
import omnetProcessing.parsers.RouterFileParser;

public class RoutersConfigsLoader {

	private String _dir = "";
	private int _numberOfVertices = 0;
	
	public RoutersConfigsLoader(String dir, int numberOfVertices){
		_dir = dir;
		_numberOfVertices = numberOfVertices;
	}
	
	public FastMap<Index, Index>[] loadRouterInterfaces(){
		FastMap<Index, Index>[] ifcMaps = new FastMap[_numberOfVertices];
		
		for (int i=1; i<=_numberOfVertices; i++){
			RouterFileParser rParser = new RouterFileParser(_dir + "R" + i + ".irt");
			FastMap<Index, Index> ifcMapping = rParser.getInterfacesMapping();
			ifcMaps[i-1] = ifcMapping;
		}
		IpToVertexIndexMap.markInitialized();
		return ifcMaps;
	}
}
