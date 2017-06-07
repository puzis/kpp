/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

/**
 *
 * @author bennyl
 */
public interface DeployableVertexInfoIfc {
    
    /**
     * @return true if this node can be embedded with features
     */
    boolean isDeployable();

    /**
     * @return the importance multiplier of traffic getting out from this node (0-1)
     */
    double getImportanceIn();

    /**
     * @return the importance multiplier of traffic getting into this node (0-1)
     */
    double getImportanceOut();

    /**
     * @param protocolId
     * @return true if this node can be a client for a server talking in the given protocol
     */
    boolean isClientOf(int protocolId);

    /**
     * @param protocolId
     * @return true if this node is a server talking in the given protocol
     */
    boolean isServerOf(int protocolId);
}
