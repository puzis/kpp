package common;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable
{
    private static final long serialVersionUID = 1L;
	
    protected T1 m_value1 = null;
    protected T2 m_value2 = null;

    public Pair(T1 v1, T2 v2)
    {
        m_value1 = v1;
        m_value2 = v2;
    }

    public T1 getValue1()
    {
        return m_value1;
    }

    public void setValue1(T1 m_value1)
    {
        this.m_value1 = m_value1;
    }

    public T2 getValue2()
    {
        return m_value2;
    }

    public void setValue2(T2 m_value2)
    {
        this.m_value2 = m_value2;
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((m_value1 == null) ? 0 : m_value1.hashCode());
        result = PRIME * result + ((m_value2 == null) ? 0 : m_value2.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pair other = (Pair) obj;
        if (m_value1 == null)
        {
            if (other.m_value1 != null)
                return false;
        } else if (!m_value1.equals(other.m_value1))
            return false;
        if (m_value2 == null)
        {
            if (other.m_value2 != null)
                return false;
        } else if (!m_value2.equals(other.m_value2))
            return false;
        return true;
    }

    public String toString()
    {
    	
        return "<" + m_value1.toString() + ", " + m_value2.toString() + ">";
    }
}