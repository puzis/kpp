package algorithms.centralityAlgorithms.betweenness.brandes;

import java.io.Serializable;

import java.util.Arrays;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import topology.BasicVertexInfo;
import topology.DirectedSimpleEdge;
import topology.AbstractSimpleEdge;
import topology.UndirectedSimpleEdge;
import algorithms.centralityAlgorithms.betweenness.brandes.preprocessing.DataWorkshop;
import algorithms.centralityAlgorithms.tm.AbsTrafficMatrix;

import common.Pair;

/**
 * Dynamic DataWorkshop for a single Group of vertices.
 *
 * Information held by this DS describes all candidates that may join the Set.
 * From this info we are able to calculate Groups's GBC for every new member of
 * the group.
 *
 * No queries are allowed on vertices which are not candidates. (Impl. Notes:
 * Consider implicit candidate insertion)
 *
 * definitions: same definitions as in DataWorkshop GB - Group Betweenness
 * centrality of current set. c_B(x) - contribution of "candidate x" to current
 * Set GBC c_sigma - number of shortest pathes between x and y which has no
 * vertices from S c_delta(x,w,y) = c_sigma(x,w) * c_sigma(w,y) / sigma(x,y)
 * c_delta(x,w,.) = sum of c_delta(x,w,y) for all y in V c_PB{x,y} - sum of
 * c_delta(v,x,y,u) for all v,u in V
 *
 * You can avoid multiple initialization by deepcopying and reseting previously
 * created algorithm.
 *
 * In this class applicants and candidates are synonims. Different naming is for
 * the sake of future extensions.
 *
 * @author Polina Zilberman
 */
