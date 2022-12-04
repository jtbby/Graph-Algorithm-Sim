import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;

import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.EdgeType;

import org.apache.commons.collections15.Factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Class that makes an undirected graph
 * with vertices (nodes) and edges with their own unique ID's
 * that uses several methods to change and manipulate the
 * graph's properties.
 *
 * @author Justin Thomas
 *
 */
class ThreeTenGraph implements Graph<GraphNode,GraphEdge>, UndirectedGraph<GraphNode,GraphEdge> {

    /**
     * The max number of nodes the graph can hold.
     */
    private static final int MAX_NUMBER_OF_NODES = 200;

    /**
     * initialize a Linkedlist of the vertices (nodes) in the graph to null.
     */
    private LinkedList<GraphNode> nodeList = null;
    /**
     * initialize an array of LinkedLists (separate chaining), adjacent list to be null.
     */
    private LinkedList<Destination>[] adjList = null;

    /**
     * makes an object that holds a unique end node and edge
     * which marks the edge that connects to the endpoint for a current node.
     *
     */
    private class Destination {
        /**
         * The vertex which is the neighbor of the current node.
         */
        GraphNode node;
        /**
         * The edge that connects the current vertex and with the neighbor.
         */
        GraphEdge edge;
        /**
         * Constructor that initializes the destination for a vertex.
         *
         * @param n the neighbor of the vertex
         * @param e the incident edge/edge the connects this vertex to n/neighbor
         */
        Destination(GraphNode n, GraphEdge e) { this.node = n; this.edge = e; }

    }


    /**
     * Graph constructor that initializes a linked list of vertices and
     * a HashTable with a limit of 200 nodes/vertices.
     */
    public ThreeTenGraph() {
        nodeList =  new LinkedList<GraphNode>();
        adjList = (LinkedList<Destination>[]) new LinkedList[MAX_NUMBER_OF_NODES];
    }

    /**
     * Returns a view of all edges in this graph. In general, this
     * obeys the Collection contract, and therefore makes no guarantees
     * about the ordering of the vertices within the set.
     * @return a Collection view of all edges in this graph
     */
    public Collection<GraphEdge> getEdges() {

        LinkedList<GraphEdge> edges = new LinkedList<>();

        for(int i = 0; i < nodeList.size(); i++) {
            GraphNode current = nodeList.get(i);
            int id = current.id;

            Iterator<Destination> itr = adjList[id].iterator();

            if (!itr.hasNext()) {
                continue;
            }

            do {
                Destination currentDest = itr.next();
                if (edges.contains(currentDest.edge)) {
                    continue;
                }
                edges.add(currentDest.edge);
            } while(itr.hasNext());
        }

        return edges;
    }

    /**
     * Returns a view of all vertices in this graph. In general, this
     * obeys the Collection contract, and therefore makes no guarantees
     * about the ordering of the vertices within the set.
     * @return a Collection view of all vertices in this graph
     */
    public Collection<GraphNode> getVertices() {
        return nodeList;
    }

    /**
     * Returns the number of edges in this graph.
     * @return  number of edges in this graph
     */
    public int getEdgeCount() {
        return getEdges().size();
    }

    /**
     * Returns the number of vertices in this graph.
     * @return the number of vertices in this graph
     */
    public int getVertexCount() {
        return nodeList.size();
    }

    /**
     * Returns true if this graph's vertex collection contains vertex.
     * Equivalent to getVertices().contains(vertex).
     * @param vertex the vertex whose presence is being queried
     * @return true iff this graph contains a vertex vertex
     */
    public boolean containsVertex(GraphNode vertex) {
        if (adjList[vertex.id] != null) {
            return true;
        }
        return false;
    }

