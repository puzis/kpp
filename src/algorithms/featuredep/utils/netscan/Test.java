/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import server.common.ServerConstants;

/**
 *
 * @author bennyl
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{
        RocketFuelFolderReader reader = new RocketFuelFolderReader(new File(ServerConstants.DATA_DIR + "maps-n-paths"));
        NetFileWritter nfw = new NetFileWritter(new File(ServerConstants.DATA_DIR + "out.net"));
        reader.read(nfw);
        nfw.close();
        //      
//        String path = "C:\\Documents and Settings\\bennyl\\Desktop\\New Code\\Experiments\\data\\omnet\\1221\\";
//        String fileName = "1221.onet";
//        String folderName = "rte";
//        
//        File file = new File(path + fileName);
//        File folder = new File(path + folderName);
//        ONetFileReader reader = new ONetFileReader(file);
//        TopologyGraphScanner topologyScanner = new TopologyGraphScanner();
//        reader.read(topologyScanner);
//        RateFilesFolderReader ratesReader = new RateFilesFolderReader(folder, topologyScanner.getResultedTopology());
//        TrafficMatrixScanner trafficScanner = new TrafficMatrixScanner(topologyScanner.getResultedTopology(), true);
//        ratesReader.read(trafficScanner);
    }
}
