package ru.grishagin.ui;

import javax.swing.*;
import java.awt.*;

public class InfoComponent extends Container {

    private static final int LABEL_HEIGHT = 15;

    private JLabel zoomValueLabel;
    private JLabel cursorPositionXLabel;
    private JLabel cursorPositionYLabel;

    public InfoComponent() {
        super();

        setPreferredSize(new Dimension(200, 60));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 1, 3, 0);
        c.anchor = GridBagConstraints.WEST;

        JLabel zoomNameLabel = new JLabel("Zoom:");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(zoomNameLabel, c);

        zoomValueLabel = new JLabel();
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 2;
        add(zoomValueLabel, c);

        JLabel cursorLabel = new JLabel("Cursor:");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.insets.bottom = 0;
        add(cursorLabel, c);

        JLabel cursorPositionXNameLabel = new JLabel("x:");
        cursorPositionXNameLabel.setPreferredSize(new Dimension(11, LABEL_HEIGHT));
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        add(cursorPositionXNameLabel, c);

        cursorPositionXLabel = new JLabel("0");
        cursorPositionXLabel.setPreferredSize(new Dimension(30, LABEL_HEIGHT));
        c.gridx = 1;
        c.gridy = 2;
        add(cursorPositionXLabel, c);

        JLabel cursorPositionYNameLabel = new JLabel("y:");
        cursorPositionYNameLabel.setPreferredSize(new Dimension(11, LABEL_HEIGHT));
        c.gridx = 2;
        c.gridy = 2;
        add(cursorPositionYNameLabel, c);

        cursorPositionYLabel = new JLabel("0");
        cursorPositionYLabel.setPreferredSize(new Dimension(30, LABEL_HEIGHT));
        c.gridx = 3;
        c.gridy = 2;
        add(cursorPositionYLabel, c);
    }

    public void updateZoom(float zoomValue){
        String newValue = String.format("%.2f", zoomValue);
        zoomValueLabel.setText(newValue);
    }

    public void updateCursorPosition(int x, int y){
        cursorPositionXLabel.setText(String.valueOf(x));
        cursorPositionYLabel.setText(String.valueOf(y));
    }
}
