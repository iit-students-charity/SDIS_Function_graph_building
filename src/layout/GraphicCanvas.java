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
    private static final int SCALING_ROUNDING = 1;
    private static final double START_CANVAS_SIZE = 600;
    private static final double MARK_SPACING = 20;

    private static final double MAX_CANVAS_SIZE = 4000;
    private static final double X_RESIZING_BORDER = 0.85;
    private static final double Y_RESIZING_BORDER = 0.15;
    private static final double RESIZING_STEP = 0.5;
    private static final double SCROLL_PANE_CENTER_POSITION = 0.5;

    private double currentScale;
    private double newScale;
    private double canvasSize;
    private double singleScaleSegment;

    private boolean hasToRedraw;
    private boolean hasToErase;

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
        newScale = MIN_SCALE;
        currentScale = newScale;
        singleScaleSegment = MARK_SPACING / newScale;

        hasToRedraw = false;
        nextPoint = new Point();
        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        canvas = new Canvas();
        graphic = canvas.getGraphicsContext2D();
        erase();
        updateCanvasConfig();

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
            functionPointsIterators.add(-1);
        }

        lock = new ReentrantLock();
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public double getNewScale() {
        return newScale;
    }

    public double getCurrentScale() {
        return currentScale;
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
        if (hasToErase) {
            eraseFunctionGraphics();
            hasToErase = false;
        }

        if (hasToRedraw) {
            redrawFunctionGraphics();
            hasToRedraw = false;
        }

        if (newScale != currentScale) {
            lock.lock();

            canvasSize *= newScale / currentScale;
            updateCanvasConfig();
            redraw();

            currentScale = newScale;

            scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
            scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

            lock.unlock();
        }

        if ((maxX >= X_RESIZING_BORDER * canvasSize) || (minY <= Y_RESIZING_BORDER * canvasSize)) {
            lock.lock();

            if (canvasSize < MAX_CANVAS_SIZE) { // to avoid bufferOverflowException
                canvasSize += RESIZING_STEP * canvasSize * newScale;

                updateCanvasConfig();
                redraw();

                scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
                scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);
            }

            lock.unlock();
        }

        drawFunctionGraphics();
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
                graphic.strokeText(String.valueOf((int) (eachCoorMark / newScale)),
                        halfCanvasSize + eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // 'y' axis positives mark
                graphic.strokeText(String.valueOf((int) (eachCoorMark / newScale)),
                        halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize - eachCoorMark);
                // 'x' axis negatives mark
                graphic.strokeText(String.valueOf((int) (-eachCoorMark / newScale)),
                        halfCanvasSize - eachCoorMark,halfCanvasSize + 3 * coorMarkHalfLength);
                // 'y' axis negatives mark
                graphic.strokeText(String.valueOf((int) (-eachCoorMark / newScale)),
                        halfCanvasSize + 2 * coorMarkHalfLength, halfCanvasSize + eachCoorMark);
            }
        }

        graphic.strokeText("0",
                halfCanvasSize + coorMarkHalfLength, halfCanvasSize + 2 * coorMarkHalfLength); // coor. center
    }

    public void incrementScale() {
        if (canvasSize > MAX_CANVAS_SIZE) {
            return;
        }

        if (newScale < MAX_SCALE) {
            newScale = new BigDecimal(newScale + SCALING_STEP)
                    .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
        }

        singleScaleSegment = MARK_SPACING / newScale;
    }

    public void decrementScale() {
        if (newScale > MIN_SCALE) {
            newScale = new BigDecimal(newScale - SCALING_STEP)
                    .setScale(SCALING_ROUNDING, RoundingMode.HALF_UP).doubleValue();
        }

        singleScaleSegment = MARK_SPACING / newScale;
    }

    public void redraw() {
        hasToRedraw = true;
    }

    public void erase() {
        hasToErase = true;
    }

    private void redrawFunctionGraphics() {
        lock.lock();

        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        // To avoid axes overlaying
        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();

        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        for (int funIter = 0; funIter < prevPoints.size(); funIter++) {
            prevPoints.set(funIter, null);
        }

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            for (int pointsIter = 0; pointsIter <= functionPointsIterators.get(funIter); pointsIter++) {
                nextPoint = new Point(
                        functions.get(funIter).getPoints()
                                .get(pointsIter).getX() * newScale + halfCanvasSize,
                        -functions.get(funIter).getPoints()
                                .get(pointsIter).getY() * newScale + halfCanvasSize
                );

                if (prevPoints.get(funIter) == null) {
                    prevPoints.set(funIter, nextPoint);
                }

                graphic.setStroke(colors.get(funIter));
                graphic.strokeLine(
                        prevPoints.get(funIter).getX(), prevPoints.get(funIter).getY(),
                        nextPoint.getX(), nextPoint.getY()
                );

                prevPoints.set(funIter, nextPoint);
            }
        }

        lock.unlock();
    }

    private void drawFunctionGraphics() {
        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            if ((functions.get(funIter).getPoints().size() == 0)
                    || (functionPointsIterators.get(funIter) == -1)
                    || ((functions.get(funIter).getPoints().size() - 1) <= functionPointsIterators.get(funIter))) {
                continue;
            }

            nextPoint = new Point(
                    functions.get(funIter).getPoints()
                            .get(functionPointsIterators
                                    .get(funIter)).getX() * newScale + halfCanvasSize,
                    -functions.get(funIter).getPoints()
                            .get(functionPointsIterators.get(funIter))
                            .getY() * newScale + halfCanvasSize
            );

            if (prevPoints.get(funIter) == null) {
                prevPoints.set(funIter, nextPoint);
            }

            graphic.setStroke(colors.get(funIter));
            graphic.strokeLine(
                    prevPoints.get(funIter).getX(), prevPoints.get(funIter).getY(),
                    nextPoint.getX(), nextPoint.getY()
            );

            prevPoints.set(funIter, nextPoint);

            if (nextPoint.getX() > maxX) {
                maxX = nextPoint.getX();
            }
            if (nextPoint.getY() < minY) {
                minY = nextPoint.getY();
            }
        }
    }

    private void eraseFunctionGraphics() {
        lock.lock();

        canvasSize = START_CANVAS_SIZE * newScale;
        updateCanvasConfig();

        for (int funIter = 0; funIter < prevPoints.size(); funIter++) {
            prevPoints.set(funIter, null);
            functionPointsIterators.set(funIter, -1);
        }

        maxX = Function.MIN_X_DOWN_LIMIT;
        minY = Function.MAX_X_UP_LIMIT;

        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();

        lock.unlock();
    }

    public void updateFunctionIterator(Function function) {
        lock.lock();

        functionPointsIterators.set(
                functions.indexOf(function),
                functionPointsIterators.get(functions.indexOf(function)) + 1
        );

        lock.unlock();
    }

    public double getSingleScaleSegment() {
        return singleScaleSegment;
    }
}
