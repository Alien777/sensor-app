package pl.lasota.sensor.flows;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lasota.sensor.flows.nodes.Node;

@Getter
@AllArgsConstructor
public class ActiveFlow {
    private Node root;
    private String config;
}
