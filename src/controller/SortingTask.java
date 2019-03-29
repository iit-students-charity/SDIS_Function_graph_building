package controller;

import javafx.collections.ObservableList;
import model.Function;
import model.Point;
import model.Tree;

public class SortingTask implements Runnable {
    private final Function function;
    private int numberOfLists;


    public SortingTask(Function function, int numberOfLists) {
        this.function = function;
        this.numberOfLists = numberOfLists;
    }

    @Override
    public void run() {
        double mega = 1000000;
        int sleepTime = 100;

        for (int i = (int) function.getXDownLimit(); i <= function.getXUpLimit(); i++) {
                double summarySortingTime = 0;

                for (int j = 0; j < numberOfLists; j++) {
                    ObservableList<Double> doubles = ListGenerator.generate(i);

                    double startTime = System.nanoTime();
                    sort(doubles);
                    summarySortingTime += System.nanoTime() - startTime;
                }
                double averageSortingTime = (summarySortingTime / numberOfLists) / mega;

            synchronized (function) {
                function.getPoints().add(new Point(i, averageSortingTime));
                try {
                    function.notifyAll();
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
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
