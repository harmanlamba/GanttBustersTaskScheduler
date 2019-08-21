package visualisation.controller;

import algorithm.AlgorithmBuilder;
import app.App;
import fileio.IIO;
import graph.Graph;
import graph.GraphNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import visualisation.controller.timer.AlgorithmTimer;
import visualisation.controller.timer.ITimerObservable;
import visualisation.controller.timer.ITimerObserver;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.*;
import java.util.List;

public class MainController implements IObserver, ITimerObserver, Initializable {

    private final static String ALGORITHM_STATUS_TEXT = "Status: ";
    private final static String ALGORITHM_STATUS_INPROGRESS_TEXT = "In progress";
    private final static String ALGORITHM_STATUS_DONE_TEXT = "Done";

    private final static String ALGORITHM_TYPE_TEXT = "Algorithm Type: ";

    //TODO: The string needs to be changed into something that is less confusing
    private final static String NUMBER_OF_TASKS_TEXT = "Number of Tasks: ";
    private final static String NUMBER_OF_PROCESSORS_TEXT = "Number of Processors: ";
    private final static String NUMBER_OF_THREADS_TEXT = "Number of Threads: ";

    private final static String BEST_SCHEDULE_COST_TEXT = "Best Schedule Cost: ";
    private final static String NUMBER_OF_ITERATIONS_TEXT = "Number of Iterations: ";
    private final static String BRANCHES_PRUNED_TEXT = "Branches Pruned: ";
    private final static String CURRENT_LOWER_BOUND_TEXT = "Current Lower Bound: ";

    private final static String TIME_ELAPSED_TEXT = "Time Elapsed: ";
    private final static String START_TIME_TEXT = "00:00:00";



    //Private Fields
    private IObservable _observableAlgorithm;
    private Graph _algorithmGraph;
    private SingleGraph _graphStream;
    private IIO _io;
    private GraphManager _graphManager;
    private GraphUpdater _graphUpdater;
    private Map<String, GraphNode> _algorithmResultMap;
    private ITimerObservable _observableTimer;
    private ObservableList<GraphNode> _tablePopulationList = FXCollections.observableArrayList();

    //Public Control Fields from the FXML
    public HBox mainContainer;
    public VBox statsContainer;
    public Text algorithmStatus;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numberOfTasks;
    public Text numberOfProcessors;
    public Text numberOfThreads;
    public Text bestScheduleCost;
    public Text numberOfIterations;
    public Text branchesPruned;
    public Text currentLowerBound;

    public TabPane visualsContainer;
    public Tab graphTab;
    public Pane graphPane;
    public SwingNode swingNode;

    public Tab taskTab;
    public Pane ganttPane;

    public Tab resultTab;
    public TableView<GraphNode> scheduleResultsTable;
    public TableColumn<GraphNode, String> taskIDColumn;
    public TableColumn<GraphNode, Integer> startTimeColumn;
    public TableColumn<GraphNode, Integer> endTimeColumn;
    public TableColumn<GraphNode, Integer> assignedProcessorColumn;

    public MainController(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _io = App._mainIO;

        //GUI
        _graphManager = new GraphManager(_io.getNodeMap(),_io.getEdgeList());
        initializeGraph();
        initializeGantt();
        initializeStatistics();
        initializeTable();

        //TODO: None of the code below this can be in the initialize method because this occurs before the screen renders.
        // This means the algorithm/timer starts and sometimes stops before user can even see this. Please yeet this
        // somehow to make this not an issue
        //Algorithm
        _observableAlgorithm = AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm();
        _observableAlgorithm.add(this);
        _observableTimer = AlgorithmTimer.getAlgorithmTimer();
        _observableTimer.add(this);
    }

    @Override
    public void updateScheduleInformation(Map<String, GraphNode> update) {
        updateTable(update); //TODO: Platform Run Later need to figure out why we get ConcurrentModificationException

        //Run on another thread
        Platform.runLater(() -> {
            //update graph visualization using runnable
            Platform.runLater(() -> {
                for (Node node : _graphStream) {
                    //TODO: Receive and update node states via GraphUpdater
                }
            });
        });
    }

