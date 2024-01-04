package Exceptions;

public class CustomException extends Exception{
    private CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }
}
