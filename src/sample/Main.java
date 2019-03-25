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
        MainForm mainForm = new MainForm(function, controller);

        primaryStage.setResizable(false);
        primaryStage.setTitle(LayoutConstant.MAIN_FORM_TITLE);
        primaryStage.setHeight(LayoutConstant.MAIN_FORM_HEIGHT);
        primaryStage.setWidth(LayoutConstant.MAIN_FORM_WIDTH);
        primaryStage.setScene(new Scene(mainForm.getVBox()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
