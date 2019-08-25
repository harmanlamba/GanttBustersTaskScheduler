package visualisation.controller;

import algorithm.AlgorithmBuilder;
import app.App;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import fileio.IIO;
import graph.GraphNode;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import visualisation.controller.table.LegendCell;
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

    //Statistics constants
    private final static String NUMBER_OF_TASKS_TEXT = "Tasks: ";
    private final static String ALGORITHM_STATUS_DONE_TEXT = "Done";
    private final static String ALGORITHM_FILE_TEXT = "Running: ";
    private final static String NUMBER_OF_PROCESSORS_TEXT = "Processors: "; //this one
    private final static String NUMBER_OF_THREADS_TEXT = "Threads: "; //this one
    private final static String CURRENT_SCHEDULE_COST_TEXT = "Current Schedule Cost: ";
    private final static String NUMBER_OF_ITERATIONS_TEXT = "Number of Iterations: ";
    private final static String TIME_ELAPSED_TEXT = "Time Elapsed: ";
    private final static String CURRENT_MEMORY_USAGE = "Memory Usage: ";
    private final static int KB_TO_MB_CONVERSION_RATE = 1000000;
    private final static String MB_TEXT = " MB";

    //Private Fields
    private IObservable _observableAlgorithm;
    private IIO _io;
    private ITimerObservable _observableTimer;
    private ObservableList<MockGraphNode> _tablePopulationList = FXCollections.observableArrayList();

    private SingleGraph _graphStream;
    private GraphManager _graphManager;
    private GraphUpdater _graphUpdater;
    private GanttManager _ganttManager;
    private ProcessorColourHelper _processColourHelper;
    private SelectedTab _currentTab;

    private Map<String, GraphNode> _latestUpdateMap;
    private Map<Integer, Map<String, GraphNode>> _updateThreadMap = new HashMap<>();
    private Map<Integer, Integer> _updateStatisticsMap = new HashMap<>();

    //Public Control Fields from the FXML
    public VBox statsContainer;
    public VBox statusPane;
    public Text algorithmStatus;
    public Text fileNameText;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numberOfTasks;
    public Text numberOfProcessors;
    public Text numberOfThreads;
    public Text currentScheduleCost;
    public Text numberOfIterations;
    public Text currentMemoryUsage;
    public Text stats;

    //Graph pane
    private ViewPanel _viewPanel;
    public TabPane visualsContainer;
    public Pane graphPane;
    public SwingNode swingNode;

    //Gantt pane
    public Pane ganttPane;
    private GanttChart<Number, String> ganttChart;
    public Button spriteButton;
    public Button floppyButton;

    //Table pane
    public TableView<MockGraphNode> scheduleResultsTable;
    public TableColumn<MockGraphNode, String> taskIDColumn;
    public TableColumn<MockGraphNode, Integer> startTimeColumn;
    public TableColumn<MockGraphNode, Integer> endTimeColumn;
    public TableColumn<MockGraphNode, Integer> assignedProcessorColumn;

    //Legend pane
    public JFXListView<String> legendListView;
    public JFXComboBox<String> comboBox;

    /**
     * initialize - Initiate algorithm and GUI components
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _io = App._mainIO;

        //GUI
        _graphManager = new GraphManager(_io.getNodeMap(),_io.getEdgeList());
        _processColourHelper = new ProcessorColourHelper(_io.getNumberOfProcessorsForTask());

        //Algorithm
        _observableAlgorithm = AlgorithmBuilder.getAlgorithmBuilder().getAlgorithm();
        _observableAlgorithm.add(this);
        _observableTimer = AlgorithmTimer.getAlgorithmTimer();
        _observableTimer.add(this);

        initializeTabSelectionModel();
        initializeViews();
    }

    /**
     * initializeViews - Initialize all views for legend, graph, gantt, table and statistics
     */
    private void initializeViews() {
        initializeStatistics();
        initializeLegend();
        initializeGraph();
        initializeGantt();
        initializeTable();
    }

    /**
     * initializeTabSelectionModel - create tabs according to thread and thread's current solution
     */
    private void initializeTabSelectionModel() {
        _currentTab = SelectedTab.GRAPH;

        //Select initial tab with initial data and threads
        visualsContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                _currentTab = SelectedTab.values()[visualsContainer.getSelectionModel().getSelectedIndex()];
                if (oldValue != newValue) {
                    updateScheduleInformation(1, _latestUpdateMap);
                }
            }
        });
    }

    /**
     * algorithmStopped - once algorithm has been solved call upon timer to stop and status to complete -> create solution BOX
     * @param thread - thread in which the current display is on
     * @param bestCost - get the best cost statistic which updates on stats
     */
    @Override
    public void algorithmStopped(int thread, int bestCost) {
        _observableTimer.stop();
        _updateThreadMap.put(_io.getNumberOfProcessorsForParallelAlgorithm(), _updateThreadMap.get(thread));
        _updateStatisticsMap.put(_io.getNumberOfProcessorsForParallelAlgorithm(), _updateStatisticsMap.get(thread));

        //Combo box create solution selection
        ObservableList<String> comboBoxList = comboBox.getItems();
        Platform.runLater(() -> {
            comboBoxList.add("Solution Stats");
            comboBox.getSelectionModel().selectLast();

        });

        //Set status and stats
        statusPane.setStyle("-fx-background-color: #60d67f; -fx-border-color: #60d67f;");
        algorithmStatus.setText(ALGORITHM_STATUS_DONE_TEXT);
        currentScheduleCost.setText(CURRENT_SCHEDULE_COST_TEXT + bestCost);
    }

    /**
     * initializeGraph - create graph component which is placed into swing util
     */
    private void initializeGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); //Set styling renderer
        _graphStream = _graphManager.getGraph();
        _graphUpdater = new GraphUpdater(_graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD, _processColourHelper);
        _graphUpdater.enableAutoLayout();

        //Create graphstream view panel
        _viewPanel = _graphUpdater.addDefaultView(false);
        _viewPanel.setMinimumSize(new Dimension(660,530)); //Window size
        _viewPanel.setOpaque(false);
        _viewPanel.setBackground(Color.getHSBColor(0, 0, 25));
        _graphUpdater.setMouseManager(_viewPanel);

        //Assign graph using swing node
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(_viewPanel);
        });
        swingNode.setLayoutX(10);
        swingNode.setLayoutY(10);

        //Assign button icons
        Image spriteImage = new Image(getClass().getResourceAsStream("/images/sprite.png"));
        spriteButton.setGraphic(new ImageView(spriteImage));
        spriteButton.getStyleClass().add("button-active");
        Image floppyImage = new Image(getClass().getResourceAsStream("/images/floppy.png"));
        floppyButton.setGraphic(new ImageView(floppyImage));
        floppyButton.getStyleClass().add("button-active");
    }

    /**
     * initializeGantt - create gantt component and its xAxis + YAxis properties
     */
    private void initializeGantt() {
        _ganttManager = new GanttManager(ganttChart, ganttPane, _io.getNumberOfProcessorsForTask(), _observableAlgorithm.getMaximumPossibleCost());
        ganttChart = _ganttManager.getGanttChart();
    }

    /**
     * initializeLegend - create legend list component on stats given colours and num processes
     */
    private void initializeLegend(){
        //Get number of processors
        ObservableList<String> processorList = FXCollections.observableArrayList();
        for(int i=0 ; i < _io.getNumberOfProcessorsForTask(); i++){
            processorList.add(String.valueOf(i));
        }

        //Set processor list
        legendListView.setItems(processorList);
        legendListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new LegendCell(_processColourHelper);
            }
        });
    }

    /**
     * initializeStatistics - initialize statistics values + initial CL values set by user
     */
    private void initializeStatistics() {
        initializeCheckParallelisationForStats();
        fileNameText.setText(ALGORITHM_FILE_TEXT + _io.getFileName());
        algorithmTypeText.setText(AlgorithmBuilder.getAlgorithmBuilder().getAlgorithmType().getName());
        numberOfTasks.setText(NUMBER_OF_TASKS_TEXT + _io.getNodeMap().size());
        numberOfProcessors.setText(NUMBER_OF_PROCESSORS_TEXT + _io.getNumberOfProcessorsForTask());
        numberOfThreads.setText(NUMBER_OF_THREADS_TEXT + _io.getNumberOfProcessorsForParallelAlgorithm());
    }

    /**
     * initializeCheckParallelisationForStats - create parallelization for stats if parallelization is enabled
     */
    private void initializeCheckParallelisationForStats(){
        if (_io.getNumberOfProcessorsForParallelAlgorithm() > 1){
            //Remove the stats text as a child of the VBox
            statsContainer.getChildren().remove(stats);
            initializeComboBox();
        } else {
            //Remove ComboBox as a Child for the VBox
            statsContainer.getChildren().remove(comboBox);
        }
    }

    /**
     * initializeComboBox - create combo boxes depending on if parallelization is enabled
     */
    private void initializeComboBox(){
        ObservableList<String> parallelProcessorList = FXCollections.observableArrayList();
        for(int i = 0; i < _io.getNumberOfProcessorsForParallelAlgorithm(); i++){
            parallelProcessorList.add("Stats Thread " + i);
        }
        comboBox.setItems(parallelProcessorList);
        comboBox.getSelectionModel().selectFirst();

        //On change combobox action
        comboBox.setOnAction(e -> {
            int threadNumber = comboBox.getSelectionModel().getSelectedIndex();
            if (_updateThreadMap.get(threadNumber) != null){ //Update has happened in thread
                graphPane.setVisible(true);
                scheduleResultsTable.setVisible(true);
                ganttPane.setVisible(true);
                updateScheduleInformation(threadNumber, _updateThreadMap.get(threadNumber));
                updateIterationInformation(threadNumber, _updateStatisticsMap.get(threadNumber));
            } else { //No update has happened
                graphPane.setVisible(false);
                scheduleResultsTable.setVisible(false);
                ganttPane.setVisible(false);
            }

        });
    }

    /**
     * initializeTable - create table component with appropriate column ids
     */
    private void initializeTable() {
        Comparator<String> stringToIntComparator = Comparator.comparingInt(Integer::parseInt);

        //Update appropriate properties of table values
        taskIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskIDColumn.setComparator(stringToIntComparator);
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        assignedProcessorColumn.setCellValueFactory((new PropertyValueFactory<>("processor")));

        scheduleResultsTable.setItems(_tablePopulationList);
    }

    /**
     * updateScheduleInformation - for every update called from observer, input current thread user is in + the update of information
     * including the nodes and edges in which processor allocated + start time allocated.
     * @param threadNumber - current thread user is on
     * @param update - map of data to update all components on
     */
    @Override
    public void updateScheduleInformation(int threadNumber, Map<String, GraphNode> update) {
        _latestUpdateMap = update;
        _updateThreadMap.put(threadNumber, update);
        int selectedThread = comboBox.getSelectionModel().getSelectedIndex();

        if (selectedThread == 0 || selectedThread == -1 || selectedThread == threadNumber) { //Update depending on combo box values
            Platform.runLater(() -> {
                //update graph visualization using runnable
                try {
                    List<GraphNode> test = new ArrayList<>(update.values());
                    if(selectedThread == _io.getNumberOfProcessorsForParallelAlgorithm()){
                        updateTable(test);
                        for (Node node : _graphStream) {
                            updateGantt(test);
                        }
                        _graphManager.updateGraphStream(test);
                        _graphStream = _graphManager.getGraph();
                        _graphUpdater.updateGraph(_graphStream);
                    }else{
                        switch (_currentTab) {
                            case TABLE:
                                updateTable(test);
                                break;
                            case GANTT:
                                for (Node node : _graphStream) {
                                    updateGantt(test);
                                }
                                break;
                            default: //graph
                                _graphManager.updateGraphStream(test);
                                _graphStream = _graphManager.getGraph();
                                _graphUpdater.updateGraph(_graphStream);
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("Thread is too fast!"); //null pointer for thread race -> won't break algorithm
                }
            });
        }
    }

    /**
     * updateTable - update table component give update list data from observable
     * @param update - list of nodes to update the table according to start time + processor
     */
    private void updateTable(List<GraphNode> update) {
        _tablePopulationList.clear();
        Map<String,String> colorMap =  new HashMap<>();

        //Repopulate with the new GraphNode Details
        for(GraphNode node : update){

            //If assigned nodes are not "-1" (unassigned)
            if(node.getStartTime() != -1 && node.getProcessor() != -1){
                MockGraphNode tempMockGraphNode = new MockGraphNode(node.getId(),node.getWeight(),node.getProcessor(),node.getStartTime());
                colorMap.put(tempMockGraphNode.getId(),_processColourHelper.getProcessorColour(tempMockGraphNode.getProcessor()));

                //Get specific cell of id and set border colour to processor colour
                taskIDColumn.setCellFactory(cell -> new TableCell<MockGraphNode,String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }else{
                            setText(item);
                            String color = colorMap.get(item);
                            setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 0 9;");
                        }
                    }
                });
                _tablePopulationList.add(new MockGraphNode(node.getId(),node.getWeight(),node.getProcessor(),node.getStartTime()));
            }
        }
    }

    /**
     * updateGantt - updates gantt chart graph when updated by observable
     * @param graphNodeList - list of nodes to update processor and start time
     */
    public void updateGantt(List<GraphNode> graphNodeList) {
        ganttChart.getData().clear();

        //Get list of processors
        List<String> processors = new ArrayList<>();
        for (int i = 0; i < _io.getNumberOfProcessorsForTask(); i++) {
            processors.add(Integer.toString(i));
        }

        //Assign position and colour for processor list
        for (String processor : processors) {
            for (GraphNode graphNode : graphNodeList) {
                String processorColour = _processColourHelper.getProcessorColour(graphNode.getProcessor());
                ganttChart.getData().add(_ganttManager.updateGanttChart(processor, processorColour, graphNode));
            }
        }
    }

    /**
     * updateIterationInformation - for each iteration of the algorithm, update statistics given from BBA* implementation
     * @param threadNumber
     */
    @Override
    public void updateIterationInformation(int threadNumber, int upperBound) {
        _updateStatisticsMap.put(threadNumber, upperBound);
        int selectedThread = comboBox.getSelectionModel().getSelectedIndex();

        if (selectedThread == 0 || selectedThread == -1 || selectedThread == threadNumber) { //Update depending on combo box values
            // Get memory usage in MBs
            currentScheduleCost.setText(CURRENT_SCHEDULE_COST_TEXT + ((upperBound == -1) ? "-" : upperBound));
            long memoryUsage = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / KB_TO_MB_CONVERSION_RATE;
            currentMemoryUsage.setText(CURRENT_MEMORY_USAGE + memoryUsage + MB_TEXT);
        }
    }

    /**
     * updateTimer - elapsed time update for each millisecond
     * @param time - new time in string to set timeElapsedText component
     */
    @Override
    public void updateTimer(String time) {
        timeElapsedText.setText(TIME_ELAPSED_TEXT + time);
    }

    /**
     * toggleSprite - toggle sprite information being displayed on graph
     * @param event - on click event from spriteButton
     */
    @FXML
    public void toggleSprite(ActionEvent event) {
        //If on -> set opacity less
        if (_graphUpdater.getSpriteFlag()) {
            spriteButton.getStyleClass().add("button-active");
        } else {
            spriteButton.getStyleClass().remove("button-active");
        }
        _graphUpdater.toggleSprites(_graphStream);
    }

    /**
     * toggleFloppy - toggle floppy of graph information being displayed on graph
     * @param event - on click even from floppyButton
     */
    @FXML
    public void toggleFloppy(ActionEvent event) {
        //If on -> set opacity less
        if (_graphUpdater.getFloppyFlag()) {
            floppyButton.getStyleClass().add("button-active");
        } else {
            floppyButton.getStyleClass().remove("button-active");
        }
        _graphUpdater.toggleMouseManager(_viewPanel);
    }
}