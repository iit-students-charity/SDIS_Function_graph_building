package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Function {
    private static final double X_DOWN_LIMIT = -1000;
    private static final double X_UP_LIMIT = 1000;

    private double xDownLimit;
    private double xUpLimit;

    private ObservableList<Point> points;


    public Function(double xDownLimit, double xUpLimit) {
        this.xDownLimit = xDownLimit;
        this.xUpLimit = xUpLimit;

        points = FXCollections.observableArrayList();
    }

    public Function() {
        this(X_DOWN_LIMIT, X_UP_LIMIT);
    }

    public double getXUpLimit() {
        return xUpLimit;
    }

    public void setXUpLimit(double xUpLimit) {
        this.xUpLimit = xUpLimit;
    }

    public double getXDownLimit() {
        return xDownLimit;
    }

    public void setXDownLimit(double xDownLimit) {
        this.xDownLimit = xDownLimit;
    }

    public ObservableList<Point> getPoints() {
        return points;
    }
}
