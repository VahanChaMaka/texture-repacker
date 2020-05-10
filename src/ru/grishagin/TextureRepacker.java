package ru.grishagin;

import ru.grishagin.common.Grid;
import ru.grishagin.ui.ImageComponent;
import ru.grishagin.ui.InfoComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.event.ItemEvent.SELECTED;

public class TextureRepacker extends JFrame {

    private static final String OUTPUT_DIR_NAME = "processed";

    private BufferedImage originalImage;
    private File originalImageFile;

    private ImageComponent imageComponent;
    private InfoComponent infoComponent;

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

        this.setBounds(100,100,500,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        infoComponent = new InfoComponent();//create now, add later in toolbar
        imageComponent = new ImageComponent(infoComponent);
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1.0;
        container.add(imageComponent, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0;
        c.weightx = 1.0;
        container.add(createToolbar(), c);
    }

    private Container createToolbar(){
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(0,0,0,8);

        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        container.add(infoComponent, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        container.add(getGridChanger(imageComponent.getOriginalGrid(), "Original grid"), c);

        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        container.add(getGridChanger(imageComponent.getNewGrid(), "New grid"), c);

        //open save buttons
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        JButton openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            fileChooser.setDialogTitle("Open image");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                openImage(file);
            }

        });
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 1;
        container.add(openButton, c);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                BufferedImage processedImage = Repacker.repack(originalImage, imageComponent.getOriginalGridSize(), imageComponent.getNewGridSize());

                File processedDir = new File(originalImageFile.getParent() + File.separatorChar + OUTPUT_DIR_NAME);
                processedDir.mkdir();
                ImageIO.write(processedImage, "png",
                        new File(processedDir.getPath() + File.separatorChar + originalImageFile.getName()));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        c.gridx = 3;
        c.gridy = 1;
        c.anchor = GridBagConstraints.SOUTH;
        container.add(saveButton, c);

        return container;
    }

    private Container getGridChanger(Grid grid, String title){
        Container container = new Container();
        container.setBackground(Color.BLUE);
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
        c.gridx = 1;
        c.gridy = 1;
        container.add(originalXSpinner, c);

        JLabel originalYLabel = new JLabel("y");
        c.gridx = 0;
        c.gridy = 2;
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
            originalImageFile = file;
            originalImage = ImageIO.read(file);
            imageComponent.setImage(originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
