package io.github.keheck.window.dialogs;

import io.github.keheck.Main;
import io.github.keheck.util.Log;

import javax.swing.*;
import java.awt.*;

public class DialogErrorNamespaceReserved extends JDialog
{
    private JButton ok;

    public DialogErrorNamespaceReserved(String name, JDialog dialog)
    {
        super(dialog, "Namaspace Reserved!");

        JPanel panel = new JPanel();
        this.add(panel);

        Log.i("Opening dialog DialogErrorNamespaceReserved");
        ok = new JButton("Ok");
        JLabel label = new JLabel("This name is alreade reserved for compilation namespaces:");
        JPanel textWrapper = new JPanel();
        JLabel area = new JLabel(name);
        textWrapper.setBackground(new Color(255, 255, 255));
        area.setForeground(new Color(0xA40000));
        textWrapper.add(area);

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label)
                .addComponent(textWrapper, GroupLayout.Alignment.CENTER)
                .addComponent(ok)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(label)
                .addGap(10)
                .addComponent(textWrapper, 25, 25, 25)
                .addComponent(ok)
        );

        setMinimumSize(new Dimension(350, 125));
        setResizable(false);
        setLocationRelativeTo(null);
        setupActions();
        setVisible(true);
    }

    private void setupActions() { ok.addActionListener(e -> setVisible(false)); }
}
