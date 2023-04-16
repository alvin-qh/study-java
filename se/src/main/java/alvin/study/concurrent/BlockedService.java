package alvin.study.concurrent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;

public class BlockedService {
    public record Model(long id, String name) {}

    private Map<Long, Model> modelMap = new ConcurrentHashMap<>();

    public BlockedService() {}

    @SafeVarargs
    public BlockedService(Model... initModels) {
        for (var m : initModels) {
            modelMap.put(m.id(), m);
        }
    }

    public boolean createModel(Model model) {
        delay();

        if (modelMap.containsKey(model.id())) {
            return false;
        }

        modelMap.put(model.id(), model);
        return true;
    }

    public Optional<Model> loadModel(long id) {
        delay();

        return Optional.ofNullable(modelMap.get(id));
    }

    @SneakyThrows
    private static void delay() {
        Thread.sleep(1000);
    }
}
