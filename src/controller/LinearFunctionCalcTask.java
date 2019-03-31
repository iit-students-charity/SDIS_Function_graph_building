package controller;

import model.Function;
import model.Point;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinearFunctionCalcTask implements Runnable {
    private final Function linearFunction;


    public LinearFunctionCalcTask(Function linearFunction) {
        this.linearFunction = linearFunction;
    }

    @Override
    public void run() {
        double a = 5;
        double b = -1;
        double step = 5;
        int sleepTime = 70;

        Lock lock = new ReentrantLock();

        for (double x = linearFunction.getXDownLimit(); x <= linearFunction.getXUpLimit(); x += step) {
            lock.lock();

            try {
                linearFunction.getPoints().add(new Point(x, a*x + b));
                System.out.println(new Point(x, a*x + b));

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } finally {
                lock.unlock();
            }
        }

        Thread.currentThread().interrupt();
    }
}
