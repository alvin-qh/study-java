package alvin.study.springboot.graphql.core.context;

public final class ContextHolder {
    private static final ThreadLocal<Context> HOLDER = new ThreadLocal<>();

    public static void setValue(Context ctx) {
        HOLDER.set(ctx);
    }

    public static Context getValue() { return HOLDER.get(); }

    public static void reset() {
        HOLDER.remove();
    }
}
