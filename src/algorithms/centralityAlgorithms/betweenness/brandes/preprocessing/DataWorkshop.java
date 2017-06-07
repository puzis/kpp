package algorithms.centralityAlgorithms.betweenness.brandes.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import common.IndexFastList;

import javolution.util.FastList;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.GraphInterface;
import algorithms.centralityAlgorithms.betweenness.brandes.WeightedUlrikNG;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DefaultTrafficMatrix;
import algorithms.centralityAlgorithms.tm.DenseTrafficMatrix;
import algorithms.shortestPath.ShortestPathAlgorithmInterface.ShortestPathAlg;

/**
 * @author Polina Zilberman
 *
 * version 1.0
 *
 * A set of data structures and algorithms required for betweenness
 * calculations. given: G=(V,E) - undirected unweighted graph n = |V|, m = |E|
 *
 * O(1) retrieved data: B(x) - individual vertex betweenness (with full
 * preprocessing only) d{x,y} - distance between vertices x and y sigma{x,y} -
 * number of shortest pathes between x and y delta(x,w,y) -
 * sigma{x,w}*sigma{w,y}/sigma{x,y} delta(x,w,.) - sum of delta(x,w,y) for all y
 * in V PB{x,y} - sum of delta(v,x,y,u) for all v,u in V
 *
 * {} - curly braces indicate unknown/unimportant order of arguments
 *
 * other data: AverageSigma AverageDistance PathDispersion
 */
public class DataWorkshop implements Serializable {

	protected static final long serialVersionUID = 1L;
	protected static LoggingManager logger = LoggingManager.getInstance();
	protected static double DISTANCE_PRECISION = 1E-8;
    
    protected int m_numberOfVertices;
    protected GraphInterface<Index, BasicVertexInfo> m_graph = null;
    protected double[][] m_pathBetweeness;
    protected double[][] m_distanceMatrix;
    protected double[][] m_sigma;
    protected double[][] m_deltaDot;
    protected AbsTrafficMatrix m_communicationWeights;
    protected double m_totalCommunicationWeight;
    protected FastList<Index>[][] m_routingTable;

    public DataWorkshop() {
    }

    public DataWorkshop(int numberOfVertices) {
        m_numberOfVertices = numberOfVertices;

        m_distanceMatrix = new double[m_numberOfVertices][m_numberOfVertices];
        m_sigma = new double[m_numberOfVertices][m_numberOfVertices];
        m_deltaDot = new double[m_numberOfVertices][m_numberOfVertices];
        m_routingTable = new IndexFastList[m_numberOfVertices][m_numberOfVertices];
        m_communicationWeights = new DenseTrafficMatrix(m_numberOfVertices); // new double [m_numberOfVertices][m_numberOfVertices];
        m_pathBetweeness = new double[m_numberOfVertices][m_numberOfVertices];
        for (int i = 0; i < m_numberOfVertices; i++) {
            for (int j = 0; j < m_numberOfVertices; j++) {
                m_pathBetweeness[i][j] = Double.NaN;
            }
        }
    }

    /**
     * (GroupBasedAlgorithm, Graph) -> GroupBasedAlgorithm Performs
     * precomputation O(n^3 + nm) Why +nm ? may be it works on multigraphs too
     * :) impl: consider just-in-time calculation of PB values)
     *
     * @param graph
     * @param communicationWeights
     */
    public DataWorkshop(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, boolean createRoutingTable, AbstractExecution progress, double percentage) {
        init(spAlg, graph, communicationWeights, createRoutingTable, false, progress, percentage);
    }

    public DataWorkshop(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, boolean createRoutingTable, AbstractExecution progress, double percentage) {
        AbsTrafficMatrix communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        init(spAlg, graph, communicationWeights, createRoutingTable, false, progress, percentage);
    }

