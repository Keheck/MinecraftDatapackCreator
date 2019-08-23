package io.github.keheck.project.exceptions;

public class WrappedCompilationException extends CompilationException
{
    public WrappedCompilationException(int lineNum, Throwable reason, String function)
    {
        super(lineNum, reason.getClass().getName() + " " + reason.getMessage(), function, true);
        this.initCause(reason);
    }
}
