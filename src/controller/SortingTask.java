package controller;

import javafx.collections.ObservableList;
import model.Function;
import model.Point;
import model.Tree;


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

        for (int arrSize = (int) arrayFunction.getXDownLimit(); arrSize <= arrayFunction.getXUpLimit(); arrSize++) {
            double summarySortingTime = 0;

            for (int numbOfArrayToCheck = 0; numbOfArrayToCheck < numberOfLists; numbOfArrayToCheck++) {
                summarySortingTime += sort(ListGenerator.generate(arrSize));
            }
            double averageSortingTime = (summarySortingTime / numberOfLists) / mega;

            arrayFunction.getPoints().add(new Point(arrSize, averageSortingTime));

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        Thread.currentThread().interrupt();
    }

    private double sort(ObservableList<Double> doubles) {
        double startSortingTime = System.nanoTime();
        Tree tree = new Tree(doubles.get(0));

        for (int doublesIter = 1; doublesIter < doubles.size(); doublesIter++) {
            tree.insert(new Tree(doubles.get(doublesIter)));
        }

        double endSortingTime = System.nanoTime() - startSortingTime;

        doubles.clear();
        doubles.addAll(tree.toList());

        return endSortingTime;
    }
}
