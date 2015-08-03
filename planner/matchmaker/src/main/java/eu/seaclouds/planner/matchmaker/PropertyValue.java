package eu.seaclouds.planner.matchmaker;

public class PropertyValue<T> {
    private final String name;
    private T value;

    public PropertyValue(String name, T value){
        this.name = name;
        this.value = value;
    }

    public T getValue(){ return value; }
    public String getName() { return name; }
}
