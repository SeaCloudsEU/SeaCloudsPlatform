package eu.seaclouds.platform.repository.store;

import javax.ws.rs.core.MediaType;

public class MemoryItem implements Item {
    static MediaType DEFAULT_TYPE = MediaType.APPLICATION_OCTET_STREAM_TYPE;

    private String id;
    private String contentType;
    private Object data;

    public MemoryItem(String id, MediaType contentType, Object data) {
        this.id = id;
        this.contentType = contentType != null? contentType.toString() : DEFAULT_TYPE.toString();
        this.data = data;
    }
    
    @Override
    public String getContentType() {
        return contentType;
    }
    
    @Override
    public Object getData() {
        return data;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MemoryItem other = (MemoryItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}