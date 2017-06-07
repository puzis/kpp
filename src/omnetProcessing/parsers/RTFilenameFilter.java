package omnetProcessing.parsers;

import java.io.File;
import java.io.FilenameFilter;

public class RTFilenameFilter implements FilenameFilter{
	
	private String m_ext="";
	private String m_routerIndex = "";
		
	public RTFilenameFilter (String ext, String routerIdx){
		m_ext=ext;
		m_routerIndex = routerIdx;
	}
	
	public boolean accept (File dir, String name){
		if (name.endsWith("."+m_ext) && name.startsWith("R"+m_routerIndex+"_")) return true;
		else return false;
	}
}
