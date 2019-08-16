package visualisation.controller;

import algorithm.AlgorithmBuilder;
import com.jfoenix.controls.JFXTreeTableView;
import fileio.IIO;
import graph.Graph;
import javafx.embed.swing.SwingNode;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements IObserver, Initializable {

    //Private Fields
    private IObservable _observableAlgorithm;
    private Graph _algorithmGraph;
    private SingleGraph _graphStream;
    private IIO _io;
    private GraphController _graphController;
    private GraphUpdater _graphUpdater;

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
    public BarChart<?, ?> ganttChart;

    public Tab resultTab;
    public JFXTreeTableView<?> scheduleResultsTable;
    public TreeTableColumn<?, ?> taskIDColumn;
    public TreeTableColumn<?, ?> startTimeColumn;
    public TreeTableColumn<?, ?> assignedProcessorColumn;

    public MainController(IObservable observableAlgorithm, IIO io){
        _observableAlgorithm=observableAlgorithm;
        _io = io;
        _algorithmGraph=observableAlgorithm.getAlgorithmGraph();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _graphController = new GraphController(_io.getNodeMap(),_io.getEdgeList());
        initializeGraph();
        initializeStatistics();
    }

    @Override
    public void update() {
        _observableAlgorithm.getBestFState();
    }

    private void initializeGraph() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer"); //Set styling renderer
        _graphStream = _graphController.getGraph();
        _graphUpdater = new GraphUpdater(_graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        _graphUpdater.enableAutoLayout();

        //Create graphstream view panel
        ViewPanel viewPanel = _graphUpdater.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(700,500)); //Window size
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.white);
        _graphUpdater.setMouseManager(viewPanel); //Disable mouse drag of nodes

        //Assign graph using swing node
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(viewPanel);
        });
        swingNode.setLayoutX(5);
        swingNode.setLayoutY(5);
    }

    private void initializeStatistics() {
        algorithmStatus.setText(algorithmStatus.getText() + "In progress");
        algorithmTypeText.setText(algorithmTypeText.getText() + AlgorithmBuilder._algorithmType);
        //set time
        numberOfTasks.setText(numberOfTasks.getText() + _io.getNodeMap().size());
        numberOfProcessors.setText(numberOfProcessors.getText() + _io.getNumberOfProcessorsForTask());
        numberOfThreads.setText(numberOfThreads.getText() + _io.getNumberOfProcessorsForParallelAlgorithm());
    }
}
