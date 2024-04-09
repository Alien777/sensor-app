package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.apis.FlowsMicroserviceEndpoint;
import pl.lasota.sensor.core.apis.model.flow.FlowSaveT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.apis.model.flow.FlowT;

import java.util.List;

@RestController
@RequestMapping("/api/flow")
@Slf4j
@RequiredArgsConstructor
public class FlowsController {

    private final FlowsMicroserviceEndpoint sfe;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public FlowStatusT save(@RequestBody FlowSaveT flowT) throws Exception {
        return sfe.save(flowT);
    }

    @PostMapping("/start/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusT startFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.start(id);
    }

    @DeleteMapping("/stop/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusT stopFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.stop(id);
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public List<FlowT> getAll() throws Exception {
        return sfe.get();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowT get(@PathVariable("id") Long id) throws Exception {
        return sfe.get(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable("id") Long id) throws Exception {
        sfe.delete(id);
    }
}
