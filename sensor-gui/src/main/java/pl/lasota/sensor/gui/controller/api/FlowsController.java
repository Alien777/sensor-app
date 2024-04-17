package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.internal.apis.api.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.flows.FlowI;
import pl.lasota.sensor.internal.apis.api.flows.FlowSaveI;
import pl.lasota.sensor.internal.apis.api.flows.FlowStatusI;

import java.util.List;


@RestController
@RequestMapping("/api/flow")
@Slf4j
@RequiredArgsConstructor
public class FlowsController {

    private final FlowsMicroserviceEndpoint sfe;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public FlowStatusI save(@RequestBody FlowSaveI flowT) throws Exception {
        return sfe.save(flowT);
    }

    @PostMapping("/start/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusI startFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.start(id);
    }

    @DeleteMapping("/stop/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusI stopFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.stop(id);
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public List<FlowI> getAll() throws Exception {
        return sfe.get();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowI get(@PathVariable("id") Long id) throws Exception {
        return sfe.get(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable("id") Long id) throws Exception {
        sfe.delete(id);
    }
}
