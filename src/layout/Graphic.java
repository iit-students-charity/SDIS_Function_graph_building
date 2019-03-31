package layout;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import model.ColoredPoint;
import model.Function;
import model.Point;

public class Graphic {
    private static final int MIN_SCALE = 2;
    private static final int POINT_SIZE = 2;

    private double scale;
    private double currentCanvasSize;

    private Canvas canvas;
    private ScrollPane scrollPane;
    private GraphicsContext graphic;

    private ObservableList<ColoredPoint> coloredPoints;


    public Graphic() {
        scale = MIN_SCALE;

        currentCanvasSize = Function.MAX_X_UP_LIMIT - Function.MIN_X_DOWN_LIMIT;
        canvas = new Canvas();
        initCanvasConfig();

        graphic = canvas.getGraphicsContext2D();

        scrollPane = new ScrollPane();
        initScrollPaneConfig();

        coloredPoints = FXCollections.observableArrayList();
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    private void initScrollPaneConfig() {

        scrollPane.setPannable(true);
        scrollPane.setContent(canvas);
    }

    private void initCanvasConfig() {
        canvas.setWidth(currentCanvasSize);
        canvas.setHeight(currentCanvasSize);
    }


    // Drawing
    public void drawPoint(Point point) {
        coloredPoints.add(new ColoredPoint(point, (Color) graphic.getFill()));
        graphic.fillOval(point.getX() * scale + currentCanvasSize / 2,
                point.getY() * scale + currentCanvasSize / 2,
                POINT_SIZE, POINT_SIZE
        );
    }

    public void setDrawingColor(Color color) {
        graphic.setFill(color);
    }

    public void clear() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
}
