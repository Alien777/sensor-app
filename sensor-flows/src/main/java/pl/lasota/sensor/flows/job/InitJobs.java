package pl.lasota.sensor.flows.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        Date startTime = DateBuilder.futureDate(30, DateBuilder.IntervalUnit.SECOND);

        Trigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("reactive_job_" + UUID.randomUUID(), "flows_job")
                .startAt(startTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(reactiveJob, trigger1);
    }
}
