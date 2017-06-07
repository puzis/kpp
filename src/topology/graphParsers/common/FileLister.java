/**
 * 
 */
package topology.graphParsers.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author Omer Zohar
 * This class returns names of files with given extention for a given directory 
 */
public class FileLister {
	
	private String m_sfilepath=null;
	private FilenameFilter m_filter= null;
	
	
	/**
	 * 
	 */
	public FileLister(String path, FilenameFilter filter) {
		m_sfilepath=path;
		m_filter=filter;
	}

	public String[] getfilesfromdir(){
		File dir = null;
		try {
			dir = new File (m_sfilepath).getCanonicalFile();
		} catch (IOException e) {
			System.out.println("Error getting canonical file");
			e.printStackTrace();
		}
		String[] s=new String[0];
		
		if (dir.isDirectory()){
			s=dir.list(m_filter);
			for (int i=0;i<s.length;i++)
				s[i]=m_sfilepath+s[i];
		}
		else {
			System.out.println(m_sfilepath + "is not a directory.");
		}
		return s;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FilenameFilter extFilter = new FilenameExtentionFilter("fvl"); 
		FileLister f=new FileLister("D:\\Java\\Projects\\betweness\\res\\plankton\\www.ircache.net\\Plankton\\Data\\199810",extFilter);
		String[] s=f.getfilesfromdir();
		for (int i=0;i<s.length;i++)
			System.out.println(s[i]);

	}

}
