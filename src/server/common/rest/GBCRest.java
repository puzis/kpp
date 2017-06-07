/**
 * Created by IntelliJ IDEA.
 * User: Christoph Giese
 * Date: July 25, 2011
 *
 *<a href=mailto:christoph.giese@gmail.com>christoph.giese@gmail.com</a>
 */
package server.common.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import server.shortestPathBetweenness.GBCController;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

@Path("/gbc")

public class GBCRest
{

  private GBCController alg;

  public GBCRest()
  {
    alg = new GBCController();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_communicationWeightsStr_createRoutingTable_fullPrecomutation")
  public int create_communicationWeightsStr_createRoutingTable_fullPrecomutation(
    @FormParam("netID") int netID,
    @FormParam("communicationWeightsStr") String communicationWeightsStr,
    @FormParam("createRoutingTable") boolean createRoutingTable,
    @FormParam("fullPrecomutation") boolean fullPrecomutation) {
    return alg.create(netID, communicationWeightsStr, createRoutingTable, fullPrecomutation);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_tmID_createRoutingTable_fullPrecomutation")
  public int create_tmID_createRoutingTable_fullPrecomutation(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID,
    @FormParam("createRoutingTable") boolean createRoutingTable,
    @FormParam("fullPrecomutation") boolean fullPrecomutation) {
    return alg.create(netID, tmID, createRoutingTable, fullPrecomutation);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_tmID_spAlg_createRoutingTable_fullPrecomutation")
  public int create_tmID_spAlg_createRoutingTable_fullPrecomutation(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID,
    @FormParam("spAlg") String spAlg,
    @FormParam("createRoutingTable") boolean createRoutingTable,
    @FormParam("fullPrecomutation") boolean fullPrecomutation) {
    return alg.create(netID, tmID, spAlg,createRoutingTable, fullPrecomutation);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("dataWorkshopExists")
  public int dataWorkshopExists(
    @FormParam("netID") int netID){
    return alg.dataWorkshopExists(netID);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("deleteAnalysis")
  public int deleteAnalysis(
    @FormParam("netID") int netID){
    return alg.deleteAnalysis(netID);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("createAsynch_communicationWeightsStr_createRoutingTable")
  public int createAsynch_communicationWeightsStr_createRoutingTable(
    @FormParam("netID") int netID,
    @FormParam("communicationWeightsStr") String communicationWeightsStr,
    @FormParam("createRoutingTable") boolean createRoutingTable) {
    return alg.createAsynch(netID, communicationWeightsStr, createRoutingTable);
  }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("createAsynch_tmID_createRoutingTable")
    public int createAsynch_tmID_createRoutingTable(
      @FormParam("netID") int netID,
      @FormParam("tmID") int tmID,
      @FormParam("createRoutingTable") boolean createRoutingTable) {
      return alg.createAsynch(netID, tmID, createRoutingTable);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("createAsynch_createRoutingTable")
    public int createAsynch_createRoutingTable(
      @FormParam("netID") int netID,
      @FormParam("createRoutingTable") boolean createRoutingTable) {
      return alg.createAsynch(netID, createRoutingTable);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("storeAnalysis")
    public boolean storeAnalysis(
      @FormParam("netID") int netID,
      @FormParam("algID") int algID) {
      return alg.storeAnalysis(algID, netID);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("storeAnalysisAsynch")
    public int storeAnalysisAsynch(
      @FormParam("netID") int netID,
      @FormParam("algID") int algID) {
      return alg.storeAnalysisAsynch(algID, netID);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("writeDeployment")
    public String writeDeployment(
      @FormParam("netID") int netID,
      @FormParam("algID") int algID) {
      return alg.writeDeployment(algID, netID);
    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("writeDeploymentAsynch")
    public int writeDeploymentAsynch(
      @FormParam("netID") int netID,
      @FormParam("algID") int algID) {
      return alg.writeDeploymentAsynch(algID, netID);
    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getEdgeBetweenness")
    public DoubleArray getEdgeBetweenness(
      @FormParam("algID") int algID){
      Object[] edgebetweenness = alg.getEdgeBetweenness(algID);
      return toDoubleArray(edgebetweenness);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getSumGroup")
    public double getSumGroup(
      @FormParam("algID") int algID,
      @FormParam("vertices") IntArray vertices,
      @FormParam("edges") IntArray edges) {
      Object[] o_vertices = toObjectArray(vertices);
      Object[] o_edges = toObjectArray(edges);
      return alg.getSumGroup(algID, o_vertices, o_edges);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getGBC")
    public double getGBC(
      @FormParam("algID") int algID,
      @FormParam("vertices") IntArray vertices,
      @FormParam("edges") IntArray edges) {
      Object[] o_vertices = toObjectArray(vertices);
      Object[] o_edges = toObjectArray(edges);
      return alg.getGBC(algID, o_vertices, o_edges);
    }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getGBCAsynch")
    public int getGBCAsynch(
      @FormParam("algID") int algID,
      @FormParam("vertices") IntArray vertices,
      @FormParam("edges") IntArray edges) {
      Object[] o_vertices = toObjectArray(vertices);
      Object[] o_edges = toObjectArray(edges);
      return alg.getGBCAsynch(algID, o_vertices, o_edges);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getCentralVertices")
    public IntArray getCentralVertices(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      Object[] centralVertices = alg.getCentralVertices(algID,
        k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
      return toIntArray(centralVertices);
  }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getCentralEdges")
    public IntArray getCentralEdges(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      Object[] centralEdges = alg.getCentralEdges(algID,
        k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
      return toIntArray(centralEdges);
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getCentralVerticesAsynch")
    public int getCentralVerticesAsynch(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      return alg.getCentralVerticesAsynch(algID, k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getCentralEdgesAsynch")
    public int getCentralEdgesAsynch(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      return alg.getCentralEdgesAsynch(algID, k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getTopkCentralVertices")
    public IntArray getTopkCentralVertices(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      Object[] TopkCentralVertices = alg.getTopkCentralVertices(algID,
        k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
      return toIntArray(TopkCentralVertices);
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getTopkCentralEdges")
    public IntArray getTopkCentralEdges(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      Object[] TopkCentralEdges = alg.getTopkCentralEdges(algID,
        k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
      return toIntArray(TopkCentralEdges);
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getTopkCentralVerticesAsynch")
    public int getTopkCentralVerticesAsynch(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      return alg.getTopkCentralVerticesAsynch(algID, k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
  }
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("getTopkCentralEdgesAsynch")
    public int getTopkCentralEdgesAsynch(
      @FormParam("algID") int algID,
      @FormParam("k") int k,
      @FormParam("candidatesObj") IntArray candidatesObj,
      @FormParam("givenVerticesObj") IntArray givenVerticesObj,
      @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
      return alg.getTopkCentralEdgesAsynch(algID, k,
        toObjectArray(candidatesObj),
        toObjectArray(givenVerticesObj),
        toObjectArray(givenEdgesObj));
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getCharactersicticPathLength")
  public double getCharactersicticPathLength(
    @FormParam("algID") int algID) {
    return alg.getCharactersicticPathLength(algID);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("destroy")
  public int destroy(
    @FormParam("algID") int algID) {
    return alg.destroy(algID);
  }
  private Object[] toObjectArray(IntArray a) {
    List<Integer> array = a.getArray();
    Object[] res = new Object[array.size()];
    for (Integer i : array)
      res[i] = i;
    return res;
  }

  private DoubleArray toDoubleArray(Object[] doubles) {
    DoubleArray res = new DoubleArray();
    List<Double> array = res.getArray();
    for (Object d : doubles)
      array.add((Double) d);
    return res;
  }

  private IntArray toIntArray(Object[] ints) {
    IntArray res = new IntArray();
    List<Integer> array = res.getArray();
    for (Object i : ints)
      array.add((Integer) i);
    return res;
  }

  private int[] toIntArray(IntArray ints) {
    List<Integer> array = ints.getArray();
    int[] res = new int[array.size()];
    for (int i = 0; i < array.size(); i++)
      res[i] = array.get(i);
    return res;
  }
}