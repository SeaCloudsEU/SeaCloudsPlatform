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
package eu.atos.sla.datamodel.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import eu.atos.sla.datamodel.ICompensationDefinition;

//@Entity
//@Table(name="compensation")
//@Access(AccessType.FIELD)
@MappedSuperclass
public abstract class CompensationDefinition implements Serializable, ICompensationDefinition {
    private static final long serialVersionUID = 1L;
    
    protected static int DEFAULT_COUNT = 0;
    protected static Date DEFAULT_INTERVAL = new Date(0);
    protected static String DEFAULT_VALUE_EXPRESSION = "";
    protected static String DEFAULT_VALUE_UNIT = "";
    protected static String DEFAULT_ACTION = "";
    protected static String DEFAULT_VALIDITY = "";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    
    @Column(name="kind", nullable=false)
    @Enumerated(EnumType.STRING)
    private CompensationKind kind;

    @Column(name = "time_interval", nullable=false)
    private Date timeInterval;
    
    @Column(name="number", nullable=false)
    private int count;
    
    @Column(name="value_unit", nullable=false)
    private String valueUnit;
    
    @Column(name="value_expression", nullable=false)
    private String valueExpression;
    
    @Column(name="action", nullable=false)
    private String action;
    
    @Column(name="validity", nullable=false)
    private String validity;
    
    public CompensationDefinition() {
        this.kind = ICompensationDefinition.CompensationKind.UNKNOWN;
        this.timeInterval = DEFAULT_INTERVAL;
        this.count = DEFAULT_COUNT;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
        this.valueExpression = DEFAULT_VALUE_EXPRESSION;
        this.valueUnit = DEFAULT_VALUE_UNIT;
    }
    
    /**
     * Constructor for wsag compensations
     */
    protected CompensationDefinition(CompensationKind kind, Date timeInterval,
            String valueUnit, String valueExpression) {
        
        checkNotNull(kind, "kind");
        checkNotNull(timeInterval, "timeInterval");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");

        this.kind = kind;
        this.timeInterval = timeInterval;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;

        this.count = DEFAULT_COUNT;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
    }

    /**
     * Constructor for wsag compensations
     */
    protected CompensationDefinition(CompensationKind kind, 
            int count, String valueUnit, String valueExpression) {    
        
        checkNotNull(kind, "kind");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");

        this.kind = kind;
        this.count = count;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;
        
        this.timeInterval = DEFAULT_INTERVAL;
        this.action = DEFAULT_ACTION;
        this.validity = DEFAULT_VALIDITY;
    }

    /**
     * Constructor for extended compensations
     */
    protected CompensationDefinition(CompensationKind kind, int count, Date timeInterval, String action, 
            String valueUnit, String valueExpression, String validity) {
        
        checkNotNull(kind, "kind");
        checkNotNull(timeInterval, "timeInterval");
        checkNotNull(action, "action");
        checkNotNull(valueUnit, "valueUnit");
        checkNotNull(valueExpression, "valueExpression");
        checkNotNull(validity, "validity");

        this.kind = kind;
        this.count = count;
        this.timeInterval = timeInterval;
        this.valueUnit = valueUnit;
        this.valueExpression = valueExpression;
        this.action = action;
        this.validity = validity;
    }
    
    private void checkNotNull(Object o, String property) {
        if (o == null) {
            throw new NullPointerException(property + " cannot be null");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public CompensationKind getKind() {
        return kind;
    }

    @Override
    public Date getTimeInterval() {
        return timeInterval;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public String getValueUnit() {
        return valueUnit;
    }

    @Override
    public String getValueExpression() {
        return valueExpression;
    }
    
    @Override
    public String getAction() {
        return action;
    }
    
    @Override
    public String getValidity() {
        return validity;
    }
    
    @Override
    public String toString() {
        String fmt = "";
        
        fmt = "<CompensationDefinition("
                + "kind=%s,timeInterval=%d ms,count=%d,action='%s',valueUnit=%s,valueExpression=%s,validity=%s)>";
        return String.format(fmt, 
                kind.toString(),
                timeInterval.getTime(),
                count,
                action,
                valueUnit,
                valueExpression,
                validity);
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + count;
        result = prime * result + ((kind == null) ? 0 : kind.hashCode());
        result = prime * result
                + ((timeInterval == null) ? 0 : timeInterval.hashCode());
        result = prime * result
                + ((validity == null) ? 0 : validity.hashCode());
        result = prime * result
                + ((valueExpression == null) ? 0 : valueExpression.hashCode());
        result = prime * result
                + ((valueUnit == null) ? 0 : valueUnit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CompensationDefinition)) {
            return false;
        }
        CompensationDefinition other = (CompensationDefinition) obj;
        if (action == null) {
            if (other.action != null) {
                return false;
            }
        } else if (!action.equals(other.action)) {
            return false;
        }
        if (count != other.count) {
            return false;
        }
        if (kind != other.kind) {
            return false;
        }
        /*
         * Direct Date compare gives a lot of problems with timezones
         */
        
        if (timeInterval == null) {
            if (other.timeInterval != null) {
                return false;
            }
        } else if (timeInterval.getTime() != other.timeInterval.getTime()) {
            return false;
        }
        if (validity == null) {
            if (other.validity != null) {
                return false;
            }
        } else if (!validity.equals(other.validity)) {
            return false;
        }
        if (valueExpression == null) {
            if (other.valueExpression != null) {
                return false;
            }
        } else if (!valueExpression.equals(other.valueExpression)) {
            return false;
        }
        if (valueUnit == null) {
            if (other.valueUnit != null) {
                return false;
            }
        } else if (!valueUnit.equals(other.valueUnit)) {
            return false;
        }
        return true;
    }

    public static final ICompensationDefinition EMPTY_COMPENSATION_DEFINITION = new ICompensationDefinition() {
        
        @Override
        public String getValueUnit() {
            return DEFAULT_VALUE_UNIT;
        }
        
        @Override
        public String getValueExpression() {
            return DEFAULT_VALUE_EXPRESSION;
        }
        
        @Override
        public Date getTimeInterval() {
            return DEFAULT_INTERVAL;
        }
        
        @Override
        public CompensationKind getKind() {
            return CompensationKind.UNKNOWN;
        }
        
        @Override
        public Long getId() {
            return null;
        }
        
        @Override
        public Integer getCount() {
            return DEFAULT_COUNT;
        }
        
        @Override
        public String getAction() {
            return DEFAULT_ACTION;
        }
        
        @Override
        public String getValidity() {
            return DEFAULT_VALIDITY;
        }
    };
    

}