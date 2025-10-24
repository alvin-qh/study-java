package alvin.study.se.jdbc.mptt;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import alvin.study.se.jdbc.mptt.model.MPTT;
import alvin.study.se.jdbc.mptt.model.MPTTTree;

/**
 * 测试通过 {@link MPTTTree} 类型来处理数据记录, 将数据整理为一颗树
 *
 * <p>
 * 本例在数据表中建立如下图中的树结构:
 * <img src="../../../../../../../assets/mptt.gif"/>
 * </p>
 */
class MPTTTreeTest extends MPTTBaseTest {
    /**
     * 通过 {@link MPTT} 类型数据集合创建 {@link MPTTTree} 类型对象
     */
    @Test
    @SneakyThrows
    void buildTree_shouldBuildTreeStructByMPTTRecords() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 创建 MPTTTree 类型对象
        var tree = MPTTTree.build(repository.findAll());
        // 确认根节点
        then(tree.getRoot()).extracting("id", "name", "lft", "rht").containsExactly(1L, "Food", 1L, 18L);

        // 通过迭代器将 MPTTTree 内元素转为 List 集合
        var nodes = StreamSupport.stream(tree.spliterator(), false).toList();

        // 确认迭代器迭代顺序, 为广度优先遍历结果
        then(nodes).extracting("id", "name", "lft", "rht").containsExactly(
            tuple(1L, "Food", 1L, 18L),
            tuple(3L, "Fruit", 2L, 11L),
            tuple(2L, "Meat", 12L, 17L),
            tuple(5L, "Red", 3L, 6L),
            tuple(4L, "Yellow", 7L, 10L),
            tuple(9L, "Beef", 13L, 14L),
            tuple(8L, "Pork", 15L, 16L),
            tuple(6L, "Cherry", 4L, 5L),
            tuple(7L, "Banana", 8L, 9L));
    }

    /**
     * 测试对 {@link MPTTTree} 类型对象的各种操作
     */
    @Test
    @SneakyThrows
    void treeOperation_shouldOperateTree() {
        // 构建 MPTT 树记录
        createMptt(buildTree());

        // 创建 MPTTTree 类型对象
        var tree = MPTTTree.build(repository.findAll());

        // 保存遍历结果的集合
        var results = new ArrayList<MPTT>();

        // 创建用于深度优先遍历的栈, 并将根节点入栈
        var stack = new ArrayDeque<>(List.of(tree.getRoot()));
        // 通过栈遍历节点
        while (!stack.isEmpty()) {
            // 弹出栈顶元素
            var parent = stack.pop();

            // 保存本次栈顶元素, 表示已经访问过此节点
            results.add(parent);

            // 遍历栈顶元素的子节点
            tree.children(parent).forEach(c -> {
                // 确认子节点的父节点为栈顶元素
                then(tree.parent(c)).isPresent().get().isEqualTo(parent);
                // 将子节点入栈
                stack.push(c);
            });
        }

        // 确认遍历结果为深度遍历结果
        then(results).extracting("id", "name", "lft", "rht").containsExactly(
            tuple(1L, "Food", 1L, 18L),
            tuple(2L, "Meat", 12L, 17L),
            tuple(8L, "Pork", 15L, 16L),
            tuple(9L, "Beef", 13L, 14L),
            tuple(3L, "Fruit", 2L, 11L),
            tuple(4L, "Yellow", 7L, 10L),
            tuple(7L, "Banana", 8L, 9L),
            tuple(5L, "Red", 3L, 6L),
            tuple(6L, "Cherry", 4L, 5L));
    }
}
