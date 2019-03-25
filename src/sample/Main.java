package sample;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import layout.LayoutConstant;
import layout.MainForm;
import model.Function;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Function function = new Function();
        Controller controller = new Controller(function);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Function graph building");
        primaryStage.setHeight(LayoutConstant.MAIN_FORM_HEIGHT);
        primaryStage.setWidth(LayoutConstant.MAIN_FORM_WIDTH);
        primaryStage.setScene(new Scene(new MainForm(function, controller).getVBox()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
