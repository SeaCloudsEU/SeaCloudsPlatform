package model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adrian on 15/10/2014.
 */
public class Application implements Module {

    // TODO: Search application/module by ID

    private String id;
    private String name;
    private List<Module> children;

    public Application(String id, String name) {
        this.id = id;
        this.name = name;
        this.children = new LinkedList<>();
    }

    public String getName() {
        return name;
    }


    @Override
    public String getId() {
        return id;
    }


    @Override
    public Collection<Module> getChildren() {
        return children;
    }

    @Override
    public void addChild(Module child) {
        children.add(child);
    }

    @Override
    public Module getParentApplication() {
        return this;
    }

}
