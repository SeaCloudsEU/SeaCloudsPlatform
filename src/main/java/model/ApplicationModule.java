package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Adrian on 15/10/2014.
 */
public class ApplicationModule implements Module {

    private String id;
    private Application parentApplication;
    private List<Module> children;

    public ApplicationModule(String id, Application application, Module parent){
        this.id = id;
        this.parentApplication = application;
        this.children = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Module getParentApplication() {
        return parentApplication;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationModule that = (ApplicationModule) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
