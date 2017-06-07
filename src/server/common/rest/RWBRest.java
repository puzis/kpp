/**
 * Created by IntelliJ IDEA.
 * User: Aviram Dayan
 * Date: Feb 18, 2010
 * Time: 10:26:08 AM
 *
 *<a href=mailto:avdayan@cs.bgu.ac.il>avdayan@cs.bgu.ac.il</a>
 */
package server.common.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import server.randomWalkBetweenness.RWBAPI;
import server.randomWalkBetweenness.RWBController;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

@Path("/rwb")
public class RWBRest implements RWBAPI {

  private RWBController alg;

  public RWBRest() {
    alg = new RWBController();
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create")
  public String create(
    @FormParam("netID") int netID) {
    return String.valueOf(alg.create(netID));
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("createAsynch")
  public int createAsynch(
    @FormParam("netID") int netID) {
    return alg.createAsynch(netID);
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("destroy")
  public int destroy(
    @FormParam("algID") int algID) {
    return alg.destroy(algID);
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweennessVertex")
  public double getBetweennessVertex(
    @FormParam("algID") int algID, @FormParam("vertex") int vertex) {
    return alg.getBetweenness(algID, vertex);
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweennessVertices")
  public DoubleArray getBetweennessVertices(
    @FormParam("algID") int algID, @FormParam("vertices") IntArray vertices) {
    Object[] betweenness = alg.getBetweenness(algID, toIntArray(vertices));
    return toDoubleArray(betweenness);
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getBetweenness")
  public DoubleArray getBetweenness(
    @FormParam("algID") int algID) {
    return toDoubleArray(alg.getBetweenness(algID));
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getSumGroup")
  public double getSumGroup(
    @FormParam("algID") int algID, @FormParam("vertices") IntArray vertices) {
    Object[] a = toObjectArray(vertices);
    return alg.getSumGroup(algID, a);
  }

  @Override
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

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getCentralVerticesAsynch")
  public int getCentralVerticesAsynch(
    @FormParam("algID") int algID,
    @FormParam("k") int k,
    @FormParam("candidatesObj") IntArray candidatesObj,
    @FormParam("givenVerticesObj") IntArray givenVerticesObj,
    @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
    return alg.getCentralVerticesAsynch(algID,
      k,
      toObjectArray(candidatesObj),
      toObjectArray(givenVerticesObj),
      toObjectArray(givenEdgesObj));
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