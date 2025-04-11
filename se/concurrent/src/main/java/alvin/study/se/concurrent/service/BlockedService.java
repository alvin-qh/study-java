package alvin.study.se.concurrent.service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用于测试异步调用的服务类
 *
 * <p>
 * 该类中的公共方法均进行阻塞, 以模拟实际场景中的 IO 延迟等
 * </p>
 */
@NoArgsConstructor
public class BlockedService {
    // 每次操作需要等待的时间, 单位毫秒
    private static final long DELAY_MILLS = 100;

    // 保存 id 和模型对象对应关系的 Map 对象
    private final Map<Long, Model> modelMap = new ConcurrentHashMap<>();

    /**
     * 构造器, 通过模型对象数组进行初始化
     *
     * @param initModels 模型对象数组, 其元素作为当前对象的初始存储内容
     */
    public BlockedService(Model... initModels) {
        Arrays.stream(initModels).forEach(m -> modelMap.put(m.id(), m));
    }

    /**
     * 将模型进行保存
     *
     * @param model 要保存的模型对象
     * @return 是否保存成功
     */
    public boolean saveModel(Model model) {
        delay();

        if (modelMap.containsKey(model.id())) {
            return false;
        }

        modelMap.put(model.id(), model);
        return true;
    }

    /**
     * 根据 id 读取模型对象
     *
     * @param id 模型的 id 值
     * @return 模型对象的 {@link Optional} 包装对象
     */
    public Optional<Model> loadModel(long id) {
        delay();
        return Optional.ofNullable(modelMap.get(id));
    }

    /**
     * 令当前线程阻塞 1s, 以模拟 IO 延迟等阻塞情况
     */
    @SneakyThrows
    private void delay() {
        Thread.sleep(DELAY_MILLS);
    }

    // 模型类型
    public record Model(long id, String name) {}
}
