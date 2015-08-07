package od.exception;

import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Cookie Not Found") //404
public class MissingCookieException extends ServletException{
    private static final long serialVersionUID = -156338622418684632L;
    private String message;
    
    public MissingCookieException() {
        super();
    }

    public MissingCookieException(String message) {
        super(String.format("Cookie not found: %s", message));
        this.message = String.format("Cookie not found: %s", message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
