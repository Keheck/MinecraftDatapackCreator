package io.github.keheck.project.exceptions;

public class CompilationException extends RuntimeException
{
    private boolean isError;
    private static String message;

    CompilationException(int lineNum, String reason, String function, boolean isError)
    {
        message = this.toString() + ": " + reason + " at " + function + ":" + lineNum;
        this.isError = isError;
    }

    /**
     * When an exception is "heavy" it means it has
     * such a strong impact of the code that the compilation
     * cannot be continued on the function and the compiler will
     * interpret this as an error and continue with the next file
     */
    public boolean isError() { return isError; }

    @Override
    public String getMessage() { return message; }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
