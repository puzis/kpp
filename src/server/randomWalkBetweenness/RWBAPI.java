package server.randomWalkBetweenness;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

public interface RWBAPI {

  /**
   * Creates RWB algorithm with the given network.
   *
   * @param netID index
   * @return Index of the algorithm in the Database (wrapped as string for jersey)
   */
  String create(int netID);

  /**
   * Starts an execution that creates RWB algorithm with the given network.
   *
   * @param netID index
   * @return Execution index in the Database
   */
  int createAsynch(int netID);

  /**
   * Removes the given RWB algorithm from the Database maps.
   *
   * @param algID index
   * @return 0
   */
  int destroy(int algID);

  /**
   * Returns the betweenness value of the given vertex in the given RWB algorithm instance.
   *
   * @param algID index
   * @param vertex the vertex id
   * @return betweenness value
   */
  double getBetweennessVertex(int algID, int vertex);

  /**
   * Returns an array of betweenness values of the given vertices in the given RWB algorithm instance.
   * The order of the betweenness values in the array corresponds to the order of the given vertices.
   *
   * @param algID index
   * @param vertices     of vertices
   * @return array of betweenness values
   */
  DoubleArray getBetweennessVertices(int algID, IntArray vertices);

  /**
   * Returns an array of betweenness values of all vertices in the given RWB algorithm instance.
   * The order of the betweenness values in the array corresponds to the order of the vertices in the graph.
   *
   * @param algID index
   * @return array of betweenness values
   */
  DoubleArray getBetweenness(int algID);

  /**
   * Returns the sum of betweenness values of the given vertices in the given RWB instance.
   *
   * @param algID index
   * @param vertices     of vertices
   * @return betweenness value
   */
  double getSumGroup(int algID, IntArray vertices);

  /**
   * Searches for deployment of vertices (using TopK algorithm) according to given parameters.
   *
   * @param algID           algorithm index
   * @param k             is the size of the desired deployment
   * @param candidatesObj        candidates for the deployment (can be an empty list)
   * @param givenVerticesObj are the already deployed vertices (can be an empty list)
   * @param givenEdgesObj    are the already deployed links (can be an empty list)
   * @return array of vertices
   */
  IntArray getCentralVertices(
    int algID,
    int k,
    IntArray candidatesObj,
    IntArray givenVerticesObj,
    IntArray givenEdgesObj
  );

  /**
   * Starts an execution which searches for deployment of vertices (using TopK algorithm) according to given parameters.
   *
   *
   * @param algID           algorithm index
   * @param k             is the size of the desired deployment
   * @param candidatesObj    for the deployment (can be an empty list)
   * @param givenVerticesObj are the already deployed vertices (can be an empty list)
   * @param givenEdgesObj    are the already deployed links (can be an empty list)
   * @return execution index
   */
  int getCentralVerticesAsynch(
    int algID,
    int k,
    IntArray candidatesObj,
    IntArray givenVerticesObj,
    IntArray givenEdgesObj
  );
}