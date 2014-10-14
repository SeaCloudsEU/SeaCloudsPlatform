package model.exceptions;

/**
 * @author MBarrientos
 */
public class ApplicationNotFoundException extends MonitorRuntimeException{

    public ApplicationNotFoundException(String application){
        super("Application not found: " + application);
    }
}
