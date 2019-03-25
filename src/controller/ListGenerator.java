package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Random;

public class ListGenerator {
    public static ObservableList<Double> generate(int sizeOfList) {
        ObservableList<Double> resultList = FXCollections.observableArrayList();

        for (int listIterator = 0; listIterator < sizeOfList; listIterator++) {
            resultList.add(new Random().nextDouble());
        }

        return resultList;
    }
}
