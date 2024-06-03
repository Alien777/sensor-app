package pl.lasota.sensor.flow.model;

import java.io.Serializable;

public enum FlowStatusI implements Serializable {
    OK, NOT_FOUND, ERROR, IS_ACTIVE_ALREADY, NOT_ACTIVE
}
