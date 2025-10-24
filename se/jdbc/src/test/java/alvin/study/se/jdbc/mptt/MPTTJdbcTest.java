package alvin.study.se.jdbc.mptt;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.jdbc.mptt.model.MPTT;

/**
 * 测试通过 MPTT 在数据表中建立树结构
 *
 * <p>
 * 本例在数据表中建立如下图中的树结构:
 * <img src="assets/mptt.gif"/>
 * </p>
 */
class MPTTJdbcTest extends MPTTBaseTest {
    /**
     * 测试构建 MPTT 数据表记录
     */
    @Test
    @SneakyThrows
    void createMptt_shouldCreateRecordAsTree() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 查询 MPTT 数据表中所有记录
        var records = repository.findAll();

        // 确认记录以及其"父节点 (pid)", "左节点 (lft)", "右节点 (rht)" 都符合预期
        then(records).extracting("id", "name", "pid", "lft", "rht").containsExactly(
            tuple(1L, "Food", 0L, 1L, 18L),
            tuple(3L, "Fruit", 1L, 2L, 11L),
            tuple(5L, "Red", 3L, 3L, 6L),
            tuple(6L, "Cherry", 5L, 4L, 5L),
            tuple(4L, "Yellow", 3L, 7L, 10L),
            tuple(7L, "Banana", 4L, 8L, 9L),
            tuple(2L, "Meat", 1L, 12L, 17L),
            tuple(9L, "Beef", 2L, 13L, 14L),
            tuple(8L, "Pork", 2L, 15L, 16L));
    }

    /**
     * 查询指定节点记录的所有子孙节点记录
     */
    @Test
    @SneakyThrows
    void findChildren_shouldFindAllChildrenOfParentRecord() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 查询一条记录作为父节点记录
        var parent = repository.findByName("Fruit").orElseThrow();

        // 根据父节点记录查询其子节点记录
        var children = repository.findChildren(parent);

        // 确认记录以及其"父节点 (pid)", "左节点 (lft)", "右节点 (rht)" 都符合预期
        then(children).extracting("id", "name", "pid", "lft", "rht").containsExactly(
            tuple(5L, "Red", 3L, 3L, 6L),
            tuple(6L, "Cherry", 5L, 4L, 5L),
            tuple(4L, "Yellow", 3L, 7L, 10L),
            tuple(7L, "Banana", 4L, 8L, 9L));
    }

    /**
     * 测试查找两个 {@link MPTT} 类型对象对应的树节点之间的"路径"
     */
    @Test
    @SneakyThrows
    void findPath_shouldFindRecordsInPath() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 选择两个节点
        var start = repository.findByName("Food").orElseThrow();
        var end = repository.findByName("Banana").orElseThrow();

        // 获取所选节点的之间的路径
        var path = repository.findPath(start, end);
        // 确认查询的路径符合预期
        then(path).extracting("id", "name", "pid", "lft", "rht")
                .containsExactly(
                    tuple(1L, "Food", 0L, 1L, 18L),
                    tuple(3L, "Fruit", 1L, 2L, 11L),
                    tuple(4L, "Yellow", 3L, 7L, 10L),
                    tuple(7L, "Banana", 4L, 8L, 9L));

        // 重新选择两个节点
        start = repository.findByName("Fruit").orElseThrow();
        end = repository.findByName("Pork").orElseThrow();

        // 获取所选节点的之间的路径
        path = repository.findPath(start, end);
        // 确认所选节点之间不存在路径
        then(path).isEmpty();
    }

    /**
     * 查询记录中的叶子节点, 即树中最后一层的节点
     */
    @Test
    @SneakyThrows
    void findLeaves_shouldFindAllLeafRecords() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 查询叶子节点记录
        var leaves = repository.findLeaves();
        // 确认查询结果符合预期
        then(leaves).extracting("id", "name", "pid", "lft", "rht")
                .containsExactly(
                    tuple(6L, "Cherry", 5L, 4L, 5L),
                    tuple(7L, "Banana", 4L, 8L, 9L),
                    tuple(9L, "Beef", 2L, 13L, 14L),
                    tuple(8L, "Pork", 2L, 15L, 16L));
    }

    /**
     * 查询直属子节点 (即只包含子节点, 不包含孙节点)
     */
    @Test
    @SneakyThrows
    void findImmediateChildren_shouldImmediateChildRecords() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 查询一条记录作为父节点
        var parent = repository.findByName("Fruit").orElseThrow();

        // 查询父节点记录的直属子节点记录集合
        var children = repository.findImmediateChildren(parent.getId());
        // 确认查询结果符合预期
        then(children).extracting("id", "name", "pid", "lft", "rht")
                .containsExactly(
                    tuple(5L, "Red", 3L, 3L, 6L),
                    tuple(4L, "Yellow", 3L, 7L, 10L));
    }

    /**
     * 将一个节点记录作为"兄弟节点"加入
     */
    @Test
    @SneakyThrows
    void createAsSibling_shouldCreateSiblingRecord() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 获取一个节点作为兄弟节点
        var sibling = repository.findByName("Yellow").orElseThrow();

        getConnectionManager().beginTransaction();
        try {
            // 创建记录作为所给节点的兄弟节点
            repository.createAsSibling(new MPTT("Green"), sibling.getId());

            getConnectionManager().commit();
        } catch (Exception e) {
            getConnectionManager().rollback();
            throw e;
        }

        // 获取所有节点记录
        var records = repository.findAll();
        // 确认兄弟节点已经被加入
        then(records).extracting("id", "name", "pid", "lft", "rht")
                .containsExactly(
                    tuple(1L, "Food", 0L, 1L, 20L),
                    tuple(3L, "Fruit", 1L, 2L, 13L),
                    tuple(5L, "Red", 3L, 3L, 6L),
                    tuple(6L, "Cherry", 5L, 4L, 5L),
                    tuple(4L, "Yellow", 3L, 7L, 10L),
                    tuple(7L, "Banana", 4L, 8L, 9L),
                    tuple(10L, "Green", 3L, 11L, 12L),
                    tuple(2L, "Meat", 1L, 14L, 19L),
                    tuple(9L, "Beef", 2L, 15L, 16L),
                    tuple(8L, "Pork", 2L, 17L, 18L));
    }
}
