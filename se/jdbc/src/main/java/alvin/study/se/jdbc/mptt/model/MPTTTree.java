package alvin.study.se.jdbc.mptt.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * MPTT 树结构类型
 *
 * <p>
 * 将 {@link MPTT} 类型数据记录集合整理为树形结构, 并提供相关的访问方法
 * </p>
 */
public class MPTTTree implements Iterable<MPTT> {
    // 根节点对象
    private final Node root;
    // 保存 MPTT 对象和其对应节点的 Map 对象
    private final Map<MPTT, Node> nodeMap;

    /**
     * 构造器, 通过根节点和 {@code MPTT => Node} 字典构建对象
     *
     * @param root    根节点对象
     * @param nodeMap MPTT 对象和其对应节点的 Map 对象
     */
    private MPTTTree(Node root, Map<MPTT, Node> nodeMap) {
        this.root = root;
        this.nodeMap = nodeMap;
    }

    /**
     * 通过一个 {@link MPTT} 类型对象集合创建 {@link MPTTTree} 类型对象
     *
     * @param vals {@link MPTT} 类型对象集合
     * @return {@link MPTTTree} 类型对象
     */
    @Contract("_ -> new")
    public static @NotNull MPTTTree build(@NotNull List<MPTT> vals) {
        // 将 vals 参数中的元素进行排序
        // 根据对象的 lft 字段排序, 结果为上级节点在前, 下级节点在后
        // 并且将所有的 MPTT 对象包装为 Node 对象
        var nodes = vals.stream().sorted((l, r) -> (int) (l.getLft() - r.getLft())).map(Node::new).toList();

        // 定义用于查找父节点的栈
        var stack = new ArrayDeque<>(List.of(nodes.get(0)));

        // 定义 MPTT => Node 对应关系的
        var nodeMap = new HashMap<MPTT, Node>();

        // 遍历节点集合
        nodes.forEach(n -> {
            // 查找该节点的父节点
            var parent = findParent(stack, n.value);
            if (parent != null) {
                // 当前节点引用到父节点
                n.parent = parent;
                // 为父节点增加子节点
                parent.addChildren(n);
            }

            // 将该节点入栈
            stack.push(n);
            // 记录节点值和节点的对应关系
            nodeMap.put(n.value, n);
        });

        return new MPTTTree(nodes.get(0), Map.copyOf(nodeMap));
    }

    /**
     * 根据 {@link MPTT} 对象值获取其对应节点的父节点
     *
     * @param stack 保存已访问节点的栈
     * @param val   当前节点的 {@link MPTT} 对象值
     * @return {@link MPTT} 对象对应对象的父节点对象
     */
    private static @Nullable Node findParent(@NotNull ArrayDeque<Node> stack, MPTT val) {
        // 按照栈的顺序查找已经访问过的节点
        // 因为遍历 MPTT 记录的顺序时按照 lft 节点排序结果进行的, 所以子节点一定会在父节点之后
        while (!stack.isEmpty()) {
            // 获取栈顶元素
            var node = stack.peek();
            // 查看栈顶元素是否为当前节点的父节点, 即当前节点的 lft 和 rht 在父节点对象 lft 和 rht 范围内
            if (node.value.getLft() < val.getLft() && node.value.getRht() > val.getRht()) {
                // 返回父节点
                return node;
            }

            // 如果栈顶元素不是当前节点的父节点, 说明栈顶元素已经不可能再有子节点, 将其弹出
            stack.pop();
        }
        return null;
    }

    /**
     * 获取根节点对象
     *
     * @return 根节点对象
     */
    public MPTT getRoot() {
        return root.value;
    }

    /**
     * 通过子节点值, 获取父节点值
     *
     * @param child 和子节点对应的 {@link MPTT} 类型对象
     * @return 和父节点对应的 {@link MPTT} 类型对象
     */
    public Optional<MPTT> parent(MPTT child) {
        // 获取 MPTT 对象对应的 Node 对象
        var node = nodeMap.get(child);
        // 获取子 Node 对象的父节点对象
        return Optional.ofNullable(node.parent.value);
    }

    /**
     * 获取 {@link MPTT} 对象对应节点的子节点值
     *
     * @param parent 父节点对应的 {@link MPTT} 对象
     * @return 子节点对应的 {@link MPTT} 对象集合
     */
    public List<MPTT> children(MPTT parent) {
        var node = nodeMap.get(parent);
        return node.getChildren().stream().map(c -> c.value).toList();
    }

    /**
     * 获取一个迭代器对象, 以广度优先原则 (BFS) 对树中的节点进行遍历, 每次迭代一个节点, 返回该节点对应的 {@link MPTT} 对象
     *
     * @return 迭代器对象
     */
    public Iterator<MPTT> bfsIterator() {
        // 创建用于广度优先遍历的队列, 并将根节点入队
        var queue = new ArrayDeque<>(List.of(root));

        // 返回迭代器对象
        return new Iterator<>() {
            /**
             * 是否可以继续迭代
             *
             * @return {@link true} 表示可以继续迭代, {@link false} 表示已经到达迭代器末尾, 迭代结束
             */
            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            /**
             * 获取当前迭代元素, 迭代器向后移动
             *
             * @return 本次迭代的 {@link MPTT} 值
             */
            @Override
            public MPTT next() {
                var node = queue.poll();
                if (node == null) {
                    throw new NoSuchElementException();
                }

                node.getChildren().forEach(queue::offer);
                return node.value;
            }
        };
    }

    @Override
    public Iterator<MPTT> iterator() {
        return bfsIterator();
    }

    /**
     * {@link MPTTTree} 节点类型
     */
    private static class Node {
        // 节点值
        MPTT value;

        // 父节点引用
        Node parent;

        // 子节点集合
        List<Node> children;

        /**
         * 构造器, 通过 {@link MPTT} 类型对象构建节点对象
         *
         * @param value {@link MPTT} 类型对象
         */
        Node(MPTT value) {
            this.value = value;
        }

        /**
         * 向当前节点对象添加一个子节点对象
         *
         * @param child 子节点对象
         */
        public void addChildren(Node child) {
            if (children == null) {
                // 使用链表结构避免内存损耗
                children = new LinkedList<>();
            }
            // 添加子节点
            children.add(child);
        }

        /**
         * 获取当前节点的子节点集合
         *
         * @return 当前节点的子节点集合
         */
        public List<Node> getChildren() {
            return children == null ? List.of() : children;
        }
    }
}
