package visualisation.controller;

import com.jfoenix.controls.JFXTreeTableView;
import fileio.IIO;
import graph.Graph;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements IObserver, Initializable {

    //Private Fields

    //Reference to the ALgorithm, in order to know when we get notified about the specific stats such as Branch
    //Pruning
    private IObservable _observableAlgorithm;
    private Graph _algorithmGraph;
    private IIO _io;



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

    //graph view
    public Tab graphTab;
    public Pane graphPane;
    private GraphController _graphController;

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

    }



}