public class CandidatesBasedAlgorithm implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    private FastSet<Index> m_candidates;
    /**
     * List of distinct vertices. Imposes order on all data matrices.
     */
    private FastSet<Index> m_members = new FastSet<Index>();
    /**
     * The subject group of vertices of this algorithm.
     */
    private FastSet<AbstractSimpleEdge<Index, BasicVertexInfo>> m_edges = new FastSet<AbstractSimpleEdge<Index, BasicVertexInfo>>();
    double m_groupBetweenness = 0;
    /**
     * Group Betweenness Centrality of currentGroup.
     */
    private int[] m_candidatesMap;
    /**
     * Map of vertices vs. their candidate index {vertex:index}.
     */
    private DataWorkshop m_dataWorkshop = null;
    private double[][] m_pathBetweenessClone = null;
    /**
     * The updated dataWorkshop's path betweeness matrix.
     */
    private double[][] m_sigmaClone = null;

    /**
     * The updated dataWorkshop's sigma matrix
     */
    public CandidatesBasedAlgorithm(DataWorkshop otherWorkShop, Object[] candidates) {
        init(otherWorkShop, candidates);
    }

    public CandidatesBasedAlgorithm(DataWorkshop dw, int[] candidates) {
        Index[] candidatesIdx = new Index[candidates.length];
        for (int i = 0; i < candidates.length; i++) {
            candidatesIdx[i] = Index.valueOf(candidates[i]);
        }
        init(dw, candidatesIdx);
    }

    @SuppressWarnings("unchecked")
    private void init(DataWorkshop otherWorkShop, Object[] candidates) {
        m_candidates = new FastSet<Index>();
        for (int i = 0; i < candidates.length; i++)// && !((Thread.currentThread() instanceof ExecutionInterface) && ((ExecutionInterface)Thread.currentThread()).isDone()); i++)
        {
            Object c = candidates[i];
            if (c instanceof Index) {
                Index vIdx = (Index) c;
                if (!m_candidates.contains(vIdx)) {
                    m_candidates.add(vIdx);
                }
            } else if (c instanceof AbstractSimpleEdge) {
                Index v1 = ((AbstractSimpleEdge<Index, BasicVertexInfo>) c).getV0();
                Index v2 = ((AbstractSimpleEdge<Index, BasicVertexInfo>) c).getV1();
                if (!m_candidates.contains(v1)) {
                    m_candidates.add(v1);
                }
                if (!m_candidates.contains(v2)) {
                    m_candidates.add(v2);
                }
            } else {
                LoggingManager.getInstance().writeSystem("Candidates list contains invalid object: " + c.toString(), "CandidatesBasedAlgorithm", "CandidatesBasedAlgorithm", null);
                throw new RuntimeException("Candidates list contains invalid object: " + c.toString());
            }
        }
        m_dataWorkshop = new DataWorkshop(m_candidates.size());
        m_dataWorkshop.setCommunicationWeight(otherWorkShop.getCommunicationWeight());
        m_candidatesMap = new int[otherWorkShop.getNumberOfVertices()];

        int i = 0, j;
        double[][] distanceMatrix = m_dataWorkshop.getDistanceMatrix();
        double[][] sigma = m_dataWorkshop.getSigma();
        m_sigmaClone = new double[m_dataWorkshop.getNumberOfVertices()][m_dataWorkshop.getNumberOfVertices()];
        double[][] deltaDot = m_dataWorkshop.getDeltaDot();
        FastList<Index>[][] routingTable = m_dataWorkshop.getRoutingTable();
        double[][] pathBetweeness = m_dataWorkshop.getPathBetweeness();
        m_pathBetweenessClone = new double[m_dataWorkshop.getNumberOfVertices()][m_dataWorkshop.getNumberOfVertices()];
        AbsTrafficMatrix communicationWeights = m_dataWorkshop.getCommunicationWeights();

        for (Index v : m_candidates) {
            int vValue = v.intValue();
            m_candidatesMap[vValue] = i;

            j = 0;
            for (Index w : m_candidates) {
                int wValue = w.intValue();
                distanceMatrix[i][j] = otherWorkShop.getDistance(vValue, wValue);
                sigma[i][j] = otherWorkShop.getSigma(vValue, wValue);
                deltaDot[i][j] = otherWorkShop.getDeltaDot(vValue, wValue);
                routingTable[i][j] = otherWorkShop.getRoutingTable(vValue, wValue);
                communicationWeights.setWeight(i, j, otherWorkShop.getCommunicationWeight(vValue, wValue)); //[i][j] = otherWorkShop.getCommunicationWeight(vValue, wValue);
//                if (grid == null)	/** If Grid is enabled, candidates' pair betweeness matrix has already been computed. */
                pathBetweeness[i][j] = otherWorkShop.getPairBetweenness(vValue, wValue);
                j++;
            }
            i++;
        }
//		/** If Grid is enabled, compute candidates' pair betweeness matrix, 
//		 *  else retrieve pair betweeness values from the whole graph dataworkshop. */
//		if (grid != null)
//			grid.execute(PairBetweenessComputationTask.class.getName(), m_dataWorkshop).get();
    }

    /**
     * Copy constructor
     *
     * @param other
     */
    protected CandidatesBasedAlgorithm(CandidatesBasedAlgorithm other) {
        m_candidates = new FastSet<Index>();
        m_candidates.addAll(other.m_candidates);

        m_candidatesMap = new int[other.m_candidatesMap.length];
        m_candidatesMap = Arrays.copyOf(other.m_candidatesMap, other.m_candidatesMap.length);

        m_members = new FastSet<Index>();
        m_members.addAll(other.m_members);
        m_edges = new FastSet<AbstractSimpleEdge<Index, BasicVertexInfo>>();
        m_edges.addAll(other.m_edges);

        m_groupBetweenness = other.m_groupBetweenness;

        m_candidatesMap = Arrays.copyOf(other.m_candidatesMap, other.m_candidatesMap.length);

        m_dataWorkshop = other.m_dataWorkshop;

        m_pathBetweenessClone = new double[other.m_pathBetweenessClone.length][];
        for (int i = 0; i < m_pathBetweenessClone.length; i++) {
            m_pathBetweenessClone[i] = Arrays.copyOf(other.m_pathBetweenessClone[i], other.m_pathBetweenessClone.length);
        }

        m_sigmaClone = new double[other.m_sigmaClone.length][];
        for (int i = 0; i < m_sigmaClone.length; i++) {
            m_sigmaClone[i] = Arrays.copyOf(other.m_sigmaClone[i], other.m_sigmaClone.length);
        }
    }

    public static double calculateNormalizedGB(DataWorkshop workShop, Object[] group, AbstractExecution progress, double percentage) {
        CandidatesBasedAlgorithm algorithm = new CandidatesBasedAlgorithm(workShop, group);
        addMembers(group, progress, percentage, algorithm);
        double result = algorithm.getNormalizedGroupBetweenness();
        return result;
    }

    private static void addMembers(Object[] group, AbstractExecution progress, double percentage, CandidatesBasedAlgorithm algorithm) {
        double p = progress.getProgress();
        for (Object member : group) {
            algorithm.addMember(member);
            p += (1 / (double) group.length) * percentage;
            progress.setProgress(p);
        }
    }

    public static double calculateGB(DataWorkshop workShop, Object[] group, AbstractExecution progress, double percentage) {
        CandidatesBasedAlgorithm algorithm = new CandidatesBasedAlgorithm(workShop, group);
        addMembers(group, progress, percentage, algorithm);
        double result = algorithm.getGroupBetweenness();
        return result;
    }

    public static double calculateSumGroup(DataWorkshop workShop, Object[] group, AbstractExecution progress, double percentage) {
        double p = progress.getProgress();

        double result = 0;
        for (Object member : group) {
            if (member instanceof Index) {
                result += workShop.getBetweenness(((Index) member).intValue());
            } else if (member instanceof AbstractSimpleEdge) {
                AbstractSimpleEdge<Index, BasicVertexInfo> e = (AbstractSimpleEdge) member;
                result += workShop.getPairBetweenness(e.getV0().intValue(), e.getV1().intValue());
            } else {
                LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "CandidatesBasedAlgorithm", "calculateSumGroup", null);
                throw new IllegalArgumentException("The given member is of invalid type: " + member.toString());
            }

            p += (1 / (double) group.length) * percentage;
            progress.setProgress(p);
        }

        return result;
    }

    public void addMember(Object member) {
        if (member instanceof Index) {
            Index v = (Index) member;
            assert (m_candidates.contains(v));
            addMember(v.intValue());
        } else if (member instanceof UndirectedSimpleEdge<?, ?>) {
            AbstractSimpleEdge<Index, BasicVertexInfo> e = (AbstractSimpleEdge<Index, BasicVertexInfo>) member;
            assert (m_candidates.contains(e.getV0()));
            assert (m_candidates.contains(e.getV1()));
            addMember(e);
            addMember(e.flip());
        } else if (member instanceof DirectedSimpleEdge<?, ?>) {
            AbstractSimpleEdge<Index, BasicVertexInfo> e = (AbstractSimpleEdge<Index, BasicVertexInfo>) member;
            assert (m_candidates.contains(e.getV0()));
            assert (m_candidates.contains(e.getV1()));
            addMember(e);
        } else {
            LoggingManager.getInstance().writeSystem("The given member is of invalid type: " + member.toString(), "CandidatesBasedAlgorithm", "addMember", null);
            throw new RuntimeException("The given member is of invalid type: " + member.toString());
        }
    }

    public void addMember(int v) {
        if (m_members.contains(Index.valueOf(v))) {
            return;
        }

        m_members.add(Index.valueOf(v));

        m_groupBetweenness += m_dataWorkshop.getPairBetweenness(m_candidatesMap[v], m_candidatesMap[v]);

        computeUpdatedMatrixes(v, m_pathBetweenessClone, m_sigmaClone);

        double[][] tmp_arr;

        tmp_arr = m_pathBetweenessClone;
        m_pathBetweenessClone = m_dataWorkshop.getPathBetweeness();
        m_dataWorkshop.setPathBetweeness(tmp_arr);

        tmp_arr = m_sigmaClone;
        m_sigmaClone = m_dataWorkshop.getSigma();
        m_dataWorkshop.setSigma(tmp_arr);

        //m_candidates.remove(Index.valueOf(v)); performance optimization caused bugs.
    }

    public void addMember(AbstractSimpleEdge<Index, BasicVertexInfo> e) {
//		if (m_edges.contains(e))
//			return;

        m_edges.add(e);

        if (!(m_members.contains(e.getV0()) || m_members.contains(e.getV1()))) {
            m_groupBetweenness += m_dataWorkshop.getPairBetweenness(m_candidatesMap[e.getV0().intValue()], m_candidatesMap[e.getV1().intValue()]);

            computeUpdatedMatrixes(e, m_pathBetweenessClone, m_sigmaClone);

            double[][] tmp_arr;

            tmp_arr = m_pathBetweenessClone;
            m_pathBetweenessClone = m_dataWorkshop.getPathBetweeness();
            m_dataWorkshop.setPathBetweeness(tmp_arr);

            tmp_arr = m_sigmaClone;
            m_sigmaClone = m_dataWorkshop.getSigma();
            m_dataWorkshop.setSigma(tmp_arr);
        }
    }

    public int[] getCandidatesMap() {
        return m_candidatesMap;
    }

    public double[][] getSigmaClone() {
        return m_sigmaClone;
    }

    public double[][] getPathBetweenessClone() {
        return m_pathBetweenessClone;
    }

    public double getGroupBetweenness() {
        return m_groupBetweenness;
    }

    public double getNormalizedGroupBetweenness() {
        return getGroupBetweenness() / m_dataWorkshop.getCommunicationWeight();
    }

    //public FastSet<Index> getCandidates(){	return m_candidates;	}
    public FastSet<Index> getMembers() {
        return m_members;
    }

    public FastSet<AbstractSimpleEdge<Index, BasicVertexInfo>> getEdgeMembers() {
        return m_edges;
    }

    public int getVertexID(int v) {
        return m_candidatesMap[v];
    }

    private void computeUpdatedMatrixes(int v, double[][] pathBetweenness, double[][] sigma) {
        for (Index x : m_candidates) {
            int valueX = x.intValue();
            updatePerX(v, valueX, pathBetweenness, sigma);
        }
    }

    public Pair<double[], double[]> updatePerX(int v, int indexX, double[][] pathBetweenness, double[][] sigma) {
        int appX = m_candidatesMap[indexX];
        for (Index y : m_candidates) {
            int indexY = y.intValue();

            int appV = m_candidatesMap[v];
            int appY = m_candidatesMap[indexY];

            sigma[appX][appY] = m_dataWorkshop.getSigma(appX, appY) * (1 - m_dataWorkshop.getDelta(appX, appV, appY));
            computeUpdatedPB(appV, appX, appY, pathBetweenness);
        }
        Pair<double[], double[]> xUpdates = new Pair<double[], double[]>(sigma[appX], pathBetweenness[appX]);
        return xUpdates;
    }

    private void computeUpdatedPB(int v, int x, int y, double[][] pb) {
        pb[x][y] = m_dataWorkshop.getPairBetweenness(x, y) - m_dataWorkshop.getPairBetweenness(x, y) * m_dataWorkshop.getDelta(x, v, y);

        if (y != v) {
            pb[x][y] -= m_dataWorkshop.getPairBetweenness(x, v) * m_dataWorkshop.getDelta(x, y, v);
        }

        if (x != v) {
            pb[x][y] -= m_dataWorkshop.getPairBetweenness(v, y) * m_dataWorkshop.getDelta(v, x, y);
        }
    }

    private void computeUpdatedMatrixes(AbstractSimpleEdge<Index, BasicVertexInfo> e, double[][] pathBetweenness, double[][] sigma) {
        for (Index x : m_candidates) {
            int indexX = x.intValue();
            updatePerX(e, indexX, pathBetweenness, sigma);
        }
    }

    public Pair<double[], double[]> updatePerX(AbstractSimpleEdge<Index, BasicVertexInfo> e, int indexX, double[][] pathBetweenness, double[][] sigma) {
        int appX = m_candidatesMap[indexX];
        for (Index y : m_candidates) {
            int indexY = y.intValue();

            int appY = m_candidatesMap[indexY];

            sigma[appX][appY] = m_dataWorkshop.getSigma(appX, appY) * (1
                    - m_dataWorkshop.getDelta(appX, m_candidatesMap[e.getV0().intValue()], m_candidatesMap[e.getV1().intValue()])
                    * m_dataWorkshop.getDelta(appX, m_candidatesMap[e.getV1().intValue()], appY));


            computeUpdatedPB(e, appX, appY, pathBetweenness);
        }
        Pair<double[], double[]> xUpdates = new Pair<double[], double[]>(sigma[appX], pathBetweenness[appX]);
        return xUpdates;
    }

    private void computeUpdatedPB(AbstractSimpleEdge<Index, BasicVertexInfo> e, int x, int y, double[][] pb) {
        int u = m_candidatesMap[e.getV0().intValue()];
        int v = m_candidatesMap[e.getV1().intValue()];

        double delta_x_u_v = m_dataWorkshop.getDelta(x, u, v);

        pb[x][y] = m_dataWorkshop.getPairBetweenness(x, y) - m_dataWorkshop.getPairBetweenness(x, y) * delta_x_u_v * m_dataWorkshop.getDelta(x, v, y);

        pb[x][y] -= m_dataWorkshop.getPairBetweenness(u, y) * m_dataWorkshop.getDelta(u, v, x) * m_dataWorkshop.getDelta(u, x, y);

        pb[x][y] -= m_dataWorkshop.getPairBetweenness(x, v) * m_dataWorkshop.getDelta(x, y, u) * delta_x_u_v;
    }

    public double getBetweenness(int v) {
        return m_dataWorkshop.getBetweenness(m_candidatesMap[v]);
    }

    public DataWorkshop getDataWorkshop() {
        return m_dataWorkshop;
    }

    public double getPairBetweenness(int v1, int v2) {
        return m_dataWorkshop.getPairBetweenness(m_candidatesMap[v1], m_candidatesMap[v2]);
    }

    public CandidatesBasedAlgorithm clone() {
        return new CandidatesBasedAlgorithm(this);
    }
}