    public DataWorkshop(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, boolean createRoutingTable, boolean fullPrecomp, AbstractExecution progress, double percentage) {
        AbsTrafficMatrix communicationWeights = new DefaultTrafficMatrix(graph.getNumberOfVertices()); //MatricesUtils.getDefaultWeights(graph.getNumberOfVertices());
        init(spAlg, graph, communicationWeights, createRoutingTable, fullPrecomp, progress, percentage);
    }

    protected void init(ShortestPathAlg spAlg, GraphInterface<Index, BasicVertexInfo> graph, AbsTrafficMatrix communicationWeights, boolean createRoutingTable, boolean fullPrecomputation, AbstractExecution progress, double percentage) {
        if (graph == null) {
            throw new RuntimeException("Graph is null");
        }

        m_numberOfVertices = graph.getNumberOfVertices();
        m_graph = graph;
        m_communicationWeights = communicationWeights;
        /**
         * single all sources shortest paths traversal O(nm)
         */
        WeightedUlrikNG gb = new WeightedUlrikNG(spAlg, graph, communicationWeights, createRoutingTable, progress, percentage * 0.85);
        gb.run();

        m_distanceMatrix = gb.getDistance();
        m_deltaDot = gb.getDeltaDot();
        m_sigma = gb.getSigma();
        m_routingTable = gb.getRoutingTable();

        m_pathBetweeness = new double[m_numberOfVertices][m_numberOfVertices];
        if (!progress.isDone()) {
            for (int i = 0; i < m_numberOfVertices && !progress.isDone(); i++) {
                for (int j = 0; j < m_numberOfVertices && !progress.isDone(); j++) {
                    m_pathBetweeness[i][j] = Double.NaN;
                }
            }
        }

        double p = progress.getProgress();
        p += percentage * 0.15;

        progress.setProgress(p);

        if (!progress.isDone()) {
            computeTotalWeight();
        }

        if (fullPrecomputation) {
            computePairBetweenness();
        }
    }

    public void computePairBetweenness() {
        for (int indexI = 0; indexI < m_numberOfVertices; indexI++) {
            for (int indexJ = 0; indexJ < m_numberOfVertices; indexJ++) {
                m_pathBetweeness[indexI][indexJ] = computePairBetweeness(indexI, indexJ);
            }
        }
    }

    public double computePairBetweeness(int v1, int v2) {
        double result = 0, delta;
        for (int u = 0; u < m_numberOfVertices; u++) {
//        	delta = getDelta(v2, v1, u); //local mem access optimization (~10-20% speed) //won't work with directed networks
            delta = getDelta(u, v1, v2);
            result += m_deltaDot[u][v2] * delta;
        }
        m_pathBetweeness[v1][v2] = result;
        return result;
    }

    public double getPairBetweenness(int v1, int v2) {
        if (!Double.isNaN(m_pathBetweeness[v1][v2])) {
            return m_pathBetweeness[v1][v2];
        } else {
            return computePairBetweeness(v1, v2);
        }
    }

    public double getBetweenness(int v) {
        return getPairBetweenness(v, v);
    }

    /**
     * Precodition: distance matrix has to be already computed.
     */
    protected void computeTotalWeight() {
        //TODO: move method to traffic matrix implementation. it has better knowledge of its structure and can can compute the total weight faster.
//		if (m_communicationWeights.length == 0)
//            m_totalCommunicationWeight = m_numberOfVertices * (m_numberOfVertices - 1);
//        else
//		{
        m_totalCommunicationWeight = 0;
        for (int i = 0; i < m_numberOfVertices; ++i) {
            for (int j = 0; j < m_numberOfVertices; ++j) {
                if (!Double.isNaN(m_distanceMatrix[i][j])) {
                    m_totalCommunicationWeight += m_communicationWeights.getWeight(i, j); //[i][j];
                }//		}	
            }
        }
    }

    public double getDelta(int u, int w, int v) {
        if (Math.abs(m_distanceMatrix[u][v] - (m_distanceMatrix[u][w] + m_distanceMatrix[w][v])) < DISTANCE_PRECISION) {
            if (m_sigma[u][w] * m_sigma[w][v] * m_sigma[u][v] == 0) {
                return (double) 0;
            }
            return m_sigma[u][w] * m_sigma[w][v] / (double) m_sigma[u][v];
        } else {
            return 0;
        }
    }

