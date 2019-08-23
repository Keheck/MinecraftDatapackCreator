package io.github.keheck.project.exceptions;

public class ExpectedTokenException extends SyntaxException
{
    public ExpectedTokenException(String syntaxError, int lineNum, String function)
    {
        super("Expected token: " + syntaxError, lineNum, function);
    }
}
