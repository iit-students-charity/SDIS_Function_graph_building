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


public class GraphicBuildingComponent {
    private GridPane gridPane;

    private TextField nTextField;
    private TextField kTextField;
    private Button startBuildButton;
    private Button pauseBuildButton;
    private TableView<Point> functionTable;
    private GraphicForm graphicForm;

    private Controller controller;


    public GraphicBuildingComponent(Function function, Controller controller) {
        nTextField = new TextField();
        kTextField = new TextField();
        initStartButton();
        initPauseButton();
        initFunctionTable(function);
        graphicForm = new GraphicForm(function);

        gridPane = new GridPane();
        gridPane.getColumnConstraints().add(0, new ColumnConstraints(LayoutConstant.MAIN_FORM_WIDTH / 4));
        gridPane.getColumnConstraints().add(1, new ColumnConstraints(LayoutConstant.MAIN_FORM_WIDTH));
        gridPane.add(new VBox(
                new TwoNodesGrid(new Label("n"), nTextField).getGridPane(),
                new TwoNodesGrid(new Label("k"), kTextField).getGridPane(),
                new TwoNodesGrid(startBuildButton, pauseBuildButton).getGridPane(),
                functionTable
        ), 0, 0);
        gridPane.add(graphicForm.getGridPane(), 1,0);

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

            graphicForm.getFunction().setXUpLimit(n);

            controller.startGraphBuilding(k);
        });
    }

    private void initPauseButton() {
        pauseBuildButton = new Button("Pause");
        pauseBuildButton.setOnAction(e -> {

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
}
