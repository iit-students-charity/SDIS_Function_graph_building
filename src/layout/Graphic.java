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
    private static final double MIN_SCALE = 0.2; // no more dec cos of centring and white filling in scroll pane off
    private static final double MAX_SCALE = 2.8;
    private static final double SCALING_STEP = 0.2;
    private static final double SCROLL_PANE_CENTER_POSITION = 0.5;

    private double prevScale;
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
        scale = MIN_SCALE;
        prevScale = scale;

        canvas = new Canvas();
        updateCanvasConfig();

        graphic = canvas.getGraphicsContext2D();
        clear();

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

    public double getScale() {
        return scale;
    }


    // Components config' initialization
    private void initScrollPaneConfig() {
        scrollPane.setPannable(true);
        scrollPane.setContent(canvas);
        scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
        scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);
    }


    // Drawing and updating
    private void updateCanvasConfig() {
        canvasSize = (Function.MAX_X_UP_LIMIT - Function.MIN_X_DOWN_LIMIT + Function.MAX_X_UP_LIMIT / 8) * scale;
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
    }

    public void update() {
        if (scale != prevScale) {
            updateCanvasConfig();
            clear();

            scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
            scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

            prevScale = scale;
        }

        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        if (arrayFunPointsIter < arrayFunPoints.size()) {
            graphic.setStroke(Color.RED);

            Point nextPoint = new Point(
                    arrayFunPoints.get(arrayFunPointsIter).getX() * scale + halfCanvasSize,
                    -arrayFunPoints.get(arrayFunPointsIter).getY() * scale + halfCanvasSize
            );

            if (arrayFunPrevPoint == null) {
                arrayFunPrevPoint = nextPoint;
            }

            graphic.strokeLine(arrayFunPrevPoint.getX(), arrayFunPrevPoint.getY(), nextPoint.getX(), nextPoint.getY());

            arrayFunPrevPoint = nextPoint;
            arrayFunPointsIter++;
        }

        if (linearFunPointsIter < linearFunPoints.size()) {
            graphic.setStroke(Color.GREEN);

            Point nextPoint = new Point(
                    linearFunPoints.get(linearFunPointsIter).getX() * scale + halfCanvasSize,
                    -linearFunPoints.get(linearFunPointsIter).getY() * scale + halfCanvasSize
            );

            if (linearFunPrevPoint == null) {
                linearFunPrevPoint = nextPoint;
            }

            graphic.strokeLine(linearFunPrevPoint.getX(), linearFunPrevPoint.getY(), nextPoint.getX(), nextPoint.getY());

            linearFunPrevPoint = nextPoint;
            linearFunPointsIter++;
        }
    }

    private void updateCoordinateAxes() {
        double markLineWidth = 0.5;
        double markTextSize = 8;

        graphic.setStroke(Color.GRAY);
        graphic.setLineWidth(markLineWidth);
        graphic.setFont(Font.font(markTextSize));

        double halfCanvasSize = canvasSize / 2;

        graphic.strokeLine(halfCanvasSize, 0, halfCanvasSize, canvasSize); // 'y' axis base
        graphic.strokeLine(0, halfCanvasSize, canvasSize, halfCanvasSize); // 'x' axis base

        double coorMarkSpacing = 20;
        double coorMarkHalfLength = 5;
        int whereToWriteMarkText = 2;

        for (double eachCoorMark = 0; eachCoorMark <= canvasSize / 2; eachCoorMark += coorMarkSpacing) {
            if (eachCoorMark == 0) {
                continue;
            }

            graphic.strokeLine(
                    eachCoorMark + halfCanvasSize, halfCanvasSize - coorMarkHalfLength,
                    eachCoorMark + halfCanvasSize, halfCanvasSize + coorMarkHalfLength
            ); // 'x' axis positives

            graphic.strokeLine(
                    halfCanvasSize - coorMarkHalfLength, halfCanvasSize - eachCoorMark,
                    halfCanvasSize + coorMarkHalfLength, halfCanvasSize - eachCoorMark
            ); // 'y' axis positives

            graphic.strokeLine(
                    halfCanvasSize - eachCoorMark, halfCanvasSize - coorMarkHalfLength,
                    halfCanvasSize - eachCoorMark, halfCanvasSize + coorMarkHalfLength
            ); // 'x' axis negatives

            graphic.strokeLine(
                    halfCanvasSize - coorMarkHalfLength, halfCanvasSize + eachCoorMark,
                    halfCanvasSize + coorMarkHalfLength, halfCanvasSize + eachCoorMark
            ); // 'y' axis negatives

            if ((eachCoorMark % (coorMarkSpacing * whereToWriteMarkText)) == 0) {
                // 'x' axis positives mark
                graphic.strokeText(String.valueOf((int) (eachCoorMark / scale)),
                        halfCanvasSize + eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // 'y' axis positives mark
                graphic.strokeText(String.valueOf((int) (eachCoorMark / scale)),
                        halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize - eachCoorMark);
                // 'x' axis negatives mark
                graphic.strokeText(String.valueOf((int) (-eachCoorMark / scale)),
                        halfCanvasSize - eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // 'y' axis negatives mark
                graphic.strokeText(String.valueOf((int) (-eachCoorMark / scale)),
                        halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize + eachCoorMark);
            }
        }

        graphic.strokeText("0",
                halfCanvasSize + coorMarkHalfLength, halfCanvasSize + 2 * coorMarkHalfLength); // coor. center
    }

    public void incrementScale() {
        if (scale >= MAX_SCALE) {
            return;
        }
        scale += SCALING_STEP;
    }

    public void decrementScale() {
        if (scale <= MIN_SCALE) {
            return;
        }
        scale -= SCALING_STEP;
    }

    public void clear() {
        arrayFunPointsIter = 0;
        linearFunPointsIter = 0;
        arrayFunPrevPoint = null;
        linearFunPrevPoint = null;

        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();
    }
}
