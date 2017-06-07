/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.utils.netscan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author bennyl
 */
public class ONetFileReader implements VisitingReader<GraphElement.GraphElementVisitor> {

    private static final int READ_STATE = 0;
    private static final int READ_VERTEX_STATE = 1;
    private static final int READ_ARC_STATE = 2;
    private static final int READ_EDGE_STATE = 3;
    private File source;

    public ONetFileReader(File source) {
        this.source = source;
    }

    protected String parseString(String element) {
        if (element.startsWith("\"") && element.endsWith("\"")) {
            element = element.substring(1, element.length() - 1);
        }

        return element;
    }

    @Override
    public void read(GraphElement.GraphElementVisitor visitor) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(source));
        String line;
        int state = READ_STATE;
        int lineNumber = 0;

        while ((line = br.readLine()) != null) {
            lineNumber++;
            line = line.trim();
            if (line.isEmpty()) {
                continue; //skip empty lines.. 
            }
            if (line.startsWith("*")) {
                final String lowerLine = line.toLowerCase();
                if (lowerLine.contains("vertices")) {
                    state = READ_VERTEX_STATE;
                } else if (lowerLine.contains("arcs")) {
                    state = READ_ARC_STATE;
                } else if (lowerLine.contains("edges")) {
                    state = READ_EDGE_STATE;
                } else {
                    throw new ParseException("Error parsing op: " + line, lineNumber);
                }
            } else {
                String[] parts = line.split("\\s+");
                try {
                    switch (state) {
                        case READ_VERTEX_STATE:
                            new GraphElement.VertexElement(parseString(parts[1]), Integer.parseInt(parts[0])).visit(visitor);
                        break;
                        case READ_ARC_STATE:
                            new GraphElement.EdgeElement(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), "", Double.parseDouble(parts[2]), true).visit(visitor);
                            break;
                        case READ_EDGE_STATE:
                            new GraphElement.EdgeElement(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), "", Double.parseDouble(parts[2]), false).visit(visitor);
                            break;
                        default:
                            throw new ParseException("File Currapted, dont know what to do with the given line: " + line, lineNumber);
                    }
                } catch (NumberFormatException e) {
                    throw new ParseException("expected number in " + line + " (" + e.getMessage() + ")", lineNumber);
                } catch (IndexOutOfBoundsException e) {
                    throw new ParseException("bad number of elements in line: " + line + " (" + e.getMessage() + ")", lineNumber);
                }
            }
        }
        
        br.close();
    }
}
