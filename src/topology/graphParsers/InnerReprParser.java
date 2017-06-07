package topology.graphParsers;

public class InnerReprParser extends NBasedPajekParser{
	
private static final String EXTENSION = "net0";
	
	private static final int INDEX_BASE = 0;

	public InnerReprParser() { super(INDEX_BASE); }
	
	@Override
	public String getextension() {
		return EXTENSION;
	}
	


}
