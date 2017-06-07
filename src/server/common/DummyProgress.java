package server.common;

import server.execution.AbstractExecution;

public class DummyProgress extends AbstractExecution{

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void run() {}
}
