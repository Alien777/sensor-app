package pl.lasota.sensor.flow;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;

import java.util.List;


public interface FlowApiInterface {

    void save(@RequestBody FlowSaveI flowSaveI) throws SensorFlowException;

    void startFlows(@PathVariable(value = "id") Long id) throws SensorFlowException;

    void stopFlows(@PathVariable(value = "id") Long id) throws SensorFlowException;

    void fireOnce(@PathVariable(value = "id") Long id) throws SensorFlowException;

    void restartFlow(@PathVariable(value = "id") Long id) throws SensorFlowException;

    void delete(@PathVariable(value = "id") Long id) throws SensorFlowException;

    FlowI get(@PathVariable(value = "id") Long id) throws SensorFlowException;

    List<FlowI> get() throws SensorFlowException;

}
