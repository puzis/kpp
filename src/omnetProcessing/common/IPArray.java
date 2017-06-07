package omnetProcessing.common;

import java.util.Arrays;

public class IPArray {

	private int[] m_ipAddress = null;
	
	public IPArray(int [] ip) {
		m_ipAddress = ip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(m_ipAddress);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IPArray other = (IPArray) obj;
		if (!Arrays.equals(m_ipAddress, other.m_ipAddress))
			return false;
		return true;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<4; i++){
			sb.append(m_ipAddress[i]);
			if (i<3)
				sb.append(".");
		}
		return sb.toString();
	}
}
