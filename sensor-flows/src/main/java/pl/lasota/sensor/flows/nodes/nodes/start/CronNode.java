package pl.lasota.sensor.flows.nodes.nodes.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@FlowNode
public class CronNode extends Node {


    public CronNode(String id, GlobalContext globalContext, TaskScheduler taskScheduler, String cron) {
        super(id, globalContext);
        ScheduledFuture<?> newSchedule = taskScheduler.schedule(() -> {
            try {
                LocalContext localContext = new LocalContext();
                super.execute(localContext);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, CronTrigger.forFixedExecution(cron));
        Map<String, ScheduledFuture<?>> schedules = globalContext.getSchedules();
        schedules.put(id, newSchedule);
    }

    @Override
    public void execute(LocalContext localContext) throws Exception {

    }

    @Override
    public void clear() {
        globalContext.getSchedules().get(id).cancel(true);
        globalContext.getSchedules().remove(id);
        super.clear();
    }
}
