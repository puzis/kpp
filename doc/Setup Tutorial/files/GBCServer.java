import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class GBCServer {
    public static void main(String[] args) throws MalformedURLException, XmlRpcException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080/AlgorithmsServer"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        
        // server.network.NetworkController.importNetwork(String filename, String importedNet, String ext)
        Object[] params = new Object[]{"test2.sel", ""};
        Integer netID = (Integer) client.execute("Network.importNetwork", params);
        System.out.println("NetID is:" + netID);

        //network.NetworkController.getNumberOfVertices(NetID) //server.shortestPathBetweenness.GBCController.create 
        params = new Object[]{netID};
        Integer result = (Integer) client.execute("Network.getNumberOfVertices", params);
        System.out.println("Vertices:" + result);
        
        params = new Object[]{+netID, "", false, false};
        Integer algID = (Integer) client.execute("GBC.create", params);
        System.out.println("AlgID is:" + algID);
        
        
        params = new Object[]{algID, new Object[]{3}, new Object[]{}};
        Double gbc = (Double) client.execute("GBC.getGBC", params);
        System.out.println("GBC({3}):" + gbc);
        
    }
}
