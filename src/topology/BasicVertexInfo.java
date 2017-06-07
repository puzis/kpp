package topology;

import java.io.Serializable;

public class BasicVertexInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    protected int m_vertexNum = 0;
    protected String m_label = null;
    /**
     * Vertex's coordinates in the network.
     */
    protected double m_x, m_y, m_z = 0;
    protected int m_multiplicity = 1;
    protected double m_latency = 0.0;

    public BasicVertexInfo() {
    }

    public void setVertexNum(int vertexNum) {
        m_vertexNum = vertexNum;
    }

    public int getVertexNum() {
        return m_vertexNum;
    }

    public int getMultiplicity() {
        return m_multiplicity;
    }

    public void setMultiplicity(int m) {
        m_multiplicity = m;
    }

    public double getLatency() {
        return m_latency;
    }

    public void setLatency(double l) {
        m_latency = l;
    }

    public void setLabel(String lable) {
        m_label = lable;
    }

    public String getLable() {
        return m_label;
    }

    public void setX(double x) {
        m_x = x;
    }

    public double getX() {
        return m_x;
    }

    public void setY(double y) {
        m_y = y;
    }

    public double getY() {
        return m_y;
    }

    public void setZ(double z) {
        m_z = z;
    }

    public double getz() {
        return m_z;
    }

    public BasicVertexInfo(int vertexNum, String label) {
        m_vertexNum = vertexNum;
        m_label = label;
    }

    public BasicVertexInfo(int vertexNum, String label, double x, double y, double z) {
        this(vertexNum, label);

        m_x = x;
        m_y = y;
        m_z = z;
        m_multiplicity = 1;
        m_latency = 0;
    }

    public BasicVertexInfo(int vertexNum, String label, double x, double y, double z, int multiplicity, double latency) {
        this(vertexNum, label, x, y, z);
        m_multiplicity = multiplicity;
        m_latency = latency;

    }

    public BasicVertexInfo(BasicVertexInfo other) {
        this(other.m_vertexNum, other.m_label, other.m_x, other.m_y, other.m_z, other.m_multiplicity, other.m_latency);
    }

    public BasicVertexInfo clone() {
        return new BasicVertexInfo(this);
    }
}
