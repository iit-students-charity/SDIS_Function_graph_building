package layout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.ColoredPoint;
import model.Function;
import model.Point;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Graphic {
    private static final int MARK_SPACING = 20; // 100 %

    private double scale;
    private double canvasSize;

    private Canvas canvas;
    private ScrollPane scrollPane;
    private GraphicsContext graphic;

    private ObservableList<ColoredPoint> coloredPoints;
    private ObservableList<Point> arrayFunPoints;
    private ObservableList<Point> linearFunPoints;
    private int arrayFunPointsIter;
    private int linearFunPointsIter;
    private Point arrayFunPrevPoint;
    private Point linearFunPrevPoint;


    public Graphic(Function arrayFunction, Function linearFunction) {
        scale = MARK_SPACING;

        canvasSize = Function.MAX_X_UP_LIMIT - Function.MIN_X_DOWN_LIMIT;
        canvas = new Canvas();
        initCanvasConfig();

        graphic = canvas.getGraphicsContext2D();
        updateCoordinateAxes();

        scrollPane = new ScrollPane();
        initScrollPaneConfig();

        coloredPoints = FXCollections.observableArrayList();
        arrayFunPoints = arrayFunction.getPoints();
        linearFunPoints = linearFunction.getPoints();
        arrayFunPointsIter = 0;
        linearFunPointsIter = 0;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    private void initScrollPaneConfig() {
        scrollPane.setPannable(true);
        scrollPane.setContent(canvas);
    }

    private void initCanvasConfig() {
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
    }


    // Drawing and updating
    public void update() {
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(1);

        Lock lock = new ReentrantLock();
        lock.lock();

        try {
            if (arrayFunPointsIter < arrayFunPoints.size()) {
                graphic.setStroke(Color.RED);

                Point nextPoint = new Point(
                        arrayFunPoints.get(arrayFunPointsIter).getX() + halfCanvasSize,
                        -arrayFunPoints.get(arrayFunPointsIter).getY() + halfCanvasSize
                );

                if (arrayFunPrevPoint == null) {
                    arrayFunPrevPoint = nextPoint;
                    System.out.println(1);
                }

                graphic.strokeLine(arrayFunPrevPoint.getX(), arrayFunPrevPoint.getY(), nextPoint.getX(), nextPoint.getY());

                arrayFunPrevPoint = nextPoint;
                arrayFunPointsIter++;
            }
        } finally {
            lock.unlock();
        }

        lock.lock();

        try {
            if (linearFunPointsIter < linearFunPoints.size()) {
                graphic.setStroke(Color.GREEN);

                Point nextPoint = new Point(
                        linearFunPoints.get(linearFunPointsIter).getX() + halfCanvasSize,
                        -linearFunPoints.get(linearFunPointsIter).getY() + halfCanvasSize
                );

                if (linearFunPrevPoint == null) {
                    linearFunPrevPoint = nextPoint;
                    System.out.println(1);
                }

                graphic.strokeLine(linearFunPrevPoint.getX(), linearFunPrevPoint.getY(), nextPoint.getX(), nextPoint.getY());

                linearFunPrevPoint = nextPoint;
                linearFunPointsIter++;
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateCoordinateAxes() {
        graphic.setStroke(Color.GRAY);
        graphic.setLineWidth(0.5);
        graphic.setFont(Font.font(8));

        double halfCanvasSize = canvasSize / 2;

        graphic.strokeLine(halfCanvasSize, 0, halfCanvasSize, canvasSize);
        graphic.strokeLine(0, halfCanvasSize, canvasSize, halfCanvasSize);

        double coorMarkStep = scale;
        double coorMarkHalfLength = 5;
        int whereToWriteMarkText = 3;

        for (double eachCoorMark = 0; eachCoorMark <= canvasSize / 2; eachCoorMark += coorMarkStep) {
            graphic.strokeLine(
                    eachCoorMark + halfCanvasSize, halfCanvasSize - coorMarkHalfLength,
                    eachCoorMark + halfCanvasSize, halfCanvasSize + coorMarkHalfLength
            );

            graphic.strokeLine(
                    halfCanvasSize - coorMarkHalfLength, halfCanvasSize - eachCoorMark,
                    halfCanvasSize + coorMarkHalfLength, halfCanvasSize - eachCoorMark
            );

            graphic.strokeLine(
                    halfCanvasSize - eachCoorMark, halfCanvasSize - coorMarkHalfLength,
                    halfCanvasSize - eachCoorMark, halfCanvasSize + coorMarkHalfLength
            );

            graphic.strokeLine(
                    halfCanvasSize - coorMarkHalfLength, halfCanvasSize + eachCoorMark,
                    halfCanvasSize + coorMarkHalfLength, halfCanvasSize + eachCoorMark
            );

            if (eachCoorMark == 0) {
                continue;
            }

            if ((eachCoorMark % (coorMarkStep * whereToWriteMarkText)) == 0) {
                // x+
                graphic.strokeText(String.valueOf((int) eachCoorMark), halfCanvasSize + eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // y+
                graphic.strokeText(String.valueOf((int) eachCoorMark), halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize - eachCoorMark);
                // x-
                graphic.strokeText(String.valueOf((int) -eachCoorMark), halfCanvasSize - eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // y-
                graphic.strokeText(String.valueOf((int) -eachCoorMark), halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize + eachCoorMark);
            }
        }

        graphic.strokeText("0", halfCanvasSize + coorMarkHalfLength, halfCanvasSize + 2 * coorMarkHalfLength);
    }

    public void clear() {linearFunPointsIter = 0;
        arrayFunPointsIter = 0;
        arrayFunPrevPoint = null;
        linearFunPrevPoint = null;

        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();
    }
}
