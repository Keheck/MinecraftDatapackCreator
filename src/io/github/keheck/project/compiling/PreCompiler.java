package io.github.keheck.project.compiling;

import io.github.keheck.project.exceptions.CompilationException;
import io.github.keheck.project.exceptions.UnknownStatementException;
import io.github.keheck.project.exceptions.WrappedCompilationException;
import io.github.keheck.tree.NavTreeFile;
import io.github.keheck.util.Util;
import io.github.keheck.window.MainMenu;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * This class handles all the stuff required for the compilation process such as
 * <ul>
 *     <li>removing whitespaces</li>
 *     <li>simplifiying redundant code like if statements that are always true/false</li>
 * </ul>
 */

class PreCompiler
{
    static final String[] char2CompArray = {"<=", ">="};
    static final String[] char1CompArray = {">", "<", "="};
    private static final String[] operands = {"+=", "-=", "*=", "/=", "%=", "=", "><", ">", "<"};
    private static final String[] jsonStrings = {":", "[", "]", "{", "}", ","};

    static ArrayList<String> preCompile(NavTreeFile function, ArrayList<String> text)
    {
        ArrayList<String> trimmed = new ArrayList<>();
        for(String oldLine : text) trimmed.add(oldLine.trim());
        int lineNum = 0;
        //does not exist (somehow) OR is blank OR is a one-line comment AND "keep comments" is false
        trimmed.removeIf((s) -> s == null || s.matches("\\s*") || !MainMenu.keepComments.getState() && s.startsWith("#"));

        for(String line : trimmed)
        {
            try
            {
                String nonWhitespace = removeWhitespaces(line, function, lineNum);
                trimmed.set(lineNum, nonWhitespace);
            }
            catch(CompilationException e)
            {
                if(!e.getClass().equals(WrappedCompilationException.class))
                    e.printStackTrace();
                else
                    e.getCause().printStackTrace();

                if(e.isError())
                {
                    //Compilation will continue to find other errors, but won't export
                    //(Wait, isn't that how a compiler works and what it's supposed to do?)
                    Compiler.skippedFiles.add(function);
                    Compiler.errors.add(e.getMessage());
                    Compiler.failed = true;
                }
                else
                    Compiler.warnings.add(e.getMessage());
            }

            //BACKGROUND INFO: This came previously *before* the trimming... wasn't such a good idea
            lineNum++;
        }

        trimmed = simplify(trimmed);
        //trimmed.removeIf((s) -> s == null || s.matches("\\s*") || !MainMenu.keepComments.getState() && s.startsWith("#"));
        return trimmed;
    }

