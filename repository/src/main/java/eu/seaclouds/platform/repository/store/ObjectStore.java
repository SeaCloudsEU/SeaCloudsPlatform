package eu.seaclouds.platform.repository.store;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectStore {

    private ConcurrentHashMap<String, Item> map;
    
    public ObjectStore() {
        this.map = new ConcurrentHashMap<String, Item>();
    }
    
    public boolean containsKey(String id) {
        
        return map.containsKey(id);
    }
    
    public Item get(String id) {
        if (!map.containsKey(id)) {
            throw new IllegalArgumentException("Id " + id + " not found in Object Store");
        }
        return map.get(id);
    }
    
    public boolean remove(String id) {
        
        synchronized(map) {
            if (!map.containsKey(id)) {
                return false;
            }
            map.remove(id);
            return true;
        }
    }
    
    public boolean put(String id, Item item) {
        
        synchronized(map) {
            if (this.containsKey(id)) {
                return false;
            }
            map.put(id, item);
            return true;
        }
    }
}
