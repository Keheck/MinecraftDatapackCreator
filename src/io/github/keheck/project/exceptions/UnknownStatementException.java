package io.github.keheck.project.exceptions;

public class UnknownStatementException extends SyntaxException
{
    public UnknownStatementException(String syntaxError, int lineNum, String function)
    {
        super("unknown or wrong syntax: \"" + syntaxError + "\"", lineNum, function);
    }
}
