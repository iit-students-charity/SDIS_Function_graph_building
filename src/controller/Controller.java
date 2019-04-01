package controller;

import layout.Graphic;
import model.Function;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Controller {private Function arrayFunction;
    private Function linearFunction;
    private Graphic graphic;
    private Thread arrayFunCalcThread;
    private Thread linFunCalcThread;

    public Controller(Function arrayFunction, Function linearFunction, Graphic graphic) {
        this.arrayFunction = arrayFunction;
        this.linearFunction = linearFunction;
        this.graphic = graphic;
        arrayFunCalcThread = new Thread();
        linFunCalcThread = new Thread();

        Thread drawThread = new Thread(new DrawingTask(graphic));
        drawThread.setDaemon(true);
        drawThread.start();
    }

    public void startGraphicBuilding(int numberOfLists) {
        if (!arrayFunCalcThread.isAlive() || arrayFunCalcThread.isInterrupted()) {
            arrayFunction.getPoints().clear();
            linearFunction.getPoints().clear();
            graphic.clear();

            arrayFunCalcThread = new Thread(new SortingTask(arrayFunction, numberOfLists));
            linFunCalcThread = new Thread(new LinearFunctionCalcTask(linearFunction));

            arrayFunCalcThread.setDaemon(true);
            linFunCalcThread.setDaemon(true);

            arrayFunCalcThread.start();
            linFunCalcThread.start();
        }
    }

    public void incrementGraphicScale() {
        Lock lock = new ReentrantLock();
        lock.lock();

        try {
            graphic.incrementScale();
        } finally {
            lock.unlock();
        }
    }

    public void decrementGraphicScale() {
        Lock lock = new ReentrantLock();
        lock.lock();

        try {
            graphic.decrementScale();
        } finally {
            lock.unlock();
        }
    }

    public void stopGraphicBuilding() {
        if (!arrayFunCalcThread.isInterrupted()) {
            arrayFunCalcThread.interrupt();
            linFunCalcThread.interrupt();
        }
    }
}
