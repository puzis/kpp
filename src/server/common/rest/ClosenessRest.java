///**
// * Created by IntelliJ IDEA.
// * User: Christoph Giese
// * Date: July 25, 2011
// *
// *<a href=mailto:christoph.giese@gmail.com>christoph.giese@gmail.com</a>
// */
//package server.common.rest;
//import java.util.List;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.FormParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.MediaType;
//
////import server.closeness_old.ClosenessController;
//
//import commons.vo.DoubleArray;
//import commons.vo.IntArray;
//
//@Path("/closeness")
//public class ClosenessRest {
//
//  private ClosenessController alg;
//
//  public ClosenessRest() {
//    alg = new ClosenessController();
//  }
//
//    /**
//     *
//     * @param netID
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("create")
//  public String create(
//    @FormParam("netID") int netID) {
//    //return alg.create(netID);
//    return String.valueOf(alg.create(netID));
//    }
//
//    /**
//     *
//     * @param netID
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createNormal")
//  public String createNormal(
//    @FormParam("netID") int netID) {
//    //return alg.createNormal(netID);
//    return (new Integer(alg.createNormal(netID))).toString();
//  }
//
//    /**
//     *
//     * @param netID
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createReciprocal")
//  public String createReciprocal(
//    @FormParam("netID") int netID) {
//    //return alg.createReciprocal(netID);
//    return (new Integer(alg.createReciprocal(netID))).toString();
//  }
//
//    /**
//     *
//     * @param netID int
//     * @param immunityP double.toString()
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createExponential")
//  public String createExponential(
//    @FormParam("netID") int netID,
//    @FormParam("immunityP") String immunityP) {
//    return (new Integer(alg.createExponential(netID, Double.parseDouble(immunityP)))).toString();
//  }
//
//    /**
//     *
//     * @param netID
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("createAsynch")
//  public String createAsynch(
//    @FormParam("netID") int netID) {
//    //return alg.createAsynch(netID);
//    return (new Integer(alg.createAsynch(netID))).toString();
//  }
//
//    /**
//     *
//     * @param algID
//     * @return algID (int.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("destroy")
//  public String destroy(
//    @FormParam("algID") int algID) {
//    //return alg.destroy(algID);
//    return (new Integer(alg.destroy(algID))).toString();
//  }
//
//    /**
//     *
//     * @param algID
//     * @param v
//     * @return Closeness (double.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCloseness_v")
//  public String getCloseness_v(
//    @FormParam("algID") int algID,
//    @FormParam("v") int v) {
//    return (new Double(alg.getCloseness(algID, v))).toString();
//  }
//
//    /**
//     *
//     * @param algID
//     * @param vertices IntArray.toString()
//     * @return Closeness (DoubleArray.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCloseness_vertices")
//  public String getCloseness_vertices(
//    @FormParam("algID") int algID,
//    @FormParam("vertices") String vertices) {
//    Object[] betweenness = alg.getCloseness(algID, toIntArray(toIntArray(vertices)));
//    //return toDoubleArray(betweenness);
//    return toDoubleStringArray(betweenness);
//  }
//
//    /**
//     *
//     * @param algID
//     * @return Closeness (DoubleArray.toString)
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCloseness")
//  public String getCloseness(
//    @FormParam("algID") int algID) {
//    Object[] betweenness = alg.getCloseness(algID);
//    //return to DoubleArray(betweenness);
//    return toDoubleStringArray(betweenness);
//  }
//
//    /**
//     *
//     * @param algID
//     * @param vertices IntArray.toString()
//     * @param edges IntArray.toString()
//     * @return Double.toString()
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getSumGroup")
//  public String getSumGroup(
//    @FormParam("algID") int algID,
//    @FormParam("vertices") String vertices,
//    @FormParam("edges") String edges) {
//    //return alg.getSumGroup(algID, toObjectArray(vertices), toObjectArray(edges));
//    return (new Double(alg.getSumGroup(algID, toObjectArray(toIntArray(vertices)), toObjectArray(toIntArray(edges))))).toString();
//  }
//
//    /**
//     *
//     * @param algID
//     * @param k
//     * @param candidatesObj IntArray.toString()
//     * @param givenVerticesObj IntArray.toString()
//     * @param givenEdgesObj IntArray.toString()
//     * @return Intarray.toString()
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralVertices")
//  public String getCentralVertices(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") String candidatesObj,
//    @FormParam("givenVerticesObj") String givenVerticesObj,
//    @FormParam("givenEdgesObj") String givenEdgesObj) {
//    Object[] centralVertices = alg.getCentralVertices(algID,
//            k,
//            toObjectArray(toIntArray(candidatesObj)),
//            toObjectArray(toIntArray(givenVerticesObj)),
//            toObjectArray(toIntArray(givenEdgesObj)));
//    return toIntStringArray(centralVertices);
//  }
//
//    /**
//     *
//     * @param algID
//     * @param k
//     * @param candidatesObj IntArray.toString()
//     * @param givenVerticesObj IntArray.toString()
//     * @param givenEdgesObj IntArray.toString()
//     * @return Intarray.toString()
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralEdges")
//  public String getCentralEdges(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") String candidatesObj,
//    @FormParam("givenVerticesObj") String givenVerticesObj,
//    @FormParam("givenEdgesObj") String givenEdgesObj)
//  {
//    Object[] CentralEdges = alg.getCentralEdges(algID,
//            k,
//            toObjectArray(toIntArray(candidatesObj)),
//            toObjectArray(toIntArray(givenVerticesObj)),
//            toObjectArray(toIntArray(givenEdgesObj)));
//    return toIntStringArray(CentralEdges);
//  }
//
//    /**
//     *
//     * @param algID
//     * @param k
//     * @param candidatesObj IntArray.toString()
//     * @param givenVerticesObj IntArray.toString()
//     * @param givenEdgesObj IntArray.toString()
//     * @return Int.toString()
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralVerticesAsynch")
//  public String getCentralVerticesAsynch(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") String candidatesObj,
//    @FormParam("givenVerticesObj") String givenVerticesObj,
//    @FormParam("givenEdgesObj") String givenEdgesObj) {
//    return (new Integer(alg.getCentralVerticesAsynch(algID,
//            k,
//            toObjectArray(toIntArray(candidatesObj)),
//            toObjectArray(toIntArray(givenVerticesObj)),
//            toObjectArray(toIntArray(givenEdgesObj))))).toString();
//  }
//
//    /**
//     *
//     * @param algID
//     * @param k
//     * @param candidatesObj  IntArray.toString()
//     * @param givenVerticesObj IntArray.toString()
//     * @param givenEdgesObj  IntArray.toString()
//     * @return
//     */
//  @POST
//  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//  @Path("getCentralEdgesAsynch")
//  public String getCentralEdgesAsynch(
//    @FormParam("algID") int algID,
//    @FormParam("k") int k,
//    @FormParam("candidatesObj") String candidatesObj,
//    @FormParam("givenVerticesObj") String givenVerticesObj,
//    @FormParam("givenEdgesObj") String givenEdgesObj) {
//    return (new Integer(alg.getCentralEdgesAsynch(algID,
//      k,
//      toObjectArray(toIntArray(candidatesObj)),
//      toObjectArray(toIntArray(givenVerticesObj)),
//      toObjectArray(toIntArray(givenEdgesObj))))).toString();
//  }
//
//  private Object[] toObjectArray(IntArray a) {
//    List<Integer> array = a.getArray();
//    Object[] res = new Object[array.size()];
////    for (Integer i : array)
////      res[i] = i;
////    return res;
//
//    for (int i = 0; i < array.size(); i++)
//      res[i] = array.get(i);
//    return res;
//
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
//  private String toDoubleStringArray(Object[] doubles)
//  {
//    String res="";
//    for (Object d : doubles)
//    {
//      if(res.equals(""))
//      {
//         res = ((Double) d).toString();
//      }
//        else
//      {
//         res = res + ";" +((Double) d).toString();
//      }
//    }
//    return res;
//  }
//  private String toIntStringArray(Object[] ints)
//  {
//    String res ="";
//    for (Object i : ints)
//    {
//      if(res.equals(""))
//      {
//         res = ((Integer) i).toString();
//      }
//        else
//      {
//         res = res + ";" +((Integer) i).toString();
//      }
//    }
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
//  private IntArray toIntArray(String s)
//  {
//      String[] values = s.split(",");
//      IntArray res = new IntArray();
//      List<Integer> array = res.getArray();
//      for(int i=0; i<values.length;i++)
//      {
//          array.add(Integer.parseInt(values[i]));
//      }
//      return res;
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