package pl.lasota.sensor.flows.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitJobs {
    private final Scheduler scheduler;

    @PostConstruct
    public void init() throws SchedulerException {
        JobDetail reactiveJob = JobBuilder.newJob(ReactiveJob.class)
                .withIdentity("reactive_job", "flows_job")
                .build();
        Trigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("reactive_job_" + UUID.randomUUID(), "flows_job")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(reactiveJob, trigger1);
    }
}
