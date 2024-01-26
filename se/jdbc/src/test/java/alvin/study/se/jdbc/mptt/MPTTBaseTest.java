package alvin.study.se.jdbc.mptt;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;

import alvin.study.se.jdbc.JDBCBaseTest;
import alvin.study.se.jdbc.mptt.model.MPTT;
import alvin.study.se.jdbc.mptt.repository.MPTTRepository;

/**
 * MPTT 测试超类
 */
abstract class MPTTBaseTest extends JDBCBaseTest {
    // MPTT 存储对象
    protected final MPTTRepository repository = new MPTTRepository();

    /**
     * 在每次测试执行前执行, 并准备数据表
     */
    @Override
    @BeforeEach
    protected void beforeEach() throws SQLException {
        super.beforeEach();

        var conn = getConnectionManager().get();

        // 对测试用数据表进行清空操作
        try (var stat = conn.prepareStatement("truncate table `mptt`")) {
            stat.executeUpdate();
        }
    }

    /**
     * 创建测试用的树形结构数据
     *
     * @return 表示树形结构的根节点的 {@link TreeNode} 类型对象
     */
    protected TreeNode buildTree() {
        return new TreeNode("Food",
            List.of(
                new TreeNode("Meat",
                    List.of(
                        new TreeNode("Pork", List.of()),
                        new TreeNode("Beef", List.of()))),
                new TreeNode("Fruit",
                    List.of(
                        new TreeNode("Yellow",
                            List.of(
                                new TreeNode("Banana", List.of()))),
                        new TreeNode("Red",
                            List.of(
                                new TreeNode("Cherry", List.of())))))));
    }

    /**
     * 根据表示根节点的 {@link TreeNode} 对象, 在数据表中创建符合 MPTT 规范的记录
     *
     * @param root 表示根节点的 {@link TreeNode} 对象
     */
    protected void createMptt(TreeNode root) throws SQLException {
        getConnectionManager().beginTransaction();

        try {
            // 创建保存树节点和数据表记录 ID 对应关系的 Map 对象
            // 存储根节点对象, 并保存根节点和数据表 ID 的关系
            var recordIdMap = new HashMap<>(
                Map.of(root, repository.createAsRoot(new MPTT(root.value())).getId()));

            // 构建用于深度优先遍历的栈对象, 并存储根节点
            var stack = new ArrayDeque<>(List.of(root));

            // 通过栈进行深度优先遍历
            while (!stack.isEmpty()) {
                // 获取栈顶元素作为当前节点
                var parentNode = stack.pop();
                // 查询到当前节点对应的 ID
                var parentId = recordIdMap.get(parentNode);

                // 遍历当前节点的子节点
                for (var c : parentNode.children()) {
                    // 通过子节点创建数据表实体对象
                    var child = new MPTT(c.value());

                    // 并存储子节点对象, 并保存子节点和数据表 ID 的对应关系
                    recordIdMap.put(c, repository.createAsChild(child, parentId).getId());
                    // 将子节点放入栈中
                    stack.push(c);
                }
            }

            // 完成操作提交事务
            getConnectionManager().commit();
        } catch (Exception e) {
            // 异常回滚事务
            getConnectionManager().rollback();
            throw new SQLException(e);
        }
    }

    /**
     * 定义树节点以存储测试数据
     *
     * @param value    节点名称
     * @param children 子节点集合
     */
    private record TreeNode(String value, List<@NotNull TreeNode> children) { }
}