    /**
     * The new alhorythm for removing spaces to make the line comfortable to compile
     * NOT FROM DEV: Why don't I just remove all whitespaces? Because I can
     * @param line the line that is being adjusted
     * @param function the funtion that hosts the line
     * @param lineNum the line number
     * @return the adjusted string
     */
    private static String removeWhitespaces(String line, NavTreeFile function, int lineNum)
    {
        line = line.trim();
        ArrayList<Character> lineChars = arrayListFromString(line);
        String adjustedString;
        //a universal index for required indices in the line
        //is used for indices that are only temporary used to conserve resources
        int uniIndex = 0;

        if(line.matches("\\{") || line.matches("}")) return line;

        try
        {
            if(line.startsWith("run"))
            {
                while(lineChars.get(3).equals(' ')) lineChars.remove(3);
                while(lineChars.get(4).equals(' ')) lineChars.remove(4);
            }
            else if(line.startsWith("if"))
            {
                while(lineChars.get(2).equals(' ')) lineChars.remove(2);
                while(lineChars.get(3).equals(' ')) lineChars.remove(3);

                adjustedString = stringFromArrayList(lineChars);
                boolean lengthIs2 = false;

                //variable check
                if(adjustedString.matches("if\\([a-zA-Z0-9]+\\s*(<|<=|=|>=|>)\\s*[a-zA-Z0-9]+\\s*\\)(\\s*\\{)?"))
                {
                    for(String char2Comp : char2CompArray)
                    {
                        uniIndex = adjustedString.indexOf(char2Comp);
                        if(uniIndex != -1)
                        {
                            lengthIs2 = true;
                            break;
                        }
                    }

                    if(uniIndex == -1)
                    {
                        for(String char1Comp : char1CompArray)
                        {
                            uniIndex = adjustedString.indexOf(char1Comp);
                            if(uniIndex != -1) break;
                        }
                    }

                    if(lengthIs2) while(lineChars.get(uniIndex+2).equals(' ')) lineChars.remove(uniIndex+2);
                    else while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(;lineChars.get(uniIndex-1).equals(' ');uniIndex--) lineChars.remove(uniIndex-1);

                    adjustedString = stringFromArrayList(lineChars);

                    uniIndex = adjustedString.lastIndexOf(')');

                    if(uniIndex < adjustedString.length()-1)
                        while(lineChars.get(uniIndex).equals(' ')) lineChars.remove(uniIndex+1);

                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);
                }
                //nbt check
                else if(adjustedString.matches("if\\([a-zA-Z0-9]+\\s*=\\s*§[a-z_]+§\\s*(\\[(\\s*[a-zA-Z0-9]+\\s*=\\s*[a-zA-Z0-9]+\\s*,)*\\s*[a-zA-Z0-9]+\\s*=\\s*[a-zA-Z0-9]+])?\\s*(\\{[\\s\\S]+})?\\)(\\s*\\{)?"))
                {
                    for(int i = adjustedString.indexOf('{'); i >= 0; i--)
                    {
                        char character = lineChars.get(i);
                        if(character == '§' || character == ',' || character == '=')
                        {
                            while(lineChars.get(i+1).equals(' ')) lineChars.remove(i+1);
                            for(; lineChars.get(i-1).equals(' '); i--) lineChars.remove(i - 1);
                        }
                    }
                }

                adjustedString = stringFromArrayList(lineChars);

                if((uniIndex = adjustedString.lastIndexOf('{')) == adjustedString.length()-1)
                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--)
                        lineChars.remove(uniIndex-1);
            }
            else if(line.startsWith("unless"))
            {
                while(lineChars.get(6).equals(' ')) lineChars.remove(6);
                while(lineChars.get(7).equals(' ')) lineChars.remove(7);

                adjustedString = stringFromArrayList(lineChars);
                boolean lengthIs2 = false;

                //variable check
                if(adjustedString.matches("unless\\([a-zA-Z0-9]+\\s*(<|<=|=|>=|>)\\s*[a-zA-Z0-9]+\\s*\\)(\\s*\\{)?"))
                {
                    for(String char2Comp : char2CompArray)
                    {
                        uniIndex = adjustedString.indexOf(char2Comp);
                        if(uniIndex != -1)
                        {
                            lengthIs2 = true;
                            break;
                        }
                    }

                    if(uniIndex == -1)
                    {
                        for(String char1Comp : char1CompArray)
                        {
                            uniIndex = adjustedString.indexOf(char1Comp);
                            if(uniIndex != -1) break;
                        }
                    }

                    if(lengthIs2) while(lineChars.get(uniIndex+2).equals(' ')) lineChars.remove(uniIndex+2);
                    else while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(;lineChars.get(uniIndex-1).equals(' ');uniIndex--) lineChars.remove(uniIndex-1);

                    adjustedString = stringFromArrayList(lineChars);

                    uniIndex = adjustedString.lastIndexOf(')');

                    if(uniIndex < adjustedString.length()-1)
                        while(lineChars.get(uniIndex).equals(' ')) lineChars.remove(uniIndex+1);

                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);
                }
                //nbt check
                else if(adjustedString.matches("unless\\([a-zA-Z0-9]+\\s*=\\s*§[a-z_]+§\\s*(\\[(\\s*[a-zA-Z0-9]+\\s*=\\s*[a-zA-Z0-9]+\\s*,)*\\s*[a-zA-Z0-9]+\\s*=\\s*[a-zA-Z0-9]+])?\\s*(\\{[\\s\\S]+})?\\)(\\s*\\{)?"))
                {
                    for(int i = adjustedString.indexOf('{'); i >= 0; i--)
                    {
                        char character = lineChars.get(i);
                        if(character == '§' || character == ',' || character == '=')
                        {
                            while(lineChars.get(i+1).equals(' ')) lineChars.remove(i+1);
                            for(; lineChars.get(i-1).equals(' '); i--) lineChars.remove(i - 1);
                        }
                    }
                }

