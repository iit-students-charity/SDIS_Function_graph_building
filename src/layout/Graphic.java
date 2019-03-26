package layout;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Point;

import java.util.Random;

public class Graphic {
    private Group group;

    Canvas canvas;
    private GraphicsContext graphic;


    public Graphic() {
        canvas = new Canvas(
                3 * LayoutConstant.MAIN_FORM_WIDTH / 4 - LayoutConstant.GRAPHIC_PADDING,
                LayoutConstant.MAIN_FORM_HEIGHT - LayoutConstant.GRAPHIC_PADDING);
        graphic = canvas.getGraphicsContext2D();
        initGraphicConfig();

        group = new Group();
        group.getChildren().add(canvas);
    }

    private void initGraphicConfig() {
        graphic.setStroke(
                Color.color(
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND),
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND),
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND))
        );
        graphic.setFill(
                Color.color(
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND),
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND),
                        new Random().nextInt(LayoutConstant.GRAPHIC_COLOR_BOUND))
        );
    }

    public Group getGroup() {
        return group;
    }

    // Drawing
    public void addPoint(Point point) {
        graphic.fillOval(point.getX() * 2, point.getY() * 500, 2, 2);
    }

    public void clear() {
        graphic.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

}
