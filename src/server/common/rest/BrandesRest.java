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

import server.shortestPathBetweenness.BrandesController;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

//!!!!!!!!!!!!!!!!!!!!!!!!!!
@Path("/brandes")
//!!!!!!!!!!!!!!!!!!!!!!!!!!
public class BrandesRest{

  private BrandesController alg;

  public BrandesRest()
  {
    alg = new BrandesController();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_communicationWeightsStr")
  public int create_communicationWeightsStr(
    @FormParam("netID") int netID,
    @FormParam("communicationWeightsStr") String communicationWeightsStr) {
    return alg.create(netID, communicationWeightsStr);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create")
  public int create(
    @FormParam("netID") int netID) {
    return alg.create(netID);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_tmID")
  public int create_tmID(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID) {
    return alg.create(netID, tmID);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_tmID_sources")
  public int create_tmID_sources(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID,
    @FormParam("sources") IntArray sources) {
      Object[] a = toObjectArray(sources);
    return alg.create(netID, tmID, a);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create_tmID_sources_shortestPathAlg")
  public int create_tmID_sources_shortestPathAlg(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID,
    @FormParam("sources") IntArray sources,
    @FormParam("shortestPathAlg") String shortestPathAlg) {
      Object[] a = toObjectArray(sources);
    return alg.create(netID, tmID, a, shortestPathAlg);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("createAsynch_communicationWeightsStr")
  public int createAsynch_communicationWeightsStr(
    @FormParam("netID") int netID,
    @FormParam("communicationWeightsStr") String communicationWeightsStr) {
    return alg.createAsynch(netID, communicationWeightsStr);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("createAsynch_tmID")
  public int createAsynch_tmID(
    @FormParam("netID") int netID,
    @FormParam("tmID") int tmID) {
    return alg.createAsynch(netID, tmID);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("createAsynch")
  public int createAsynch(
    @FormParam("netID") int netID){
    return alg.createAsynch(netID);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("destroy")
  public int destroy(
    @FormParam("algID") int algID) {
    return alg.destroy(algID);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweennessVertex")
  public double getBetweennessVertex(
    @FormParam("algID") int algID,
    @FormParam("vertex") int vertex) {
    return alg.getBetweenness(algID, vertex);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweennessVertices")
  public DoubleArray getBetweenness(
    @FormParam("algID") int algID,
    @FormParam("vertices") IntArray vertices) {
    Object[] betweenness = alg.getBetweenness(algID, toIntArray(vertices));
    return toDoubleArray(betweenness);
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweenness")
  public DoubleArray getBetweenness(
    @FormParam("algID") int algID) {
    return toDoubleArray(alg.getBetweenness(algID));
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