package model.exceptions;

/**
 * @author MBarrientos
 */
public class ModuleNotFoundException extends MonitorRuntimeException{

    public ModuleNotFoundException(String module){
        super("Module not found: " + module);
    }
}
