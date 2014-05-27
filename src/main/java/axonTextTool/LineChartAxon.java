package axonTextTool;
import axonTextTool.ReadText;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


/**
 * Created by Patrick on 5/16/14.
 */
public class LineChartAxon extends Application {

    private XYChart.Series selectedSeries; //Will Use to store which ever series is currently selected
    private DropShadow ds = new DropShadow(); //use Drop shadow effect to highlight series that is desired
    private ContextMenu contextMenu;
    private String fileName;
    private Scene scene;

    public void start(final Stage primaryStage) {
        //Create the LineChart for Stimulation (Voltage) Data
        NumberAxis yVAxis = new NumberAxis();
        NumberAxis xVAxis = new NumberAxis();
        yVAxis.setLabel("MilliVolts"); // This needs to be dynamic for either voltage or Current
        final LineChart voltageChart = new LineChart(xVAxis,yVAxis); //Create a line chart node
        voltageChart.setCreateSymbols(false); //disables Symbols on each data point
        voltageChart.setLegendVisible(false); //Disable the legend that displays the series
        //Create LineChart for Response (Current) Data
        NumberAxis xCAxis = new NumberAxis();
        NumberAxis yCAxis = new NumberAxis();
        final LineChart currentChart = new LineChart(xCAxis,yCAxis);
        yCAxis.setLabel("picoAmps");
        currentChart.setCreateSymbols(false);
        currentChart.setLegendVisible(false);
        //Create Right Click menu
        contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Print Data");
        contextMenu.getItems().add(menuItem);
        contextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (selectedSeries != null) { //checks to make sure mouse is over a series ser set
                    System.out.println(selectedSeries.getData());
                }
            }
        });

        //Create Grid node for the Root
        final GridPane grid = new GridPane(); //ROOT NODE
        grid.setAlignment(Pos.TOP_LEFT);//Aligns the grid origin
        grid.setHgap(1); //manage the horizontal gap
        grid.setVgap(10); //manages the vertical gap
        grid.setPadding(new Insets(25,10,25,25)); //set the padding between cells
        grid.add(voltageChart,0,0);
        grid.add(currentChart,0,1);

        final Button openFileBtn = new Button("Select a File");
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Axon Text Files", "*.atf"); //Show only Axon Text Files by default
        fileChooser.getExtensionFilters().add(txtFilter);
        final Text filenameTxt = new Text("test");
        openFileBtn.setOnAction(//Click the Button!
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        removeSeries(voltageChart,currentChart,primaryStage);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if(file != null) {
                            Trace[] data = loadFile(file,filenameTxt);
                            setData(voltageChart,currentChart, data); //Use this function to create several series and poulate each with data
                        }
                    }
                }
        );

        final Button savePNGBtn = new Button("Save PNG");
        savePNGBtn.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        saveAsPng(grid);

                    }
                }
        );

        grid.add(filenameTxt,2,0);
        grid.add(savePNGBtn,1,2);
        grid.add(openFileBtn,0,2);
        this.scene = new Scene(grid,800,1000,Color.GREY);
//        scene.getStylesheets().add(LineChartAxon.class.getResource("LineChartAxon.css").toExternalForm());
        primaryStage.setScene(scene);

        scene.getStylesheets().add("lineStyle.css");

        primaryStage.show();

    }

    /**
     * modify a LineChart object, adding Data Series to it, based on an array of Trace objects
     * @param lineChart
     * @param data
     */
    private void setData(LineChart lineChart,LineChart currentLine, Trace[] data) {
        long t = System.currentTimeMillis();
        for (int i = 0; i < data.length; i++) {
            XYChart.Series series = new XYChart.Series();
            series.setName("sweepNum"+data[i].getSweepNum());
            XYChart.Series seriesC = new XYChart.Series();
            seriesC.setName("SweepNum"+data[i].getSweepNum());

            for (int j = 0; j < data[i].getTimeList().size(); j++) {
                series.getData().add(new XYChart.Data(data[i].getTimeList().get(j),data[i].getVoltageList().get(j)));
                seriesC.getData().add(new XYChart.Data(data[i].getTimeList().get(j),data[i].getCurrentList().get(j)));
            }
//                  for (int j = 0; j < data[i].getTime().length; j++) {
//                series.getData().add(new XYChart.Data(data[i].getTime()[j],data[i].getVoltageData()[j]));
//                seriesC.getData().add(new XYChart.Data(data[i].getTime()[j],data[i].getCurrentData()[j]));
//            }
            lineChart.getData().add(series);
            currentLine.getData().add(seriesC);
            applyMouseEvents(series);
        }
        this.fileName = data[0].getFilename();
        long d = System.currentTimeMillis();
        System.out.printf("It took %d milliseconds to load %s\n",(d-t),this.fileName);
    }

    private Trace[] loadFile(File file, Text text) {
        ReadText readText = new ReadText(file);
        try {
            readText.readFile(file);

        } catch (Exception ex) {
            System.out.println("That didn't work.  Here's why:");
            ex.printStackTrace();
        }
        text.setText(file.getName());
        return readText.getExperiment();
    }

    /**
     * Save a png ScreenShot of the entire Grid to local directory
     * Adapted from http://code.makery.ch/blog/javafx-2-snapshot-as-png-image/
     * @param grid
     */
    private void saveAsPng(GridPane grid) {
        WritableImage image = grid.snapshot(new SnapshotParameters(), null); //Create empty, writable image
        File file = new File(this.fileName.substring(0,fileName.length()-4) + ".png"); //Name
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Image Saved");
        }
        catch (IOException e) {
            // TODO: handle exception here
            System.out.println("nope");
        }
    }

    /**
     * applyMouseEvents makes a node for a series and gives a mouseOnClicked highlight
     * Adapted from http://stackoverflow.com/questions/11538195/javafx-2-x-how-to-highlight-plotted-data-on-a-chart
     * @param series
     */
    private void applyMouseEvents(final XYChart.Series series) {

        final Node node = series.getNode();
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (node.getEffect() == null) {
                    node.setEffect(ds); //Dropshadow highlight
                }
                else {
                    node.setEffect(null);
                }
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

    private  void removeSeries(LineChart lineChart,LineChart lineChart2, Stage primaryStage) {
        lineChart.getData().clear();
        lineChart2.getData().clear();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
//TODO Save each chart as a small image file, eventually make a mosaic for an entire experiment
//TODO comment checked, MOUSEOVER to
//TODO Load in second graph with Voltage