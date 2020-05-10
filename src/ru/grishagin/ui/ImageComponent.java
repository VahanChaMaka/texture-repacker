package ru.grishagin.ui;

import ru.grishagin.common.Grid;
import ru.grishagin.common.Vector2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class ImageComponent extends JComponent {

    private InfoComponent infoComponent;

    private Grid originalGrid = new Grid(new Vector2<>(16, 16));
    private Grid newGrid = new Grid(new Vector2<>(20, 20));
    private Grid pixelGrid = new Grid(new Vector2<>(1, 1));

    private static final long serialVersionUID = 1L;
    private BufferedImage image;

    private float zoom = 1;

    public ImageComponent(InfoComponent infoComponent){
        this.infoComponent = infoComponent;

        originalGrid.setVisible(true);

        infoComponent.updateZoom(zoom);
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            if(rotation > 0 && zoom >= 8 || rotation < 0 && zoom < 0.1) {
                return;
            } else if(zoom <= 1 && rotation < 0){
                zoom = zoom / (rotation - 1) * -1;
            } else if(rotation > 0 && zoom > 0 && zoom < 1){
                zoom = zoom * (rotation + 1);
            } else {
                zoom = (int)(zoom + rotation);
            }
            infoComponent.updateZoom(zoom);
            this.repaint();
        });

        addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseMoved(MouseEvent e) {
                infoComponent.updateCursorPosition((int)(e.getX()/zoom), (int)(e.getY()/zoom));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                //TODO: move large image
            }
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

    public float getZoom() {
        return zoom;
    }

    @Override
    public void paintComponent (Graphics g){
        if(image == null) return;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int displayWidth = (int)(imageWidth * zoom);
        int displayHeight = (int)(imageHeight * zoom);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, displayWidth - (int)zoom, displayHeight);

        //BufferedImage imageToDraw = image.getSubimage(0, 0, displayWidth, displayHeight);
        BufferedImage imageToDraw = new BufferedImage(displayWidth, displayHeight, image.getType());
        imageToDraw.getGraphics().drawImage(image, 0, 0, displayWidth, displayHeight, this);

        g.drawImage(imageToDraw, 0, 0, displayWidth, displayHeight, this);

        //pixels
        if(zoom > 3) {
            drawGrid(g, imageToDraw, pixelGrid.getSize(), new Color(0, 0, 0, 0.2f));
        }

        //original grid
        if(originalGrid.isVisible()) {
            drawGrid(g, imageToDraw, originalGrid.getSize(), new Color(0, 0, 1, 0.5f));
        }

        //new grid
        if(newGrid.isVisible()) {
            drawGrid(g, imageToDraw, newGrid.getSize(), new Color(1, 1, 0, 0.5f));
        }
    }

    private void drawGrid(Graphics g, BufferedImage imageToDraw, Vector2<Integer> gridSize, Color color){
        //don't draw very small grid to prevent infinite looping
        if(gridSize.x*zoom <= 1 || gridSize.y*zoom <= 1){
            return;
        }

        g.setColor(color);
        for (int i = 0; i <= imageToDraw.getWidth(); i += gridSize.x * zoom) {
            g.drawLine(i, 0, i, imageToDraw.getHeight() - (imageToDraw.getHeight() % (int)(gridSize.y*zoom)));
        }
        for (int j = 0; j <= imageToDraw.getHeight(); j += gridSize.y * zoom) {
            g.drawLine(0, j, imageToDraw.getWidth() - (imageToDraw.getWidth() % (int)(gridSize.x*zoom)), j);
        }
    }
}