                adjustedString = stringFromArrayList(lineChars);

                if((uniIndex = adjustedString.lastIndexOf('{')) == adjustedString.length()-1)
                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--)
                        lineChars.remove(uniIndex-1);
            }
            else if(line.startsWith("var"))
            {
                while(lineChars.get(3).equals(' ')) lineChars.remove(3);
                while(lineChars.get(4).equals(' ')) lineChars.remove(4);

                adjustedString = stringFromArrayList(lineChars);
                String usedOperand = "=";
                int index = 1;

                for (String operand : operands)
                {
                    if(adjustedString.contains(operand))
                    {
                        usedOperand = operand;
                        index = adjustedString.indexOf(operand);
                        break;
                    }
                }

                int opLen = usedOperand.length();

                for(; lineChars.get(index-1).equals(' '); index--) lineChars.remove(index-1);
                while(lineChars.get(index+opLen).equals(' ')) lineChars.remove(index+opLen);
            }
            else if(line.startsWith("fnc"))
            {
                while(lineChars.get(3).equals(' ')) lineChars.remove(3);
                while(lineChars.get(4).equals(' ')) lineChars.remove(4);

                adjustedString = stringFromArrayList(lineChars);

                if((uniIndex = adjustedString.indexOf('?')) > -1)
                {
                    while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(; lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);
                }
                else
                {
                    uniIndex = adjustedString.indexOf("->");
                    while(lineChars.get(uniIndex+2).equals(' ')) lineChars.remove(uniIndex+2);
                    for(; lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);
                }
            }
            else if(line.startsWith("blk"))
            {
                while(lineChars.get(3).equals(' ')) lineChars.remove(3);
                while(lineChars.get(4).equals(' ')) lineChars.remove(4);

                adjustedString = stringFromArrayList(lineChars);

                //initialize block variable
                if(adjustedString.matches("blk:[a-zA-Z0-9]+\\s*=\\s*-?\\d+\\s*,\\s*-?\\d+\\s*,\\s*-?\\d+"))
                {
                    int[] commas = Util.getAllIndicesOf(adjustedString, ",");

                    for(int i = commas.length-1; i >= 0; i--)
                    {
                        uniIndex = commas[i];

                        while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                        for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);
                    }

                    int assign = adjustedString.indexOf('=');

                    while(lineChars.get(assign+1).equals(' ')) lineChars.remove(assign+1);
                    for(;lineChars.get(assign-1).equals(' '); assign--) lineChars.remove(assign-1);
                }
                //set the type and state
                else if(adjustedString.matches("blk:[a-zA-Z0-9]+\\s*\\.\\s*type\\s*=\\s*§[a-z_]+§\\s*(\\[\\s*([a-z_0-9]+\\s*=\\s*[a-zA-Z0-9_]+\\s*,\\s*)*[a-z_0-9]+\\s*=\\s*[a-zA-Z0-9_]+\\s*])?(\\s*\\{[\\s\\S]+})?"))
                {
                    ArrayList<Integer> allAssigns = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(Util.getAllIndicesOf(adjustedString, '='))));
                    final int firstCurly = adjustedString.indexOf('{');
                    allAssigns.removeIf((i) -> i > firstCurly);

                    for(int i = allAssigns.size()-1; i >= 0; i--)
                    {
                        int index = allAssigns.get(i);
                        while(lineChars.get(index+1).equals(' ')) lineChars.remove(index+1);
                        for(;lineChars.get(index-1).equals(' '); index--) lineChars.remove(index-1);
                    }

                    adjustedString = stringFromArrayList(lineChars);

                    int typeStart = adjustedString.indexOf('§');
                    int typeEnd = adjustedString.indexOf('§', typeStart+1);

                    while(lineChars.get(typeEnd+1).equals(' ')) lineChars.remove(typeEnd+1);
                    for(;lineChars.get(typeStart-1).equals(' '); typeStart--) lineChars.remove(typeStart);

                    adjustedString = stringFromArrayList(lineChars);

                    uniIndex = adjustedString.indexOf('.');

                    while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);

                    adjustedString = stringFromArrayList(lineChars);

                    uniIndex = adjustedString.indexOf(']');

                    while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);

                    uniIndex = adjustedString.indexOf('[');

                    while(lineChars.get(uniIndex+1).equals(' ')) lineChars.remove(uniIndex+1);
                    for(;lineChars.get(uniIndex-1).equals(' '); uniIndex--) lineChars.remove(uniIndex-1);

                    adjustedString = stringFromArrayList(lineChars);

                    ArrayList<Integer> allCommas = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(Util.getAllIndicesOf(adjustedString, '='))));
                    final int firstCurly2 = adjustedString.indexOf('{');
                    allCommas.removeIf((i) -> i > firstCurly2);

                    for(int i = allCommas.size()-1; i >= 0; i--)
                    {
                        int index = allCommas.get(i);
                        while(lineChars.get(index+1).equals(' ')) lineChars.remove(index+1);
                        for(;lineChars.get(index-1).equals(' '); index--) lineChars.remove(index-1);
                    }

                    adjustedString = stringFromArrayList(lineChars);
                    int openNBT = adjustedString.indexOf('{');

                    String beforeNBT = adjustedString.substring(0, openNBT);
                    String theNBT = adjustedString.substring(openNBT);
                    ArrayList<Character> nbtChars = arrayListFromString(theNBT);

                    ArrayList<Integer> quotes = new ArrayList<>();

                    for(int i = 1; i < nbtChars.size(); i++)
                        if (nbtChars.get(i).equals('"') && !nbtChars.get(i - 1).equals('\\'))
                            quotes.add(i);

                    boolean inquote = false;

                    for(int i = nbtChars.size()-2; i >= 0; i--)
                    {
                        if(quotes.contains(i))
                        {
                            inquote = !inquote;
                            continue;
                        }

                        if(inquote) continue;

                        if(ArrayUtils.contains(jsonStrings, nbtChars.get(i).toString()))
                        {
                            while(nbtChars.size() != i+1 && nbtChars.get(i+1).equals(' ')) nbtChars.remove(i+1);
                            if(i > 0)
                                for(;nbtChars.get(i-1).equals(' '); i--) nbtChars.remove(i-1);

                            if(i < 0) break;
                        }
                    }

                    uniIndex = nbtChars.lastIndexOf('}');

                    for(;nbtChars.get(uniIndex-1).equals(' '); uniIndex--) nbtChars.remove(uniIndex-1);

                    theNBT = stringFromArrayList(nbtChars);
                    adjustedString = beforeNBT.concat(theNBT);
                    lineChars = arrayListFromString(adjustedString);
                }
                //append
                else if(adjustedString.matches("blk:[a-zA-Z0-9]+(\\s*\\.\\s*[a-zA-Z]+)+\\s*->\\s*((-?\\d*?\\.\\d+|-?\\d+[sfdb]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]*})"))
                {
                    int assign = adjustedString.indexOf('>');
                    while(lineChars.get(assign+1).equals(' ')) lineChars.remove(assign+1);
                    assign = adjustedString.indexOf('-');
                    for(;lineChars.get(assign-1).equals(' '); assign--) lineChars.remove(assign-1);

                    adjustedString = stringFromArrayList(lineChars);
                    int newAssign = adjustedString.indexOf('-');
                    ArrayList<Integer> dotIndicesList = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(Util.getAllIndicesOf(adjustedString, "."))));
                    dotIndicesList.removeIf((i) -> i >= newAssign);
                    Collections.reverse(dotIndicesList);
                    int[] dotIndices = ArrayUtils.toPrimitive(dotIndicesList.toArray(new Integer[0]));

                    for(int dotIndex : dotIndices)
                    {
                        while(lineChars.get(dotIndex+1).equals(' ')) lineChars.remove(dotIndex+1);
                        for(;lineChars.get(dotIndex-1).equals(' '); dotIndex--) lineChars.remove(dotIndex-1);
                    }

                    adjustedString = stringFromArrayList(lineChars);

                    int squareIndex = adjustedString.indexOf('[');

                    if(squareIndex != -1)
                    {
                        while(lineChars.get(squareIndex+1).equals(' ')) lineChars.remove(squareIndex+1);
                        adjustedString = stringFromArrayList(lineChars);
                        squareIndex = adjustedString.indexOf(']');
                        for(;lineChars.get(squareIndex-1).equals(' '); squareIndex--) lineChars.remove(squareIndex-1);
                    }
                }
                //prepend
                else if(adjustedString.matches("blk:[a-zA-Z0-9]+(\\s*\\.\\s*[a-zA-Z]+)+\\s*<-\\s*((-?(\\d*?\\.\\d+)|-?(\\d+)[sfdb]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]*})"))
                {
                    int assign = adjustedString.indexOf('-');
                    while(lineChars.get(assign+1).equals(' ')) lineChars.remove(assign+1);
                    assign = adjustedString.indexOf('<');
                    for(;lineChars.get(assign-1).equals(' '); assign--) lineChars.remove(assign-1);

                    adjustedString = stringFromArrayList(lineChars);
                    int newAssign = adjustedString.indexOf('<');
                    ArrayList<Integer> dotIndicesList = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(Util.getAllIndicesOf(adjustedString, "."))));
                    dotIndicesList.removeIf((i) -> i >= newAssign);
                    Collections.reverse(dotIndicesList);
                    int[] dotIndices = ArrayUtils.toPrimitive(dotIndicesList.toArray(new Integer[0]));

                    for(int dotIndex : dotIndices)
                    {
                        while(lineChars.get(dotIndex+1).equals(' ')) lineChars.remove(dotIndex+1);
                        for(;lineChars.get(dotIndex-1).equals(' '); dotIndex--) lineChars.remove(dotIndex-1);
                    }

                    adjustedString = stringFromArrayList(lineChars);

                    int squareIndex = adjustedString.indexOf('[');

                    if(squareIndex != -1)
                    {
                        while(lineChars.get(squareIndex+1).equals(' ')) lineChars.remove(squareIndex+1);
                        adjustedString = stringFromArrayList(lineChars);
                        squareIndex = adjustedString.indexOf(']');
                        for(;lineChars.get(squareIndex-1).equals(' '); squareIndex--) lineChars.remove(squareIndex-1);
                    }
                }
                //insert into array
                else if(adjustedString.matches("blk:[a-zA-Z0-9]+(\\s*\\.\\s*[a-zA-Z]+)+\\s*\\[\\d+]\\s*\\+=\\s*((-?(\\d*?\\.\\d+)|-?(\\d+)[sfdb]?)|(\"[\\s\\S]*\")|\\{[\\s\\S]*})"))
                {
                    int assign = adjustedString.indexOf('=');
                    while(lineChars.get(assign+1).equals(' ')) lineChars.remove(assign+1);
                    assign = adjustedString.indexOf('+');
                    for(;lineChars.get(assign-1).equals(' '); assign--) lineChars.remove(assign-1);

                    adjustedString = stringFromArrayList(lineChars);
                    int newAssign = adjustedString.indexOf('+');
                    ArrayList<Integer> dotIndicesList = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(Util.getAllIndicesOf(adjustedString, "."))));
                    dotIndicesList.removeIf((i) -> i >= newAssign);
                    Collections.reverse(dotIndicesList);
                    int[] dotIndices = ArrayUtils.toPrimitive(dotIndicesList.toArray(new Integer[0]));

                    for(int dotIndex : dotIndices)
                    {
                        while(lineChars.get(dotIndex+1).equals(' ')) lineChars.remove(dotIndex+1);
                        for(;lineChars.get(dotIndex-1).equals(' '); dotIndex--) lineChars.remove(dotIndex-1);
                    }

                    adjustedString = stringFromArrayList(lineChars);

                    int squareIndex = adjustedString.indexOf('[');

                    if(squareIndex != -1)
                    {
                        while(lineChars.get(squareIndex+1).equals(' ')) lineChars.remove(squareIndex+1);
                        adjustedString = stringFromArrayList(lineChars);
                        squareIndex = adjustedString.indexOf(']');
                        for(;lineChars.get(squareIndex-1).equals(' '); squareIndex--) lineChars.remove(squareIndex-1);
                    }
                }
            }
            else throw new UnknownStatementException(line, lineNum, function.getPath());

            return stringFromArrayList(lineChars);
        }
        catch(Throwable t)
        {
            if(!(t instanceof CompilationException))
                throw new WrappedCompilationException(lineNum, t, function.getPath());
            else
                throw t;
        }
    }

    private static ArrayList<String> simplify(ArrayList<String> text)
    {
        for(int i = 0; i < text.size(); i++)
        {
            String line = text.get(i);

            if((line.startsWith("if") || line.startsWith("unless")) && line.endsWith("{"))
            {
                line = line.substring(0, line.length()-1);
                text.set(i, line);
                text.add(i+1, "{");
            }

            if(line.matches("var:([a-zA-Z0-9]+)=\\1"))
            {
                text.remove(i);
                continue;
            }

            if(line.matches("if\\(([a-zA-Z0-9]+)[<>]\\1\\)") || line.matches("unless\\(([a-zA-Z0-9]+)(==|<=|>=)\\1\\)"))
            {
                int blocks = 1;

                text.remove(i);
                text.remove(i);

                while(blocks != 0)
                {
                    String removed = text.remove(i);

                    if(removed.equals("{"))
                        blocks++;
                    if(removed.equals("}"))
                        blocks--;
                }

                if(i == text.size())
                    return text;

                i = -1;
            }

            if(line.matches("if\\(([a-zA-Z0-9]+)(==|>=|<=)\\1\\)") || line.matches("unless\\(([a-zA-Z0-9]+)[<>]\\1\\)"))
            {
                int blocks = 1;

                text.remove(i);
                text.remove(i);
                int j;

                for(j = i; blocks != 0; j++)
                {
                    String s = text.get(j);

                    if(s.equals("{"))
                        blocks++;
                    if(s.equals("}"))
                        blocks--;
                }

                text.remove(j-1);

                if(j == text.size())
                    return text;

                i = -1;
            }
        }

        return text;
    }

    private static String stringFromArrayList(ArrayList<Character> chars)
    {
        return new String(ArrayUtils.toPrimitive(chars.toArray(new Character[0])));
    }

    private static ArrayList<Character> arrayListFromString(String string)
    {
        return new ArrayList<>(Arrays.asList(ArrayUtils.toObject(string.toCharArray())));
    }
}
