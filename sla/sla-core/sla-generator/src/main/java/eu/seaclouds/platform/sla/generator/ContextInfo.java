/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.sla.generator;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

public class ContextInfo {
    private final String provider;
    private final String consumer;
    private final String service;
    private final Validity validity;
    
    public ContextInfo(String provider, String consumer, String service, Validity validity) {
        this.provider = provider;
        this.consumer = consumer;
        this.service = service;
        this.validity = validity;
    }
    
    public ContextInfo(String provider, String consumer, String service, String duration) {
        this.provider = provider;
        this.consumer = consumer;
        this.service = service;
        this.validity = DurationParser.parse(duration);
    }

    public String getProvider() {
        return provider;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getService() {
        return service;
    }
    
    public Validity getValidity() {
        return validity;
    }
    
    public Date calcExpirationTime(Date now) {
        
        Date result = validity.add(now);
        return result;
    }
    
    public Date calcExpirationTime() {
        Date result = calcExpirationTime(new Date());
        return result;
    }
    
    public static class Validity {
        private int years;
        private int months;
        private int days;
        
        public Validity(int years, int months, int days) {
            this.years = years;
            this.months = months;
            this.days = days;
        }

        public int getYears() {
            return years;
        }

        public int getMonths() {
            return months;
        }

        public int getDays() {
            return days;
        }
        
        public Date add(Date date) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            
            c.add(Calendar.YEAR, years);
            c.add(Calendar.MONTH, months);
            c.add(Calendar.DATE, days);
            
            Date result = c.getTime();
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + days;
            result = prime * result + months;
            result = prime * result + years;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Validity other = (Validity) obj;
            if (days != other.days)
                return false;
            if (months != other.months)
                return false;
            if (years != other.years)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("Validity [years=%s, months=%s, days=%s]",
                    years, months, days);
        }
    }
    
    public static class DurationParser {
         
        private static DatatypeFactory dtFactory;

        public static Validity parse(String duration) {
            try {
                if (dtFactory == null) {
                    dtFactory = DatatypeFactory.newInstance();
                }
                Duration d = dtFactory.newDuration(duration);
                Validity result = new Validity(
                        d.getYears(),
                        d.getMonths(),
                        d.getDays()
                );
                return result;
            } catch (DatatypeConfigurationException e) {
                throw new SlaGeneratorException("Could not instantiate DatatypeFactory", e);
            }
        }
    }
}