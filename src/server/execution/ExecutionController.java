package server.execution;

import server.AlgorithmsServer;
import server.common.DataBase;

public class ExecutionController 
{
	//todo: eliminate memory leak: execution creation without destroy.	
	public static final String ALIAS = "Execution";
	
	/** Returns the progress of the given execution.
	 * @param Execution index 
	 * @return Progress in the range [0, 1] */
	public double getProgress(int exeID){
		double progress = DataBase.getExecution(exeID).getProgress(); 
		return progress;
	}
	
	/** Returns the success of the given execution.
	 * @param Execution index 
	 * @return Success is 0 if the execution has been successful, and -1 if it failed. */
	public int getSuccess(int exeID){
		int success = DataBase.getExecution(exeID).getSuccess(); 
		return success;
	}
	
	/** @param Execution index
	 *  @return Result of the execution, its class depends on the execution.
	 */
	public Object getResult(int exeID){
		Object result = DataBase.getExecution(exeID).getResult();
		return result;
	}
	
	/** Cancels the execution during its run.
	 * @param Execution index
	 * @return 1
	 */
	public int cancel(int exeID)
	{
		DataBase.getExecution(exeID).cancel();
		(new AlgorithmsServer()).getMemoryConsumption();
		return 1;
	}
}
