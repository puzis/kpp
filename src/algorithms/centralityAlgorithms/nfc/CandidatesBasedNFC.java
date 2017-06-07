package algorithms.centralityAlgorithms.nfc;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.Index;
import server.common.LoggingManager;
import server.execution.AbstractExecution;
import algorithms.centralityAlgorithms.nfc.preprocessing.DataWorkshopNFC;

import common.Pair;

/**
 * @author Polina Zilberman
 */
public class CandidatesBasedNFC{

	/**
     * Map of vertices vs. their candidate index {vertex:index}.
     */
    private int[] m_candidatesMap;
    private FastSet<Index> m_candidates;

    private FastMap<Pair<Index, Index>, Double>[] m_candidatesConfidence;
    
    private FastSet<Index> m_members;
    private FastMap<Pair<Index, Index>, Double> m_membersConfidence;
    private double m_groupBetweenness = 0;
    
    private double m_confidence_threshold;

    @SuppressWarnings("unchecked")
	public CandidatesBasedNFC(DataWorkshopNFC dw, Object[] candidates){
    	m_candidatesMap = new int[dw.getNumberOfVertices()];
    	m_candidates = new FastSet<>();
    	m_candidatesConfidence = new FastMap[candidates.length];
    	m_members = new FastSet<>();
    	m_membersConfidence = new FastMap<Pair<Index,Index>, Double>();
    	m_confidence_threshold = dw.getConfidenceThreshold();
    	init(dw, candidates);
    }

    private void init(DataWorkshopNFC dw, Object[] candidates) {
    	
    	int mapIdx = 0;
    	for (Object o : candidates){
    		if (o instanceof Index){
    			Index c = (Index)o;
	    		m_candidatesMap[c.intValue()] = mapIdx;
	    		m_candidates.add(c);	
	    		
	    		m_candidatesConfidence[mapIdx] = new FastMap<Pair<Index,Index>, Double>();
	    		
	    		for (int s=0; s<dw.getNumberOfVertices(); s++){
	    			for (int t=0; t<dw.getNumberOfVertices(); t++){
	        			double delta = dw.getDeltaBC(s, c.intValue(), t);
	        			if (delta >= m_confidence_threshold){
	        				m_candidatesConfidence[mapIdx].put(new Pair<Index, Index>(Index.valueOf(s), Index.valueOf(t)), delta);
	        			}
	        		}
	    		}
	    		mapIdx++;
    		}
    	}
		
	}

	public static double calculateNormalizedGB(DataWorkshopNFC workShop, Object[] group, AbstractExecution progress, double percentage) {
        CandidatesBasedNFC algorithm = new CandidatesBasedNFC(workShop, group);
        addMembers(group, progress, percentage, algorithm);
        double result = algorithm.getNormalizedGroupBetweenness();
        return result;
    }

    private static void addMembers(Object[] group, AbstractExecution progress, double percentage, CandidatesBasedNFC algorithm) {
        double p = progress.getProgress();
        for (Object member : group) {
            algorithm.addMember(member);
            p += (1 / (double) group.length) * percentage;
            progress.setProgress(p);
        }
    }

    public static double calculateGB(DataWorkshopNFC workShop, Object[] group, AbstractExecution progress, double percentage) {
        CandidatesBasedNFC algorithm = new CandidatesBasedNFC(workShop, group);
        addMembers(group, progress, percentage, algorithm);
        double result = algorithm.getGroupBetweenness();
        return result;
    }

    public void addMember(Object member) {
        if (member instanceof Index) {
            Index v = (Index) member;
            assert (m_candidates.contains(v));
            addMember(v.intValue());
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

        for (int s=0; s<m_candidatesMap.length; s++){
			for (int t=0; t<m_candidatesMap.length; t++){
				
				Pair<Index, Index> pair = new Pair<Index, Index>(Index.valueOf(s), Index.valueOf(t));
				double v_confidence = m_candidatesConfidence[m_candidatesMap[v]].get(pair)==null?0:m_candidatesConfidence[m_candidatesMap[v]].get(pair);
				double members_confidence = m_membersConfidence.get(pair)==null?0:m_membersConfidence.get(pair);
				
				if (v_confidence > members_confidence){
					m_membersConfidence.put(pair, v_confidence);
					if (members_confidence == 0){//s-t pair was not inspected till v has been added to the group
						m_groupBetweenness ++;
					}
				}
			}
        }
    }


    public int[] getCandidatesMap() {
        return m_candidatesMap;
    }

    public double getGroupBetweenness() {
        return m_groupBetweenness;
    }

    public double getNormalizedGroupBetweenness() {
        return getGroupBetweenness() / Math.pow(m_candidatesMap.length, 2);
    }

    public FastSet<Index> getMembers() {
        return m_members;
    }

    public int getVertexID(int v) {
        return m_candidatesMap[v];
    }

    public double getBetweenness(int v) {
        return m_candidatesConfidence[m_candidatesMap[v]].size();
    }

//    public double getPairBetweenness(int v1, int v2) {
//        return m_dataWorkshop.getPairBetweenness(m_candidatesMap[v1], m_candidatesMap[v2]);
//    }
}