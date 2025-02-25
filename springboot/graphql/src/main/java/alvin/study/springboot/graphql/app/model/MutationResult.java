package alvin.study.springboot.graphql.app.model;

public record MutationResult<T>(T result) {
    public static <T> MutationResult<T> of(T result) {
        return new MutationResult<>(result);
    }
}
