/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.featuredep.utils.netscan.GraphElement.GraphElementVisitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class RocketFuelFolderReader implements VisitingReader<GraphElement.GraphElementVisitor> {

    File root;

    public RocketFuelFolderReader(File root) {
        this.root = root;
    }

    @Override
    public void read(GraphElementVisitor visitor) throws IOException, ParseException {
        Map<String, Integer> names = new HashMap<String, Integer>();
        int nextIndex = 0;
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                System.out.printf("Parsing Folder: %s\n", f.getName());
                File edgesf = new File(f.getAbsolutePath() + "/edges.lat");
                if (edgesf.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(edgesf));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split("\\s*->\\s*");
                        String fromName = parts[0].trim();
                        String[] tmp = parts[1].split("\\s+");
                        String toName = parts[1].substring(0, parts[1].length()-tmp[tmp.length-1].length());
                        String sLatency = parts[1].substring(toName.length());
                        toName = toName.trim();
                        
                        if (!names.containsKey(fromName)) {
                            GraphElement.VertexElement v = new GraphElement.VertexElement(fromName, nextIndex);
                            visitor.visit(v);
                            names.put(fromName, nextIndex++);
                        }

                        if (!names.containsKey(toName)) {
                            GraphElement.VertexElement v = new GraphElement.VertexElement(toName, nextIndex);
                            visitor.visit(v);
                            names.put(toName, nextIndex++);
                        }
                        
                        GraphElement.EdgeElement e = new GraphElement.EdgeElement(names.get(fromName), names.get(toName), "", 1, true, "latency", sLatency);
                        visitor.visit(e);
                    }
                    
                    br.close();
                } else {
                    System.out.println("There is no information file in this folder");
                }
            }
        }
    }
}
