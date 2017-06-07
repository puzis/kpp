/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.featuredep.utils.netscan.GraphElement.EdgeElement;
import algorithms.featuredep.utils.netscan.GraphElement.VertexElement;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author bennyl
 */
public class NetFileWritter implements GraphElement.GraphElementVisitor, Closeable{
    File output;
    List<VertexElement> vertices = new LinkedList<VertexElement>();
    List<EdgeElement> edges = new LinkedList<EdgeElement>();

    public NetFileWritter(File output) {
        this.output = output;
    }
    
    @Override
    public void visit(VertexElement e) {
        vertices.add(e);
    }

    @Override
    public void visit(EdgeElement e) {
        edges.add(e);
    }

    @Override
    public void close() throws IOException {
        PrintWriter pw = new PrintWriter(output);
        pw.println("*Vertices " + vertices.size());
        for (VertexElement v : vertices){
            pw.println(Integer.toString(v.getId()+1) + " \"" + v.getName() + "\"");
        }
        
        pw.println("*Edges");
        for (EdgeElement e : edges){
            pw.print(Integer.toString(e.getVertexFromId()+1) + " " + Integer.toString(e.getVertexToId()+1) + " " + e.getWeight());
            for (Entry<String, String> ad : e.getAdditionalProperties().entrySet()){
                pw.print(" " + ad.getKey() + " " + ad.getValue());
            }
            pw.println();
        }
        
        edges = null;
        vertices = null;
        pw.close();
    }
    
}
