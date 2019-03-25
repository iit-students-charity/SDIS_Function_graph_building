package controller;

import model.Function;

public class Controller {
    private Function function;
    private Thread calcThread;
    private Thread drawThread;


    public Controller(Function function) {
        this.function = function;
        calcThread = new Thread();
        drawThread = new Thread();
    }

    public void startGraphicBuilding(int numberOfLists) {
        if (!calcThread.isAlive()) {
            calcThread = new Thread(new SortingTask(function, numberOfLists));
            calcThread.start();
        }
    }

    public void pauseGraphicBuilding() {
        if (calcThread.isAlive()) {
            calcThread.interrupt();
            function.getPoints().clear();
        }
    }

    public void addPointToGraphic() {

    }
}
