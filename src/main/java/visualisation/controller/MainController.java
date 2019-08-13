package visualisation.controller;

import ModelController.IObservable;
import ModelController.IObserver;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController implements IObserver {

    @FXML private HBox mainContainer;
    @FXML private VBox statsContainer;
    @FXML private TabPane visualsContainer;
    @FXML private Tab graphTab;
    @FXML private Tab taskTab;
    @FXML private Tab resultTab;

    //Reference to the ALgorithm, in order to know when we get notified about the specific stats such as Branch
    //Pruning
    private IObservable _observableAlgorithm;

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
