package ru.grishagin;

import ru.grishagin.common.Grid;
import ru.grishagin.ui.ImageComponent;
import ru.grishagin.ui.InfoComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.event.ItemEvent.SELECTED;

public class TextureRepacker extends JFrame {

    private static final String OUTPUT_DIR_NAME = "processed";

    private BufferedImage originalImage;
    private File originalImageFile;

    private ImageComponent imageComponent;
    private InfoComponent infoComponent;

    private List<Integer> skipX = List.of();
    private List<Integer> skipY = List.of();

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

        this.setBounds(100,100,800,600);
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

        //skip rows/columns
        Container skipBlock = getSkip();
        c.gridx = 3;
        c.gridy = 0;
        container.add(skipBlock, c);

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
        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 1;
        container.add(openButton, c);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                BufferedImage processedImage = Repacker.repack(originalImage,
                        imageComponent.getOriginalGridSize(), imageComponent.getNewGridSize(),
                        skipX, skipY);

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
        c.gridx = 4;
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

    private Container getSkip() {
        Container container = new Container();
        container.setBackground(Color.BLUE);
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 2, 3);

        JLabel originalGridLabel = new JLabel("Skip");
        c.gridx = 1;
        c.gridy = 0;
        container.add(originalGridLabel, c);

        //x
        JLabel originalXLabel = new JLabel("x");
        c.gridx = 0;
        c.gridy = 1;
        container.add(originalXLabel, c);

        JTextField skipXField = new JTextField("", 5);
        skipXField.setToolTipText("Columns to skip. Numbers separated by comma");
        skipXField.getDocument()
                .addDocumentListener(getSkipListener(() ->
                        skipX = getNumbers(skipXField.getText())
                        .stream()
                        .filter(v -> imageComponent.getOriginalGrid().getSize().x * v <= originalImage.getWidth())
                        .collect(Collectors.toList())));
        c.gridx = 1;
        c.gridy = 1;
        container.add(skipXField, c);

        //y
        JLabel originalYLabel = new JLabel("y");
        c.gridx = 0;
        c.gridy = 2;
        container.add(originalYLabel, c);

        JTextField skipYField = new JTextField("", 5);
        skipYField.setToolTipText("Rows to skip. Numbers separated by comma");
        skipYField.getDocument()
                .addDocumentListener(getSkipListener(() ->
                        skipY = getNumbers(skipYField.getText())
                        .stream()
                        .filter(v -> imageComponent.getOriginalGrid().getSize().y * v <= originalImage.getHeight())
                        .collect(Collectors.toList())));
        c.gridx = 1;
        c.gridy = 2;
        container.add(skipYField, c);

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

    private List<Integer> getNumbers(String rawInput){
        try {
            return Arrays.stream(rawInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .filter(v -> v > 0)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null,
                    "Error: Please enter only numbers separated by comma", "Error Message",
                    JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }

    private DocumentListener getSkipListener(Runnable func) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                func.run();
                imageComponent.setSkips(skipX, skipY);
                imageComponent.repaint();
            }
        };
    }
}
