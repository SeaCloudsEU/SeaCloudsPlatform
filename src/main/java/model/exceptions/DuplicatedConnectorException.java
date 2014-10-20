package model.exceptions;

/**
 * @author MBarrientos
 */
public class DuplicatedConnectorException extends MonitorRuntimeException {
    public DuplicatedConnectorException(String connector, String application) {
        super("Duplicated connector " + connector + "at " + application);

    }
}
