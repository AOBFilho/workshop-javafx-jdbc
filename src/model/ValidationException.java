package model;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private Map<String,String> errorsMessage = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public Map<String,String> getErrorsMessage() {
        return errorsMessage;
    }

    public void addErrorMessage(String fieldName, String msg) {
        errorsMessage.put(fieldName,msg);
    }
}
