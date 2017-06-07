/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import algorithms.featuredep.utils.netscan.GraphElement.EdgeElement;
import algorithms.featuredep.utils.netscan.GraphElement.VertexElement;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class DotFileWriter implements GraphElement.GraphElementVisitor, Closeable {

    PrintWriter pw;
    boolean directed = true;
    int subgraphId = 0;

    public DotFileWriter(File output) throws FileNotFoundException {
        pw = new PrintWriter(output);
        pw.println("digraph {");
    }

    @Override
    public void visit(VertexElement e) {
        pw.println("" + e.getId() + " [label=\"" + e.getName() + "\"];");
    }

    @Override
    public void visit(EdgeElement e) {
        if (!e.isDirected() && directed) {
            pw.println("subgraph Rel" + (subgraphId++) + " {edge [dir=none]");
            directed = false;
        }

        if (e.isDirected() && !directed) {
            pw.println("}"); //close the subgraph..
            directed = true;
        }

        String labelAttr = "label=\"" + (!e.getName().isEmpty() ? e.getName() + "(" + e.getWeight() + ")\"" : e.getWeight() + "\"");

        pw.println("" + e.getVertexFromId() + " -> " + e.getVertexToId() + "[" + labelAttr + "]" + ";");
    }

    @Override
    public void close() throws IOException {
        if (!directed) {
            pw.println("}");
            directed = true;
        }

        pw.println("}");
        pw.close();
    }
}
