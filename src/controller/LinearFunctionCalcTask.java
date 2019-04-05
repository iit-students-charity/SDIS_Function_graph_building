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
        double step = 5;
        int sleepTime = 50;

        for (double x = linearFunction.getXDownLimit(); x <= linearFunction.getXUpLimit(); x += step) {
            linearFunction.getPoints().add(new Point(x, a*x + b));
            //System.out.println(new Point(x, a*x + b));

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Thread.currentThread().interrupt();
    }
}
