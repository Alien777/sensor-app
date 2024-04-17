package pl.lasota.sensor.flows.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.flows.nodes.Node;

import java.util.List;

@Getter
@AllArgsConstructor
public class ActiveFlow {
    private List<Node> roots;
    private String config;
}
