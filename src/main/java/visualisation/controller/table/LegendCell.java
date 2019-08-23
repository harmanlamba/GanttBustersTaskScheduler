package visualisation.controller.table;


import com.jfoenix.controls.JFXListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import visualisation.controller.ProcessorColourHelper;


public class LegendCell extends JFXListCell<String> {
    private HBox hBox  = new HBox();
    private Label label = new Label("(empty)");
    private Pane spacerPane = new Pane();
    private Pane colorPane = new Pane();
    private ProcessorColourHelper _processColourHelper;

    public LegendCell(ProcessorColourHelper processorColorHelper){
        super();
        _processColourHelper = processorColorHelper;
        hBox.getChildren().addAll(label, spacerPane,colorPane);
        hBox.setHgrow(spacerPane, Priority.ALWAYS);
        hBox.setHgrow(colorPane,Priority.ALWAYS);
        colorPane.setBackground(Background.EMPTY);
        //colorPane.setScaleX(0.5);

    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if(empty){

        }else{
            label.setText(item!=null ? "Processor: " + item : "<null>");
            String color = _processColourHelper.getProcessorColour(Integer.parseInt(item));
            String style = "-fx-background-color: " + color + ";"+"";
            colorPane.setStyle(style);
            setGraphic(hBox);
        }
    }
}
