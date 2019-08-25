package visualisation.controller;

import graph.GraphNode;
import javafx.collections.FXCollections;
import javafx.geometry.NodeOrientation;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GanttManager {
    private GanttChart<Number, String> _ganttChart;
    private Pane _ganttPane;
    private int _maxCostBound;
    private int _numbmerOfTasks;

    public GanttManager(GanttChart<Number, String> ganttChart, Pane ganttPane, int numberOfTasks, int maxCostBound) {
        _ganttChart = ganttChart;
        _ganttPane = ganttPane;
        _numbmerOfTasks = numberOfTasks;
        _maxCostBound = maxCostBound;
        createGantt();
    }

    private void createGantt() {
        //Gantt chart initialize
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        _ganttChart = new GanttChart<>(xAxis, yAxis);
        _ganttPane.getChildren().add(_ganttChart);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/stylesheet.css").toExternalForm()); //style

        //ganttchart fx properties
        _ganttChart.setPrefWidth(650);
        _ganttChart.setPrefHeight(500);
        _ganttChart.setLayoutY(10);
        _ganttChart.setLegendVisible(false);
        _ganttChart.setBlockHeight(50);
        _ganttChart.setAlternativeRowFillVisible(false);
        _ganttChart.setHorizontalGridLinesVisible(false);
        _ganttChart.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        //x axis (xValue=Starttime, lengthMs=Worktime)
        xAxis.setLabel("Start time (s)");
        xAxis.setTickUnit(50);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(_maxCostBound);
        xAxis.setStyle("-fx-font-family: 'Space Mono', monospace;");

        //y axis (processor count)
        List<String> processors = new ArrayList<>();
        for (int i = 0; i < _numbmerOfTasks; i++) {
            processors.add(Integer.toString(i));
        }
        yAxis.setLabel("Processor(s)");
        yAxis.setTickLabelGap(20);
        yAxis.setCategories(FXCollections.observableList(processors));
        yAxis.setStyle("-fx-font-family: 'Space Mono', monospace;");
    }

    /**
     * updateGanttChart - Update gantt chart on observer update (via creating series)
     * @param processor - processor string for each gantt chart x
     * @param processorColour - processor colour assigned to each processor
     * @param graphNode - current graph node/task rectangle of gantt chart
     * @return
     */
    public XYChart.Series updateGanttChart(String processor, String processorColour, GraphNode graphNode) {
        XYChart.Series series1 = new XYChart.Series();
        if (Integer.toString(graphNode.getProcessor()).equals(processor)) {
            series1.getData().add(new XYChart.Data(graphNode.getStartTime(), processor,
                    new GanttChart.Properties(graphNode.getWeight(),
                            processorColour, graphNode.getId())));
        }
        return series1;
    }

    /**
     * getGanttChart - return ganttchart that is initially created
     * @return
     */
    public GanttChart<Number, String> getGanttChart() {
        return _ganttChart;
    }
}
