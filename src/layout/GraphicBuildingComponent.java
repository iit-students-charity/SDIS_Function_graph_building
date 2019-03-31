package layout;


import controller.Controller;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Function;
import model.Point;


public class GraphicBuildingComponent {
    private GridPane gridPane;

    private TextField nTextField;
    private TextField kTextField;
    private Button startBuildButton;
    private Button stopBuildButton;
    private TableView<Point> functionTable;
    private Graphic graphic;

    private Button incGraphicScaleButton;
    private Button decGraphicScaleButton;
    private Label currentGraphicScaleLabel;

    private Function arrayFunction;
    private Controller controller;


    public GraphicBuildingComponent(Function arrayFunction, Graphic graphic, Controller controller) {
        nTextField = new TextField();
        kTextField = new TextField();
        startBuildButton = new Button("Start");
        initStartButtonConfig();
        stopBuildButton = new Button("Stop");
        initStopButtonConfig();
        functionTable = new TableView<>();
        initFunctionTableConfig(arrayFunction);

        this.graphic = graphic;

        incGraphicScaleButton = new Button("+");
        initIncGraphicScaleButtonConfig();
        decGraphicScaleButton = new Button("-");
        initDecGraphicScaleButtonConfig();
        currentGraphicScaleLabel = new Label("Scale: 100%");

        gridPane = new GridPane();
        initGridPaneConfig();

        this.arrayFunction = arrayFunction;
        this.controller = controller;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    // Configs
    private void initGridPaneConfig() {
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(20);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(80);
        gridPane.getColumnConstraints().add(0, column0);
        gridPane.getColumnConstraints().add(1, column1);

        VBox controlPanel = new VBox(
                new TwoNodesGrid(new Label("n"), nTextField).getGridPane(),
                new TwoNodesGrid(new Label("k"), kTextField).getGridPane(),
                new TwoNodesGrid(startBuildButton, stopBuildButton).getGridPane(),
                functionTable
        );

        HBox scalingBox = new HBox(decGraphicScaleButton, currentGraphicScaleLabel, incGraphicScaleButton);
        scalingBox.setAlignment(Pos.CENTER);
        scalingBox.setSpacing(5);

        VBox graphicsPanel = new VBox(
                graphic.getScrollPane(),
                scalingBox
        );
        graphicsPanel.setAlignment(Pos.CENTER);
        graphicsPanel.setSpacing(5);

        gridPane.add(controlPanel, 0, 0);
        gridPane.add(graphicsPanel, 1,0);
        GridPane.setMargin(controlPanel, new Insets(10));
        GridPane.setMargin(graphicsPanel, new Insets(10));
        GridPane.setHalignment(graphicsPanel, HPos.CENTER);
    }

    private void initStartButtonConfig() {
        startBuildButton.setOnAction(e -> {
            String nString = nTextField.getText();
            String kString = kTextField.getText();

            double n;
            int k;
            try {
                n = Double.parseDouble(nString);
                k = Integer.parseInt(kString);
            } catch (NumberFormatException ex) {
                createEmptyDialog("Error", new Label("Entered data is not valid")).show();
                return;
            }

            if (n < arrayFunction.getXDownLimit()) {
                createEmptyDialog("Error", new Label("Entered data is not valid")).show();
                return;
            }

            arrayFunction.setXUpLimit(n);

            controller.startGraphicBuilding(k);
        });
    }

    private void initStopButtonConfig() {
        stopBuildButton.setOnAction(e -> {
            controller.stopGraphicBuilding();
        });
    }

    private void initFunctionTableConfig(Function function) {
        functionTable.setItems(function.getPoints());
        functionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Point, Double> xColumn = new TableColumn<>("x");
        TableColumn<Point, Double> yColumn = new TableColumn<>("y");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        functionTable.getColumns().addAll(xColumn, yColumn);
    }

    private void initIncGraphicScaleButtonConfig() {

    }

    private void initDecGraphicScaleButtonConfig() {

    }


    private Alert createEmptyDialog(String title, Node content) {
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle(title);
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().add(ButtonType.OK);

        return alert;
    }
}
