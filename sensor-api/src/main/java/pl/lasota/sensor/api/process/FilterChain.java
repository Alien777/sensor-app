package pl.lasota.sensor.api.process;

import pl.lasota.sensor.api.exception.SensorApiException;

import java.util.ArrayList;
import java.util.List;


public class FilterChain implements Chain<Object> {
    private final List<Filter> filters = new ArrayList<>();
    private final FilterContext context = new FilterContext();
    private int index = 0;


    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void doFilter(Object request) {
        if (index < filters.size()) {
            Filter filter = filters.get(index++);
            try {
                filter.execute(request, context, this);
                filter.postExecute(request, context);
            } catch (Exception e) {
                throw new SensorApiException("Index of chain " + index + " of " + filters.size(), e);
            }
        }
    }

}

