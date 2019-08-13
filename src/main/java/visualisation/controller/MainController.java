package visualisation.controller;

import ModelController.IObservable;
import ModelController.IObserver;
import algorithm.Algorithm;

public class MainController implements IObserver {
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
