package io.github.keheck.tree;

import io.github.keheck.Main;
import io.github.keheck.window.EditPanel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NavTreeSelectionListener implements TreeSelectionListener
{
    private static AbstractNavTreeNode lastFileSelected;

    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
        //TEMPORÄRE LÖSUNG
        AbstractNavTreeNode node = (AbstractNavTreeNode)Main.navTree.getLastSelectedPathComponent();

        if(lastFileSelected instanceof NavTreeFile)
        {
            String lineBreak = "\n";
            String text = Main.editArea.getText();
            text = String.join("    ", text.split("\t"));
            String[] lines = text.split(lineBreak);
            ArrayList<String> linesList = new ArrayList<>(Arrays.asList(lines));
            Main.texts.put((NavTreeFile)lastFileSelected, linesList);
        }

        if(node != null && node.getType().isLeaf())
        {
            ArrayList<String> lines = Main.texts.get(node);
            String text = String.join("\n", lines.toArray(new String[0]));
            Main.editArea = new JTextArea(text);
            Font mono = new Font("Monospaced", Font.PLAIN, 12);
            Main.editArea.setFont(mono);
            Main.editArea.setTabSize(5);
            EditPanel.setupActions(Main.editArea);
            Main.editContainer.setViewportView(Main.editArea);
            lastFileSelected = node;
        }
        else
        {
            Main.editArea = new JTextArea("");
            Main.editArea.setEnabled(false);
            Main.editContainer.setViewportView(Main.editArea);
            lastFileSelected = null;
        }
    }
}
