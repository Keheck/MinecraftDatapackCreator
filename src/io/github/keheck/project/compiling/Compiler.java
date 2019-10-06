package io.github.keheck.project.compiling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.keheck.Main;
import io.github.keheck.Tasks;
import io.github.keheck.project.exceptions.*;
import io.github.keheck.project.saveandload.Save;
import io.github.keheck.tree.*;
import io.github.keheck.util.Log;
import io.github.keheck.window.MainMenu;
import io.github.keheck.window.dialogs.DialogCompilationFail;
import io.github.keheck.window.dialogs.DialogCompilationSuccess;

import java.io.File;
import java.util.*;

import static io.github.keheck.project.compiling.PreCompiler.char1CompArray;
import static io.github.keheck.project.compiling.PreCompiler.char2CompArray;

public class Compiler
{
    static HashMap<NavTreeFile, ArrayList<String>> mcfunctionCode = new HashMap<>();
    static HashMap<NavTreeFile, ArrayList<String>> compiledVirtualCode = new HashMap<>();
    static HashMap<NavTreeFile, ArrayList<NavTreeFolder>> filePaths = new HashMap<>();
    private static HashMap<VirtualIfNode, ArrayList<String>> virtualIfs = new HashMap<>();
    private static HashMap<VirtualUnlessNode, ArrayList<String>> virtualUnlesss = new HashMap<>();
    static ArrayList<ArrayList<NavTreeFolder>> presentPaths = new ArrayList<>();
    static ArrayList<NavTreeFile> skippedFiles = new ArrayList<>();
    static HashMap<String, File> nodePathMap = new HashMap<>();
    static File root;
    static NavTreeFolder rootNode;

    static ArrayList<String> errors = new ArrayList<>();
    static ArrayList<String> warnings = new ArrayList<>();

    static boolean failed = false;

    static Tasks.DialogUpdater thread;

    public static void init(File path)
    {
        thread = (Tasks.DialogUpdater)Tasks.constructDynamicDialog();
        thread.start();

        Log.f1("Initializing Compiler...");
        Save.cleanDirtyFile();
        root = new File(path, Main.project);

        if(root.exists())
        {
            Log.f2("Found an old root node! Deleting to make space...");
            deleteChildren(root);
        }

        root.mkdirs();
        rootNode = (NavTreeFolder)Main.navTree.getModel().getRoot();

        Set<NavTreeFile> keySet = Main.texts.keySet();

        for(NavTreeFile key : keySet)
        {
            ArrayList<NavTreeFolder> fromRootToNode = new ArrayList<>();
            NavTreeFolder parent = (NavTreeFolder)key.getParent();

            while(parent != null && parent != rootNode)
            {
                fromRootToNode.add(parent);
                parent = (NavTreeFolder)parent.getParent();
            }

            Collections.reverse(fromRootToNode);
            filePaths.put(key, fromRootToNode);

            if(key.getType() == AbstractNavTreeNode.NodeType.MCFUNCTION)
                mcfunctionCode.put(key, Main.texts.get(key));
        }

        Log.f1("Compiler initialized!");
        Log.f1("Flushing writing all .json files");
        PostCompiler.writeMCMeta();
        PostCompiler.writeJsons();
        if(MainMenu.commandsOnly.getState()) { PostCompiler.writeMCFunctions(); }
        else
        {
            try
            {
                preCompile();
                compile();
                compileVirtuals();
            }
            catch(CompilationException e)
            {
                if(e.isError())
                    errors.add(e.getMessage());
                else
                    warnings.add(e.getMessage());
            }
        }

        Log.i("Warnings encountered: " + warnings.size());
        Log.i("Errors encountered: " + errors.size());

        if(warnings.size() != 0)
            for(String warning : warnings)
                Log.w(warning);

        if(errors.size() != 0)
            for(String error : errors)
                Log.e(error);

        thread.interrupt();

        if(failed)
        {
            Log.e("Compilation failed!");
            new DialogCompilationFail();
        }
        else
        {
            PostCompiler.writeMCFunctions();
            PostCompiler.writeVirtuals();
            Log.i("Compilation finished! Project saved at: " + root.getAbsolutePath());
            new DialogCompilationSuccess(root.getAbsolutePath());
        }

        deinit();
    }

    /**
     * Here is all the stuff handled that doesn't (really) contribute to
     * converting the source code into the command blocks
     */
    private static void preCompile()
    {
        Log.f1("Starting pre-compilation process...");

        Set<NavTreeFile> keys = mcfunctionCode.keySet();

        for(NavTreeFile key : keys)
        {
            thread.updateDialog("Simplifying " + key.getPath());
            mcfunctionCode.put(key, PreCompiler.preCompile(key, mcfunctionCode.get(key)));
        }
    }

