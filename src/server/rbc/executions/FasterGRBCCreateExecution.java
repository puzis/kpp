package server.rbc.executions;

import common.ShadowedHistoriedCacheArr;

import javolution.util.FastList;
import javolution.util.Index;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import server.common.DataBase;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;

public class FasterGRBCCreateExecution extends AbstractExecution {

	
	private int _netID;
	private String _comWeightStr;
	private AbsTrafficMatrix _cw = null;
	private FastList<Index> _candidates = null;
	private int _cacheType;
	private int _algID;

	public FasterGRBCCreateExecution(int netID, AbsTrafficMatrix cw)
	{
		this._netID= netID;
		this._cw = cw;
		this._cacheType = ShadowedHistoriedCacheArr.CACHEID;
	}
	
	public FasterGRBCCreateExecution(int netID, String communicationWeightsStr, Object []  cands,int cachetype)
	{
		this._netID= netID;
		this._comWeightStr = communicationWeightsStr;
		if (null!=cands && cands.length>0){// empty set means all group
				this._candidates = new FastList<Index> (cands.length);
				for (Object o : cands)
					this._candidates.add(Index.valueOf(((Integer)o).intValue()));
		}
		this._cacheType = cachetype;
	}

	@Override
	public void run() {
		
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		
		
		try{
				try{
					GraphInterface<Index,BasicVertexInfo> graph = DataBase.getNetwork(this._netID).getGraphSimple();
					
					FasterGRBC fastgrbc = null;
					if (graph != null)
					{
						if (_cw !=null){
							if (this._comWeightStr != null && !this._comWeightStr.isEmpty())
								_cw = new DenseTrafficMatrix(this._comWeightStr, graph.getNumberOfVertices()); // WeightsLoader.loadWeightsFromString(communicationWeightsStr, graph.getNumberOfVertices());
							else
								_cw = new DefaultTrafficMatrix(graph.getNumberOfVertices()); // MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
						}
						fastgrbc =new FasterGRBC(graph, this._cw, this._candidates, this._cacheType);
					}
					this._algID = DataBase.putAlgorithm(fastgrbc, this._netID); 
									success = true;
				}
				catch(Exception ex){
					
					LoggingManager.getInstance().writeSystem("An exception has occured while creating FasterGRBC Algorithm:\n" 
							+ ex.getMessage() + "\n" + ex.getStackTrace(), "FasterGRBCCreateExecution", "run", ex);
					success = false;
				}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while creating FasterGRBC Algorithm.", 
					"FasterGRBCCreateExecution", "run", ex);
			success = false;
		}
		
		LoggingManager.getInstance().writeTrace("Finishing creating FasterGRBC Algorithm.", "FasterGRBCCreateExecution", "run", null);
		reportSuccess((success? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
	}

	@Override
	public Object getResult() {
		return this._algID;
	}
	
}