    /**
     * Returns the collection of vertices which are connected to vertex
     * via any edges in this graph.
     * If vertex is connected to itself with a self-loop, then
     * it will be included in the collection returned.
     *
     * @param vertex the vertex whose neighbors are to be returned
     * @return  the collection of vertices which are connected to vertex,
     *     or null if vertex is not present
     */
    public Collection<GraphNode> getNeighbors(GraphNode vertex) {

        if (adjList[vertex.id] == null) {
            return null;
        }

        LinkedList<GraphNode> neighbors = new LinkedList<GraphNode>();

        for(int i = 0; i < adjList[vertex.id].size(); i++) {
            Destination current = adjList[vertex.id].get(i);
            neighbors.add(current.node);
        }
        return neighbors;
    }

    /**
     * Returns the number of vertices that are adjacent to vertex
     * (that is, the number of vertices that are incident to edges in vertex's
     * incident edge set).
     *
     * <p>Equivalent to getNeighbors(vertex).size().
     * @param vertex the vertex whose neighbor count is to be returned
     * @return the number of neighboring vertices
     */
    public int getNeighborCount(GraphNode vertex) {

        if (adjList[vertex.id] == null) {
            return 0;
        }

        return adjList[vertex.id].size();
    }

    /**
     * Returns the collection of edges in this graph which are connected to vertex.
     *
     * @param vertex the vertex whose incident edges are to be returned
     * @return  the collection of edges which are connected to vertex,
     *     or null if vertex is not present
     */
    public Collection<GraphEdge> getIncidentEdges(GraphNode vertex) {

        LinkedList<GraphEdge> edges = new LinkedList<GraphEdge>();

        if(adjList[vertex.id] == null) {
            return null;
        }

        for(int i = 0; i < adjList[vertex.id].size(); i++) {
            Destination current = adjList[vertex.id].get(i);

            edges.add(current.edge);

        }
        return edges;
    }
    /**
     * Returns the endpoints of edge as a Pair<GraphNode>.
     * @param edge the edge whose endpoints are to be returned
     * @return the endpoints (incident vertices) of edge
     */
    public Pair<GraphNode> getEndpoints(GraphEdge edge) {
        GraphNode one = null;
        GraphNode two = null;

        for(int i = 0; i < nodeList.size(); i++) {

            LinkedList<Destination> currentChain = adjList[nodeList.get(i).id];
            Iterator<Destination> itr = currentChain.iterator();

            if (!itr.hasNext()) {
                continue;
            }
            do {
                Destination current = itr.next();
                if(current.edge.id == edge.id) {
                    if (one == null) {
                        one = current.node;
                    }
                    else if(two == null && one != null) {
                        two = current.node;
                        Pair<GraphNode> twoNodes = new Pair<>(one, two);
                        return twoNodes;
                    }
                }
            }while(itr.hasNext());
        }
        return null;
    }

    /**
     * Returns an edge that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting
     * v1 to v2), any of these edges
     * may be returned.  findEdgeSet(v1, v2) may be
     * used to return all such edges.
     * Returns null if either of the following is true:
     * <ul>
     * <li/>v1 is not connected to v2
     * <li/>either v1 or v2 are not present in this graph
     * </ul>
     *
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be connected to
     * v2 via a given <i>directed</i> edge e if
     * v1 == e.getSource() && v2 == e.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if
     * u is incident to both v1 and v2.)
     * @param v1  first vertex
     * @param v2  second vertex
     * @return  an edge that connects v1 to v2,
     *     or null if no such edge exists (or either vertex is not present)
     * @see Hypergraph#findEdgeSet(Object, Object)
     */
    public GraphEdge findEdge(GraphNode v1, GraphNode v2) {

        if(adjList[v1.id] == null || adjList[v1.id].size() == 0) {
            return  null;
        }
        if(adjList[v2.id] == null || adjList[v2.id].size() == 0) {
            return null;
        }

        GraphEdge connection = null;

        for(int i = 0; i < adjList[v1.id].size(); i++) {

            Destination current = adjList[v1.id].get(i);

            if (current.node == v2) {
                connection = current.edge;
            }

        }

        for(int i = 0; i < adjList[v2.id].size(); i++) {

            Destination current = adjList[v2.id].get(i);

            if(current.node == v1 && connection != null) {
                return connection;
            }

        }
        return null;
    }

