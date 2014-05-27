/**
 * This is for testing formatting and css Style on charts
 * @author Patrick Daniel
 *
 */
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class LearnHighlightSeries extends Application {

    private DropShadow ds = new DropShadow();

    private ContextMenu contextMenu;

    private XYChart.Series selectedSeries;

    public String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    @Override
    public void start(Stage stage) {
        stage.setTitle("Linear plot");

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, 22, 0.5);

        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return String.format("%7.2f", object);
            }
        });
        final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

        lineChart.setCreateSymbols(false);
        lineChart.setAlternativeRowFillVisible(false);
        lineChart.setLegendVisible(false);

        Button gainUpBtn = new Button("Increase Gain");
        gainUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println(yAxis.getScaleY());
                yAxis.setScaleY(2);
            }
        });


        BorderPane pane = new BorderPane();
        pane.setCenter(lineChart);
        pane.setRight(gainUpBtn);
        Scene scene = new Scene(pane, 800, 600);
        scene.getStylesheets().add("lineStyle.css");

        contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Save data");
        contextMenu.getItems().add(menuItem);
        menuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                if (selectedSeries != null) {
                    System.out.println("Save data of " + selectedSeries.getData());
                    // Saving logic here
                }
            }
        });

        // for every series in linechart
//        applyMouseEvents(series1);
//        applyMouseEvents(series2);
        makeData(lineChart);
        stage.setScene(scene);
        stage.show();
    }

    private void applyMouseEvents(final XYChart.Series series, String name) {

        final Node node = series.getNode();
        node.setId(name);

        node.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                node.setEffect(ds);
                node.setCursor(Cursor.HAND);
            }
        });

        node.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                node.setEffect(null);
                node.setCursor(Cursor.DEFAULT);
            }
        });

        node.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(node, mouseEvent.getScreenX() + 1, mouseEvent.getScreenY() + 1);
                    // Set as selected
                    selectedSeries = series; //Set as the selectedSeries, if
                    System.out.println("Selected Series data " + selectedSeries.getData());
                }
            }
        });
    }

    public void makeData(LineChart lineChart) {
        for (int j = 0; j < 3; j++) {
            final XYChart.Series series = new XYChart.Series();
            for (int i = 0; i < this.months.length; i++) {
                double num = Math.random() * 20d;
                series.getData().add(new XYChart.Data(months[i], (int)num));
            }
            lineChart.getData().addAll(series);
            String name = "Series" +j;
            applyMouseEvents(series,name);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

