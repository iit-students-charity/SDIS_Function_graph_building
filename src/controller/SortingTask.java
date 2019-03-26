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

        for (int i = (int) function.getXDownLimit(); i <= function.getXUpLimit(); i++) {
                double summarySortingTime = 0;

                for (int j = 0; j < numberOfLists; j++) {
                    ObservableList<Double> doubles = ListGenerator.generate(i);

                    double startTime = System.nanoTime();
                    sort(doubles);
                    summarySortingTime += System.nanoTime() - startTime;
                }
                double averageSortingTime = (summarySortingTime / numberOfLists) / mega;
                Point point = new Point(i, averageSortingTime);

            synchronized (function) {
                function.getPoints().add(point);
                try {
                    function.notifyAll();
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
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
