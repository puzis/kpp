package tests.closeness;

import javolution.util.Index;

import topology.BasicVertexInfo;
import topology.GraphAsHashMap;

/**
 * creates a star shaped graph.
 *	   *   *
 *	    \ /
 *	 * - * - *
 *	    / \
 *	   *   *
 * @author yuri
 *
 */
public class StarGraph extends GraphAsHashMap<Index,BasicVertexInfo> {
	
	private int ray_length;
	private int rays;

	/**
	 * 
	 * @param rays number of "rays" of the star, graph will have an additional central vertex connecting all "rays"
	 */
	public StarGraph(int rays)
	{
		super();
		this.rays = rays;
		this.ray_length = 1;
		this.addVertex(Index.valueOf(0));		// create center vertex
		for (int i = 1; i <= rays; i++) {
			this.addVertex(Index.valueOf(i));		// create "ray" vertex
			this.addEdge(Index.valueOf(0),Index.valueOf(i));	// connect ray to center
		}
	}
	
	/**
	 * 
	 * @param rays		number of "rays" of the star, graph will have an additional central vertex connecting all "rays"
	 * @param length 	length of each ray (number of vertices excluding center).
	 */
	public StarGraph(int rays, int length)
	{
		super();
		this.rays = rays;
		this.ray_length = length;
		this.addVertex(Index.valueOf(0));		// create center vertex
		
		for (int i = 0; i < length*rays+1; i++) {
			this.addVertex(Index.valueOf(i));
			int prev = i-rays;	// prev vertex index
			if (prev != i)
			{
				this.addEdge(Index.valueOf(Math.max(0, prev)),Index.valueOf(i));
			}
		}
		
	}
	
	public int[] getOuterVertices()
	{
		int[] result = new int[this.rays];
		for (int i=0; i < this.rays; i++)
			result[i] = this.getNumberOfVertices()-this.rays+i;
		return result;
	}
	
	public static void main(String[] args)
	{
		StarGraph s = new StarGraph(5,2);
		int[] outer = s.getOuterVertices();
		for (int i = 0; i < outer.length; i++) {
			System.out.println(outer[i]);
		}
	}

}
