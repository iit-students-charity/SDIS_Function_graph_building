package controller;

import layout.Graphic;
import model.Function;


public class Controller {private Function arrayFunction;
    private Function linearFunction;
    private Graphic graphic;
    private Thread arrayFunCalcThread;
    private Thread linFunCalcThread;
    private Thread drawThread;


    public Controller(Function arrayFunction, Function linearFunction, Graphic graphic) {
        this.arrayFunction = arrayFunction;
        this.linearFunction = linearFunction;
        this.graphic = graphic;
        arrayFunCalcThread = new Thread();
        linFunCalcThread = new Thread();
        drawThread = new Thread();
    }

    public void startGraphicBuilding(int numberOfLists) {
        if ((!arrayFunCalcThread.isAlive() && !linFunCalcThread.isAlive()) ||
                (arrayFunCalcThread.isInterrupted() && linFunCalcThread.isInterrupted())) {
            arrayFunction.getPoints().clear();
            linearFunction.getPoints().clear();
            graphic.clear();

            arrayFunCalcThread = new Thread(new SortingTask(arrayFunction, numberOfLists));
            linFunCalcThread = new Thread(new LinearFunctionCalcTask(linearFunction));
            drawThread = new Thread(new DrawingTask(graphic));

            arrayFunCalcThread.setDaemon(true);
            linFunCalcThread.setDaemon(true);
            drawThread.setDaemon(true);

            arrayFunCalcThread.start();
            linFunCalcThread.start();
            drawThread.start();
        }
    }

    public void stopGraphicBuilding() {
        if (!arrayFunCalcThread.isInterrupted()) {
            arrayFunCalcThread.interrupt();
            linFunCalcThread.interrupt();
            drawThread.interrupt();
        }
    }
}
