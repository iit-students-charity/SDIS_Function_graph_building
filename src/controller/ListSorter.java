package controller;

import javafx.collections.ObservableList;
import model.Tree;

public class ListSorter {
    public static void sort(ObservableList<Double> doubles) {
        Tree tree = new Tree(doubles.get(0));

        for (int doublesIter = 1; doublesIter < doubles.size(); doublesIter++) {
            tree.insert(new Tree(doubles.get(doublesIter)));
        }

        doubles.clear();
        doubles.addAll(tree.toList());
    }
}
