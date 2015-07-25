package eu.seaclouds.platform.discoverer.ws;

import eu.seaclouds.platform.discoverer.core.Offering;
import java.util.Calendar;
import java.util.Date;

public class CrawlingResult {
    public Date date;
    public Offering offering;

    public CrawlingResult(Date d, Offering o) {
        this.date = d;
        this.offering = o;
    }

    public CrawlingResult(Offering o) {
        this(Calendar.getInstance().getTime(), o);
    }
}
