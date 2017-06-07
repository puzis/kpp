///**
// * Created by IntelliJ IDEA.
// * User: Christoph Giese
// * Date: July 25, 2011
// *
// *<a href=mailto:christoph.giese@gmail.com>christoph.giese@gmail.com</a>
// */
//package server.common.rest;
//
//import java.util.List;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.FormParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.MediaType;
//
//import server.closeness_old.GroupClosenessController;
//
//import commons.vo.DoubleArray;
//import commons.vo.IntArray;
//
//@Path("/groupcloseness")
//public class GroupClosenessRest {
//
//  private GroupClosenessController alg;
//
//  public GroupClosenessRest() {
//    alg = new GroupClosenessController();
//  }
//
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("create")
//  public int create(
//    @FormParam("netID") int netID) {
//    return alg.create(netID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("create_importanceVec")
//  public int create_importanceVec(
//    @FormParam("netID") int netID,
//    @FormParam("importanceVec") IntArray importanceVec) {
//    return alg.create(netID, toObjectArray(importanceVec));
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createNormal")
//  public int createNormal(
//    @FormParam("netID") int netID) {
//    return alg.createNormal(netID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createOptimized")
//  public int createOptimized(
//    @FormParam("netID") int netID) {
//    return alg.createOptimized(netID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createOptimized_importanceVec")
//  public int createOptimized_importanceVec(
//    @FormParam("netID") int netID,
//    @FormParam("importanceVec") IntArray importanceVec) {
//    return alg.createOptimized(netID, toObjectArray(importanceVec));
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createReciprocal")
//  public int createReciprocal(
//    @FormParam("netID") int netID) {
//    return alg.createReciprocal(netID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createExponential")
//  public int createExponential(
//    @FormParam("netID") int netID,
//    @FormParam("immunityProbability") double immunityProbability) {
//    return alg.createExponential(netID, immunityProbability);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createMSBFS")
//  public int createMSBFS(
//    @FormParam("netID") int netID) {
//    return alg.createMSBFS(netID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createAsynch")
//  public int createAsynch(
//    @FormParam("netID") int netID,
//	@FormParam("formulaType") int formulaType,
//	@FormParam("formulaParam") Object[] formulaParam,
//	@FormParam ("algType") int algType){
//    return alg.createAsynch(netID, formulaType, formulaParam, algType);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("destroy")
//  public int destroy(
//    @FormParam("algID") int algID) {
//    return alg.destroy(algID);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getGroupCloseness")
//  public double getGroupCloseness(
//    @FormParam("algID") int algID,
//    @FormParam("vertices") IntArray vertices,
//    @FormParam("edges") IntArray edges) {
//    return alg.getGroupCloseness(algID, toObjectArray(vertices), toObjectArray(edges));
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getGroupClosenessAsynch")
//  public int getGroupClosenessAsynch(
//    @FormParam("algID") int algID,
//    @FormParam("vertices") IntArray vertices,
//    @FormParam("edges") IntArray edges) {
//    return alg.getGroupClosenessAsynch(algID, toObjectArray(vertices), toObjectArray(edges));
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralVertices")
//  public IntArray getCentralVertices(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") IntArray candidatesObj,
//    @FormParam("givenVerticesObj") IntArray givenVerticesObj,
//    @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
//    Object[] centralVertices = alg.getCentralVertices(algID,
//      k,
//      toObjectArray(candidatesObj),
//      toObjectArray(givenVerticesObj),
//      toObjectArray(givenEdgesObj));
//    return toIntArray(centralVertices);
//  }  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralVerticesOptimized")
//  public IntArray getCentralVerticesOptimized(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") IntArray candidatesObj,
//    @FormParam("givenVerticesObj") IntArray givenVerticesObj) {
//    Object[] centralVertices = alg.getCentralVerticesOptimized(algID,
//      k,
//      toObjectArray(candidatesObj),
//      toObjectArray(givenVerticesObj));
//    return toIntArray(centralVertices);
//  }
//
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralEdges")
//  public IntArray getCentralEdges(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") IntArray candidatesObj,
//    @FormParam("givenVerticesObj") IntArray givenVerticesObj,
//    @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
//    Object[] centralEdges = alg.getCentralEdges(algID,
//      k,
//      toObjectArray(candidatesObj),
//      toObjectArray(givenVerticesObj),
//      toObjectArray(givenEdgesObj));
//    return toIntArray(centralEdges);
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralVerticesAsynch")
//  public int getCentralVerticesAsynch(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") IntArray candidatesObj,
//    @FormParam("givenVerticesObj") IntArray givenVerticesObj,
//    @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
//    return alg.getCentralVerticesAsynch(algID,
//      k,
//      toObjectArray(candidatesObj),
//      toObjectArray(givenVerticesObj),
//      toObjectArray(givenEdgesObj));
//  }
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralEdgesAsynch")
//  public int getCentralEdgesAsynch(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") IntArray candidatesObj,
//    @FormParam("givenVerticesObj") IntArray givenVerticesObj,
//    @FormParam("givenEdgesObj") IntArray givenEdgesObj) {
//    return alg.getCentralEdgesAsynch(algID,
//      k,
//      toObjectArray(candidatesObj),
//      toObjectArray(givenVerticesObj),
//      toObjectArray(givenEdgesObj));
//  }
//
//
//  private Object[] toObjectArray(IntArray a) {
//    List<Integer> array = a.getArray();
//    Object[] res = new Object[array.size()];
//    for (Integer i : array)
//      res[i] = i;
//    return res;
//  }
//
//  private DoubleArray toDoubleArray(Object[] doubles) {
//    DoubleArray res = new DoubleArray();
//    List<Double> array = res.getArray();
//    for (Object d : doubles)
//      array.add((Double) d);
//    return res;
//  }
//
//  private IntArray toIntArray(Object[] ints) {
//    IntArray res = new IntArray();
//    List<Integer> array = res.getArray();
//    for (Object i : ints)
//      array.add((Integer) i);
//    return res;
//  }
//
//  private int[] toIntArray(IntArray ints) {
//    List<Integer> array = ints.getArray();
//    int[] res = new int[array.size()];
//    for (int i = 0; i < array.size(); i++)
//      res[i] = array.get(i);
//    return res;
//  }
//}