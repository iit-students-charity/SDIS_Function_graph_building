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


public class GraphicCanvas {
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 1.5;
    private static final double SCALING_STEP = 0.1;
    private static final double SCROLL_PANE_CENTER_POSITION = 0.5;
    private final double MARK_SPACING = 20;

    private double prevScale;
    private double scale;
    private double canvasSize;
    private boolean hasToClear;

    private ScrollPane scrollPane;
    private Canvas canvas;
    private GraphicsContext graphic;

    private ObservableList<Function> functions;
    private ObservableList<Point> prevPoints;
    private ObservableList<Color> colors;
    private ObservableList<Integer> functionPointsIterators;


    public GraphicCanvas(ObservableList<Function> functions) {
        scale = MIN_SCALE;
        prevScale = scale;
        hasToClear = false;

        canvas = new Canvas();
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
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public double getScale() {
        return scale;
    }

    public Color getArrayFunColor() {
        return colors.get(0);
    }

    public Color getLinearFunColor() {
        return colors.get(1);
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
        canvasSize = (Math.max(Math.abs(Function.MIN_X_DOWN_LIMIT), Function.MAX_X_UP_LIMIT)) * scale;
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
    }

    public void update() {
        if (hasToClear) {
            clear();
            hasToClear = false;
        }

        if (scale != prevScale) {
            updateCanvasConfig();
            eraseCanvas();

            scrollPane.setVvalue(SCROLL_PANE_CENTER_POSITION);
            scrollPane.setHvalue(SCROLL_PANE_CENTER_POSITION);

            prevScale = scale;
        }

        double functionLineWidth = 1;
        double halfCanvasSize = canvasSize / 2;
        graphic.setLineWidth(functionLineWidth);

        Point nextPoint;

        for (int funIter = 0; funIter < functions.size(); funIter++) {
            if (functionPointsIterators.get(funIter) < functions.get(funIter).getPoints().size()) {
                graphic.setStroke(colors.get(funIter));

                try {
                    nextPoint = new Point(
                            functions.get(funIter).getPoints().get(functionPointsIterators.get(funIter)).getX() *
                                    scale + halfCanvasSize,
                            -functions.get(funIter).getPoints().get(functionPointsIterators.get(funIter)).getY() *
                                    scale + halfCanvasSize
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
            }
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

        double coorMarkHalfLength = 5;
        int whereToWriteMarkText = 2;

        for (double eachCoorMark = 0; eachCoorMark <= canvasSize / 2; eachCoorMark += MARK_SPACING) {
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
        if (scale < MAX_SCALE) {
            scale = new BigDecimal(scale + SCALING_STEP).setScale(1, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public void decrementScale() {
        if (scale > MIN_SCALE) {
            scale = new BigDecimal(scale - SCALING_STEP).setScale(1, RoundingMode.HALF_UP).doubleValue();
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

        // To avoid axes overlaying
        graphic.setFill(Color.WHITE);
        graphic.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        updateCoordinateAxes();
    }


    public double getSingleScaleSegment() {
        return MARK_SPACING / scale;
    }
}
