package layout;

import controller.Controller;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import model.Function;

public class MainForm {
    private VBox vBox;

    private MenuBar menuBar;
    private GraphicBuildingComponent graphicBuildingComponent;
    private ToolBar toolBar;

    private Controller controller;


    public MainForm(Function function, Graphic graphic, Controller controller) {
        menuBar = createMenuBar();
        graphicBuildingComponent = new GraphicBuildingComponent(function, graphic, controller);
        toolBar = createToolBar();

        vBox = new VBox(
            menuBar,
            graphicBuildingComponent.getGridPane(),
            toolBar
        );
        this.controller = controller;
    }

    public VBox getVBox() {
        return vBox;
    }

    public GraphicBuildingComponent getGraphicBuildingComponent() {
        return graphicBuildingComponent;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        return menuBar;
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        return toolBar;
    }
}
