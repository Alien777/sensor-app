package pl.lasota.sensor.flow;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;
import pl.lasota.sensor.flow.model.FlowSensorI;
import pl.lasota.sensor.flow.model.FlowStatusI;

import java.util.List;


public interface FlowApiInterface {

    FlowStatusI save(@RequestBody FlowSaveI flowSaveI) throws Exception;

    FlowStatusI enabling(@PathVariable(value = "id") Long id) throws Exception;

    FlowStatusI disabling(@PathVariable(value = "id") Long id) throws Exception;

    FlowStatusI fireOnce(@PathVariable(value = "id") Long id) throws Exception;

    FlowStatusI delete(@PathVariable(value = "id") Long id) throws Exception;

    FlowI get(@PathVariable(value = "id") Long id) throws Exception;

    List<FlowI> get() throws Exception;

    @Async
    void valueOfSensor(@RequestBody FlowSensorI sensor) throws Exception;

}
