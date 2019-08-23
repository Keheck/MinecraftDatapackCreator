package io.github.keheck.project.exceptions;

public class SyntaxException extends CompilationException
{
    public SyntaxException(String syntaxError, int lineNum, String function)
    {
        super(lineNum, syntaxError, function, true);
    }
}
