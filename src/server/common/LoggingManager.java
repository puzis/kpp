package server.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingManager
{
	private static LoggingManager loggingManager = new LoggingManager();
	private Handler messageFH = null;
	private Handler systemFH = null;
	private Handler traceFH = null;
	
	private Logger messagelogger = Logger.getLogger("messageLogger");
	private Logger systemLogger = Logger.getLogger("systemLogger");
	private Logger traceLogger = Logger.getLogger("traceLogger");
	
	public static LoggingManager getInstance(){	return loggingManager;	}
	
	public LoggingManager()
	{
		try{
			File logDir = new File(ServerConstants.LOGS_DIR);
			if (!logDir.exists())
				logDir.mkdir();
			
			messageFH = new FileHandler(ServerConstants.LOGS_DIR + "/messageException.log");
			systemFH = new FileHandler(ServerConstants.LOGS_DIR + "/systemException.log");
			traceFH = new FileHandler(ServerConstants.LOGS_DIR + "/traceException.log");
		}
		catch(IOException ex)
		{
			System.out.println(ex);
			ex.printStackTrace();
		}
		
		Formatter fmt = new SingleLineFormatter();
		
		messageFH.setFormatter(fmt);
		systemFH.setFormatter(fmt);
		traceFH.setFormatter(fmt);
		
		messagelogger.setUseParentHandlers(false);
		systemLogger.setUseParentHandlers(false);
		traceLogger.setUseParentHandlers(false);
		
		messagelogger.addHandler(messageFH);
		systemLogger.addHandler(systemFH);
		traceLogger.addHandler(traceFH);
		
		messagelogger.setLevel(Level.FINEST);
		systemLogger.setLevel(Level.FINEST);
		traceLogger.setLevel(Level.FINEST);
	}

	public void writeMessage(String message, String className, String methodNAme, Throwable tr)
	{
		messagelogger.logp(Level.INFO, className, methodNAme, message, tr);
	}
	
	public void writeSystem(String message, String className, String methodNAme, Throwable tr)
	{
		systemLogger.logp(Level.INFO, className, methodNAme, message, tr);
	}
	
	public void writeTrace(String message, String className, String methodNAme, Throwable tr)
	{
		traceLogger.logp(Level.INFO, className, methodNAme, message, tr);
	}
	
	public static String composeStackTrace(Exception ex){
		StringWriter str = new StringWriter();
		PrintWriter prt = new PrintWriter(str, true);
		ex.printStackTrace(prt);
		return str.toString();
	}
}