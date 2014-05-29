package axonTextTool;
import javafx.application.Application;
import javafx.collections.ObservableList;
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
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * A plotting tool for analyzing Axon Text Files, the data structure from the Molecular Devices DataDigi DAQ
 * @author Patrick Daniel
 * @date May 2014
 */
public class LineChartAxon extends Application {

    private XYChart.Series selectedSeries; //Will Use to store which ever series is currently selected
    private DropShadow ds = new DropShadow(); //use Drop shadow effect to highlight series that is desired
    private ContextMenu contextMenu;
    private String fileName;
    private Scene scene;
    private double[] defaultVX = new double[2];
    private double[] defaultVY = new double[2];

    public void start(final Stage primaryStage) {

        BorderPane borderPane = new BorderPane();
        //Create the LineChart for Stimulation (Voltage) Data
        final NumberAxis yVAxis = new NumberAxis();
        final NumberAxis xVAxis = new NumberAxis();
        yVAxis.setLabel("MilliVolts"); // This needs to be dynamic for either voltage or Current
        final LineChart voltageChart = new LineChart(xVAxis,yVAxis); //Create a line chart node
        voltageChart.setCreateSymbols(false); //disables Symbols on each data point
        voltageChart.setLegendVisible(false); //Disable the legend that displays the series
        voltageChart.setAnimated(false); //Animation with multiple series looks sluggish


        //Create LineChart for Response (Current) Data
        NumberAxis xCAxis = new NumberAxis();
        final NumberAxis yCAxis = new NumberAxis();
        final LineChart currentChart = new LineChart(xCAxis,yCAxis);
        yCAxis.setLabel("picoAmps");
        currentChart.setCreateSymbols(false);
        currentChart.setLegendVisible(false);
        currentChart.setAnimated(false);

        //Gain Control Buttons
        final Image ICON_UP = new Image("up-icon-30x30.png");
        Button upGainC = new Button();
        upGainC.setGraphic(new ImageView(ICON_UP));
        upGainC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                yVAxis.setAutoRanging(false);
                yVAxis.setUpperBound(yVAxis.getUpperBound() - yVAxis.getTickUnit()*2);
            }
        });
        final Image ICON_DOWN = new Image("down-icon-30x30.png");
        Button downGainC = new Button();
        downGainC.setGraphic(new ImageView(ICON_DOWN));
        downGainC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                yVAxis.setAutoRanging(false);
                System.out.println(yVAxis.getUpperBound());
                System.out.println(yVAxis.getTickUnit());
                yVAxis.setUpperBound(yVAxis.getUpperBound() + yVAxis.getTickUnit()*2);
            }
        });
        Button upGainV = new Button();
        upGainV.setGraphic(new ImageView(ICON_UP));
        upGainV.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                yVAxis.setAutoRanging(false);
                yVAxis.setUpperBound(yVAxis.getUpperBound() - yVAxis.getTickUnit()*2);
            }
        });
        Button downGainV = new Button();
        downGainV.setGraphic(new ImageView(ICON_DOWN));
        downGainV.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                yVAxis.setAutoRanging(false);
                System.out.println(yVAxis.getUpperBound());
                System.out.println(yVAxis.getTickUnit());
                yVAxis.setUpperBound(yVAxis.getUpperBound() + yVAxis.getTickUnit()*2);
            }
        });



        Button resetAxes = new Button("Reset Scale");
        resetAxes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                yVAxis.setLowerBound(defaultVY[0]);
                yVAxis.setUpperBound(defaultVY[1]);
                xVAxis.setLowerBound(defaultVX[0]);
                xVAxis.setUpperBound(defaultVX[1]);
            }
        });

        //Create Right Click menu
        contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Print Data");
        contextMenu.getItems().add(menuItem);
        contextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (selectedSeries != null) { //checks to make sure mouse is over a series ser set
                      ObservableList<XYChart.Data> data = selectedSeries.getData();
                    for (int i = 0; i < data.size(); i++) {
                        System.out.println("(" +data.get(i).getXValue() + "," + data.get(i).getYValue() + ")");
                    }
                }
            }
        });
        //Toolbar on the top
        ToolBar toolBar = new ToolBar();

        //text to display the filename
        final Text filenameTxt = new Text("");

        //Create Grid node for the charts
        final GridPane grid = new GridPane(); //ROOT NODE
        grid.setAlignment(Pos.TOP_LEFT);//Aligns the grid origin
        grid.setHgap(1); //manage the horizontal gap
        grid.setVgap(10); //manages the vertical gap
        grid.setPadding(new Insets(25,10,25,25)); //set the padding between cells
        grid.add(voltageChart,0,0);
        grid.add(currentChart,0,1);
        GridPane btnGrid =  new GridPane();

        btnGrid.add(upGainC, 0, 0);
        btnGrid.add(downGainC,1,0);

        btnGrid.add(upGainV, 0,1);
        btnGrid.add(downGainV,1,1);
        grid.add(btnGrid,1,0);

        //Open File Button
        final Button openFileBtn = new Button("Load");
        final FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Axon Text Files", "*.atf"); //Show only Axon Text Files by default
        fileChooser.getExtensionFilters().add(txtFilter);

        openFileBtn.setOnAction(//Click the Button!
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        removeSeries(voltageChart, currentChart, primaryStage);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            Trace[] data = loadFile(file, filenameTxt);
                            setData(voltageChart, currentChart, data); //Use this function to create several series and poulate each with data
                        }
                        //Collect the default axes sizes in case of reset
                        xVAxis.setAutoRanging(true);
                        yVAxis.setAutoRanging(true);
                        defaultVX[0] = xVAxis.getLowerBound();
                        defaultVX[1] = xVAxis.getUpperBound();
                        defaultVY[0] = yVAxis.getLowerBound();
                        defaultVY[1] = yVAxis.getUpperBound();
                    }
                }
        );
        toolBar.getItems().add(openFileBtn);
        //Save Picture Button
        final Button savePNGBtn = new Button("Save PNG");
        savePNGBtn.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        saveAsPng(grid);

                    }
                }
        );
        toolBar.getItems().add(savePNGBtn);
//        toolBar.getItems().add(upGain);
//        toolBar.getItems().add(downGain);
        toolBar.getItems().add(resetAxes);
        toolBar.getItems().add(filenameTxt);
        borderPane.setTop(toolBar);
        borderPane.setCenter(grid);

        this.scene = new Scene(borderPane,800,1000,Color.GREY);
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
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    contextMenu.show(node, mouseEvent.getScreenX() + 1, mouseEvent.getScreenY() + 1);
                    // Set as selected
                    selectedSeries = series; //Set as the selectedSeries, if
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