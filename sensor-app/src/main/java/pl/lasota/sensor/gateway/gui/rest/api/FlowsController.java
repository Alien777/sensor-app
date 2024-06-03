package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.flow.FlowApiInterface;
import pl.lasota.sensor.flow.services.FlowDataService;
import pl.lasota.sensor.flow.model.FlowI;
import pl.lasota.sensor.flow.model.FlowSaveI;
import pl.lasota.sensor.flow.model.FlowStatusI;

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
    public FlowStatusI save(@RequestBody FlowSaveI flowT) throws Exception {
        return sfe.save(flowT);
    }

    @PostMapping("/start/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusI startFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.enabling(id);
    }

    @DeleteMapping("/stop/{id}")
    @PreAuthorize("isAuthenticated()")
    public FlowStatusI stopFlows(@PathVariable("id") Long id) throws Exception {
        return sfe.disabling(id);
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