    /**
     * Returns true if vertex and edge
     * are incident to each other.
     * Equivalent to getIncidentEdges(vertex).contains(edge) and to
     * getIncidentVertices(edge).contains(vertex).
     * @param vertex being checked
     * @param edge being checked
     * @return true if vertex and edge
     *     are incident to each other
     */
    public boolean isIncident(GraphNode vertex, GraphEdge edge) {
        return getIncidentEdges(vertex).contains(edge);
    }

    /**
     * Adds edge e to this graph such that it connects
     * vertex v1 to v2.
     * Equivalent to addEdge(e, new Pair<GraphNode>(v1, v2)).
     * If this graph does not contain v1, v2,
     * or both, implementations may choose to either silently add
     * the vertices to the graph or throw an IllegalArgumentException.
     * If this graph assigns edge types to its edges, the edge type of
     * e will be the default for this graph.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object, EdgeType)
     */
    public boolean addEdge(GraphEdge e, GraphNode v1, GraphNode v2) {
        if(getIncidentEdges(v1) == null || getIncidentEdges(v2) == null) {
            return false;
        }

        if (getIncidentEdges(v1).contains(e) || getIncidentEdges(v2).contains(e)) {
            return false;
        }

        Destination newest = new Destination(v2, e);

        adjList[v1.id].add(newest);

        newest = new Destination(v1, e);

        adjList[v2.id].add(newest);

        return true;
    }

    /**
     * Adds vertex to this graph.
     * Fails if vertex is null or already in the graph.
     *
     * @param vertex    the vertex to add
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if vertex is null
     */
    public boolean addVertex(GraphNode vertex) {

        if (adjList[vertex.id] != null) {
            return false;
        }

        adjList[vertex.id] = new LinkedList<>();

        nodeList.add(vertex);

        return true;
    }

    /**
     * Removes edge from this graph.
     * Fails if edge is null, or is otherwise not an element of this graph.
     *
     * @param edge the edge to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeEdge(GraphEdge edge) {

        if(edge == null) {
            return false;
        }

        int removed = 0;



        for(int i = 0; i < nodeList.size(); i++) {

            Iterator<Destination> itr = adjList[nodeList.get(i).id].iterator();

            if(!itr.hasNext()) {
                continue;
            }
            do {
                Destination current = itr.next();
                if(current.edge == edge) {
                    adjList[nodeList.get(i).id].remove(current);
                    removed++;
                }
            } while(itr.hasNext());
        }
        if(removed == 2) {
            return true;
        }
        return false;
    }

    /**
     * Removes vertex from this graph.
     * As a side effect, removes any edges e incident to vertex if the
     * removal of vertex would cause e to be incident to an illegal
     * number of vertices.  (Thus, for example, incident hyperedges are not removed, but
     * incident edges--which must be connected to a vertex at both endpoints--are removed.)
     *
     * <p>Fails under the following circumstances:
     * <ul>
     * <li/>vertex is not an element of this graph
     * <li/>vertex is null
     * </ul>
     *
     * @param vertex the vertex to remove
     * @return true if the removal is successful, false otherwise
     */
    public boolean removeVertex(GraphNode vertex) {

        if(vertex == null) {
            return false;
        }

        if(adjList[vertex.id] == null) {
            return false;
        }

        for (int i = 0; i < adjList[vertex.id].size(); i++) {
            Destination current = adjList[vertex.id].get(i);
            Destination removeDest = new Destination(vertex, current.edge);
            adjList[current.node.id].remove(removeDest);
       }

        adjList[vertex.id] = null;
        return true;
    }

    //********************************************************************************
    //   testing code goes here... edit this as much as you want!
    //********************************************************************************

    /**
     *  {@inheritDoc}
     */
    public String toString() {
        return super.toString();
    }

