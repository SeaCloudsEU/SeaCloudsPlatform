package model;

import java.util.Collection;

/**
 * @author MBarrientos
 */
public interface Module {

    public String getId();
    public Collection<Module> getChildren();
    public void addChild(Module child);
    public Module getParentApplication();

    //TODO: Define toString

}
