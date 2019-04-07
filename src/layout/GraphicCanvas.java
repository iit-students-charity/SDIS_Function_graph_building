package layout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Function;
import model.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GraphicCanvas {
    private static final double MIN_SCALE = 0.7;
    private static final double MAX_SCALE = 3.0;
    private static final double SCALING_STEP = 0.1;
    private static final double MAX_CANVAS_SIZE_IN_NATIVE_SCALE =
            2 * (Math.max(Math.abs(Function.MIN_X_DOWN_LIMIT), Math.abs(Function.MAX_X_UP_LIMIT)));
    private static final double MAX_CANVAS_SIZE = 6000;

    private static final double SCROLL_PANE_CENTER_POSITION = 0.5;
    private static final int SCALING_ROUNDING = 1;
    private final double MARK_SPACING = 20;

    private double prevScale;
    private double scale;
    private double canvasSize;
    private boolean hasToClear;
    private Point nextPoint;
    private double maxX;
    private double minY;

    private ScrollPane scrollPane;
    private Canvas canvas;
    private GraphicsContext graphic;

    private ObservableList<Function> functions;
    private ObservableList<Point> prevPoints;
    private ObservableList<Color> colors;
    private ObservableList<Integer> functionPointsIterators;

    private Lock lock;


    public GraphicCanvas(ObservableList<Function> functions) {
        scale = MIN_SCALE;
        prevScale = scale;
        hasToClear = false;
        nextPoint = new Point(0,0);
        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        canvas = new Canvas();
        canvasSize = (MAX_CANVAS_SIZE_IN_NATIVE_SCALE / 5) * scale;
        updateCanvasConfig();

        graphic = canvas.getGraphicsContext2D();
        eraseCanvas();

        scrollPane = new ScrollPane();
        initScrollPaneConfig();

        this.functions = functions;
        prevPoints = FXCollections.observableArrayList();
        colors = FXCollections.observableArrayList();
        functionPointsIterators = FXCollections.observableArrayList();
        Random forColors = new Random(System.currentTimeMillis());


        for (int funIter = 0; funIter < functions.size(); funIter++) {
            prevPoints.add(null);
            colors.add(Color.color(forColors.nextDouble(), forColors.nextDouble(), forColors.nextDouble()));
            functionPointsIterators.add(0);
        }

        lock = new ReentrantLock();
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public double getScale() {
        return scale;
    }

    public ObservableList<Color> getColors() {
        return colors;
    }

    public ObservableList<Function> getFunctions() {
        return functions;
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
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
    }

    public void update() {
        if (hasToClear) {
            clear();
            hasToClear = false;
        }

        if (scale != prevScale && canvasSize < MAX_CANVAS_SIZE) {
            canvasSize *= scale / prevScale;
            updateCanvasConfig();
            eraseCanvas();

            scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
            scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

            prevScale = scale;
        }

        if ((maxX >= 0.85 * canvasSize) || (minY <= 0.15 * canvasSize)) {
            lock.lock();
            try {
                if (canvasSize < MAX_CANVAS_SIZE) { // to avoid bufferOverflowException
                    canvasSize += 0.15 * canvasSize * scale;

                    updateCanvasConfig();
                    eraseCanvas();

                    scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
                    scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);
                }
            } finally {
                lock.unlock();
            }
        }

        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);


        for (int funIter = 0; funIter < functions.size(); funIter++) {
            if (functionPointsIterators.get(funIter) < functions.get(funIter).getPoints().size()) {
                graphic.setStroke(colors.get(funIter));

                try {
                    nextPoint = new Point(
                            functions.get(funIter).getPoints()
                                    .get(functionPointsIterators
                                    .get(funIter)).getX() * scale + halfCanvasSize,
                            -functions.get(funIter).getPoints()
                                    .get(functionPointsIterators.get(funIter))
                                    .getY() * scale + halfCanvasSize
                    );
                } catch (IndexOutOfBoundsException ex) {
                    continue;
                }

                if (prevPoints.get(funIter) == null) {
                    prevPoints.set(funIter, nextPoint);
                }

                graphic.strokeLine(
                        prevPoints.get(funIter).getX(), prevPoints.get(funIter).getY(),
                        nextPoint.getX(), nextPoint.getY()
                );

                prevPoints.set(funIter, nextPoint);
                functionPointsIterators.set(funIter, functionPointsIterators.get(funIter) + 1);

                if (nextPoint.getX() > maxX) {
                    maxX = nextPoint.getX();
                }
                if (nextPoint.getY() < minY) {
                    minY = nextPoint.getY();
                }
            }
        }
    }

    private void updateCoordinateAxes() {
        double markLineWidth = 0.5;
        double markTextSize = 9;

        graphic.setStroke(Color.GRAY);
        graphic.setLineWidth(markLineWidth);
        graphic.setFont(Font.font(markTextSize));

        double halfCanvasSize = canvasSize / 2;

        graphic.strokeLine(halfCanvasSize, 0, halfCanvasSize, canvasSize); // 'y' axis base
        graphic.strokeLine(0, halfCanvasSize, canvasSize, halfCanvasSize); // 'x' axis base
        // 'x' axis "arrow" and "arrow"'s text
        graphic.strokeLine(canvasSize - 10, halfCanvasSize - 4, canvasSize, halfCanvasSize);
        graphic.strokeLine(canvasSize - 10, halfCanvasSize + 4, canvasSize, halfCanvasSize);
        graphic.strokeText("x", canvasSize - 10, halfCanvasSize - 10);
        // 'y' axis "arrow" and "arrow"'s text
        graphic.strokeLine(halfCanvasSize - 4,10, halfCanvasSize, 0);
        graphic.strokeLine(halfCanvasSize + 4,10, halfCanvasSize, 0);
        graphic.strokeText("y", halfCanvasSize - 15, 10);


        double coorMarkHalfLength = 5;
        int whereToWriteMarkText = 2;

        for (double eachCoorMark = 0; eachCoorMark < canvasSize / 2 - MARK_SPACING; eachCoorMark += MARK_SPACING) {
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

            if ((eachCoorMark % (MARK_SPACING * whereToWriteMarkText)) == 0) {
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
        try {
            if (lock.tryLock()) {
                if (scale < MAX_SCALE) {
                    scale = new BigDecimal(scale + SCALING_STEP)
                            .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
                }
            }
        } finally {
             lock.unlock();
        }
    }

    public void decrementScale() {
        try {
            if (lock.tryLock()) {
                if (scale > MIN_SCALE) {
                    scale = new BigDecimal(scale - SCALING_STEP)
                            .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void eraseCanvas() {
        hasToClear = true;
    }

    private void clear() {
        for (int funIter = 0; funIter < prevPoints.size(); funIter++) {
            prevPoints.set(funIter, null);
            functionPointsIterators.set(funIter, 0);
        }

        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        // To avoid axes overlaying
        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();
    }


    public double getSingleScaleSegment() {
        return MARK_SPACING / scale;
    }
}