    /**
     * Main method that initializes and runs tests
     * for the graph.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        GraphNode[] nodes = {
            new GraphNode(0),
            new GraphNode(1),
            new GraphNode(2),
            new GraphNode(3),
            new GraphNode(4),
            new GraphNode(5),
            new GraphNode(6),
            new GraphNode(7),
            new GraphNode(8),
            new GraphNode(9)
        };

        GraphEdge[] edges = {
            new GraphEdge(0),
            new GraphEdge(1),
            new GraphEdge(2),
            new GraphEdge(3),
            new GraphEdge(4),
            new GraphEdge(5)
        };


        ThreeTenGraph graph = new ThreeTenGraph();
        for(GraphNode n : nodes) {
            graph.addVertex(n);
        }

        graph.addEdge(edges[0],nodes[0],nodes[1]);
        graph.addEdge(edges[1],nodes[1],nodes[2]);
        graph.addEdge(edges[2],nodes[3],nodes[2]);
        graph.addEdge(edges[3],nodes[6],nodes[7]);
        graph.addEdge(edges[4],nodes[8],nodes[9]);
        graph.addEdge(edges[5],nodes[9],nodes[0]);

        if(graph.getVertexCount() == 10 && graph.getEdgeCount() == 6) {
            System.out.println("Yay 1");
        }

        if(graph.containsVertex(new GraphNode(0)) && graph.containsEdge(new GraphEdge(3))) {
            System.out.println("Yay 2");
        }

    }

    /**
     * Returns true if v1 and v2 share an incident edge.
     * Equivalent to getNeighbors(v1).contains(v2).
     *
     * @param v1 the first vertex to test
     * @param v2 the second vertex to test
     * @return true if v1 and v2 share an incident edge
     */
    public boolean isNeighbor(GraphNode v1, GraphNode v2) {
        return (findEdge(v1, v2) != null);
    }

    /**
     * Returns true if this graph's edge collection contains edge.
     * Equivalent to getEdges().contains(edge).
     * @param edge the edge whose presence is being queried
     * @return true iff this graph contains an edge edge
     */
    public boolean containsEdge(GraphEdge edge) {
        return (getEndpoints(edge) != null);
    }

    /**
     * Returns the collection of edges in this graph which are of type edge_type.
     * @param edgeType the type of edges to be returned
     * @return the collection of edges which are of type edge_type, or
     *     null if the graph does not accept edges of this type
     * @see EdgeType
     */
    public Collection<GraphEdge> getEdges(EdgeType edgeType) {
        if(edgeType == EdgeType.UNDIRECTED) {
            return getEdges();
        }
        return null;
    }

    /**
     * Returns the number of edges of type edge_type in this graph.
     * @param edgeType the type of edge for which the count is to be returned
     * @return the number of edges of type edge_type in this graph
     */
    public int getEdgeCount(EdgeType edgeType) {
        if(edgeType == EdgeType.UNDIRECTED) {
            return getEdgeCount();
        }
        return 0;
    }

    /**
     * Returns the number of edges incident to vertex.
     * Special cases of interest:
     * <ul>
     * <li/> Incident self-loops are counted once.
     * <li> If there is only one edge that connects this vertex to
     * each of its neighbors (and vice versa), then the value returned
     * will also be equal to the number of neighbors that this vertex has
     * (that is, the output of getNeighborCount).
     * <li> If the graph is directed, then the value returned will be
     * the sum of this vertex's indegree (the number of edges whose
     * destination is this vertex) and its outdegree (the number
     * of edges whose source is this vertex), minus the number of
     * incident self-loops (to avoid double-counting).
     * </ul>
     *
     * <p>Equivalent to getIncidentEdges(vertex).size().
     *
     * @param vertex the vertex whose degree is to be returned
     * @return the degree of this node
     * @see Hypergraph#getNeighborCount(Object)
     */
    public int degree(GraphNode vertex) {
        return getNeighborCount(vertex);
    }

    /**
     * Returns a Collection view of the predecessors of vertex
     * in this graph.  A predecessor of vertex is defined as a vertex v
     * which is connected to
     * vertex by an edge e, where e is an outgoing edge of
     * v and an incoming edge of vertex.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a Collection view of the predecessors of
     *     vertex in this graph
     */
    public Collection<GraphNode> getPredecessors(GraphNode vertex) {
        return getNeighbors(vertex);
    }

