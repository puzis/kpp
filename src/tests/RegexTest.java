package tests;

import java.util.regex.Matcher;

import topology.GraphRegularExpressions;

public class RegexTest 
{
	public static void main(String[] args) 
	{
		String line = "     100      112 1 Latency		 0.2    blab    2342";
		Matcher edgesLine = GraphRegularExpressions.EDGE_LINE.matcher(line);
    	if (edgesLine.find())
    	{
    		String[] groups = line.split("\\s+");
    		String label = null;
    		String value = null;
    		for (int i = 0; i < groups.length; i++)
    		{
    			try{
    				if (!groups[i].equals(" ") && !groups[i].isEmpty())
    					Integer.parseInt(groups[i]);
    			}
    			catch(Exception ex)
    			{
    				label = groups[i];
    				value = groups[++i];
    				System.out.println(label);
					System.out.println(value);
    			}
    		}
//    		int infoIndex = line.lastIndexOf(edgesLine.group(3)) + edgesLine.group(3).length() + 1;
//    		if (line.length() > infoIndex)
//    		{
//    			String infoStr = line.substring(infoIndex);
//    			String infoStr = line.substring(line.indexOf(label));
//    			Matcher vertexInfo = GraphRegularExpressions.OPTIONAL_INFO.matcher(infoStr);
//    	
//    			while (vertexInfo.find())
//    			{
//    				String labelName = vertexInfo.group(1);
//					String labelValue = vertexInfo.group(2);
//		
//					System.out.println(labelName);
//					System.out.println(labelValue);
//    			}
    		
    	}
	}
}