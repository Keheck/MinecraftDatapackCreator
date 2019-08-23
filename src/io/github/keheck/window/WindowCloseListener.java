package io.github.keheck.window;

import io.github.keheck.util.Log;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowCloseListener implements WindowListener
{
    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e)
    {
        Log.convertLogs();
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        Log.convertLogs();
    }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }
}
