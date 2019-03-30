package layout;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Point;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;

public class Graphic {
    private static final int MIN_X_SIZE = 2;
    private static final int MIN_Y_SIZE = 500;
    private static final int MIN_POINT_SIZE = 2;

    private Group group;

    private Canvas canvas;
    private GraphicsContext graphic;


    public Graphic() {
        canvas = new Canvas(MAIN_FORM_WIDTH, MAIN_FORM_HEIGHT);
        graphic = canvas.getGraphicsContext2D();

        group = new Group();
        group.getChildren().add(canvas);
    }

    public Group getGroup() {
        return group;
    }


    // Drawing
    public void drawPoint(Point point) {
        graphic.fillOval(point.getX() * 10, point.getY() * 10, MIN_POINT_SIZE, MIN_POINT_SIZE);
    }

    public void setDrawingColor(Color color) {
        graphic.setFill(color);
    }

    public void clear() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

}
