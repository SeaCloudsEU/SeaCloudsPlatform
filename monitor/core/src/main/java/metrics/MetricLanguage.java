package metrics;

/**
 * Created by Adrian on 17/10/2014.
 */
public abstract class MetricLanguage {
    /**
     * Adds a new translation into the dictionary
     * @param from the word in the source language
     * @param to the translated word
     */
   public abstract void addTranslation(String from, String to);

    /**
     * Retrieves the translation
      * @param input word to translate
     * @return translation if it's available otherwise null
     */
   public abstract String getTranslation(String input);

    /**
     * Retrieves the inverse translation
      * @param input word to translate
     * @return translation if it's available otherwise null
     */
   public abstract String getInverseTranslation(String input);

}
