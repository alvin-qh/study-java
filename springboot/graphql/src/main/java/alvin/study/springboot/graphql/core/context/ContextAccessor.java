package alvin.study.springboot.graphql.core.context;

import io.micrometer.context.ThreadLocalAccessor;

public class ContextAccessor implements ThreadLocalAccessor<Context> {
    public static final String KEY = ContextAccessor.class.getCanonicalName();

    @Override
    public Object key() {
        return KEY;
    }

    @Override
    public Context getValue() { return ContextHolder.getValue(); }

    @Override
    public void setValue(Context ctx) {
        ContextHolder.setValue(ctx);
    }

    @Override
    public void setValue() {
        ContextHolder.reset();
    }
}