    @Override
    public void algorithmStopped(int bestCost) {
        _observableTimer.stop();
        algorithmStatus.setText(ALGORITHM_STATUS_TEXT + ALGORITHM_STATUS_DONE_TEXT);
        bestScheduleCost.setText(BEST_SCHEDULE_COST_TEXT + bestCost);
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); //Set styling renderer
        _graphStream = _graphManager.getGraph();
        _graphUpdater = new GraphUpdater(_graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        _graphUpdater.enableAutoLayout();

        //Create graphstream view panel
        ViewPanel viewPanel = _graphUpdater.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(700,500)); //Window size
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.white);
        _graphUpdater.setProcessorColours(_io.getNumberOfProcessorsForTask());
        //_graphUpdater.setMouseManager(viewPanel); //Disable mouse drag of nodes

        //Assign graph using swing node
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(viewPanel);
        });
        swingNode.setLayoutX(5);
        swingNode.setLayoutY(5);
    }

    private void initializeGantt() {
        _algorithmResultMap = _io.getAlgorithmResultMap();

        //Gantt chart initialize
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final GanttChart<Number, String> ganttChart = new GanttChart<>(xAxis, yAxis);
        ganttPane.getChildren().add(ganttChart);
        ganttChart.getStylesheets().add(getClass().getResource("/view/stylesheet.css").toExternalForm()); //style

        //ganttchart fx properties
        ganttChart.setPrefWidth(640);
        ganttChart.setLayoutX(20);
        ganttChart.setLayoutY(40);
        ganttChart.setLegendVisible(false);
        ganttChart.setBlockHeight(60);

        //y axis (processor count)
        List<String> machines = new ArrayList<>();
        for (int i = 0; i < _io.getNumberOfProcessorsForTask(); i++) {
            machines.add("Processor " + Integer.toString(i));
        }
        yAxis.setLabel("");
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.observableList(machines));

        //x axis (xValue=Starttime, lengthMs=Worktime)
        xAxis.setLabel("Start time (s)");
        xAxis.setMinorTickCount(10);
        for (String processor : machines) {
            XYChart.Series series1 = new XYChart.Series();
            series1.getData().add(new XYChart.Data(0, processor, new GanttChart.ExtraData(1, "status-red")));
            series1.getData().add(new XYChart.Data(1, processor, new GanttChart.ExtraData(2, "status-red")));
            series1.getData().add(new XYChart.Data(3, processor, new GanttChart.ExtraData(3, "status-red")));
            series1.getData().add(new XYChart.Data(6, processor, new GanttChart.ExtraData(4, "status-red")));
            ganttChart.getData().addAll(series1);
        }
    }

    private void initializeStatistics() {
        algorithmStatus.setText(ALGORITHM_STATUS_TEXT + ALGORITHM_STATUS_INPROGRESS_TEXT);
        algorithmTypeText.setText(ALGORITHM_TYPE_TEXT + AlgorithmBuilder.getAlgorithmBuilder().getAlgorithmType().getName());
        numberOfTasks.setText(NUMBER_OF_TASKS_TEXT + _io.getNodeMap().size());
        numberOfProcessors.setText(NUMBER_OF_PROCESSORS_TEXT + _io.getNumberOfProcessorsForTask());
        numberOfThreads.setText(NUMBER_OF_THREADS_TEXT + _io.getNumberOfProcessorsForParallelAlgorithm());
        timeElapsedText.setText(TIME_ELAPSED_TEXT + START_TIME_TEXT);
    }

    private void initializeTable() {
        taskIDColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("_startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("_endTime"));
        assignedProcessorColumn.setCellValueFactory((new PropertyValueFactory<>("_processor")));
        scheduleResultsTable.setItems(_tablePopulationList);
    }

    private void updateTable(Map<String, GraphNode> update) {
        _tablePopulationList.clear();
        //Repopulate with the new GraphNode Details
        for(Map.Entry<String,GraphNode> node : update.entrySet()){
            //Setting the end-time for each GraphNode
            node.getValue().setEndTime(node.getValue().getStartTime() + node.getValue().getWeight());
            _tablePopulationList.add(node.getValue());
        }
    }

    @Override
    public void updateIterationInformation(int prunedBranches, int iterations, int lowerBound) {
        branchesPruned.setText(BRANCHES_PRUNED_TEXT + prunedBranches);
        numberOfIterations.setText(NUMBER_OF_ITERATIONS_TEXT + iterations);
        currentLowerBound.setText(CURRENT_LOWER_BOUND_TEXT + ((lowerBound == -1) ? "N/A" : lowerBound));
    }

    @Override
    public void updateTimer(String time) {
        timeElapsedText.setText(TIME_ELAPSED_TEXT + time);
    }
}