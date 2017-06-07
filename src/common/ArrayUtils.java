package common;

import java.util.Arrays;
import javolution.util.Index;

public class ArrayUtils {
    
    public static Integer[] toObject(int[] array){
        Integer[] res = new Integer[array.length];
        for (int i=0; i<res.length; i++) res[i] = array[i];
        return res;
    }
    
    public static int[] toPrimitive(Integer[] array){
        int[] res = new int[array.length];
        for (int i=0; i<res.length; i++) res[i] = array[i].intValue();
        return res;
    }
    
    public static void shuffle(Index[] m_vertices) {
        for (int i = 0; i < m_vertices.length; i++) {
            int j = (int) (Math.random() * (m_vertices.length - i));
            Index tmp = m_vertices[i];
            m_vertices[i] = m_vertices[j];
            m_vertices[j] = tmp;
        }
    }

    /**
     * will create a deep copy of {@code from} into {@code to}, there is no runtime check for
     * the sizes of the matrixes. you must check by yourself if needed.
     *
     * @param from
     * @param to
     */
    public static void copy(double[][] from, double[][] to) {
        for (int i = 0; i < from.length; i++) {
            System.arraycopy(from[i], 0, to[i], 0, from[i].length);
        }
    }
    
    public static String toString(double[][] array){
        StringBuilder sb = new StringBuilder("[\n");
        for (int i=0; i<array.length; i++){
            sb.append("\t").append(Arrays.toString(array[i])).append("\n");
        }
        return sb.append("]").toString();
    }
   
    public static String arrayToString(Index[] array){
		if (array == null || array.length == 0)
			return "null";
		
    	StringBuilder buffer = new StringBuilder();
		
		buffer.append("[");
		buffer.append(array[0].toString());
		for (int i=1; i<array.length; i++){
			buffer.append("-");
			buffer.append(array[i].toString());
		}
		buffer.append("]");
		return buffer.toString();
	}
}
