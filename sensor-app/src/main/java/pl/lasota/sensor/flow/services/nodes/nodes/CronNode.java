package pl.lasota.sensor.flow.services.nodes.nodes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import pl.lasota.sensor.flow.services.nodes.FlowNode;
import pl.lasota.sensor.flow.services.nodes.Node;
import pl.lasota.sensor.flow.services.nodes.StartFlowNode;
import pl.lasota.sensor.flow.services.nodes.builder.ParserFlows;
import pl.lasota.sensor.flow.services.nodes.utils.FlowContext;
import pl.lasota.sensor.flow.services.nodes.utils.GlobalContext;
import pl.lasota.sensor.flow.services.nodes.utils.LocalContext;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Slf4j
@FlowNode
public class CronNode extends Node implements StartFlowNode, Runnable {

    private final TaskScheduler taskScheduler;
    private final String cron;

    private CronNode(String id, GlobalContext globalContext, TaskScheduler taskScheduler, String cron) {
        super(id, globalContext);
        this.taskScheduler = taskScheduler;
        this.cron = cron;
    }

    public static Node create(String ref, GlobalContext globalContext, JsonNode node, ApplicationContext context) {
        String cron = ParserFlows.fString(node, "cron");
        TaskScheduler contextBean = context.getBean(TaskScheduler.class);
        return new CronNode(ref, globalContext, contextBean, cron);
    }


    public void start(FlowContext flowContext) throws Exception {
        super.propagateFlowContext(flowContext);
        ScheduledFuture<?> newSchedule = taskScheduler.schedule(this, CronTrigger.forFixedExecution(cron));
        Map<String, ScheduledFuture<?>> schedules = super.flowContext.getSchedules();
        schedules.put(id, newSchedule);
    }

    @Override
    public void execute(LocalContext localContext) {
        throw new UnsupportedOperationException("Please execute start instead execute");
    }

    @Override
    public void clear() {
        super.clear();
        ScheduledFuture<?> scheduledFuture = flowContext.getSchedules().get(id);
        if (scheduledFuture != null) {
            flowContext.getSchedules().remove(id);
            scheduledFuture.cancel(true);
        }
    }

    @Override
    public void run() {
        LocalContext localContext = new LocalContext();
        try {
            super.execute(localContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
