package pl.lasota.sensor.api.mqtt.filter;

import java.util.ArrayList;
import java.util.List;


public class FilterChain implements Chain<Object> {
    private final List<Filter> filters = new ArrayList<>();
    private int index = 0;

    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void doFilter(Object request) {
        if (index < filters.size()) {
            Filter filter = filters.get(index++);
            filter.execute(request, this);
        }
    }
}

