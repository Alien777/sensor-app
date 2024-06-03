package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.flow.FlowApiInterface;
import pl.lasota.sensor.exceptions.SensorFlowException;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;
import pl.lasota.sensor.flow.services.FlowDataService;

import java.util.List;


@RestController
@RequestMapping("/api/flow")
@Slf4j
@RequiredArgsConstructor
public class FlowsController {

    private final FlowApiInterface sfe;
    private final FlowDataService fds;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public void save(@RequestBody FlowSaveI flowT) throws SensorFlowException {
        sfe.save(flowT);
    }

    @PostMapping("/start/{id}")
    @PreAuthorize("isAuthenticated()")
    public void startFlows(@PathVariable("id") Long id) throws SensorFlowException {
        sfe.enabling(id);
    }

    @DeleteMapping("/stop/{id}")
    @PreAuthorize("isAuthenticated()")
    public void stopFlows(@PathVariable("id") Long id) throws SensorFlowException {
        sfe.disabling(id);
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public List<FlowI> getAll() throws SensorFlowException {
        return sfe.get();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowI get(@PathVariable("id") Long id) throws SensorFlowException {
        return sfe.get(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable("id") Long id) throws SensorFlowException {
        sfe.delete(id);
    }
}
