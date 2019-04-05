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
        double mega = 100;
        int sleepTime = 70;

        Lock lock = new ReentrantLock();

        for (int arrSize = (int) arrayFunction.getXDownLimit(); arrSize <= arrayFunction.getXUpLimit(); arrSize++) {
            double summarySortingTime = 0;

            for (int numbOfArrayToCheck = 0; numbOfArrayToCheck < numberOfLists; numbOfArrayToCheck++) {
                ObservableList<Double> doubles = ListGenerator.generate(arrSize);

                double startTime = System.nanoTime();
                sort(doubles);
                summarySortingTime += System.nanoTime() - startTime;
            }
            double averageSortingTime = (summarySortingTime / numberOfLists) / mega;

            lock.lock();

            try {
                arrayFunction.getPoints().add(new Point(arrSize, averageSortingTime));

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
