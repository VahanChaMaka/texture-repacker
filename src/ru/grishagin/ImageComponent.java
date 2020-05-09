package ru.grishagin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static java.awt.Image.SCALE_SMOOTH;

public class ImageComponent extends JComponent {

    private Grid originalGrid = new Grid(new Vector2<>(16, 16));
    private Grid newGrid = new Grid(new Vector2<>(20, 20));

    private static final long serialVersionUID = 1L;
    private BufferedImage image;

    private float zoom = 1;

    public ImageComponent(){
        originalGrid.setVisible(true);

        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            if(rotation > 0 && zoom >= 8 || rotation < 0 && zoom < 0.1) {
                return;
            } else if(zoom <= 1 && rotation < 0){
                zoom = zoom / (rotation - 1) * -1;
            } else {
                zoom = (int)(zoom + rotation);
            }
            this.repaint();
        });
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.repaint();
    }

    public Vector2<Integer> getOriginalGridSize() {
        return originalGrid.getSize();
    }

    public Vector2<Integer> getNewGridSize() {
        return newGrid.getSize();
    }

    public Grid getOriginalGrid() {
        return originalGrid;
    }

    public Grid getNewGrid() {
        return newGrid;
    }

    @Override
    public void paintComponent (Graphics g){
        if(image == null) return;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int displayWidth = (int)(imageWidth * zoom);
        int displayHeight = (int)(imageHeight * zoom);

        //BufferedImage imageToDraw = image.getSubimage(0, 0, displayWidth, displayHeight);
        BufferedImage imageToDraw = new BufferedImage(displayWidth, displayHeight, image.getType());
        imageToDraw.getGraphics().drawImage(image, 0, 0, displayWidth, displayHeight, this);

        g.drawImage(imageToDraw, 0, 0, displayWidth, displayHeight, this);

        //original grid
        if(originalGrid.isVisible()) {
            drawGrid(g, imageToDraw, originalGrid.getSize(), new Color(0, 0, 0, 0.5f));
        }

        //new grid
        if(newGrid.isVisible()) {
            drawGrid(g, imageToDraw, newGrid.getSize(), new Color(1, 1, 0, 0.5f));
        }
    }

    private void drawGrid(Graphics g, BufferedImage imageToDraw, Vector2<Integer> gridSize, Color color){
        g.setColor(color);
        for (int i = 0; i <= imageToDraw.getWidth(); i += gridSize.x * zoom) {
            g.drawLine(i, 0, i, imageToDraw.getHeight() - (imageToDraw.getHeight() % gridSize.y));
        }
        for (int j = 0; j <= imageToDraw.getHeight(); j += gridSize.y * zoom) {
            g.drawLine(0, j, imageToDraw.getWidth() - (imageToDraw.getWidth() % gridSize.x), j);
        }
    }
}