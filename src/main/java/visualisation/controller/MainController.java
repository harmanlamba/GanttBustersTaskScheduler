package visualisation.controller;

import algorithm.AlgorithmBuilder;
import app.App;
import com.jfoenix.controls.JFXTreeTableView;
import fileio.IIO;
import graph.Graph;
import graph.GraphNode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.scene.chart.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableColumn;
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

    private final static String BEST_SCHEDULE_TIME_TEXT = "Best Schedule Time: ";
    private final static String BRANCHES_BOUNDED_TEXT = "Branches Bounded: ";
    private final static String BRANCHES_PRUNED_TEXT = "Branches Pruned: ";
    private final static String STATES_GENERATED_TEXT = "States Generated: ";

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

    //Public Control Fields from the FXML
    public HBox mainContainer;
    public VBox statsContainer;
    public Text algorithmStatus;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numberOfTasks;
    public Text numberOfProcessors;
    public Text numberOfThreads;
    public Text currentBestSchedule;
    public Text branchesBounded;
    public Text branchesPruned;
    public Text statesGenerated;

    public TabPane visualsContainer;
    public Tab graphTab;
    public Pane graphPane;
    public SwingNode swingNode;

    public Tab taskTab;
    public Pane ganttPane;

    public Tab resultTab;
    public JFXTreeTableView<?> scheduleResultsTable;
    public TreeTableColumn<?, ?> taskIDColumn;
    public TreeTableColumn<?, ?> startTimeColumn;
    public TreeTableColumn<?, ?> assignedProcessorColumn;

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
    public void updateGraph() {
        Map<String, GraphNode> update = _observableAlgorithm.getCurrentBestSolution();
        updateStatistics();
        //Run on another thread
        Platform.runLater(() -> {
            //update graph visualization using runnable
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    for (Node node : _graphStream) {
                        //TODO: Receive and update node states via GraphUpdater
                    }
                }
            });
        });
    }

    @Override
    public void stopTimer() {
        _observableTimer.stop();
        algorithmStatus.setText(ALGORITHM_STATUS_TEXT + ALGORITHM_STATUS_DONE_TEXT);
        updateGraph();
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

    private void updateStatistics() {
        branchesBounded.setText(BRANCHES_BOUNDED_TEXT + _observableAlgorithm.getBranchesBounded());
        branchesPruned.setText(BRANCHES_PRUNED_TEXT + _observableAlgorithm.getBranchesPruned());
        statesGenerated.setText(STATES_GENERATED_TEXT + _observableAlgorithm.getStatesGenerated());
    }

    @Override
    public void updateTimer(String time) {
        timeElapsedText.setText(TIME_ELAPSED_TEXT + time);
    }
}
