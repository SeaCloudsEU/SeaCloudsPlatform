/**
 * Copyright 2014 Atos
 * Contact: Atos <roman.sosa@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.atos.sla.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeAdapter extends XmlAdapter<String, Date>{ 
    private static Logger logger = LoggerFactory.getLogger(DateTimeAdapter.class);
    
    static private String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";

    static private String unmarshallTimezone = TimeZone.getDefault().getDisplayName();

    static private String marshallTimezone = TimeZone.getDefault().getDisplayName();
    
    public String getDateFormat() {
        return dateFormat;
    }

    public String getUnmarshallTimezone() {
        return unmarshallTimezone;
    }

    public String getMarshallTimezone() {
        return marshallTimezone;
    }

    public void setDateFormat(String dateFormat) {
        DateTimeAdapter.dateFormat = dateFormat;
    }

    public void setUnmarshallTimezone(String unmarshallTimezone) {
        DateTimeAdapter.unmarshallTimezone = unmarshallTimezone;
    }

    public void setMarshallTimezone(String marshallTimezone) {
        DateTimeAdapter.marshallTimezone = marshallTimezone;
    }

    @Override
    public Date unmarshal(String dateAsString) throws Exception {
        Date date = null;
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            formatter.setTimeZone(TimeZone.getTimeZone(unmarshallTimezone));
            date = formatter.parse(dateAsString);
        }catch(Throwable t){
            throw new ParserException(
                    "Date " + dateAsString + " couldn't be unmarshal with " + dateFormat + 
                    " and timezone "+unmarshallTimezone, t);
        }
        logger.debug(date +"{} has been unmarshalled to {}", dateAsString, date);
        return date;
    }

    
    @Override
    public String marshal(Date date) throws Exception {
        String dateAsString = null; 
        try{
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            formatter.setTimeZone(TimeZone.getTimeZone(marshallTimezone));
            dateAsString = formatter.format(date);
        }catch(Throwable t){
            throw new ParserException(
                    "Date " + date + " couldn't be marshal with " + dateFormat + 
                    " and timezone " + marshallTimezone, t);
        }
        logger.debug(date +"{} has been mashalled to {}", date, dateAsString);
        return dateAsString;
    }

}
