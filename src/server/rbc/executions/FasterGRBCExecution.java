package server.rbc.executions;

import server.common.LoggingManager;
import server.execution.AbstractExecution;

public class FasterGRBCExecution extends AbstractExecution {

	
	private IExecutable _exe;
	protected String _opName;
	private Object _res;
	
	public FasterGRBCExecution(IExecutable executable, String opName)
	{
		this._exe = executable;
		this._opName = opName;
	}
	
	@Override
	public void run() {
		boolean success = true;
		reportSuccess(AbstractExecution.PHASE_FAILURE, AbstractExecution.PHASE_NONCOMPLETE);
		
		
		try{
				try{
					this._res = this._exe.execute();
				}
				catch(Exception ex){
					
					LoggingManager.getInstance().writeSystem("An exception has occured while: "+ this._opName + "\n" 
							+ ex.getMessage() + "\n" + ex.getStackTrace(), this.getClass().getName(), "run", ex);
					success = false;
				}
		}
		catch(RuntimeException ex)
		{
			LoggingManager.getInstance().writeSystem("A RuntimeException has occured while "+this._opName, 
					this.getClass().getName(), "run", ex);
			success = false;
		}
		
		LoggingManager.getInstance().writeTrace("Finishing :"+this._opName, this.getClass().getName(), "run", null);
		reportSuccess((success? AbstractExecution.PHASE_SUCCESS : AbstractExecution.PHASE_FAILURE), AbstractExecution.PHASE_COMPLETE);
		
	}

	@Override
	public Object getResult() {
		return this._res;
	}

}
