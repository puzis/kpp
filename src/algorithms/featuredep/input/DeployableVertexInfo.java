/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.input;

import common.BitArray;
import topology.BasicVertexInfo;

/**
 *
 * @author bennyl
 */
public class DeployableVertexInfo extends BasicVertexInfo implements DeployableVertexInfoIfc {

    private boolean deployable = false;
    private double importanceIn = 1.0;
    private double importanceOut = 1.0;
    private BitArray protocolsSupportAsClient;
    private BitArray protocolsSupportAsServer;

    public DeployableVertexInfo(int vertexNum, String label, int numProtocols) {
        super(vertexNum, label);
        protocolsSupportAsClient = BitArray.create(numProtocols);
        protocolsSupportAsServer = BitArray.create(numProtocols);
    }

    @Override
    public boolean isDeployable() {
        return deployable;
    }

    @Override
    public double getImportanceIn() {
        return importanceIn;
    }

    @Override
    public double getImportanceOut() {
        return importanceOut;
    }

    @Override
    public boolean isClientOf(int protocolId) {
        return protocolsSupportAsClient.get(protocolId);
    }

    @Override
    public boolean isServerOf(int protocolId) {
        return protocolsSupportAsServer.get(protocolId);
    }

    public void setImportanceIn(double importanceIn) {
        this.importanceIn = importanceIn;
    }

    public void setImportanceOut(double importanceOut) {
        this.importanceOut = importanceOut;
    }

    public void setDeployable(boolean deployable) {
        this.deployable = deployable;
    }

    public void setClientOf(int protocolId, boolean supported) {
        this.protocolsSupportAsClient.set(protocolId, supported);
    }

    public void setServerOf(int protocolId, boolean supported) {
        this.protocolsSupportAsServer.set(protocolId, supported);
    }
}
