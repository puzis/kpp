package topology.graphParsers.common;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameExtentionFilter implements FilenameFilter{
	
	private String m_ext="";
	
	public FilenameExtentionFilter (String ext){
		m_ext=ext;
	}
	public boolean accept (File dir, String name){
		return (name.contains("."+m_ext)) ;
	}
}
