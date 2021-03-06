package Sorter.Controller;

import Sorter.Algorithms.BubbleSort;
import Sorter.Algorithms.InsertionSort;
import Sorter.Algorithms.Quicksort;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Controller implements Initializable {
    // Amount of data in the barchart
    int n = 20;

    public static ArrayList<Integer> nums;
    final NumberAxis yAxis = new NumberAxis();
    final CategoryAxis xAxis = new CategoryAxis();
    final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
    XYChart.Series<String, Number> series1 = new XYChart.Series();

    String algo;

    public Controller() {
        bc.setTitle("Algorithm : " + algo);
        bc.setCategoryGap(0);
        bc.setBarGap(0);
        bc.setAnimated(false);
        bc.setMinSize(550, 200);
        bc.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        series1.setName("Random data");

        generateRandomNumberlist();

        for (Object a : nums) {
            series1.getData().add(new XYChart.Data(a.toString(), a));
        }
        bc.getData().add(series1);
    }

    public void generateRandomNumberlist() {
        ArrayList<Integer> nums = new ArrayList<>();
        int i = 1;
        while (nums.size() < n) {
            nums.add(i);
            i++;
        }
        Collections.shuffle(nums);
        new InsertionSort().resetVars();
        new Quicksort().resetVars();
        this.nums = nums;
    }

    private void updateChart() {
        XYChart.Series<String, Number> seriesA = new XYChart.Series();
        for (int i = 0; i < nums.size(); i++) {
            seriesA.getData().add(new XYChart.Data(nums.get(i).toString(), nums.get(i)));
        }
        seriesA.setName("Random data");
        bc.getData().clear();
        bc.layout();
        bc.getData().add(seriesA);
    }

    public void log(String text) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        log.appendText("(" + dtf.format(now) + "): " + text + "\n");
    }

    public boolean isSorted() {
        for (int i = 0; i < nums.size() - 1; i++) {
            if (nums.get(i) > nums.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    public void doStep(String algo){
        switch (algo) {
            case "Bubble sort":
                nums = new BubbleSort().oneStepBubbleSort(nums);
                updateChart();
                break;
            case "Insertion sort":
                nums = new InsertionSort().oneStepInsertionSort(nums);
                updateChart();
                break;
            case "Quick sort":
                nums = new Quicksort().oneStepQuickSort(nums);
                updateChart();
                break;
        }
    }

    public void sortOnTimer() {
        String text = msTextField.getText();
        if (text.matches("[0-9]+")) {
            if (Integer.parseInt(text) >= 10 && Integer.parseInt(text) <= 100)
            log("Sorting every : " + text + "ms.");
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            doStep(algo);
                            if (isSorted()) {
                                log("List is sorted. Stopping loop.");
                                t.cancel();
                            }
                        }
                    });
                }
            }, 1000, Integer.parseInt(msTextField.getText()));
        } else {
            log("Please input a number between 10 and 100.");
        }
    }

    @FXML //  fx:id="mainVBox"
    private VBox mainVBox; // Value injected by FXMLLoader

    @FXML //  fx:id="mainVBox"
    private ComboBox algorithmSelect; // Value injected by FXMLLoader

    @FXML
    private javafx.scene.control.Button sortOnTimerBtn; // Value injected by FXMLLoader

    @FXML
    private TextArea log; // Value injected by FXMLLoader

    @FXML
    private TextField msTextField; // Value injected by FXMLLoader

    @FXML
    private TextField amountOfDataTextField; // Value injected by FXMLLoader

    @FXML
    private javafx.scene.control.Button stepBtn; // Value injected by FXMLLoader

    @FXML
    private HBox barBox; // Value injected by FXMLLoader

    @FXML
    private javafx.scene.control.Button newListBtn; // Value injected by FXMLLoader

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        barBox.getChildren().add(bc);

        ObservableList<String> options = FXCollections.observableArrayList("Bubble sort", "Insertion sort", "Quick sort");

        algorithmSelect.setItems(options);
        algorithmSelect.setValue(options.get(0));
        algo = options.get(0);
        bc.setTitle("Algorithm : " + algo);

        algorithmSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                log("Selected : " + algorithmSelect.getValue());
                algo = "" + algorithmSelect.getValue();
                bc.setTitle("Algorithm : " + algo);
            }
        });

        stepBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                doStep(algo);
            }
        });

        sortOnTimerBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                sortOnTimer();
            }
        });

        newListBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = amountOfDataTextField.getText();
                if (text.matches("[0-9]+")) {
                    n = Integer.parseInt(text);
                }
                generateRandomNumberlist();
                updateChart();
            }
        });
    }
}