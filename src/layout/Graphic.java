package layout;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Point;

import static sample.Main.MAIN_FORM_HEIGHT;
import static sample.Main.MAIN_FORM_WIDTH;

public class Graphic {
    private Group group;

    private Canvas canvas;
    private GraphicsContext graphic;


    public Graphic() {
        canvas = new Canvas(MAIN_FORM_WIDTH, 2 * MAIN_FORM_HEIGHT / 3);
        graphic = canvas.getGraphicsContext2D();

        group = new Group();
        group.getChildren().add(canvas);
    }

    public Group getGroup() {
        return group;
    }


    // Drawing
    public void drawPoint(Point point) {
        graphic.fillOval(point.getX() * 2, point.getY() * 500, 2, 2);
    }

    public void setDrawingColor(Color color) {
        graphic.setFill(color);
    }

    public void clear() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

}
