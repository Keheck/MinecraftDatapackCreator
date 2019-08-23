package io.github.keheck.project.exceptions;

public class UnexpectedTokenException extends SyntaxException
{
    public UnexpectedTokenException(String syntaxError, int lineNum, String function)
    {
        super("Unexpected token: " + syntaxError, lineNum, function);
    }
}
