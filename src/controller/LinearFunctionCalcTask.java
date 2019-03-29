package controller;

import model.Function;
import model.Point;

public class LinearFunctionCalcTask implements Runnable {
    private final Function linearFunction;


    public LinearFunctionCalcTask(Function linearFunction) {
        this.linearFunction = linearFunction;
    }

    @Override
    public void run() {
        double a = 5;
        double b = -1;
        double step = 0.1;

        int sleepTime = 30;

        for (double x = linearFunction.getXDownLimit(); x <= linearFunction.getXUpLimit(); x += step) {
            synchronized (linearFunction) {
                linearFunction.getPoints().add(new Point(x, a*x + b));
                try {
                    linearFunction.notifyAll();
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        Thread.currentThread().interrupt();
    }
}
