package io.github.keheck.util;

import java.io.File;

public class Directories
{
    public static File projectsDir;
    public static File rootDir;
    public static File mcDir;
    public static File logDir;
    public static File crashDir;

    public static void initDirs()
    {
        rootDir = OSUtils.dataDir;
        mcDir = OSUtils.mcDir;

        projectsDir = new File(rootDir, "projects");
        if(!projectsDir.exists()) projectsDir.mkdirs();

        logDir = new File(rootDir, "logs");
        if(!logDir.exists()) logDir.mkdirs();

        crashDir = new File(rootDir, "crash_logs");
        if(!crashDir.exists()) crashDir.mkdirs();
    }
}
