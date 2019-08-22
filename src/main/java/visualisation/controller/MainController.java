package visualisation.controller;

import algorithm.AlgorithmBuilder;
import app.App;
import fileio.IIO;
import graph.Graph;
import graph.GraphNode;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
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
import visualisation.controller.table.MockGraphNode;
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

    //TODO: The strings may need to be changed into something that is less confusing
    private final static String NUMBER_OF_TASKS_TEXT = "Number of Tasks: ";
    private final static String ALGORITHM_STATUS_TEXT = "Status: ";
    private final static String ALGORITHM_STATUS_INPROGRESS_TEXT = "In progress";
    private final static String ALGORITHM_STATUS_DONE_TEXT = "Done";
    private final static String ALGORITHM_FILE_TEXT = "Running: ";
    private final static String ALGORITHM_TYPE_TEXT = "Algorithm Type: ";
    private final static String NUMBER_OF_PROCESSORS_TEXT = "Number of Processors: "; //this one
    private final static String NUMBER_OF_THREADS_TEXT = "Number of Threads: "; //this one
    private final static String BEST_SCHEDULE_COST_TEXT = "Best Schedule Cost: ";
    private final static String NUMBER_OF_ITERATIONS_TEXT = "Number of Iterations: ";
    private final static String BRANCHES_PRUNED_TEXT = "Branches Pruned: ";
    private final static String CURRENT_LOWER_BOUND_TEXT = "Current Lower Bound: ";
    private final static String TIME_ELAPSED_TEXT = "Time Elapsed: ";
    private final static String START_TIME_TEXT = "00:00:00";
    private final static String CURRENT_MEMORY_USAGE = "Memory Usage: ";
    private final static int KB_TO_MB_CONVERSION_RATE = 1000000;
    private final static String MB_TEXT = " MB";


    //Private Fields
    private IObservable _observableAlgorithm;
    private SingleGraph _graphStream;
    private IIO _io;
    private GraphManager _graphManager;
    private GraphUpdater _graphUpdater;
    private ProcessorColourHelper _processColourHelper;
    private ITimerObservable _observableTimer;
    private ObservableList<MockGraphNode> _tablePopulationList = FXCollections.observableArrayList();
    private SelectedTab _currentTab;
    private Map<String, GraphNode> _latestUpdateMap;


    //Public Control Fields from the FXML
    public HBox mainContainer;
    public VBox statsContainer;
    public VBox statusPane;
    public Text algorithmStatus;
    public Text fileNameText;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numberOfTasks;
    public Text numberOfProcessors;
    public Text numberOfThreads;
    public Text bestScheduleCost;
    public Text numberOfIterations;
    public Text branchesPruned;
    public Text currentLowerBound;
    public Text currentMemoryUsage;

    public TabPane visualsContainer;
    public Tab graphTab;
    public Pane graphPane;
    public SwingNode swingNode;

    public Tab taskTab;
    public Pane ganttPane;
    private GanttChart<Number, String> ganttChart;
    public Button spriteButton;

    public Tab resultTab;
    public TableView<MockGraphNode> scheduleResultsTable;
    public TableColumn<MockGraphNode, String> taskIDColumn;
    public TableColumn<MockGraphNode, Integer> startTimeColumn;
    public TableColumn<MockGraphNode, Integer> endTimeColumn;
    public TableColumn<MockGraphNode, Integer> assignedProcessorColumn;

    public MainController(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _io = App._mainIO;

        //GUI
        _graphManager = new GraphManager(_io.getNodeMap(),_io.getEdgeList());
        _processColourHelper = new ProcessorColourHelper(_io.getNumberOfProcessorsForTask());

        //TODO: None of the code below this can be in the initialize method because this occurs before the screen renders.
        // This means the algorithm/timer starts and sometimes stops before user can even see this. Please yeet this
        // somehow to make this not an issue
        //Algorithm
        _observableAlgorithm = AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm();
        _observableAlgorithm.add(this);
        _observableTimer = AlgorithmTimer.getAlgorithmTimer();
        _observableTimer.add(this);

        initializeTabSelectionModel();
        initializeViews();
    }

    private void initializeViews() {
        initializeStatistics();
        initializeGraph();
        initializeGantt();
        initializeTable();
    }

    private void initializeTabSelectionModel() {
        _currentTab = SelectedTab.GRAPH;
        visualsContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                _currentTab = SelectedTab.values()[visualsContainer.getSelectionModel().getSelectedIndex()];
                if (oldValue != newValue) {
                    updateScheduleInformation(_latestUpdateMap);
                }
            }
        });
    }

    @Override
    public void updateScheduleInformation(Map<String, GraphNode> update) {
        _latestUpdateMap = update;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //update graph visualization using runnable
                List<GraphNode> test = new ArrayList<>(update.values());

                switch (_currentTab) {
                    case TABLE:
                        updateTable(test); //TODO: Platform Run Later need to figure out why we get ConcurrentModificationException
                        break;
                    case GANTT:
                        for (Node node : _graphStream) {
                            updateGantt(test); //TODO: TEMP
                        }
                        break;
                    default: //graph
                        _graphManager.updateGraphStream(test);
                        _graphStream = _graphManager.getGraph();
                        _graphUpdater.updateGraph(_graphStream);

                }
            }
        });
    }

    @Override
    public void algorithmStopped(int bestCost) {
        _observableTimer.stop();
        statusPane.setStyle("-fx-background-color: #86e39c; -fx-border-color: #86e39c;");
        algorithmStatus.setText(ALGORITHM_STATUS_TEXT + ALGORITHM_STATUS_DONE_TEXT);
        bestScheduleCost.setText(BEST_SCHEDULE_COST_TEXT + bestCost);
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); //Set styling renderer
        _graphStream = _graphManager.getGraph();
        _graphUpdater = new GraphUpdater(_graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD, _processColourHelper);
        _graphUpdater.enableAutoLayout();

        //Create graphstream view panel
        ViewPanel viewPanel = _graphUpdater.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(700,500)); //Window size
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.white);
        //_graphUpdater.setMouseManager(viewPanel); //Disable mouse drag of nodes //TODO: MAKE JIGGLY A BUTTON

        //Assign graph using swing node
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(viewPanel);
        });
        swingNode.setLayoutX(5);
        swingNode.setLayoutY(5);
    }

    private void initializeGantt() {
        //Gantt chart initialize
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        ganttChart = new GanttChart<>(xAxis, yAxis);
        ganttPane.getChildren().add(ganttChart);
        ganttChart.getStylesheets().add(getClass().getResource("/view/stylesheet.css").toExternalForm()); //style

        //ganttchart fx properties
        ganttChart.setPrefWidth(640);
        ganttChart.setPrefHeight(450);
        ganttChart.setLayoutX(20);
        ganttChart.setLayoutY(40);
        ganttChart.setLegendVisible(false);
        ganttChart.setBlockHeight(40);
        ganttChart.setAlternativeRowFillVisible(false);
        ganttChart.setHorizontalGridLinesVisible(false);
        ganttChart.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        //x axis (xValue=Starttime, lengthMs=Worktime)
        xAxis.setLabel("Start time (s)");
        xAxis.setTickUnit(50);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(_observableAlgorithm.getMaximumPossibleCost());
        xAxis.setStyle("-fx-font-family: 'Space Mono', monospace;");

        //y axis (processor count)
        List<String> processors = new ArrayList<>();
        for (int i = 0; i < _io.getNumberOfProcessorsForTask(); i++) {
            processors.add(Integer.toString(i));
        }
        yAxis.setLabel("");
        yAxis.setTickLabelGap(20);
        yAxis.setCategories(FXCollections.observableList(processors));
        yAxis.setStyle("-fx-font-family: 'Space Mono', monospace;");
    }

    private void initializeStatistics() {
        algorithmStatus.setText(ALGORITHM_STATUS_TEXT + ALGORITHM_STATUS_INPROGRESS_TEXT);
        fileNameText.setText(ALGORITHM_FILE_TEXT + _io.getFileName());
        algorithmTypeText.setText(ALGORITHM_TYPE_TEXT + AlgorithmBuilder.getAlgorithmBuilder().getAlgorithmType().getName());
        numberOfTasks.setText(NUMBER_OF_TASKS_TEXT + _io.getNodeMap().size());
        numberOfProcessors.setText(NUMBER_OF_PROCESSORS_TEXT + _io.getNumberOfProcessorsForTask());
        numberOfThreads.setText(NUMBER_OF_THREADS_TEXT + _io.getNumberOfProcessorsForParallelAlgorithm());
        timeElapsedText.setText(TIME_ELAPSED_TEXT + START_TIME_TEXT);
    }

    private void initializeTable() {
        taskIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        Comparator<String> stringToIntComparator = (o1, o2) -> Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
        taskIDColumn.setComparator(stringToIntComparator);
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        assignedProcessorColumn.setCellValueFactory((new PropertyValueFactory<>("processor")));
        scheduleResultsTable.setItems(_tablePopulationList);
     }

    private void updateTable(List<GraphNode> update) {
        _tablePopulationList.clear();
        Map<String,String> colorMap =  new HashMap<>();
        //Repopulate with the new GraphNode Details
        for(GraphNode node : update){
            if(node.getStartTime() != -1 && node.getProcessor() != -1){
                MockGraphNode tempMockGraphNode = new MockGraphNode(node.getId(),node.getWeight(),node.getProcessor(),node.getStartTime());
                colorMap.put(tempMockGraphNode.getId(),_processColourHelper.getProcessorColour(tempMockGraphNode.getProcessor()));
                taskIDColumn.setCellFactory(cell -> new TableCell<MockGraphNode,String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }else{
                            setText(item);
                            String color = colorMap.get(item);
                            setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0.1 0 5;");
                        }
                    }
                });
                _tablePopulationList.add(new MockGraphNode(node.getId(),node.getWeight(),node.getProcessor(),node.getStartTime()));
            }
        }
    }

    public void updateGantt(List<GraphNode> test) {
        XYChart.Series series1 = new XYChart.Series();
        ganttChart.getData().clear();

        List<String> processors = new ArrayList<>();
        for (int i = 0; i < _io.getNumberOfProcessorsForTask(); i++) {
            processors.add(Integer.toString(i));
        }

        for (String processor : processors) {
            for (GraphNode graphNode : test) {
                String processorColour = _processColourHelper.getProcessorColour(graphNode.getProcessor());
                if (Integer.toString(graphNode.getProcessor()).equals(processor)) {
                    series1.getData().add(new XYChart.Data(graphNode.getStartTime(), processor, new GanttChart.Properties(graphNode.getWeight(), "-fx-background-color:" + processorColour)));
                }
            }
        }
        ganttChart.getData().addAll(series1);
    }

    @Override
    public void updateIterationInformation(int prunedBranches, int iterations, int lowerBound) {
        branchesPruned.setText(BRANCHES_PRUNED_TEXT + prunedBranches);
        numberOfIterations.setText(NUMBER_OF_ITERATIONS_TEXT + iterations);
        currentLowerBound.setText(CURRENT_LOWER_BOUND_TEXT + ((lowerBound == -1) ? "N/A" : lowerBound));
        // Get memory usage in MBs
        long memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / KB_TO_MB_CONVERSION_RATE;
        currentMemoryUsage.setText(CURRENT_MEMORY_USAGE + memoryUsage + MB_TEXT);
    }

    @Override
    public void updateTimer(String time) {
        timeElapsedText.setText(TIME_ELAPSED_TEXT + time);
    }
}