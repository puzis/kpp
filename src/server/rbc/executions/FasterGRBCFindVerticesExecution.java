package server.rbc.executions;

import javolution.util.Index;
import server.common.DataBase;
import server.common.DummyProgress;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.rbc.FasterGRBC;
import algorithms.centralityAlgorithms.rbc.GreedyContributionRBC;
import algorithms.centralityAlgorithms.rbc.sets.DynamicRBCSet;

public class FasterGRBCFindVerticesExecution extends AbstractExecution {

	private int _algID;
	private int _groupSize;
	private int[] _givenVertices;
	private int[] _candidates;
	private Object[] _res;

	public FasterGRBCFindVerticesExecution (int algID, int groupSize, int[] givenVertices, int[] candidates) {
		
		this._algID = algID;
		this._groupSize = groupSize;
		this._givenVertices = givenVertices;
		this._candidates = candidates;
	}
	
		
		//return res;


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try{
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
			
			FasterGRBC alg = (FasterGRBC)DataBase.getAlgorithm(this._algID);
			DynamicRBCSet set = new DynamicRBCSet(alg);
			Index[] tmp = GreedyContributionRBC.findVertices(set, this._groupSize, this._givenVertices, this._candidates, new DummyProgress(), 1.0);
			this._res = new Object[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				this._res[i] = Integer.valueOf(tmp[i].intValue());
			}
			
			reportSuccess(AbstractExecution.PHASE_SUCCESS, AbstractExecution.PHASE_COMPLETE);
			
		}
		catch(Exception ex)
		{
			LoggingManager.getInstance().writeSystem(ex.getMessage(), "FasterGRBCFindCentralVerticesExecution", "run", ex);
			reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_COMPLETE);
		}
		
	}

	@Override
	public Object[] getResult() {
		return this._res;
	}
}
