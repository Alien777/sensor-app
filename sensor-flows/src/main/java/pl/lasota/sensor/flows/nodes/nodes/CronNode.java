package pl.lasota.sensor.flows.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import pl.lasota.sensor.flows.nodes.FlowNode;
import pl.lasota.sensor.flows.nodes.Node;
import pl.lasota.sensor.flows.nodes.StartFlowNode;
import pl.lasota.sensor.flows.nodes.utils.GlobalContext;
import pl.lasota.sensor.flows.nodes.utils.LocalContext;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static pl.lasota.sensor.flows.nodes.builder.ParserFlows.fString;


@Slf4j
@FlowNode
public class CronNode extends Node implements StartFlowNode {

    private final TaskScheduler taskScheduler;
    private final String cron;

    private CronNode(String id, GlobalContext globalContext, TaskScheduler taskScheduler, String cron) {
        super(id, globalContext);
        this.taskScheduler = taskScheduler;
        this.cron = cron;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String cron = fString(node, "cron");
        TaskScheduler contextBean = context.getBean(TaskScheduler.class);
        return new CronNode(ref, globalContext, contextBean, cron);
    }


    public boolean start() {
        try {
            ScheduledFuture<?> newSchedule = taskScheduler.schedule(() -> {
                try {
                    if (globalContext.isStopped()) {
                        return;
                    }
                    LocalContext localContext = new LocalContext();
                    super.execute(localContext);
                } catch (Exception e) {
                    log.error("Occurred flow exception ", e);
                }
            }, CronTrigger.forFixedExecution(cron));
            Map<String, ScheduledFuture<?>> schedules = globalContext.getSchedules();
            schedules.put(id, newSchedule);
        } catch (Exception e) {
            log.error("Problem with start node ", e);
            return false;
        }
        return true;
    }

    @Override
    public void execute(LocalContext localContext) {
        throw new UnsupportedOperationException("Please execute start instead execute");
    }

    @Override
    public void clear() {
        globalContext.stopFlow();
        ScheduledFuture<?> scheduledFuture = globalContext.getSchedules().get(id);
        if (scheduledFuture != null) {
            globalContext.getSchedules().remove(id);
            scheduledFuture.cancel(true);
        }
        super.clear();
    }
}
