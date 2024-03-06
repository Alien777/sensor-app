package pl.lasota.sensor.flows.nodes.nodes.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import pl.lasota.sensor.flows.nodes.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.PrivateContext;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@FlowNode
public class CronNode extends Node {

    private long executedTimes;

    public CronNode(PrivateContext privateContext, TaskScheduler taskScheduler, boolean fastInitialization,  String cron, String executeTimesKey) {
        super(privateContext);
        ScheduledFuture<?> newSchedule = taskScheduler.schedule(() -> {
            try {
                super.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                executedTimes++;
                long howManyTimes = (long) privateContext.getVariable(executeTimesKey, -1L);
                if (howManyTimes != -1 && executedTimes > howManyTimes) {
                    privateContext.getSchedules().get(id).cancel(true);
                    privateContext.getSchedules().remove(id);
                }
            }
        }, CronTrigger.forFixedExecution(cron));
        Map<String, ScheduledFuture<?>> schedules = privateContext.getSchedules();
        schedules.put(id, newSchedule);
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public void clear() {
        privateContext.getSchedules().get(id).cancel(true);
        privateContext.getSchedules().remove(id);
        super.clear();
    }
}
