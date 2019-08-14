package algorithm.idastarbase;

import graph.Graph;
import graph.GraphEdge;
import graph.GraphNode;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

public class State {
    private Map<String, GraphNode> _assignedTasks;
    private Graph _graph;
    private Graph _remainingGraph;
    private Map<String, GraphNode> _freeTasks;     // Tasks that can be scheduled (meet dependencies)
    private int[] _processorMaxTime;        // Index i has the finishing time of the last task scheduled on processor i+1

    public State(Graph graph, int numProcTask) {
        _graph = graph;
        _remainingGraph = new Graph(_graph.get_vertexMap(), _graph.get_edgeList());

        _assignedTasks = new HashMap<>();
        _freeTasks = new HashMap<>();
        _processorMaxTime = new int[numProcTask];
    }

    public void sanitise(int depth, int numProc) {
        for (int i = _assignedTasks.size() - 1; i > depth; i--) {
            // Add nodes back to the remaining graph
            GraphNode node = new ArrayList<GraphNode>(_assignedTasks.values()).get(i);
            Set<DefaultWeightedEdge> edgeSet = _graph.getGraph().outgoingEdgesOf(node);
            _remainingGraph.getGraph().addVertex(node);

            //everything above is alg


            for (DefaultWeightedEdge currentEdge : edgeSet) {
                GraphNode from = (GraphNode) _graph.getGraph().getEdgeSource(currentEdge);

                GraphNode to = (GraphNode) _graph.getGraph().getEdgeTarget(currentEdge);
                DefaultWeightedEdge edge = (DefaultWeightedEdge) _remainingGraph.getGraph().addEdge(from, to);
                int weight = (int)_graph.getGraph().getEdgeWeight(currentEdge);
                _remainingGraph.getGraph().setEdgeWeight(edge, weight);
            }

            // Removes the task from assignedTasks
            _assignedTasks.remove(node.getId());
        }

        _processorMaxTime = new int[numProc];
        for (GraphNode task : _assignedTasks.values()) {
            if (task.getStartTime() + task.getWeight() > _processorMaxTime[task.getProcessor()]) {
                _processorMaxTime[task.getProcessor()] = task.getStartTime() + task.getWeight();
            }
        }
    }


    public void ScheduleTask(GraphNode task) {
        _freeTasks.remove(task.getId());
        _remainingGraph.getGraph().removeVertex(task);
        _assignedTasks.put(task.getId(), task);
        if(_processorMaxTime[task.getProcessor()] < task.getStartTime() + task.getWeight()) {
            _processorMaxTime[task.getProcessor()] = task.getStartTime() + task.getWeight();
        }
    }

    //TODO: check the cast

    /**
     * Update the list of free tasks by adding tasks not yet scheduled and their immediate predecessors
     * have already been scheduled, ie all dependencies are met.
     */
    private void updateFreeTasks() {
        for(GraphNode task : ((DirectedWeightedMultigraph<GraphNode, DefaultWeightedEdge>) _remainingGraph.getGraph()).vertexSet()) {
            // If a not-yet scheduled node has all of its dependencies met, it's a free node
            if(_remainingGraph.getGraph().inDegreeOf(task) == 0) {
                _freeTasks.put(task.getId(), task);
            }
        }
    }

    public int getNumberOfFreeTasks() {
        updateFreeTasks();
        return _freeTasks.size();
    }


    public int getCost() {
        return Collections.max(Arrays.asList(ArrayUtils.toObject(_processorMaxTime)));
    }

    public Map<String, GraphNode> getAssignedTasks() {
        return _assignedTasks;
    }

    public int getProcessorMaxTime(int processor) {
        return _processorMaxTime[processor];
    }

    public GraphNode getGraphNodeFromFreeTasks(int index) {
        return new ArrayList<GraphNode>(_freeTasks.values()).get(index);
    }
}
