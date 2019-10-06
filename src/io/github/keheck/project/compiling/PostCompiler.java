package io.github.keheck.project.compiling;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.keheck.Main;
import io.github.keheck.tree.NavTreeFile;
import io.github.keheck.tree.NavTreeFolder;
import io.github.keheck.tree.VirtualIfNode;
import io.github.keheck.tree.VirtualUnlessNode;
import io.github.keheck.util.Log;
import io.github.keheck.window.NavTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Set;

import static io.github.keheck.project.compiling.Compiler.*;

class PostCompiler
{
    /**
     * Writes the .mcmeta file
     */
    static void writeMCMeta()
    {
        Log.i("Writing mcmeta file...");
        flushText(NavTree.meta);
    }

    /**
     * Writes all .json files
     */
    static void writeJsons()
    {
        Log.i("Writing all json files...");
        Set<NavTreeFile> jsons = filePaths.keySet();

        for(NavTreeFile file : jsons)
        {
            if(file.getType().getExtension().equals(".json"))
            {
                try
                {
                    Log.f1("Writing json file " + file.getPath());

                    JsonParser parser = new JsonParser();
                    String json = String.join("\n", Main.texts.get(file));
                    parser.parse(json);
                }
                catch(JsonSyntaxException e)
                {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    Log.e("Failed to write " + file.getPath() + ": ", e);
                    errors.add(cause.getMessage());
                    continue;
                }

                flushText(file);
            }
        }
    }

    /**
     * Writes all .mcfunction files
     */
    static void writeMCFunctions()
    {
        Log.i("Flushing functions...");
        Set<NavTreeFile> functions = filePaths.keySet();

        for(NavTreeFile nodeFile : functions)
        {
            if(nodeFile.getType().getExtension().equals(".mcfunction"))
            {
                Log.f1("Writing mcfunction file " + nodeFile.getPath());
                ArrayList<NavTreeFolder> path = filePaths.get(nodeFile);

                if(!presentPaths.contains(path))
                {
                    if(path.size() == 0)
                    {
                        try
                        {
                            File file = new File(root, nodeFile.toString());
                            file.createNewFile();
                            ArrayList<String> lines = mcfunctionCode.get(nodeFile);
                            String text = String.join("\n", lines);
                            Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        File file = new File(root, "data");
                        file = new File(file, path.get(0).getName());

                        for(int i = 1; i < path.size(); i++)
                            file = new File(file, path.get(i).getName());

                        file.mkdirs();
                        presentPaths.add(path);
                        nodePathMap.put(getStringKeyFor(path), file);

                        try
                        {
                            file = new File(file, nodeFile.toString());
                            file.createNewFile();
                            ArrayList<String> lines = mcfunctionCode.get(nodeFile);
                            String text = String.join("\n", lines);
                            Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    try
                    {
                        File file = new File(nodePathMap.get(getStringKeyFor(path)), nodeFile.toString());
                        file.createNewFile();
                        ArrayList<String> lines = mcfunctionCode.get(nodeFile);
                        String text = String.join("\n", lines);
                        Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                    }
                    catch (IOException e)
                    {
                        Log.e("And error occured: ", e);
                        e.printStackTrace();
                    }
                }
            }
        }

        try
        {
            File loadJson = new File(root, "data/minecraft/tags/functions");
            File initFunc = new File(root, "data/" + Main.project + "init/functions");
            loadJson.mkdirs();
            initFunc.mkdirs();
            loadJson = new File(loadJson, "load.json");
            initFunc = new File(initFunc, "comp_init.mcfunction");
            loadJson.createNewFile();
            initFunc.createNewFile();

            FileWriter writer = new FileWriter(loadJson);

            JsonFactory factory = new JsonFactory();
            JsonGenerator gen = factory.createGenerator(writer);
            gen.useDefaultPrettyPrinter();
            gen.writeStartObject();
                gen.writeArrayFieldStart("values");
                    gen.writeString(Main.project + "init:comp_init");
                gen.writeEndArray();
            gen.writeEndObject();
            gen.flush();
            gen.close();

            writer = new FileWriter(initFunc);
            writer.write("scoreboard objectives add vars dummy");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    static void writeVirtuals()
    {
        Log.i("Writing virtual nodes...");
        Set<NavTreeFile> keys = Compiler.compiledVirtualCode.keySet();

        for(NavTreeFile key : keys)
        {
            if(key instanceof VirtualIfNode)
            {
                try
                {
                    Log.f1("Writing virtual if " + key.getPath());
                    File file = new File(root, "data/" + Main.project + "ifs/");
                    file.mkdirs();
                    file = new File(file, key.toString());
                    file.createNewFile();

                    String text = String.join("\n", compiledVirtualCode.get(key));
                    Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if(key instanceof VirtualUnlessNode)
            {
                try
                {
                    Log.f1("Writing virtual unless " + key.getPath());

                    File file = new File(root, "data/" + Main.project + "unlesss/");
                    file.mkdirs();
                    file = new File(file, key.toString());
                    file.createNewFile();

                    String text = String.join("\n", compiledVirtualCode.get(key));
                    Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Flushes the text associated with the given tree node
     * Is only used for the .mcmeta file and the .json
     * files because the text of mcfunction files is simplified
     * and changed before the flushing.
     *
     * @param nodeFile the file whose text is being flushed
     */
    private static void flushText(NavTreeFile nodeFile)
    {
        ArrayList<NavTreeFolder> path = filePaths.get(nodeFile);

        if(!presentPaths.contains(path))
        {
            if(nodeFile.getType().getExtension().equals(".mcmeta"))
            {
                try
                {
                    File file = new File(root, nodeFile.toString());
                    file.createNewFile();
                    ArrayList<String> lines = Main.texts.get(nodeFile);
                    String text = String.join("\n", lines.toArray(new String[0]));
                    Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e)
                {
                    Log.e("An I/O error occured while writing " + nodeFile.getPath() + ":", e);
                    e.printStackTrace();
                }
            }
            else
            {
                File file = new File(root, "data");
                file = new File(file, path.get(0).getName());

                for(int i = 1; i < path.size(); i++)
                    file = new File(file, path.get(i).getName());

                file.mkdirs();
                presentPaths.add(path);
                nodePathMap.put(getStringKeyFor(path), file);

                try
                {
                    file = new File(file, nodeFile.toString());
                    file.createNewFile();
                    ArrayList<String> lines = Main.texts.get(nodeFile);
                    String text = String.join("\n", lines.toArray(new String[0]));
                    Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e)
                {
                    Log.e("An I/O error occured while writing " + nodeFile.getPath() + ":", e);
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                File file = new File(nodePathMap.get(getStringKeyFor(path)), nodeFile.toString());
                file.createNewFile();
                ArrayList<String> lines = Main.texts.get(nodeFile);
                String text = String.join("\n", lines.toArray(new String[0]));
                Files.write(file.toPath(), text.getBytes(), StandardOpenOption.APPEND);
            }
            catch (IOException e)
            {
                Log.e("An I/O error occured while writing " + nodeFile.getPath() + ":", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * builds a key of type {@code String} representing the path
     * relative to the project root to clearly identify a file incase
     * there are two files with the same name in different directories
     *
     * @param path the path of the file
     * @return the {@code String} representing the path
     */
    private static String getStringKeyFor(ArrayList<NavTreeFolder> path)
    {
        StringBuilder builder = new StringBuilder(rootNode.getName());

        if(path.size() != 0)
        {
            builder.append("/");

            for(NavTreeFolder folder : path)
                builder.append(folder);
        }

        return builder.toString();
    }
}
