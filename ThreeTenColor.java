import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.awt.Color;

import javax.swing.JPanel;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.LinkedList;

/**
 *  Simulation of our coloring algorithm.
 *
 */
class ThreeTenColor implements ThreeTenAlg {
    /**
     *  The graph the algorithm will run on.
     */
    Graph<GraphNode, GraphEdge> graph;

    /**
     *  The priority queue of nodes for the algorithm.
     */
    WeissPriorityQueue<GraphNode> queue;

    /**
     *  The stack of nodes for the algorithm.
     */
    LinkedList<GraphNode> stack;

    /**
     *  Whether or not the algorithm has been started.
     */
    private boolean started = false;

    /**
     *  Whether or not the algorithm is in the coloring stage or not.
     */
    private boolean coloring = false;

    /**
     *  The color when a node has "no color".
     */
    public static final Color COLOR_NONE_NODE = Color.WHITE;

    /**
     *  The color when an edge has "no color".
     */
    public static final Color COLOR_NONE_EDGE = Color.BLACK;

    /**
     *  The color when a node is inactive.
     */
    public static final Color COLOR_INACTIVE_NODE = Color.LIGHT_GRAY;

    /**
     *  The color when an edge is inactive.
     */
    public static final Color COLOR_INACTIVE_EDGE = Color.LIGHT_GRAY;

    /**
     *  The color when a node is highlighted.
     */
    public static final Color COLOR_HIGHLIGHT = new Color(255,204,51);

    /**
     *  The color when a node is in warning.
     */
    public static final Color COLOR_WARNING = new Color(255,51,51);


    /**
     *  The colors used to assign to nodes.
     */
    public static final Color[] COLORS =
        {Color.PINK, Color.GREEN, Color.CYAN, Color.ORANGE,
            Color.MAGENTA, Color.YELLOW, Color.DARK_GRAY, Color.BLUE};

    /**
     *  {@inheritDoc}
     */
    public EdgeType graphEdgeType() {
        return EdgeType.UNDIRECTED;
    }

    /**
     *  {@inheritDoc}
     */
    public void reset(Graph<GraphNode, GraphEdge> graph) {
        this.graph = graph;
        started = false;
        coloring = false;
    }

    /**
     *  {@inheritDoc}
     */
    public boolean isStarted() {
        return started;
    }

    /**
     *  {@inheritDoc}
     */
    public void start() {
        this.started = true;

        //create an empty stack
        stack = new LinkedList<>();

        //create an empty priority queue
        queue = new WeissPriorityQueue<>();

        for(GraphNode v : graph.getVertices()) {

            //Set the cost of each node to be its degree
            v.setCost(graph.degree(v));

            //Set each node to be active
            //This enables the display of cost for the node
            v.setActive();

            //add node into queue
            queue.add(v);
        }

        //highlight the current node with max priority
        highlightNextMax();

    }

    /**
     *  {@inheritDoc}
     */
    public void finish() {

        // Coloring completed. Set all edges back to "no color".
        for (GraphEdge e: graph.getEdges()){
            e.setColor(COLOR_NONE_EDGE);
        }

    }

    /**
     *  {@inheritDoc}
     */
    public void cleanUpLastStep() {
    }

    /**
     *  {@inheritDoc}
     */
    public boolean setupNextStep() {


        if (coloring && stack.size() == 0)
            return false;


        if (!coloring && graph.getVertexCount() == stack.size()){
            coloring = true;
        }

        return true;
    }

    /**
     *  {@inheritDoc}
     */
    public void doNextStep() {

        if (!coloring){
            //Stage 1: pushing nodes into stack one by one & update record

            // maxNode is the active node with the highest priority
            // Remove the maxNode from priority queue and push it into stack
            GraphNode maxNode = findMax();

            //Update the cost of all nodes that is a neighbor of the maxNode
            updateNeighborCost(maxNode);

            //Identify and highlight the next max node in the updated priority queue
            highlightNextMax();


        }
        else{
            //Stage 2: pop nodes from stack one by one and choose a color for each

            //Pop off stack top
            GraphNode node = stack.pop();

            //For the node popped off, pick a color that is different from all
            //neighbors who has got assigned a color so far
            Color newColor = chooseColor(node);

            //Inform all neighbors of this node the selected color
            updateColor(node, newColor);

        }

    }


    /**
     * Highlights the next
     * node in the priority Queue.
     */
    public void highlightNextMax(){


        queue.element().color = COLOR_HIGHLIGHT;

    }

    /**
     * Removes the next node with the highest priority in the queue
     * and pushes the node into a separate stack and setting the node
     * and its incident edges to INACTIVE.
     *
     * @return the max priority node that was removed
     */
    public GraphNode findMax(){
        if(queue.size() == 0) {
            return null;
        }

        GraphNode removed = queue.remove();

        stack.push(removed);

        removed.unsetActive();
        removed.color = COLOR_INACTIVE_NODE;

        for (GraphEdge currrentEdge : graph.getIncidentEdges(removed)) {
            currrentEdge.color = COLOR_INACTIVE_EDGE;
        }


        return removed;
    }

    /**
     * Update the cost (node neighbors) of the current
     * max node (node with the highest priority) with the newest cost.
     *
     * @param maxNode the highest priority node
     */
    public void updateNeighborCost(GraphNode maxNode){

        int cost = 0;
        for (GraphNode currentNode : graph.getNeighbors(maxNode)) {
            if (currentNode.isActive()) {
                cost++;
            }
        }
        maxNode.setCost(cost);

    }

    /**
     * Changes the color of a current node that isn't
     * the same as any of its neighbors and is in the lowest possible index
     * in the COLORS' arrays.
     *
     * @param node current node
     * @return the color chosen or COLOR_WARNING if no color is available
     */
    public Color chooseColor(GraphNode node){

        if(node == null) {
            return null;
        }

        List<Color> colors = new LinkedList<>();
        colors.addAll(List.of(COLORS));

        for (GraphNode current : graph.getNeighbors(node)) {
            colors.remove(current.color);
        }

        if(colors.isEmpty()) {
            return COLOR_WARNING;
        }

        for (int i = 0; i < COLORS.length; i++) {
            if (colors.get(0) == COLORS[i]) {
                node.setNbrColor(i);
                return COLORS[i];
            }
        }
        return null;
    }

    /**
     * Changes the color of the node being passed in the method by the color
     * being passed.
     *
     * @param node to update
     * @param newColor the color to set node to
     */
    public void updateColor(GraphNode node, Color newColor){

        if (newColor == null || node == null) {
            return;
        }

        node.color = newColor;

        for (GraphEdge curr : graph.getIncidentEdges(node)) {
            if (curr.getColor() == null || curr.color == Color.BLACK) {
                curr.color = newColor;
            }
        }

    }

}