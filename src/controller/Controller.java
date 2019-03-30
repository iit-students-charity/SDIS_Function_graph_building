package controller;

import layout.Graphic;
import model.Function;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {private Function arrayFunction;
    private Function linearFunction;
    private Graphic graphic;
    private Thread arrayFunCalcThread;
    private Thread linFunCalcThread;
    private Thread arrayFunDrawThread;
    private Thread linFunDrawThread;


    public Controller(Function arrayFunction, Function linearFunction, Graphic graphic) {
        this.arrayFunction = arrayFunction;
        this.linearFunction = linearFunction;
        this.graphic = graphic;
        arrayFunCalcThread = new Thread();
        linFunCalcThread = new Thread();
        arrayFunDrawThread = new Thread();
        linFunDrawThread = new Thread();
    }

    public void startGraphicBuilding(int numberOfLists) {
        if ((!arrayFunCalcThread.isAlive() && !linFunCalcThread.isAlive()) ||
                (arrayFunCalcThread.isInterrupted() && linFunCalcThread.isInterrupted())) {
            arrayFunction.getPoints().clear();
            linearFunction.getPoints().clear();
            graphic.clear();

            arrayFunCalcThread = new Thread(new SortingTask(arrayFunction, numberOfLists));
            linFunCalcThread = new Thread(new LinearFunctionCalcTask(linearFunction));
            arrayFunDrawThread = new Thread(new DrawingTask(arrayFunction, graphic));
            linFunDrawThread = new Thread(new DrawingTask(linearFunction, graphic));
            arrayFunCalcThread.start();
            linFunCalcThread.start();
            arrayFunDrawThread.start();
            linFunDrawThread.start();
        }
    }

    public void stopGraphicBuilding() {
        if (!arrayFunCalcThread.isInterrupted()) {
            arrayFunCalcThread.interrupt();
            linFunCalcThread.interrupt();
            arrayFunDrawThread.interrupt();
            linFunDrawThread.interrupt();
        }
    }
}
