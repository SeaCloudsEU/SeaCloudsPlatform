package metrics;

/**
 * Created by Adrian on 15/10/2014.
 */
public class Metric<T> {
    private String id;
    private String description;
    private Class<T> type;

    Metric(String id,  String description, Class<T> type) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public String getId(){
        return this.id;
    }

    public Class<T> getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String translateTo(MetricLanguage language){
       return language.getTranslation(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metric that = (Metric) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