    private static void compile()
    {
        Log.f1("Starting true compilation...");

        Set<NavTreeFile> keys = mcfunctionCode.keySet();

        for(NavTreeFile key : keys)
        {
            thread.updateDialog("Compiling " + key.getPath());
            mcfunctionCode.put(key, compileFile(key));
        }
    }

    private static void compileVirtuals()
    {
        Set<VirtualIfNode> ifKeys = virtualIfs.keySet();
        Set<VirtualUnlessNode> unlessKeys = virtualUnlesss.keySet();

        for (VirtualIfNode node : ifKeys)
        {
            thread.updateDialog("Compiling if " + node.getPath());
            compiledVirtualCode.put(node, compileFile(node));
            virtualIfs.remove(node);
        }

        for(VirtualUnlessNode node : unlessKeys)
        {
            thread.updateDialog("Compiling unless " + node.getPath());
            compiledVirtualCode.put(node, compileFile(node));
            virtualUnlesss.remove(node);
        }

        if(virtualUnlesss.size() != 0 || virtualIfs.size() != 0)
            compileVirtuals();
    }

    private static ArrayList<String> compileFile(NavTreeFile key)
    {
        ArrayList<String> text;

        if(key instanceof VirtualIfNode)
            text = virtualIfs.get(key);
        else if(key instanceof VirtualUnlessNode)
            text = virtualUnlesss.get(key);
        else
            text = mcfunctionCode.get(key);

        ArrayList<String> compiled = new ArrayList<>();
        //variable name, coordinates
        HashMap<String, NumberGroup> blocks = new HashMap<>();

        {
            int codeBlocks = 0;

            for(String s : text)
                if(s.equals("{")) codeBlocks++;
                else if(s.equals("}")) codeBlocks--;

            if(codeBlocks < 0)
                throw new SyntaxException("imbalance of \"{\" and \"}\".", 0, key.getPath());
        }

        for(int i = 0; i < text.size(); i++)
        {
            try
            {
                String line = text.get(i);

                if(line.equals("{") || line.equals("}")) continue;

                if(line.startsWith("run"))
                {
                    compiled.add(line.substring(4));
                }
                else if(line.startsWith("var"))
                {
                    line = line.substring(4);

                    if(line.matches("[a-zA-Z0-9]+=-?\\d+"))
                    {
                        String[] parts = line.split("=");
                        String command = "scoreboard players set " + parts[0] + " vars " + parts[1];
                        compiled.add(command);
                    }
                    else if(line.matches("[a-zA-Z0-9]+(\\+=|-=|\\*=|/=|%=|<|>|><|=)[a-zA-Z0-9]+"))
                    {
                        String[] parts = line.split("(\\+=|-=|\\*=|/=|%=|<|>|><|=)");
                        String operand = line.substring(parts[0].length(), parts[0].length()+2);
                        if(operand.matches("[><=].")) operand = operand.substring(0, 1);
                        String command = "scoreboard players operation " + parts[0] + " vars " + operand + " " + parts[1] + " vars";
                        compiled.add(command);
                    }
                    else
                    {
                        throw new UnknownStatementException("Not a known syntax for command \"var\": " + line, i+1, key.getPath());
                    }
                }
                else if(line.startsWith("fnc"))
                {
                    line = line.substring(4);

                    if(line.matches("[a-z0-9_]+->[a-z0-9_]+"))
                    {
                        String[] parts = line.split("->");
                        String command = "function " + parts[0] + ":" + parts[1];
                        compiled.add(command);
                    }
                    else if(line.matches("[a-z0-9_]+\\?[a-z0-9_]+"))
                    {
                        String[] parts = line.split("\\?");
                        String command = "function #" + parts[0] + ":" + parts[1];
                        compiled.add(command);
                    }
                    else
                    {
                        throw new UnknownStatementException("Not a known syntax for command \"fnc\": " + line, i+1, key.getPath());
                    }
                }
                else if(line.startsWith("blk"))
                {
                    line = line.substring(4);

                    if(line.matches("[a-zA-Z0-9]+=-?\\d+,-?\\d+,-?\\d+"))
                    {
                        String[] parts = line.split("=");
                        String[] nums = parts[1].split(",");
                        blocks.put(parts[0], new NumberGroup
                                (Integer.parseInt(nums[0]), Integer.parseInt(nums[1]), Integer.parseInt(nums[2])));
                    }
                    else if(line.matches("[a-zA-Z0-9]+\\.type=§[a-z_]+§(\\[([a-z_0-9]+=[a-zA-Z0-9_]+,)*[a-z_0-9]+=[a-zA-Z0-9_]+])?(\\{[\\s\\S]+})?"))
                    {
                        String block = line.substring(0, line.indexOf('.'));
                        String leftPart = line.substring(line.indexOf("=")+1, line.length());
                        String type = leftPart.substring(1, leftPart.indexOf('§', 2));
                        String nbt = "";
                        String state = "";
                        if(leftPart.indexOf('{') > -1) nbt = leftPart.substring(leftPart.indexOf('{'));
                        if(leftPart.indexOf('[') > -1 && leftPart.indexOf('[') < leftPart.indexOf('{')) state = leftPart.substring(leftPart.indexOf('['), leftPart.indexOf(']') + 1);

                        if(!leftPart.equals(""))
                            try { new ObjectMapper().readTree(nbt);}
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("block variable " + block + " does not exist!", i+1, key.getPath());

                        NumberGroup coords = blocks.get(block);
                        String command = "setblock " + coords.get(0) + " " + coords.get(1) + " " + coords.get(2) + " " + type + state + nbt;
                        compiled.add(command);
                    }
                    else if(line.matches("[a-zA-Z0-9]+(\\.[a-zA-Z]+)+->((-?(\\d*?\\.\\d+)|-?(\\d+)[sfdbL]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]+})"))
                    {
                        String block = line.split("->")[0].substring(0, line.indexOf('.'));
                        String path = line.substring(block.length()+1, line.indexOf("->"));
                        String value = line.substring(line.indexOf("->")+2);

                        if(value.startsWith("{"))
                            try { new ObjectMapper().readTree(value); }
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("Could not find block variable " + block, i+1, key.getPath());

                        int[] coords = blocks.get(block).getGroup();
                        String command = "data modify block " + coords[0] + " " + coords[1] + " " + coords[2] + " " + path + " append value " + value;
                        compiled.add(command);
                    }
                    else if(line.matches("[a-zA-Z0-9]+(\\.[a-zA-Z]+)+<-((-?(\\d*?\\.\\d+)|-?(\\d+)[sfdbL]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]+})"))
                    {
                        String block = line.split("<-")[0].substring(0, line.indexOf('.'));
                        String path = line.substring(block.length()+1, line.indexOf("<-"));
                        String value = line.substring(line.indexOf("<-")+2);

                        if(value.startsWith("{"))
                            try { new ObjectMapper().readTree(value); }
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("Could not find block variable " + block, i+1, key.getPath());

                        int[] coords = blocks.get(block).getGroup();
                        String command = "data modify block " + coords[0] + " " + coords[1] + " " + coords[2] + " " + path + " prepend value " + value;
                        compiled.add(command);
                    }
                    else if(line.matches("[a-zA-Z0-9]+(\\.[a-zA-Z]+)+\\[\\d+]\\+=((-?(\\d*?\\.\\d+)|-?(\\d+)[sfdbL]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]+})"))
                    {
                        String block = line.split("=")[0].substring(0, line.indexOf('.'));
                        String path = line.substring(block.length()+1, line.indexOf("["));
                        String value = line.substring(line.indexOf("+=")+2);
                        String index = line.split("\\[")[1];
                        index = index.substring(0, index.indexOf(']'));

                        if(value.startsWith("{"))
                            try { new ObjectMapper().readTree(value); }
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("Could not find block variable " + block, i+1, key.getPath());

                        int[] coords = blocks.get(block).getGroup();
                        String command = "data modify block " + coords[0] + " " + coords[1] + " " + coords[2] + " " + path + " insert " + index +  " value " + value;
                        compiled.add(command);
                    }
                    else
                    {
                        throw new UnknownStatementException("Not a known syntax for command \"blk\": " + line, i+1, key.getPath());
                    }
                }
                else if(line.startsWith("if"))
                {
                    String condition = line.substring(line.indexOf("(")+1, line.indexOf(")"));

                    if(!text.get(i+1).equals("{"))
                        throw new ExpectedTokenException("\"{\" after if/unless statement", i+1, key.getPath());

                    ArrayList<String> ifCode = new ArrayList<>();
                    int codeBlocks = 1;
                    int j;

                    for(j = i+2; j < text.size() && codeBlocks > 0; j++)
                    {
                        String ifLine = text.get(j);
                        if(ifLine.equals("{")) codeBlocks++;
                        else if(ifLine.equals("}")) codeBlocks--;
                        ifCode.add(ifLine);
                    }

                    ifCode.remove(ifCode.size()-1);
                    i = j-1;
                    VirtualIfNode node = new VirtualIfNode();
                    int compIndex = -1;
                    String comparator = "=";
                    String command = "execute if ";

                    for(String char2Comp : char2CompArray)
                    {
                        compIndex = condition.indexOf(char2Comp);
                        if(compIndex != -1)
                        {
                            comparator = char2Comp;
                            break;
                        }
                    }

                    if(compIndex == -1)
                    {
                        for(String char1Comp : char1CompArray)
                        {
                            compIndex = condition.indexOf(char1Comp);
                            if(compIndex != -1)
                            {
                                comparator = char1Comp;
                                break;
                            }
                        }
                    }

                    if(compIndex == -1) throw new ExpectedTokenException("expected \"=\", \">=\", \"<=\", \"<\" or \">\"", i+1, key.getPath());

                    if(condition.matches("[a-zA-Z0-9]+" + comparator + "[a-zA-Z0-9]+"))
                    {
                        String[] args = condition.split(comparator);
                        boolean secArgIsNum;
                        try { Integer.parseInt(args[1]); secArgIsNum = true;}
                        catch(NumberFormatException ignored) { secArgIsNum = false; }
                        //ex: execute if score foo vars = bar vars run function helloworldifs:if0
                        if(secArgIsNum)
                            command += "score " + args[0] + " vars matches " + args[1] + " run function " + Main.project.toLowerCase() + "ifs:" + node.getName();
                        else
                            command += "score " + args[0] + " vars " + comparator + " " + args[1] + " vars" + " run function " + Main.project.toLowerCase() + "ifs:" + node.getName();
                    }
                    else if(condition.matches("[a-zA-Z0-9]+=§[a-z_]+§(\\[([a-zA-Z0-9]+=[a-zA-Z0-9]+,)*[a-zA-Z0-9]+=[a-zA-Z0-9]+])?(\\{[\\s\\S]+})?"))
                    {
                        String[] sides = new String[]{condition.substring(0, condition.indexOf('=')), condition.substring(condition.indexOf('=')+1)};
                        String block = sides[0];
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("block variable " + block + " does not exist!", i+1, key.getPath());
                        NumberGroup group = blocks.get(block);
                        String type = sides[1].substring(1, condition.indexOf('§', 1));
                        int nbt = condition.indexOf('{');
                        int state = condition.indexOf('[');
                        String jsonText = "";
                        String stateText = "";

                        if(nbt != -1)
                        {
                            jsonText = condition.substring(nbt, condition.length());
                            try { new ObjectMapper().readTree(jsonText); }
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        }

                        if(state != -1 && state < nbt)
                        {
                            stateText = condition.substring(state, condition.indexOf(']')+1);
                        }

                        command += group.get(0) + " " + group.get(1) + " " + group.get(2) + " " + type + jsonText + stateText
                                + " run function " + Main.project.toLowerCase() + "ifs:" + node.getName();
                    }
                    else
                    {
                        throw new UnknownStatementException("Not a known syntax for command \"if\": " + line, i+1, key.getPath());
                    }

                    virtualIfs.put(node, ifCode);
                    compiled.add(command);


                }
                else if(line.startsWith("unless"))
                {
                    String condition = line.substring(line.indexOf("(")+1, line.indexOf(")"));

                    if(!text.get(i+1).equals("{"))
                        throw new ExpectedTokenException("\"{\" after if/unless statement", i+1, key.getPath());

                    ArrayList<String> ifCode = new ArrayList<>();
                    int codeBlocks = 1;
                    int j;

                    for(j = i+2; j < text.size() && codeBlocks > 0; j++)
                    {
                        String ifLine = text.get(j);
                        if(ifLine.equals("{")) codeBlocks++;
                        else if(ifLine.equals("}")) codeBlocks--;
                        ifCode.add(ifLine);
                    }

                    ifCode.remove(ifCode.size()-1);
                    VirtualUnlessNode node = new VirtualUnlessNode();
                    int compIndex = -1;
                    String comparator = "=";
                    String command = "execute unless ";

                    for(String char2Comp : char2CompArray)
                    {
                        compIndex = condition.indexOf(char2Comp);
                        if(compIndex != -1)
                        {
                            comparator = char2Comp;
                            break;
                        }
                    }

                    if(compIndex == -1)
                    {
                        for(String char1Comp : char1CompArray)
                        {
                            compIndex = condition.indexOf(char1Comp);
                            if(compIndex != -1)
                            {
                                comparator = char1Comp;
                                break;
                            }
                        }
                    }

                    if(compIndex == -1) throw new ExpectedTokenException("expected \"=\", \">=\", \"<=\", \"<\" or \">\"", i+1, key.getPath());

                    if(condition.matches("[a-zA-Z0-9]+" + comparator + "[a-zA-Z0-9]+"))
                    {
                        String[] args = condition.split(comparator);
                        boolean secArgIsNum;
                        try { Integer.parseInt(args[1]); secArgIsNum = true;}
                        catch(NumberFormatException ignored) { secArgIsNum = false; }
                        //ex: execute if score foo vars = bar vars run function helloworldifs:if0
                        if(secArgIsNum)
                            command += "score " + args[0] + " vars matches " + args[1] + " run function " + Main.project.toLowerCase() + "unlesss:" + node.getName();
                        else
                            command += "score " + args[0] + " vars " + comparator + " " + args[1] + " vars" + " run function " + Main.project.toLowerCase() + "unlesss:" + node.getName();
                    }
                    else if(condition.matches("[a-zA-Z0-9]+=§[a-z_]+§(\\[([a-zA-Z0-9]+=[a-zA-Z0-9]+,)*[a-zA-Z0-9]+=[a-zA-Z0-9]+])?(\\{[\\s\\S]+})?"))
                    {
                        String[] sides = new String[]{condition.substring(0, condition.indexOf('=')), condition.substring(condition.indexOf('=')+1)};
                        String block = sides[0];
                        if(!blocks.containsKey(block)) throw new UnexpectedTokenException("block variable " + block + " does not exist!", i+1, key.getPath());
                        NumberGroup group = blocks.get(block);
                        String type = sides[1].substring(1, condition.indexOf('§', 1));
                        int nbt = condition.indexOf('{');
                        int state = condition.indexOf('[');
                        String jsonText = "";
                        String stateText = "";

                        if(nbt != -1)
                        {
                            jsonText = condition.substring(nbt, condition.length());
                            try { new ObjectMapper().readTree(jsonText); }
                            catch (JsonProcessingException e) { throw new WrappedCompilationException(i+1, e, key.getPath()); }
                        }

                        if(state != -1 && state < nbt)
                        {
                            stateText = condition.substring(state, condition.indexOf(']')+1);
                        }

                        command += group.get(0) + " " + group.get(1) + " " + group.get(2) + " " + type + jsonText + stateText
                                + " run function " + Main.project.toLowerCase() + "unlesss:" + node.getName();
                    }
                    else
                    {
                        throw new UnknownStatementException("Not a known syntax for command \"unless\": " + line, i+1, key.getPath());
                    }

                    virtualUnlesss.put(node, ifCode);
                    compiled.add(command);
                }
            }
            catch(CompilationException e)
            {
                if(e.isError())
                    errors.add(e.getMessage());
                else
                    warnings.add(e.getMessage());
            }
            catch(Throwable t)
            {
                //t.printStackTrace();
                Log.e("An unknown exception occured!", t);
                try { throw new WrappedCompilationException(i+1, t, key.getPath()); }
                catch(WrappedCompilationException e)
                {
                    errors.add(e.getMessage());
                }
            }
        }

        return compiled;
    }

    /**
     * This method is called when exporting a projecto to a directory
     * which is already holding a compiled version of the project.
     * To ensure that there are no leftovers from an older version of
     * the project, all the files are wiped clean first
     *
     * @param dir the root of the project
     */
    private static void deleteChildren(File dir)
    {
        File[] children = dir.listFiles();

        if(children != null)
        {
            for(File child : children)
            {
                if(child.isDirectory())
                    deleteChildren(child);
                else
                    child.delete();
            }
        }

        dir.delete();
    }

    /**
     * Resets all the lists and variables to start another
     * compilation session
     */
    private static void deinit()
    {
        Log.i("Deinitializing compiler");
        mcfunctionCode.clear();
        filePaths.clear();
        presentPaths.clear();
        nodePathMap.clear();
        root = null;
        rootNode = null;
        failed = false;
        errors.clear();
        warnings.clear();
        virtualIfs.clear();
        virtualUnlesss.clear();
        compiledVirtualCode.clear();
        VirtualIfNode.precedingIfs = 0;
        VirtualUnlessNode.precedingUnlesss = 0;
    }
}