    /**
     * TODO: While profiling, run the profiling test with the original getDelta.
     * Afterwards switch the names 'getDelta' and 'getDeltaRefactored' and
     * execute the profiling test again.
     */
    public double getDeltaRefactored(int u, int w, int v) {
        double sigma_u_w = m_sigma[u][w];
        double sigma_w_v = m_sigma[w][v];
        double sigma_u_v = m_sigma[u][v];

        if (m_distanceMatrix[u][v] == m_distanceMatrix[u][w] + m_distanceMatrix[w][v]) {
            if (sigma_u_w == 0 || sigma_w_v == 0 || sigma_u_v == 0) {
                return (double) 0;
            }
            return sigma_u_w * sigma_w_v / (double) sigma_u_v;
        } else {
            return 0;
        }
    }

    public double computeCharacteristicPathLength() {
        double totaldist = 0;
        for (int i = 0; i < this.m_numberOfVertices; i++) {
            for (int j = 0; j < this.m_numberOfVertices; j++) {
                if (!Double.isNaN(m_distanceMatrix[i][j])) {
                    totaldist += m_distanceMatrix[i][j];
                } else {
                    return Double.MAX_VALUE;
                }
            }
        }
        return (totaldist / this.m_numberOfVertices) / this.m_numberOfVertices;
    }

    public double getDistance(int u, int v) {
        return m_distanceMatrix[u][v];
    }

    public double getSigma(int u, int v) {
        return m_sigma[u][v];
    }

    public double getCommunicationWeight() {
        return m_totalCommunicationWeight;
    }

    public FastList<Index>[][] getRoutingTable() {
        return m_routingTable;
    }

    public FastList<Index> getRoutingTable(int v1, int v2) {
        return m_routingTable[v1][v2];
    }

    public double[][] getDistanceMatrix() {
        return m_distanceMatrix;
    }

    public double[][] getPathBetweeness() {
        return m_pathBetweeness;
    }

    public GraphInterface<Index, BasicVertexInfo> getGraph() {
        return m_graph;
    }

    public double[][] getDeltaDot() {
        return m_deltaDot;
    }

    public double getDeltaDot(int u, int v) {
        return m_deltaDot[u][v];
    }

    public void saveToDisk(String filename, AbstractExecution progress, double percentage) throws IOException {
        File outFile = new File(filename);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(outFile));