    /**
     * Returns a Collection view of the successors of vertex
     * in this graph.  A successor of vertex is defined as a vertex v
     * which is connected to
     * vertex by an edge e, where e is an incoming edge of
     * v and an outgoing edge of vertex.
     * @param vertex    the vertex whose predecessors are to be returned
     * @return  a Collection view of the successors of
     *     vertex in this graph
     */
    public Collection<GraphNode> getSuccessors(GraphNode vertex) {
        return getNeighbors(vertex);
    }

    /**
     * Returns true if v1 is a predecessor of v2 in this graph.
     * Equivalent to v1.getPredecessors().contains(v2).
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a predecessor of v2, and false otherwise.
     */
    public boolean isPredecessor(GraphNode v1, GraphNode v2) {
        return isNeighbor(v1, v2);
    }

    /**
     * Returns true if v1 is a successor of v2 in this graph.
     * Equivalent to v1.getSuccessors().contains(v2).
     * @param v1 the first vertex to be queried
     * @param v2 the second vertex to be queried
     * @return true if v1 is a successor of v2, and false otherwise.
     */
    public boolean isSuccessor(GraphNode v1, GraphNode v2) {
        return isNeighbor(v1, v2);
    }

    /**
     * If directed_edge is a directed edge in this graph, returns the source;
     * otherwise returns null.
     * The source of a directed edge d is defined to be the vertex for which
     * d is an outgoing edge.
     * directed_edge is guaranteed to be a directed edge if
     * its EdgeType is DIRECTED.
     * @param directedEdge the edge being checked
     * @return  the source of directed_edge if it is a directed edge in this graph, or null otherwise
     */
    public GraphNode getSource(GraphEdge directedEdge) {
        return null;
    }

    /**
     * If directed_edge is a directed edge in this graph, returns the destination;
     * otherwise returns null.
     * The destination of a directed edge d is defined to be the vertex
     * incident to d for which
     * d is an incoming edge.
     * directed_edge is guaranteed to be a directed edge if
     * its EdgeType is DIRECTED.
     *
     * @param directedEdge the edge being checked
     * @return  the destination of directed_edge if it is a directed edge in this graph, or null otherwise
     */
    public GraphNode getDest(GraphEdge directedEdge) {
        return null;
    }

    /**
     * Returns a Collection view of the incoming edges incident to vertex
     * in this graph.
     * @param vertex    the vertex whose incoming edges are to be returned
     * @return  a Collection view of the incoming edges incident
     *     to vertex in this graph
     */
    public Collection<GraphEdge> getInEdges(GraphNode vertex) {
        return getIncidentEdges(vertex);
    }

    /**
     * Returns the collection of vertices in this graph which are connected to edge.
     * Note that for some graph types there are guarantees about the size of this collection
     * (i.e., some graphs contain edges that have exactly two endpoints, which may or may
     * not be distinct).  Implementations for those graph types may provide alternate methods
     * that provide more convenient access to the vertices.
     *
     * @param edge the edge whose incident vertices are to be returned
     * @return  the collection of vertices which are connected to edge,
     *     or null if edge is not present
     */
    public Collection<GraphNode> getIncidentVertices(GraphEdge edge) {

        Pair<GraphNode> p = getEndpoints(edge);
        if(p == null) return null;

        LinkedList<GraphNode> ret = new LinkedList<>();
        ret.add(p.getFirst());
        ret.add(p.getSecond());
        return ret;
    }

    /**
     * Returns a Collection view of the outgoing edges incident to vertex
     * in this graph.
     * @param vertex    the vertex whose outgoing edges are to be returned
     * @return  a Collection view of the outgoing edges incident
     *     to vertex in this graph
     */
    public Collection<GraphEdge> getOutEdges(GraphNode vertex) {
        return getIncidentEdges(vertex);
    }

