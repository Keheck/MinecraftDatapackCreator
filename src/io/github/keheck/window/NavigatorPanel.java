package io.github.keheck.window;

import io.github.keheck.Main;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;

public class NavigatorPanel
{
    public static JPanel setupNavigatorPanel()
    {
        JFrame frame = Main.frame;

        JScrollPane scrollPane = new JScrollPane();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        scrollPane.add(panel);
        scrollPane.setMinimumSize(new Dimension(200, frame.getHeight()));
        scrollPane.setMaximumSize(new Dimension(400, frame.getHeight()));
        panel.setMinimumSize(new Dimension(200, frame.getHeight()));

        return panel;
    }
}
