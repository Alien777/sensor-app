package pl.lasota.sensor.flows.nodes;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
public class CronNode extends Node {

    private final TaskScheduler taskScheduler;
    private final String cron;
    private final long times;

    private long executedTimes;

    @Override
    public synchronized void execute(PrivateContext privateContext) throws Exception {
        Map<String, ScheduledFuture<?>> schedules = privateContext.getSchedules();
        ScheduledFuture<?> schedule = schedules.get(id);
        if (executedTimes > times && schedule != null) {
            schedule.cancel(true);
        } else if (schedule == null) {
            ScheduledFuture<?> newSchedule = taskScheduler.schedule(() -> {
                try {
                    super.execute(privateContext);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    executedTimes++;
                    if (executedTimes > times) {
                        privateContext.getSchedules().get(id).cancel(true);
                        privateContext.getSchedules().remove(id);
                    }
                }
            }, CronTrigger.forFixedExecution(cron));
            schedules.put(id, newSchedule);
        }

    }
}
