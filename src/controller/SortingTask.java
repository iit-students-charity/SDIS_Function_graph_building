package controller;

import javafx.collections.ObservableList;
import model.Function;
import model.Point;
import model.Tree;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SortingTask implements Runnable {
    private final Function arrayFunction;
    private int numberOfLists;


    public SortingTask(Function arrayFunction, int numberOfLists) {
        this.arrayFunction = arrayFunction;
        this.numberOfLists = numberOfLists;
    }

    @Override
    public void run() {
        double mega = 1000000;
        int sleepTime = 100;

        Lock lock = new ReentrantLock();

        for (int i = (int) arrayFunction.getXDownLimit(); i <= arrayFunction.getXUpLimit(); i++) {
            double summarySortingTime = 0;

            for (int j = 0; j < numberOfLists; j++) {
                ObservableList<Double> doubles = ListGenerator.generate(i);

                double startTime = System.nanoTime();
                sort(doubles);
                summarySortingTime += System.nanoTime() - startTime;
            }
            double averageSortingTime = (summarySortingTime / numberOfLists) / mega;

            lock.lock();

            try {
                arrayFunction.getPoints().add(new Point(i, averageSortingTime));

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

    private void sort(ObservableList<Double> doubles) {
        Tree tree = new Tree(doubles.get(0));

        for (int doublesIter = 1; doublesIter < doubles.size(); doublesIter++) {
            tree.insert(new Tree(doubles.get(doublesIter)));
        }

        doubles.clear();
        doubles.addAll(tree.toList());
    }
}
