package io.github.keheck.window;

import io.github.keheck.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EditPanel
{

    public static JComponent[] setupEditTextArea()
    {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(area);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        Font mono = new Font("Monospaced", Font.PLAIN, 12);
        area.setFont(mono);
        area.setTabSize(5);
        area.setEnabled(false);

        return new JComponent[]{scroll, area};
    }

    public static void setupActions(JTextArea area)
    {
        area.addKeyListener(new KeyListener()
        {
            @Override public void keyTyped(KeyEvent e) { }

            @Override public void keyPressed(KeyEvent e) { }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_TAB)
                {
                    int caretPos = Main.editArea.getCaretPosition();
                    Main.editArea.setText(Main.editArea.getText().replaceAll("\t", "    "));
                    try { Main.editArea.setCaretPosition(caretPos+3); }
                    catch (IllegalArgumentException ex) { Main.editArea.setCaretPosition(Main.editArea.getText().length()-1); }
                }
            }
        });
    }
}
