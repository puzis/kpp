package server.saritKraus;

import javolution.util.Index;
import server.common.DataBase;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.saritKraus.SKAlg;

public class SKController {
	
	public static final String ALIAS = "SK";
	
	
	public Object[] saritKraus(int net_id,Object[] placeholders,int k) {
		GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(net_id).getGraphSimple();
		Index[] p = new Index[placeholders.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = Index.valueOf((Integer) placeholders[i]);
		}
		int[] res = SKAlg.sk(graph, p,k); 
		Object[] ores = new Object[res.length];
		for (int i = 0; i < ores.length; i++) {
			ores[i] = new Integer(res[i]);
		}
		return ores;
	}

}
