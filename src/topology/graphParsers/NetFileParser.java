package topology.graphParsers;

/**
 * This class is responsible for loading graphs from *.net files.
 * It also loads communication weights from files, and parses Strings which contain the communication weights.
 * 
 * Created by IntelliJ IDEA.
 * User: Polina Zilberman
 * Date: Aug 23, 2007
 * Time: 1:38:38 PM
 * To change this template use File | Settings | File Templates.
 * 
 * modified by Yuri Bakulin on Apr 12 2011
 */
public class NetFileParser extends NBasedPajekParser
{
	private static final String EXTENSION = "net";
	
	private static final int INDEX_BASE = 1;

	public NetFileParser () { super(INDEX_BASE); }



    @Override
	public String getextension()
	{
		return EXTENSION;
	}

}