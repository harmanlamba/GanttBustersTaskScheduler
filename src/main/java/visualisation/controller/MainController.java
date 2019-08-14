package visualisation.controller;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.xml.soap.Text;

public class MainController implements IObserver {

    private HBox mainContainer;
    private VBox statsContainer;
    private Text algorithmTypeText;
    private Text timeElapsedText;
    private Text numProcessorsText;
    private Text parallelProcessorsText;
    private Text memoryUsedText;
    private TabPane visualsContainer;
    private Tab graphTab;
    private Tab taskTab;
    private Tab resultTab;
    private JFXTreeTableView<?> scheduleResultsTable;
    private TreeTableColumn<?, ?> taskIDColumn;
    private TreeTableColumn<?, ?> startTimeColumn;
    private TreeTableColumn<?, ?> assignedProcessorColumn;

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
