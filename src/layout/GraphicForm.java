package layout;

import javafx.scene.layout.GridPane;
import model.Function;

public class GraphicForm {
    private Function function;

    private GridPane gridPane;


    public GraphicForm(Function function) {
        this.function = function;

        gridPane = new GridPane();
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public Function getFunction() {
        return function;
    }
}
