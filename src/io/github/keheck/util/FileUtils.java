package io.github.keheck.util;

import java.io.File;
import java.io.InputStream;

public class FileUtils
{
    public static InputStream getResource(String path) { return FileUtils.class.getResourceAsStream(path); }
}
