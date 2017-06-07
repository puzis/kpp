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

import server.degree.DegreeController;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

@Path("/degree")
public class DegreeRest {

  private DegreeController alg;

  public DegreeRest() {
    alg = new DegreeController();
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
  @Path("createAsynch")
  public int createAsynch(
    @FormParam("netID") int netID) {
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
  @Path("getDegreeVertrex")
  public double getDegreeVertrex(
    @FormParam("algID") int algID,
    @FormParam("v") int v) {
    return alg.getDegree(algID, v);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getDegreeVertices")
  public DoubleArray getDegreeVertices(
    @FormParam("algID") int algID, @FormParam("vertices") IntArray vertices) {
    Object[] betweenness = alg.getDegree(algID, toIntArray(vertices));
    return toDoubleArray(betweenness);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getDegree")
  public DoubleArray getDegree(
    @FormParam("algID") int algID) {
    return toDoubleArray(alg.getDegree(algID));
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getSumGroup")
  public int getSumGroup(
    @FormParam("algID") int algID,
    @FormParam("vertices") IntArray vertices,
    @FormParam("edges") IntArray edges) {
    return alg.getSumGroup(algID, toObjectArray(vertices), toObjectArray(edges));
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
    Object[] CentralEdges = alg.getCentralEdges(algID,
      k,
      toObjectArray(candidatesObj),
      toObjectArray(givenVerticesObj),
      toObjectArray(givenEdgesObj));
    return toIntArray(CentralEdges);
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
    return alg.getCentralVerticesAsynch(algID,
      k,
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
    return alg.getCentralEdgesAsynch(algID,
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