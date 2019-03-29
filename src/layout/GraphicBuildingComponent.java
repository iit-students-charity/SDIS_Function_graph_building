package layout;


import controller.Controller;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Function;
import model.Point;

import static sample.Main.MAIN_FORM_WIDTH;


public class GraphicBuildingComponent {
    private GridPane gridPane;

    private TextField nTextField;
    private TextField kTextField;
    private Button startBuildButton;
    private Button stopBuildButton;
    private TableView<Point> functionTable;
    private Graphic graphicDrawingComp;

    private Function arrayFunction;
    private Controller controller;


    public GraphicBuildingComponent(Function arrayFunction, Graphic graphic, Controller controller) {
        nTextField = new TextField();
        kTextField = new TextField();
        initStartButton();
        initPauseButton();
        initFunctionTable(arrayFunction);
        graphicDrawingComp = graphic;

        gridPane = new GridPane();
        gridPane.getColumnConstraints().add(0, new ColumnConstraints(MAIN_FORM_WIDTH / 4));
        gridPane.getColumnConstraints().add(1, new ColumnConstraints(MAIN_FORM_WIDTH));
        gridPane.setGridLinesVisible(true);
        gridPane.add(new VBox(
                new TwoNodesGrid(new Label("n"), nTextField).getGridPane(),
                new TwoNodesGrid(new Label("k"), kTextField).getGridPane(),
                new TwoNodesGrid(startBuildButton, stopBuildButton).getGridPane(),
                functionTable
        ), 0, 0);
        gridPane.add(graphicDrawingComp.getGroup(), 1,0);

        this.arrayFunction = arrayFunction;
        this.controller = controller;
    }

    private void initFunctionTable(Function function) {
        functionTable = new TableView<>();
        functionTable.setItems(function.getPoints());
        functionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Point, Double> xColumn = new TableColumn<>("x");
        TableColumn<Point, Double> yColumn = new TableColumn<>("y");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        functionTable.getColumns().addAll(xColumn, yColumn);
    }

    private void initStartButton() {
        startBuildButton = new Button("Start");

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

            if (k < arrayFunction.getXDownLimit()) {
                createEmptyDialog("Error", new Label("Entered data is not valid")).show();
                return;
            }

            arrayFunction.setXUpLimit(n);

            controller.startGraphicBuilding(k);
        });
    }

    private void initPauseButton() {
        stopBuildButton = new Button("Stop");
        stopBuildButton.setOnAction(e -> {
            controller.stopGraphicBuilding();
        });
    }

    private Alert createEmptyDialog(String title, Node content) {
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle(title);
        alert.getDialogPane().setContent(content);
        alert.getButtonTypes().add(ButtonType.OK);

        return alert;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Graphic getGraphicDrawingComp() {
        return graphicDrawingComp;
    }
}
