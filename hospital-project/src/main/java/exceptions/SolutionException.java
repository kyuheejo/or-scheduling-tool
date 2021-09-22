package exceptions;

public class SolutionException extends RuntimeException {
    public SolutionException() {
    }

    public SolutionException(String message) {
        super(message);
    }
}
