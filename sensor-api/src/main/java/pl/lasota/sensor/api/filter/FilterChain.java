package pl.lasota.sensor.api.filter;

import pl.lasota.sensor.core.exceptions.FilterExecuteException;

import java.util.ArrayList;
import java.util.List;


public class FilterChain implements Chain<Object> {
    private final List<Filter> filters = new ArrayList<>();
    private final Context context = new Context();
    private int index = 0;


    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void doFilter(Object request) throws FilterExecuteException {
        if (index < filters.size()) {
            Filter filter = filters.get(index++);
            try {
                filter.execute(request, context, this);
                filter.postExecute(request, context);
            } catch (Exception e) {
                throw new FilterExecuteException("Index of chain " + index + " of " + filters.size(), e);
            }
        }
    }

}

