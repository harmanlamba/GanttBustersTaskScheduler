package visualisation.controller;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.xml.soap.Text;

public class MainController implements IObserver {

    public HBox mainContainer;
    public VBox statsContainer;
    public Text algorithmTypeText1;
    public Text algorithmTypeText;
    public Text timeElapsedText;
    public Text numProcessorsText;
    public Text parallelProcessorsText;
    public TabPane visualsContainer;
    public Tab graphTab;
    public Pane graphPane;
    public Tab taskTab;
    public Pane ganttPane;
    public Tab resultTab;
    public JFXTreeTableView<?> scheduleResultsTable;
    public TreeTableColumn<?, ?> taskIDColumn;
    public TreeTableColumn<?, ?> startTimeColumn;
    public TreeTableColumn<?, ?> assignedProcessorColumn;

    //Reference to the ALgorithm, in order to know when we get notified about the specific stats such as Branch
    //Pruning
    private IObservable _observableAlgorithm;

    public MainController() {

    }
    public MainController(IObservable observableAlgorithm){
        _observableAlgorithm=observableAlgorithm;
    }

    @Override
    public void update() {
        _observableAlgorithm.getBestFState();

        //TODO: Need to add actual View changes but now have the best state configured
        //Need to ensure that the ObservableAlgorithm is passed to every new controller.
    }
}
