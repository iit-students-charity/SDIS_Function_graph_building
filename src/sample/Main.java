package sample;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import layout.GraphicCanvas;
import layout.MainForm;
import model.Function;

public class Main extends Application {
    public static final int MAIN_FORM_HEIGHT = 550;
    public static final int MAIN_FORM_WIDTH = 800;

    @Override
    public void start(Stage primaryStage) throws Exception {
        String mainFormTitle = "Function graph building";

        int arrayFunctionXDownLimit = 2;
        Function arrayFunction = new Function();
        arrayFunction.setXDownLimit(arrayFunctionXDownLimit);
        Function linearFunction = new Function();

        GraphicCanvas graphic = new GraphicCanvas(arrayFunction, linearFunction);
        Controller controller = new Controller(arrayFunction, linearFunction, graphic);
        MainForm mainForm = new MainForm(arrayFunction, graphic, controller);

        primaryStage.setResizable(false);
        primaryStage.setTitle(mainFormTitle);
        primaryStage.setHeight(MAIN_FORM_HEIGHT);
        primaryStage.setWidth(MAIN_FORM_WIDTH);
        primaryStage.setScene(new Scene(mainForm.getVBox()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
