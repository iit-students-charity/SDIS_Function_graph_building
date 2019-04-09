package controller;

import javafx.collections.ObservableList;
import model.Function;
import model.Point;
import model.Tree;


public class SortingTask implements Runnable {
    private final Function arrayFunction;
    private final int calcTaskNumber;
    private int numberOfLists;


    public SortingTask(Function arrayFunction, int numberOfLists, int calcTaskNumber) {
        this.arrayFunction = arrayFunction;
        this.calcTaskNumber = calcTaskNumber;
        this.numberOfLists = numberOfLists;
    }


    @Override
    public void run() {
        double mega = 100;
        int sleepTime = 70;

        double averageSortingTime;
        double summarySortingTime;
        double upLimit = arrayFunction.getXUpLimit();

        for (int arrSize = (int) arrayFunction.getXDownLimit(); arrSize <= upLimit; arrSize++) {
            summarySortingTime = 0;

            for (int numbOfArrayToCheck = 0; numbOfArrayToCheck < numberOfLists; numbOfArrayToCheck++) {
                summarySortingTime += sort(ListGenerator.generate(arrSize));
            }
            averageSortingTime = (summarySortingTime / numberOfLists) / mega;

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
