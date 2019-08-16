package visualisation.controller;

import com.jfoenix.controls.JFXTreeTableView;
import fileio.IIO;
import graph.Graph;
import javafx.embed.swing.SwingNode;
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

    //Reference to the ALgorithm, in order to know when we get notified about the specific stats such as Branch
    //Pruning
    private IObservable _observableAlgorithm;
    private Graph _algorithmGraph;
    private SingleGraph _graphStream;
    private IIO _io;
    private GraphController _graphController;
    private GraphUpdater _graphUpdater;



    //Public Control Fields from the FXML
    public HBox mainContainer;
    public VBox statsContainer;
    public Text algorithmTypeText1;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numProcessorsText;
    public Text parallelProcessorsText;
    public TabPane visualsContainer;
    public JFXTreeTableView<?> scheduleResultsTable;
    public TreeTableColumn<?, ?> taskIDColumn;
    public TreeTableColumn<?, ?> startTimeColumn;
    public TreeTableColumn<?, ?> assignedProcessorColumn;
    public SwingNode swingNode;

    //graph view
    public Tab graphTab;
    public Pane graphPane;


    //gantt view
    public Tab taskTab;
    public Pane ganttPane;

    //table view
    public Tab resultTab;


    public MainController(IObservable observableAlgorithm, IIO io){
        _observableAlgorithm=observableAlgorithm;
        _io = io;
        _algorithmGraph=observableAlgorithm.getAlgorithmGraph();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmTypeText.setText("Hello Baboons");
        _graphController = new GraphController(_io.getNodeMap(),_io.getEdgeList());
        initializeGraph();
    }


    @Override
    public void update() {
        _observableAlgorithm.getBestFState();

        //TODO: Need to add actual View changes but now have the best state configured
        //Need to ensure that the ObservableAlgorithm is passed to every new controller.
    }

    //TODO: implement graphstream
    private void initializeGraph() {
        _graphStream = _graphController.getGraph();
        _graphUpdater = new GraphUpdater(_graphStream, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        _graphUpdater.enableAutoLayout();
        ViewPanel viewPanel = _graphUpdater.addDefaultView(false);
        viewPanel.setMinimumSize(new Dimension(600,500));
        viewPanel.setOpaque(false);
        viewPanel.setBackground(Color.yellow);

        //graphPane.getChildren().add((viewPanel);


    }



}
