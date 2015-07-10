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
package eu.atos.sla.enforcement.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.atos.sla.dao.IAgreementDAO;
import eu.atos.sla.dao.IEnforcementJobDAO;
import eu.atos.sla.datamodel.IEnforcementJob;

/**
 * This class is the root of the execution of the enforcement tasks.
 * 
 * When in a spring context, is executed periodically according to the value of <code>spawnlookup.cron</code>.
 * 
 * Once running, it finds the enforcement jobs to run since some date (<code>poll.interval</code>
 * dependent) and schedules a task to check each agreement.
 * 
 * 
 * Following properties must have been set before running in production mode
 * 
 * eu.atos.sla.enforcement.spawnlookup.cron=*\/5 * * * * *
 * eu.atos.sla.enforcement.poll.interval.mseconds=30000
 */
@Component
@Transactional
public class ScheduledEnforcementWorker implements InitializingBean, IScheduledEnforcementWorker {
    private static final String POLL_INTERVAL = "eu.atos.sla.enforcement.poll.interval.mseconds";

    private static final String CRON = "eu.atos.sla.enforcement.spawnlookup.cron";

    private static Logger logger = LoggerFactory.getLogger(ScheduledEnforcementWorker.class);


    @Value("ENF{" + CRON + "}")
    private String cron;
    
    @Value("ENF{" + POLL_INTERVAL + "}")
    private String pollIntervalString;
    private long pollInterval;
    
    @Autowired
    private IEnforcementJobDAO enforcementJobDAO;

    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private IAgreementDAO agreementDAO;

    /* (non-Javadoc)
     * @see eu.atos.sla.enforcement.IScheduledEnforcementWorker#spawnMonitors()
     */
    
    
    @Override
    @Scheduled(cron = "ENF{" + CRON + "}")
    public void spawnMonitors() {
        Date since = computeOffset();
        List<IEnforcementJob> nonExecuted = enforcementJobDAO.getNotExecuted(since);
        logger.debug("spawning {} tasks", nonExecuted.size());
        for (IEnforcementJob job : nonExecuted) {
            try{
                EnforcementTask et = new EnforcementTask(job);
                applicationContext.getAutowireCapableBeanFactory().autowireBean(et);
                taskExecutor.execute(et);
            }catch(Throwable t){
                logger.error("Error while executing enforcement job",t);                
            }
        }
    }
    
    
    private Date computeOffset(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -((int)(pollInterval)));
        return calendar.getTime();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            pollInterval = Long.parseLong(pollIntervalString);
        }catch(NumberFormatException npe){
            String str = String.format("Can not parse ENF{%s} value{%s}. Is it a number?", 
                    POLL_INTERVAL, pollIntervalString); 
            throw new IllegalArgumentException(str);
        }
        
        
        logger.debug("EnforcementWorker registered, cron[{}], interval[{}]", 
                cron, pollIntervalString);
    }
    

}
