package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogCompilationSuccess extends JDialog
{
    private JButton ok;

    public DialogCompilationSuccess(String loc)
    {
        super(Main.frame, "Compilation finisehd!");
        Log.i("Opening Dialog DialogCompilationSuccess");
        JLabel label = new JLabel("<html><body>Compilation finished successfuly! Project was saved at: <br>" + loc + "</body></html>");
        ok = new JButton("Ok");
        JPanel host = new JPanel();
        this.add(host);

        GroupLayout layout = new GroupLayout(host);
        host.setLayout(layout);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addComponent(ok));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label)
                .addComponent(ok));

        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(400, 200));
        setupActions();
        setVisible(true);
    }

    private void setupActions() { ok.addActionListener(e -> dispose()); }
}