    /**
     * Returns the number of incoming edges incident to vertex.
     * Equivalent to getInEdges(vertex).size().
     * @param vertex    the vertex whose indegree is to be calculated
     * @return  the number of incoming edges incident to vertex
     */
    public int inDegree(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of outgoing edges incident to vertex.
     * Equivalent to getOutEdges(vertex).size().
     * @param vertex    the vertex whose outdegree is to be calculated
     * @return  the number of outgoing edges incident to vertex
     */
    public int outDegree(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of predecessors that vertex has in this graph.
     * Equivalent to vertex.getPredecessors().size().
     * @param vertex the vertex whose predecessor count is to be returned
     * @return  the number of predecessors that vertex has in this graph
     */
    public int getPredecessorCount(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the number of successors that vertex has in this graph.
     * Equivalent to vertex.getSuccessors().size().
     * @param vertex the vertex whose successor count is to be returned
     * @return  the number of successors that vertex has in this graph
     */
    public int getSuccessorCount(GraphNode vertex) {
        return degree(vertex);
    }

    /**
     * Returns the vertex at the other end of edge from vertex.
     * (That is, returns the vertex incident to edge which is not vertex.)
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return the vertex at the other end of edge from vertex
     */
    public GraphNode getOpposite(GraphNode vertex, GraphEdge edge) {
        Pair<GraphNode> p = getEndpoints(edge);
        if(p.getFirst().equals(vertex)) {
            return p.getSecond();
        }
        else {
            return p.getFirst();
        }
    }

    /**
     * Returns all edges that connects v1 to v2.
     * If this edge is not uniquely
     * defined (that is, if the graph contains more than one edge connecting
     * v1 to v2), any of these edges
     * may be returned.  findEdgeSet(v1, v2) may be
     * used to return all such edges.
     * Returns null if v1 is not connected to v2.
     * <br/>Returns an empty collection if either v1 or v2 are not present in this graph.
     *
     * <p><b>Note</b>: for purposes of this method, v1 is only considered to be connected to
     * v2 via a given <i>directed</i> edge d if
     * v1 == d.getSource() && v2 == d.getDest() evaluates to true.
     * (v1 and v2 are connected by an undirected edge u if
     * u is incident to both v1 and v2.)
     *
     * @param v1  first node being checked
     * @param v2  second node being checked
     * @return  a collection containing all edges that connect v1 to v2,
     *     or null if either vertex is not present
     * @see Hypergraph#findEdge(Object, Object)
     */
    public Collection<GraphEdge> findEdgeSet(GraphNode v1, GraphNode v2) {
        GraphEdge edge = findEdge(v1, v2);
        if(edge == null) {
            return null;
        }

        LinkedList<GraphEdge> ret = new LinkedList<>();
        ret.add(edge);
        return ret;

    }

    /**
     * Returns true if vertex is the source of edge.
     * Equivalent to getSource(edge).equals(vertex).
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return true iff vertex is the source of edge
     */
    public boolean isSource(GraphNode vertex, GraphEdge edge) {
        return getSource(edge).equals(vertex);
    }

    /**
     * Returns true if vertex is the destination of edge.
     * Equivalent to getDest(edge).equals(vertex).
     * @param vertex the vertex to be queried
     * @param edge the edge to be queried
     * @return true iff vertex is the destination of edge
     */
    public boolean isDest(GraphNode vertex, GraphEdge edge) {
        return getDest(edge).equals(vertex);
    }

    /**
     * Adds edge e to this graph such that it connects
     * vertex v1 to v2.
     * Equivalent to addEdge(e, new Pair<GraphNode>(v1, v2)).
     * If this graph does not contain v1, v2,
     * or both, implementations may choose to either silently add
     * the vertices to the graph or throw an IllegalArgumentException.
     * If edgeType is not legal for this graph, this method will
     * throw IllegalArgumentException.
     * See Hypergraph.addEdge() for a listing of possible reasons
     * for failure.
     * @param e the edge to be added
     * @param v1 the first vertex to be connected
     * @param v2 the second vertex to be connected
     * @param edgeType the type to be assigned to the edge
     * @return true if the add is successful, false otherwise
     * @see Hypergraph#addEdge(Object, Collection)
     * @see #addEdge(Object, Object, Object)
     */
    public boolean addEdge(GraphEdge e, GraphNode v1, GraphNode v2, EdgeType edgeType) {
        //NOTE: Only directed edges allowed

        if(edgeType == EdgeType.DIRECTED) {
            throw new IllegalArgumentException();
        }

        return addEdge(e, v1, v2);
    }

    /**
     * Adds edge to this graph.
     * Fails under the following circumstances:
     * <ul>
     * <li/>edge is already an element of the graph
     * <li/>either edge or vertices is null
     * <li/>vertices has the wrong number of vertices for the graph type
     * <li/>vertices are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * </ul>
     *
     * @param edge being added
     * @param vertices being checked
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if edge or vertices is null,
     *     or if a different vertex set in this graph is already connected by edge,
     *     or if vertices are not a legal vertex set for edge
     */
    @SuppressWarnings("unchecked")
    public boolean addEdge(GraphEdge edge, Collection<? extends GraphNode> vertices) {
        if(edge == null || vertices == null || vertices.size() != 2) {
            return false;
        }

        GraphNode[] vs = (GraphNode[])vertices.toArray();
        return addEdge(edge, vs[0], vs[1]);
    }

    /**
     * Adds edge to this graph with type edge_type.
     * Fails under the following circumstances:
     * <ul>
     * <li/>edge is already an element of the graph
     * <li/>either edge or vertices is null
     * <li/>vertices has the wrong number of vertices for the graph type
     * <li/>vertices are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * <li/>edge_type is not legal for this graph
     * </ul>
     *
     * @param edge the edge being added
     * @param vertices being checked
     * @param edgeType variation of edge
     * @return true if the add is successful, and false otherwise
     * @throws IllegalArgumentException if edge or vertices is null,
     *     or if a different vertex set in this graph is already connected by edge,
     *     or if vertices are not a legal vertex set for edge
     */
    @SuppressWarnings("unchecked")
    public boolean addEdge(GraphEdge edge, Collection<? extends GraphNode> vertices, EdgeType edgeType) {
        if(edge == null || vertices == null || vertices.size() != 2) {
            return false;
        }

        GraphNode[] vs = (GraphNode[])vertices.toArray();
        return addEdge(edge, vs[0], vs[1], edgeType);
    }

    /**
     * Returns a {@code Factory} that creates an instance of this graph type.
     *
     * @param <graphNodeT> the vertex type for the graph factory
     * @param <graphEdgeT> the edge type for the graph factory
     * @return the instance of the graph type
     */
    public static <graphNodeT,graphEdgeT> Factory<UndirectedGraph<graphNodeT,graphEdgeT>> getFactory() {
        return new Factory<UndirectedGraph<graphNodeT,graphEdgeT>> () {
            @SuppressWarnings("unchecked")
            public UndirectedGraph<graphNodeT,graphEdgeT> create() {
                return (UndirectedGraph<graphNodeT,graphEdgeT>) new ThreeTenGraph();
            }
        };
    }

    /**
     * Returns the edge type of edge in this graph.
     *
     * @param edge being checked
     * @return the EdgeType of edge, or null if edge has no defined type
     */
    public EdgeType getEdgeType(GraphEdge edge) {
        return EdgeType.UNDIRECTED;
    }

    /**
     * Returns the default edge type for this graph.
     *
     * @return the default edge type for this graph
     */
    public EdgeType getDefaultEdgeType() {
        return EdgeType.UNDIRECTED;
    }

    /**
     * Returns the number of vertices that are incident to edge.
     * For hyperedges, this can be any nonnegative integer; for edges this
     * must be 2 (or 1 if self-loops are permitted).
     *
     * <p>Equivalent to getIncidentVertices(edge).size().
     * @param edge the edge whose incident vertex count is to be returned
     * @return the number of vertices that are incident to edge.
     */
    public int getIncidentCount(GraphEdge edge) {
        return 2;
    }
}