package topology;

import common.Factory;
import java.util.Random;

import javolution.util.FastMap;
import javolution.util.Index;

import common.Pair;

public class BANetworkGenerator<T extends BasicVertexInfo> {

    private AbstractUndirectedGraph<Index, T> m_graph = null;
    private double m_avgDegree = 0;
    private double m_carry = 0;
    private FastMap<Index, Pair<Index, Index>> m_edges = new FastMap<Index, Pair<Index, Index>>();

    public BANetworkGenerator(double avgDegree) {
        m_avgDegree = avgDegree;
    }

    /**
     * generate network topology using random seed
     *
     * @param numOfVertices
     * @return
     */
    public AbstractUndirectedGraph<Index, T> generate(int numOfVertices) {
        return generate(numOfVertices, new Random().nextLong());
    }

    public AbstractUndirectedGraph<Index, T> generate(int numOfVertices, long seed) {
        return generate(numOfVertices, new Factory<T, Index>() {
            @Override
            public T construct(Index argument) {
                return (T) new VertexInfo();
            }
        }, seed);
    }

    public AbstractUndirectedGraph<Index, T> generate(int numOfVertices, Factory<T, Index> vertexInfoFactory, long seed) {
        m_graph = new GraphAsHashMap<Index, T>();

        /**
         * Initialize a clique with avgDegree vertices.
         */
        int vertexCnt = (int) Math.ceil(m_avgDegree);
        for (int i = 0; i < vertexCnt; i++) {
            final Index index = Index.valueOf(i);
            m_graph.addVertex(index, vertexInfoFactory.construct(index));
        }

        int edgesCounter = 0;
        for (int i = 0; i < vertexCnt; i++) {
            for (int j = 0; j < vertexCnt; j++) {
                if (i != j && !m_graph.isEdge(Index.valueOf(i), Index.valueOf(j))) {
                    m_graph.addEdge(Index.valueOf(i), Index.valueOf(j), new EdgeInfo<Index, T>());
                    m_edges.put(Index.valueOf(edgesCounter), new Pair<Index, Index>(Index.valueOf(i), Index.valueOf(j)));
                    edgesCounter++;
                }
            }
        }

        /**
         * Build the rest of the graph and output intervals.
         */
        Random random = new Random(seed);
        while (vertexCnt < numOfVertices) {
            /**
             * Connect vertex (vertexCnt + 1) to avgDegree vertices.
             */
            m_carry += m_avgDegree;
            while (m_carry >= 1) {
                int edgeNumber = 0;
                /**
                 * Randomly chosen edge.
                 */
                int target = 0;
                /**
                 * Randomly chosen vertex.
                 */
                if (m_edges.size() > 0) {
                    edgeNumber = random.nextInt(m_edges.size());
                    /**
                     * Choose one of the vertices of the edge to connect to.
                     */
                    int r = random.nextInt(2);
                    if (r == 0) {
                        target = m_edges.get(Index.valueOf(edgeNumber)).getValue1().intValue();
                    } else {
                        target = m_edges.get(Index.valueOf(edgeNumber)).getValue2().intValue();
                    }
                }

                if (vertexCnt != target
                        && !m_edges.containsValue(new Pair<Index, Index>(Index.valueOf(vertexCnt), Index.valueOf(target)))
                        && !m_edges.containsValue(new Pair<Index, Index>(Index.valueOf(target), Index.valueOf(vertexCnt)))) {
                    if (!m_graph.isVertex(Index.valueOf(vertexCnt))) /**
                     * If the vertex has not been added to the graph yet.
                     */
                    {
                        final Index index = Index.valueOf(vertexCnt);
                        m_graph.addVertex(index, vertexInfoFactory.construct(index));
                    }
                    m_graph.addEdge(Index.valueOf(vertexCnt), Index.valueOf(target));
                    m_edges.put(Index.valueOf(m_edges.size()), new Pair<Index, Index>(Index.valueOf(vertexCnt), Index.valueOf(target)));
                    m_carry -= 1;
                }
            }
            vertexCnt++;
        }
        return m_graph;
    }

    /**
     * args[0] - The average degree of a vertex. args[1] - Number of vertices in
     * the final graph.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//		BANetworkGenerator baGenerator = new BANetworkGenerator(Integer.parseInt(args[0]));
//		int numOfVertices = Integer.parseInt(args[1]);
//		GraphInterface<Index> graph = baGenerator.generate(numOfVertices);
//		System.out.println(graph);

        BANetworkGenerator baGenerator = new BANetworkGenerator(1.1);
        int numOfVertices = 10;
        GraphInterface<Index, BasicVertexInfo> graph = baGenerator.generate(numOfVertices);
        System.out.println(graph);
    }
}