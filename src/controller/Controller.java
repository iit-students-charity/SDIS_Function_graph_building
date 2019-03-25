package controller;

import javafx.collections.ObservableList;
import model.Function;
import model.Point;

public class Controller {
    private Function function;


    public Controller(Function function) {
        this.function = function;
    }

    public void startGraphBuilding(int numberOfLists) {
        double downLimit = function.getXDownLimit();
        double n = function.getXUpLimit();
        double mega = 1000000;

        function.getPoints().clear();

        for (int i = (int) downLimit; i <= n; i++) {
            double summarySortingTime = 0;

            for (int j = 0; j < numberOfLists; j++) {
                ObservableList<Double> doubles = ListGenerator.generate(i);

                double startTime = System.nanoTime();
                ListSorter.sort(doubles);
                summarySortingTime += System.nanoTime() - startTime;
            }
            double averageSortingTime = (summarySortingTime / numberOfLists) / mega;
            function.getPoints().add(new Point(i, averageSortingTime));
        }
    }
}
