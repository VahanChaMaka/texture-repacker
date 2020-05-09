package ru.grishagin;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.awt.event.ItemEvent.SELECTED;
import static javax.swing.BoxLayout.Y_AXIS;

public class TextureRepacker extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage processedImage;

    private ImageComponent imageComponent;

    public static void main(String[] args) {
        TextureRepacker app = new TextureRepacker();

        if(args != null && args.length > 0){
            String path = args[0];
            app.openImage(new File(path));
        }

        app.setVisible(true);
    }

    public TextureRepacker() throws HeadlessException {
        super("Texture Repacker");

        this.setBounds(100,100,300,400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        imageComponent = new ImageComponent();
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1.0;
        container.add(imageComponent, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0;
        container.add(createToolbar(), c);
    }

    private Container createToolbar(){
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0,0,0,8);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        container.add(getGridChanger(imageComponent.getOriginalGrid(), "Original grid"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        container.add(getGridChanger(imageComponent.getNewGrid(), "New grid"), c);

        //open save buttons
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            fileChooser.setDialogTitle("Open image");
            // Определение режима - только каталог
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            // Если директория выбрана, покажем ее в сообщении
            if (result == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                openImage(file);
            }

        });
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 1;
        container.add(openButton, c);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                processedImage = repack(originalImage, imageComponent.getOriginalGridSize(), imageComponent.getNewGridSize());
                ImageIO.write(processedImage, "png", new File("processed.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.SOUTH;
        container.add(saveButton, c);

        return container;
    }

    private Container getGridChanger(Grid grid, String title){
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JCheckBox showGrid = new JCheckBox();
        showGrid.setSelected(grid.isVisible());
        showGrid.addItemListener(e -> {
            grid.setVisible(e.getStateChange() == SELECTED);
            imageComponent.repaint();
        });
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        container.add(showGrid);

        JLabel originalGridLabel = new JLabel(title);
        c.gridx = 1;
        c.gridy = 0;
        container.add(originalGridLabel, c);

        JLabel originalXLabel = new JLabel("x");
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 1;
        container.add(originalXLabel, c);

        SpinnerNumberModel XModel = new SpinnerNumberModel(grid.getSize().x.intValue(), 1, 300, 1);
        SpinnerNumberModel YModel = new SpinnerNumberModel(grid.getSize().y.intValue(), 1, 300, 1);

        JSpinner originalXSpinner = new JSpinner(XModel);
        originalXSpinner.addChangeListener(e -> {
            grid.getSize().x = XModel.getNumber().intValue();
            grid.getSize().y = YModel.getNumber().intValue();

            imageComponent.repaint();
        });
        c.gridx = 1;       //aligned with button 2
        c.gridy = 1;
        container.add(originalXSpinner, c);

        JLabel originalYLabel = new JLabel("y");
        c.gridx = 0;       //aligned with button 2
        c.gridy = 2;       //third row
        container.add(originalYLabel, c);

        JSpinner originalYSpinner = new JSpinner(YModel);
        originalYSpinner.addChangeListener(e -> {
            grid.getSize().x = XModel.getNumber().intValue();
            grid.getSize().y = YModel.getNumber().intValue();

            imageComponent.repaint();
        });
        c.gridx = 1;
        c.gridy = 2;
        container.add(originalYSpinner, c);

        return container;
    }

    private void openImage(File file){
        try {
            originalImage = ImageIO.read(file);
            imageComponent.setImage(originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage repack(BufferedImage image, Vector2<Integer> originalGrid, Vector2<Integer> newGrid){
        List<BufferedImage> subimages = new LinkedList<>();
        //last fractional cells are ignored
        for (int i = 0; i <= image.getWidth() - originalGrid.x; i += originalGrid.x) {
            for (int j = 0; j <= image.getHeight() - originalGrid.y; j += originalGrid.y) {
                subimages.add(image.getSubimage(i, j, originalGrid.x, originalGrid.y));
            }
        }

        int newImageWidth = (image.getWidth() / originalGrid.x) * newGrid.x;
        int newImageHeight = (image.getHeight() / originalGrid.y) * newGrid.y;

        int gridDifferenceX = newGrid.x - originalGrid.x;
        int gridDifferenceY = newGrid.y - originalGrid.y;
        if(gridDifferenceX < 0 || gridDifferenceY < 0){
            throw new IllegalArgumentException("New grid cannot be smaller than original one!");
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
            graphics.drawImage(subimages.get(i), column*newGrid.x  + gridDifferenceX/2, row*newGrid.y + gridDifferenceY/2, this);
        }

        return newImage;
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
