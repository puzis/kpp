package server.common;

import java.util.Calendar;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends java.util.logging.Formatter{

	@Override
	public String format(LogRecord record) 
	{
		// Create a StringBuffer to contain the formatted record
		// start with the date.
		StringBuffer sb = new StringBuffer();
		
		// Get the date from the LogRecord and add it to the buffer
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(record.getMillis());
		String timeStr = time.get(Calendar.DAY_OF_MONTH) + "-" + time.get(Calendar.MONTH) + "-" + time.get(Calendar.YEAR) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE) + ":" + time.get(Calendar.SECOND) + ":" + time.get(Calendar.MILLISECOND); 
		sb.append(timeStr);
		sb.append(" ");
		
		// Get the level name and add it to the buffer
		sb.append(record.getLevel().getName());
		sb.append(" ");
		
		sb.append(record.getSourceClassName());
		sb.append(" ");
		
		sb.append(record.getSourceMethodName());
		sb.append(" ");
		 
		// Get the formatted message (includes localization 
		// and substitution of paramters) and add it to the buffer
		sb.append(formatMessage(record));
		sb.append("\n");

		return sb.toString();

	}

}
