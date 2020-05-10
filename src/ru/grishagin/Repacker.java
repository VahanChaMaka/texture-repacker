package ru.grishagin;

import ru.grishagin.common.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.LinkedList;
import java.util.List;

public class Repacker {

    public static BufferedImage repack(BufferedImage image, Vector2<Integer> originalGrid, Vector2<Integer> newGrid) {
        List<BufferedImage> subimages = new LinkedList<>();
        //split image by original grid
        //last fractional cells are ignored
        for (int j = 0; j <= image.getHeight() - originalGrid.y; j += originalGrid.y) {
            for (int i = 0; i <= image.getWidth() - originalGrid.x; i += originalGrid.x) {
                subimages.add(image.getSubimage(i, j, originalGrid.x, originalGrid.y));
            }
        }

        int newImageWidth = (image.getWidth() / originalGrid.x) * newGrid.x;
        int newImageHeight = (image.getHeight() / originalGrid.y) * newGrid.y;

        int gridDifferenceX = newGrid.x - originalGrid.x;
        int gridDifferenceY = newGrid.y - originalGrid.y;
        if(gridDifferenceX < 0 || gridDifferenceY < 0){
            throw new IllegalArgumentException("New grid cannot be smaller than the original one!");
        }

        BufferedImage newImage = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_ARGB);
        // Create a graphics which can be used to draw into the buffered image
        Graphics2D graphics = newImage.createGraphics();

        int subimagesInRow = image.getWidth() / originalGrid.x;
        int column = 0;
        int row = -1;
        for (int i = 0; i < subimages.size(); i++) {
            if(i % subimagesInRow == 0){
                row++;
                column = 0;
            } else {
                column++;
            }

            //last lambda argument (observer) does nothing
            graphics.drawImage(subimages.get(i), column * newGrid.x + gridDifferenceX / 2, row * newGrid.y + gridDifferenceY / 2, (img, infoflags, x, y, width, height) -> false);
        }

        return newImage;
    }
}
