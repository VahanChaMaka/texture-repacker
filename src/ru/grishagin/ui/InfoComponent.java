package ru.grishagin.ui;

import javax.swing.*;
import java.awt.*;

public class InfoComponent extends Container {

    private JLabel zoomValueLabel;

    public InfoComponent() {
        super();

        setPreferredSize(new Dimension(100, 30));
        setLayout(new FlowLayout());

        JLabel zoomNameLabel = new JLabel("Zoom:");
        add(zoomNameLabel);

        zoomValueLabel = new JLabel();
        add(zoomValueLabel);
    }

    public void updateZoom(float zoomValue){
        String newValue;
        if(zoomValue < 1){
            int divider = (int)(1/zoomValue);
            newValue = "1/" + divider;
        } else {
            newValue = String.valueOf(zoomValue);
        }
        zoomValueLabel.setText(newValue);
    }
}
