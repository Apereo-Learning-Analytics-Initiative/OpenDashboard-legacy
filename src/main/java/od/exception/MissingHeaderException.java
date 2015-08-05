package od.exception;

import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="A required header was set incorrectly")
public class MissingHeaderException extends ServletException{
    
    private static final long serialVersionUID = -156338622418684632L;
    private String message;
    
    public MissingHeaderException() {
        super();
    }

    public MissingHeaderException(String message) {
        super(String.format("Header not found: %s", message));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    

}
