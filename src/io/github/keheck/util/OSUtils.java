package io.github.keheck.util;

import java.io.File;

public class OSUtils
{
    private static OS os;
    static File dataDir;
    static File mcDir;

    public static void init()
    {
        os = initOs();
        initDataDirs();
    }

    private static void initDataDirs()
    {
        String userHome = System.getProperty("user.home", ".");

        switch(os)
        {
            case MAC:
                dataDir = new File(userHome, "Library/Application Support/" + Constants.DIRECTORY);
                mcDir = new File(userHome, "Library/Application Support/minecraft");
                break;
            case WINDOWS:
                String appData = System.getenv("APPDATA");
                if(appData != null)
                {
                    dataDir = new File(appData, "." + Constants.DIRECTORY);
                    mcDir = new File(appData, ".minecraft");
                }
                else
                {
                    dataDir = new File(userHome, "." + Constants.DIRECTORY);
                    mcDir = new File(userHome, ".minecraft");
                }
                break;
            case LINUX:
            case SOLARIS:
                dataDir = new File(userHome, "." + Constants.DIRECTORY);
                mcDir = new File(userHome, ".minecraft");
                break;
            case UNKNOWN:
            default:
                dataDir = new File(userHome, "." + Constants.DIRECTORY);
                mcDir = new File(userHome, ".minecraft");
                break;
        }

        if(!dataDir.exists())
            dataDir.mkdirs();
    }

    private static OS initOs()
    {
        String os = System.getProperty("os.name").toLowerCase();

        if(os.contains("win"))
            return OS.WINDOWS;
        if(os.contains("mac"))
            return OS.MAC;
        if(os.contains("sunos") || os.contains("solaris"))
            return OS.SOLARIS;
        if(os.contains("linux") || os.contains("unix"))
            return OS.LINUX;

        return OS.UNKNOWN;
    }

    private enum OS
    {
        LINUX, WINDOWS, SOLARIS, MAC, UNKNOWN;

        public boolean isLinux() { return this == LINUX || this == SOLARIS; }

        public boolean isWin() { return this == WINDOWS; }

        public boolean isMac() { return this == MAC; }
    }
}
