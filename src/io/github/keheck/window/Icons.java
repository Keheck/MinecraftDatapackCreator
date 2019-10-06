package io.github.keheck.window;

import io.github.keheck.util.FileUtils;
import io.github.keheck.util.Log;
import io.github.keheck.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Icons
{
    public static ImageIcon MISSING_ICON;

    public static ImageIcon FILE_MCFUNC;
    public static ImageIcon FILE_JSON;
    public static ImageIcon FILE_NAMESPACE;
    public static ImageIcon FILE_NEWFILE;
    public static ImageIcon FILE_REMOVEFILE;
    public static ImageIcon FILE_FOLDER;
    public static ImageIcon FILE_METADATA;
    public static ImageIcon FILE_UNKNOWN;

    public static ImageIcon PROJECT_EXPORT;
    public static ImageIcon PROJECT_NEW;
    public static ImageIcon PROJECT_SAVE;
    public static ImageIcon PROJECT_LOAD;

    public static ImageIcon TREE_EXPAND;
    public static ImageIcon TREE_COLLAPSE;

    public static void init()
    {
        MISSING_ICON = loadIcon("missing_icon.png");

        FILE_MCFUNC = loadIcon("files/mcfunction.png");
        FILE_JSON = loadIcon("files/json.png");
        FILE_NAMESPACE = loadIcon("files/namespace.png");
        FILE_NEWFILE = loadIcon("files/add_file.png");
        FILE_REMOVEFILE = loadIcon("files/remove_file.png");
        FILE_FOLDER = loadIcon("files/folder.png");
        FILE_METADATA = loadIcon("files/metadata.png");
        FILE_UNKNOWN = loadIcon("files/unknown_file.png");

        PROJECT_EXPORT = loadIcon("project/export.png");
        PROJECT_NEW = loadIcon("project/new_project.png");
        PROJECT_SAVE = loadIcon("project/save_project.png");
        PROJECT_LOAD = loadIcon("project/load_project.png");

        TREE_EXPAND = loadIcon("tree/expand.png");
        TREE_COLLAPSE = loadIcon("tree/collapse.png");
    }

    private static ImageIcon loadIcon(String path)
    {
        Log.i("Loading image " + path);
        String imagePath = "/icons/";
        BufferedImage image;
        InputStream stream = null;

        try
        {
            stream = FileUtils.getResource(imagePath + path);
            image = ImageIO.read(stream);
            return new ImageIcon(image);
        }
        catch (IOException e)
        {
            Log.e("Failed to load: " + imagePath + path, e);
        }
        finally
        {
            Util.safeClose(stream);
        }

        return null;
    }
}
