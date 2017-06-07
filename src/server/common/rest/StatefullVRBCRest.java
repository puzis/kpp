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

import server.rbc.StatefullVRBCController;

import commons.vo.DoubleArray;
import commons.vo.IntArray;

//!!!!!!!!!!!!!!!!!!!!!!!!!!
@Path("/statefullvrbc")
//!!!!!!!!!!!!!!!!!!!!!!!!!!
public class StatefullVRBCRest {

  private StatefullVRBCController alg;

  public StatefullVRBCRest()
  {
    alg = new StatefullVRBCController();
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("create")
  public int create(
    @FormParam("netID") int netID,
    @FormParam("communicationWeightsStr") String communicationWeightsStr,
    @FormParam("cachetype") int cachetype) {
    return alg.create(netID, communicationWeightsStr, cachetype);
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
  @Path("getBetweenness")
  public double getBetweenness(
    @FormParam("algID") int algID,
    @FormParam("v") int v) {
    return alg.getBetweenness(algID, v);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getTargetDependency")
  public double getTargetDependency(
    @FormParam("algID") int algID,
    @FormParam("v") int v,
    @FormParam("t") int t) {
    return alg.getTargetDependency(algID, v, t);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getSourceDependency")
  public double getSourceDependency(
    @FormParam("algID") int algID,
    @FormParam("s") int s,
    @FormParam("v") int v) {
    return alg.getSourceDependency(algID, s, v);
  }
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Path("getDelta")
  public double getDelta(
    @FormParam("algID") int algID,
    @FormParam("s") int s,
    @FormParam("v") int v,
    @FormParam("t") int t) {
    return alg.getDelta(algID, s, v, t);
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