            out.writeInt(m_numberOfVertices);
            out.writeObject(m_pathBetweeness);
            updateLoadProgress(progress, percentage);
            out.writeObject(m_distanceMatrix);
            updateLoadProgress(progress, percentage);
            out.writeObject(m_sigma);
            updateLoadProgress(progress, percentage);
            out.writeObject(m_deltaDot);
            out.writeObject(m_communicationWeights);
            out.writeDouble(m_totalCommunicationWeight);
            out.writeObject(m_routingTable);
            updateLoadProgress(progress, percentage);
            out.writeObject(m_graph);
            progress.setSuccess(0);
            updateLoadProgress(progress, percentage);
        } catch (IOException ex) {
            logger.writeSystem("An IOException has occured while trying to save the DataWorkshop to file " + outFile.getName(), "DataWorkshop", "saveToDisk", ex);
            throw new IOException("An IOException has occured while trying to save the DataWorkshop to file " + outFile.getName() + "\n" + ex.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ex) {
                logger.writeSystem("An IOException has occured while trying to close the output stream after writting the file: " + outFile.getName(), "DataWorkshop", "saveToDisk", ex);
                throw new IOException("An IOException has occured while trying to close the output stream after writting the file: " + outFile.getName() + "\n" + ex.getMessage());
            }
        }
    }

    public void loadFromDisk(String filename, AbstractExecution progress, double percentage) throws IOException, ClassNotFoundException {
        File inFile = new File(filename);
        loadFromDisk(inFile, progress, percentage);
    }

    public void loadFromDisk(File inFile, AbstractExecution progress, double percentage) throws IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(inFile));
            m_numberOfVertices = in.readInt();
            m_pathBetweeness = (double[][]) in.readObject();
            updateLoadProgress(progress, percentage);
            m_distanceMatrix = (double[][]) in.readObject();
            updateLoadProgress(progress, percentage);
            m_sigma = (double[][]) in.readObject();
            m_deltaDot = (double[][]) in.readObject();
            m_communicationWeights = (AbsTrafficMatrix) in.readObject();
            m_totalCommunicationWeight = in.readDouble();
            updateLoadProgress(progress, percentage);
            m_routingTable = (FastList[][]) in.readObject();
            updateLoadProgress(progress, percentage);
            //SerializableGraphRepresentation serGraph = (SerializableGraphRepresentation)in.readObject(); 
            //m_graph = serGraph.getGraphSimple();
            m_graph = (GraphInterface<Index, BasicVertexInfo>) in.readObject();
        } catch (ClassNotFoundException ex) {
            logger.writeSystem("A ClassNotFoundException has occured while trying to read the DataWorkshop from file " + inFile.getName(), "DataWorkshop", "loadFromDisk", ex);
            throw new ClassNotFoundException("A ClassNotFoundException has occured while trying to read the DataWorkshop from file " + inFile.getName() + "\n" + ex.getMessage());
        } catch (IOException ex) {
            logger.writeSystem("An IOException has occured while trying to read the DataWorkshop from file " + inFile.getName(), "DataWorkshop", "loadFromDisk", ex);
            throw new IOException("An IOException has occured while trying to read the DataWorkshop from file " + inFile.getName() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex));
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.writeSystem("An IOException has occured while trying to close the input stream after reading the file: " + inFile.getName(), "DataWorkshop", "loadFromDisk", ex);
                throw new IOException("An IOException has occured while trying to close the input stream after reading the file: " + inFile.getName() + "\n" + ex.getMessage() + "\n" + LoggingManager.composeStackTrace(ex));
            }
        }
    }

    public void updateLoadProgress(AbstractExecution progress, double percentage) {
        double p = progress.getProgress();
        p += 0.25 * percentage;
        progress.setProgress(p);
    }

    public int getNumberOfVertices() {
        return m_numberOfVertices;
    }

    public double[][] getSigma() {
        return m_sigma;
    }

    public AbsTrafficMatrix getCommunicationWeights() {
        return m_communicationWeights;
    }

    public void setCommunicationWeight(double weight) {
        m_totalCommunicationWeight = weight;
    }

    public double getCommunicationWeight(int v1, int v2) {
        return m_communicationWeights.getWeight(v1, v2);
    }//[v1][v2];	}

    public void setDeltaDot(double[][] dot) {
        m_deltaDot = dot;
    }

    public void setDistanceMatrix(double[][] matrix) {
        m_distanceMatrix = matrix;
    }

    public void setGraph(GraphInterface<Index, BasicVertexInfo> graph) throws Exception {
        m_graph = graph;
    }

    public void setNumberOfVertices(int ofVertices) {
        m_numberOfVertices = ofVertices;
    }

    public void setPathBetweeness(double[][] betweeness) {
        this.m_pathBetweeness = betweeness;
    }

    public void setRoutingTable(FastList<Index>[][] table) {
        m_routingTable = table;
    }

    public void setSigma(double[][] sigma) {
        this.m_sigma = sigma;
    }

    public void setCommunicationWeights(AbsTrafficMatrix commWeights) {
        m_communicationWeights = commWeights;
    }

    public void closeDataworkshop() {
        m_communicationWeights = null;
        m_deltaDot = null;
        m_distanceMatrix = null;
        m_pathBetweeness = null;
        m_routingTable = null;
        m_graph = null;
        m_sigma = null;
    }
}