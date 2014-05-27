/**
 * From http://stackoverflow.com/questions/10371640/javafx-2-chart-zoom
 */

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class LearnZoom extends Application {

    Path path;//Add path for freehand
    BorderPane pane;
    Rectangle rect;
    SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
    SimpleDoubleProperty rectX = new SimpleDoubleProperty();
    SimpleDoubleProperty rectY = new SimpleDoubleProperty();

    @Override
    public void start(Stage stage) {

        System.out.println("Java Version             : " + com.sun.javafx.runtime.VersionInfo.getVersion());
        System.out.println("Java getHudsonBuildNumber: " + com.sun.javafx.runtime.VersionInfo.getHudsonBuildNumber());
        System.out.println("Java getReleaseMilestone : " + com.sun.javafx.runtime.VersionInfo.getReleaseMilestone());
        System.out.println("Java getRuntimeVersion   : " + com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

        stage.setTitle("Lines plot");

        //final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis xAxis = new NumberAxis(1, 12, 1);
        final NumberAxis yAxis = new NumberAxis(0.53000, 0.53910, 0.0005);

        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {

            @Override
            public String toString(Number object) {
                return String.format("%7.5f", object);
            }
        });


        //final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        //lineChart.setTitle("Stock quotes");
        lineChart.setCreateSymbols(false);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setAnimated(true);

        XYChart.Series series1 = new XYChart.Series();
        //series1.setName("Stock 1");
        ArrayList<Double> data = new ArrayList<Double>();
        new XYChart.Data(1,2);



        series1.getData().add(new XYChart.Data(1, 0.53185));
        series1.getData().add(new XYChart.Data(2, 0.532235));
        series1.getData().add(new XYChart.Data(3, 0.53234));
        series1.getData().add(new XYChart.Data(4, 0.538765));
        series1.getData().add(new XYChart.Data(5, 0.53442));
        series1.getData().add(new XYChart.Data(6, 0.534658));
        series1.getData().add(new XYChart.Data(7, 0.53023));
        series1.getData().add(new XYChart.Data(8, 0.53001));
        series1.getData().add(new XYChart.Data(9, 0.53589));
        series1.getData().add(new XYChart.Data(10, 0.53476));
        series1.getData().add(new XYChart.Data(11, 0.530123));
        series1.getData().add(new XYChart.Data(12, 0.53035));


        pane = new BorderPane();
        pane.setCenter(lineChart);
        //Scene scene = new Scene(lineChart, 800, 600);
        Scene scene = new Scene(pane, 800, 600);
        lineChart.getData().addAll(series1);

        stage.setScene(scene);

        path = new Path();
        path.setStrokeWidth(1);
        path.setStroke(Color.BLACK);

        scene.setOnMouseClicked(mouseHandler);
        scene.setOnMouseDragged(mouseHandler);
        scene.setOnMouseEntered(mouseHandler);
        scene.setOnMouseExited(mouseHandler);
        scene.setOnMouseMoved(mouseHandler);
        scene.setOnMousePressed(mouseHandler);
        scene.setOnMouseReleased(mouseHandler);

        //root.getChildren().add(lineChart);
        pane.getChildren().add(path);

        rect = new Rectangle();
        rect.setFill(Color.web("blue", 0.1));
        rect.setStroke(Color.BLUE);
        rect.setStrokeDashOffset(50);

        rect.widthProperty().bind(rectX.subtract(rectinitX));
        rect.heightProperty().bind(rectY.subtract(rectinitY));
        pane.getChildren().add(rect);

        stage.show();
    }
    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent mouseEvent) {

            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                rect.setX(mouseEvent.getX());
                rect.setY(mouseEvent.getY());
                rectinitX.set(mouseEvent.getX());
                rectinitY.set(mouseEvent.getY());
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                rectX.set(mouseEvent.getX());
                rectY.set(mouseEvent.getY());
            } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

                System.out.println("Zoom bounds : [" + rectinitX.get()+", "+rectinitY.get()+"] ["+ rectX.get()+", "+rectY.get()+"]");
                System.out.println("TODO: Determine bound ranges according these zoom coordinates.\n");

                // TODO: Determine bound ranges according this zoom coordinates.
                //LineChart<String, Number> lineChart = (LineChart<String, Number>) pane.getCenter();
                LineChart<Number, Number> lineChart = (LineChart<Number, Number>) pane.getCenter();

                // Zoom in Y-axis by changing bound range.
                NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
                yAxis.setLowerBound(0.532);
                yAxis.setUpperBound(0.538);

                // Zoom in X-axis by removing first and last data values.
                // Note: Maybe better if categoryaxis is replaced by numberaxis then setting the
                // LowerBound and UpperBound will be avaliable.
            /*
            XYChart.Series series1 = lineChart.getData().get(0);
            if (!series1.getData().isEmpty()) {
                series1.getData().remove(0);
                series1.getData().remove(series1.getData().size() - 1);
            }
            */

                NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
                System.out.println("(a) xAxis.getLowerBound() "+xAxis.getLowerBound()+" "+xAxis.getUpperBound());
                double Tgap = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());
                double newXlower, newXupper;
                newXlower = (rectinitX.get()/Tgap)+xAxis.getLowerBound();

                newXupper = (rectX.get()/Tgap)+xAxis.getLowerBound();
                if (newXupper > xAxis.getUpperBound())
                    newXupper = xAxis.getUpperBound();

                xAxis.setLowerBound( newXlower );
                xAxis.setUpperBound( newXupper );

                System.out.println("(b) xAxis.getLowerBound() "+xAxis.getLowerBound()+" "+xAxis.getUpperBound());

                // Hide the rectangle
                rectX.set(0);
                rectY.set(0);
            }
        }
    };

    public static void main(String[] args) {
        launch(args);
    }
}