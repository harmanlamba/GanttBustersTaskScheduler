package visualisation.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


/**
 * From https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch?answertab=votes#tab-top
 */
public class GanttChart<X,Y> extends XYChart<X,Y> {
    private double blockHeight = 10;

    public static class Properties {

        private long _length;
        private String _styleClass;
        private String _style;
        private String _taskId;

        public Properties(long lengthMs, String style, String taskId) {
            super();
            _length = lengthMs;
            _styleClass = "gantt-border";
            _taskId = taskId;

            //If task is unassigned, do not show!
            if (!style.equals("#000000")) {
                _style = "-fx-background-color:" + style;
            } else {
                _taskId = "";
                _length = 0;
                _style = "-fx-background-color: transparent;";
            }
        }

        public long getLength() {
            return _length;
        }

        public void setLength(long length) {
            _length = length;
        }

        public String getStyleClass() {
            return _styleClass;
        }

        public String getStyle() {
            return _style;
        }

        public void setStyleClass(String styleClass) {
            _styleClass = styleClass;
        }

        public void setStyle(String style) { _style = style; }

        public String getTaskId() { return _taskId; }
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getStyleClass(Object obj) {
        return ((Properties) obj).getStyleClass();
    }

    private static String getStyle(Object obj) {
        return ((Properties) obj).getStyle();
    }

    private static double getLength(Object obj) {
        return ((Properties) obj).getLength();
    }

    private static String getTaskId(Object obj) { return ((Properties) obj).getTaskId(); }

    @Override protected void layoutPlotChildren() {

        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {

            Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle rectangle;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();

                        if (region.getShape() == null) {
                            rectangle = new Rectangle( getLength( item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            rectangle = (Rectangle)region.getShape();
                        } else {
                            return;
                        }

                        rectangle.setWidth( getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        rectangle.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;

                        //Get positions of blocks
                        double paddingHeight = getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1);
                        double paddingWidth = getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1);

                        //Create regions with create rectangles
                        region.setShape(null);
                        region.setShape(rectangle);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        //Set positioning and add text with padding
                        region.setMaxHeight(rectangle.getHeight());
                        region.setMinHeight(rectangle.getHeight());
                        region.setPrefHeight(rectangle.getHeight());
                        Label text = new Label(getTaskId(item.getExtraValue())); //don't delete
                        text.setStyle("-fx-font-family: 'Space Mono', monospace; -fx-font-weight: BOLD; -fx-text-fill: white;");
                        text.setPadding(new Insets(paddingHeight, 0, 0, paddingWidth));
                        region.getChildren().add(text); //don't delete

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override protected void dataItemChanged(Data<X, Y> item) {
    }

    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }

    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add( getStyleClass(item.getExtraValue()));
        container.setStyle(getStyle(item.getExtraValue()));
        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<X>();
        if(ya.isAutoRanging()) yData = new ArrayList<Y>();
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

}