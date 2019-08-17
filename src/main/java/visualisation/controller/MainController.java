package visualisation.controller;

import algorithm.Algorithm;
import algorithm.AlgorithmBuilder;
import com.jfoenix.controls.JFXTreeTableView;
import fileio.IIO;
import graph.Graph;
import graph.GraphNode;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
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

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements IObserver, Initializable {

    //Private Fields
    private IObservable _observableAlgorithm;
    private Graph _algorithmGraph;
    private SingleGraph _graphStream;
    private IIO _io;
    private GraphManager _graphManager;
    private GraphUpdater _graphUpdater;
    private Map<String, GraphNode> _algorithmResultMap;
    private TimerHelper _timer;

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
    public LineChart<?, ?> ganttChart;

    public Tab resultTab;
    public JFXTreeTableView<?> scheduleResultsTable;
    public TreeTableColumn<?, ?> taskIDColumn;
    public TreeTableColumn<?, ?> startTimeColumn;
    public TreeTableColumn<?, ?> assignedProcessorColumn;

    public MainController(IObservable observableAlgorithm, IIO io){
        _observableAlgorithm=observableAlgorithm;
        _io = io;
        _algorithmGraph=observableAlgorithm.getAlgorithmGraph();
        _timer = new TimerHelper(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _graphManager = new GraphManager(_io.getNodeMap(),_io.getEdgeList());
        _timer.startTimer();
        initializeGraph();
        initializeGantt();
        initializeStatistics();
    }

    @Override
    public void update() {
        _observableAlgorithm.getBestFState();
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
    }

    private void initializeStatistics() {
        algorithmStatus.setText(algorithmStatus.getText() + "In progress");
        algorithmTypeText.setText(algorithmTypeText.getText() + AlgorithmBuilder.getAlgorithmBuilder().getAlgorithmType());
        numberOfTasks.setText(numberOfTasks.getText() + _io.getNodeMap().size());
        numberOfProcessors.setText(numberOfProcessors.getText() + _io.getNumberOfProcessorsForTask());
        numberOfThreads.setText(numberOfThreads.getText() + _io.getNumberOfProcessorsForParallelAlgorithm());
    }

    public void setTimerStatistic(int currentTime) {
        Platform.runLater(() -> {
            int minutes = currentTime / 6000;
            int seconds = (currentTime - minutes * 6000) / 100;
            int milliseconds = currentTime - (minutes * 6000) - (seconds * 100);

            String minutesText ="";
            String secondsText = "";
            String millisecondsText = "";
            if (seconds < 10) { //Fix seconds
                secondsText = "0" + seconds;
            } else {
                secondsText = Integer.toString(seconds);
            }

            if (minutes < 10) {//Fix minutes
                minutesText = "0" + minutes;
            } else {
                minutesText = Integer.toString(minutes);
            }

            if (milliseconds < 10) {
                millisecondsText = "00" + millisecondsText;
            } else {
                millisecondsText = Integer.toString(milliseconds);
            }

            timeElapsedText.setText("Time Elapsed: " + minutesText + " : " + secondsText + " : " + millisecondsText);
        });
    }
}
