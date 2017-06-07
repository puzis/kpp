/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.featuredep.utils.netscan.TrafficEstimationElement.TrafficEstimationVisitor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import topology.AbstractUndirectedGraph;
import topology.BasicVertexInfo;

/**
 *
 * @author bennyl
 */
public class RateFilesFolderReader implements VisitingReader<TrafficEstimationElement.TrafficEstimationVisitor> {
    
    File folder;
    AbstractUndirectedGraph topology;
    Map<String, Integer> nameToIndexMap;
    
    public RateFilesFolderReader(File folder, AbstractUndirectedGraph topology) {
        this.folder = folder;
        this.topology = topology;
        nameToIndexMap = new HashMap<String, Integer>();
        for (Object v : topology.getVertices()) {
            BasicVertexInfo info = topology.getVertex(v);
            nameToIndexMap.put(info.getLable(), info.getVertexNum());
        }
    }
    
    @Override
    public void read(TrafficEstimationVisitor visitor) throws IOException, ParseException {
        Integer currentNodeIndex = -1;
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith(".rte")) {
                System.out.println("Reading: " + f.getName());
                final String label = getLabel(f);
                currentNodeIndex = nameToIndexMap.get(label);
                if (currentNodeIndex == null) {
                    throw new ParseException("cannot find vertex with label: " + label + " in the given topology (label infered from file name)", -1);
                }
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                int lineNumber = 0;
                
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split("\\s+");
                    
                    Integer toIndex = nameToIndexMap.get(parts[0]);
                    if (toIndex == null) {
                        throw new ParseException("cannot find vertex with label: " + label + " in the given topology (label given in file: " + f.getName() + ")", lineNumber);
                    }
                    
                    try {
                        new TrafficEstimationElement.EstimatedValueElement(currentNodeIndex, toIndex, Double.parseDouble(parts[2])).visit(visitor);
                    } catch (NumberFormatException ex) {
                        throw new ParseException("expecting double value in file " + f.getName() + " at line " + lineNumber, lineNumber);
                    }
                }
                
                br.close();
            }
        }
    }
    
    private String getLabel(File f) {
        return f.getName().substring(0, f.getName().length() - ".rte".length());
    }
}
