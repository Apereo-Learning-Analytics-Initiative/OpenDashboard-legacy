package od.exception;

import javax.servlet.ServletException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Tenant Not Found") //404
public class MissingTenantException extends ServletException{
    private static final long serialVersionUID = 156338621418684632L;
    private String message;
    
    public MissingTenantException(){
        super();
    }
    
    public MissingTenantException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
