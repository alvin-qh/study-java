package alvin.study.guava.collect;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

/**
 * {@link Table} 是一个嵌套结构类型, 用于记录一个表格的所有单元格
 *
 * <p>
 * {@link Table} 的每个单元格都需要通过两个 Key 来标识, 即行标识 (RowKey) 和列标识 (ColumnKey), 两个标识交叉的位置即为单元格
 * </p>
 *
 * <p>
 * {@link Table} 的内部存储包括使用 {@link java.util.Map Map} 和通过数组两种方式, 具体为:
 * <ul>
 * <li>{@link HashBasedTable}, 即 {@code HashMap<R, HashMap<C, V>>}</li>
 * <li>{@link com.google.common.collect.TreeBasedTable TreeBasedTable}, 即 {@code TreeMap<R, TreeMap<C, V>>}</li>
 * <li>{@link ImmutableTable}, 即 {@code ImmutableMap<R, ImmutableMap<C, V>>}</li>
 * <li>{@link ArrayTable}, 通过二维数组实现的 {@code Table}, 需要在初始化时明确行列数</li>
 * </ul>
 * </p>
 *
 * <p>
 * {@link Table} 的主要操作包括:
 * <ul>
 * <li>
 * 向表格中添加单元格:
 * <ul>
 * <li>{@link Table#put(Object, Object, Object)}, 通过行标识和列标识设置一个单元格值</li>
 * <li>{@link Table#putAll(Table)}, 将另一个 {@code Table} 对象的值存储到当前 {@code Table} 对象中</li>
 * </ul>
 * </li>
 * <li>
 * 获取表格内容 (包括单元格, 行或列):
 * <ul>
 * <li>{@link Table#get(Object, Object)}, 通过行标识和列标识获取一个单元格值</li>
 * <li>{@link Table#row(Object)}, 通过行标识获取当前行所有列的 {@code Map<C, V>} 结果</li>
 * <li>{@link Table#column(Object)}, 通过列标识获取当前列所有行的 {@code Map<R, V>} 结果</li>
 * </ul>
 * </li>
 * <li>
 * 匹配表格内容:
 * <ul>
 * <li>{@link Table#contains(Object, Object)}, 通过行标识和列标识, 确认一个单元格是否存在</li>
 * <li>{@link Table#containsRow(Object)}, 通过行标识确认该行是否存在</li>
 * <li>{@link Table#containsColumn(Object)}, 通过列标识确认该列是否存在</li>
 * <li>{@link Table#containsValue(Object)}, 确认 {@code Table} 中是否包含指定值的单元格</li>
 * </ul>
 * </li>
 * <li>
 * 行列标识操作:
 * <ul>
 * <li>{@link Table#rowKeySet()}, 获取表格中所有的行标识</li>
 * <li>{@link Table#columnKeySet()}, 获取表格中所有的列标识</li>
 * <li>{@link Table#cellSet()}, 获取所有单元格组成的集合, 单元格由 {@link Table.Cell} 接口对象表示</li>
 * </ul>
 * </li>
 * <li>
 * 行列内容:
 * <ul>
 * <li>{@link Table#rowMap()}, 得到一个以行标识为 Key, 包含每行所有单元格的 {@code Map} 对象, 类似 {@code Map<R, Map<C, V>>} 类型</li>
 * <li>{@link Table#columnMap()}, 得到一个以列标识为 Key, 包含每列所有单元格的 {@code Map} 对象, 类似 {@code Map<C, Map<R, V>>} 类型</li>
 * </ul>
 * </li>
 * <li>
 * 获取全部值:
 * <ul>
 * <li>{@link Table#values()}, 获取表格中所有单元格值组成的集合</li>
 * </ul>
 * </li>
 * <li>
 * 删除表格单元格
 * <ul>
 * <li>{@link Table#remove(Object, Object)}, 根据行标识和列标识删除一个单元格</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 */
@SuppressWarnings("deprecation")
class TableTest {
    /**
     * 根据所给的行标识范围和列标识范围, 对一个 {@link Table} 对象填充单元格
     *
     * <p>
     * 单元格的值为字符串, 遵守以下规则: {@code "<列标识><行标识>"}
     * </p>
     *
     * @param table    要填充的 {@link Table} 对象
     * @param rowStart 行标识范围起始值
     * @param rowTo    行标识范围终止值, 开区间
     * @param colStart 列标识范围起始值
     * @param colTo    列标识范围终止值, 开区间
     */
    private void fillTable(
            Table<Integer, String, String> table, int rowStart, int rowTo, char colStart, char colTo) {
        // 遍历行标识范围内的所有值
        for (var r = rowStart; r < rowTo; r++) {
            // 遍历列标识范围内的所有值
            for (var c = colStart; c < colTo; c++) {
                // 组成单元格内容, 并填充到 Table 对象中
                table.put(r, String.valueOf(c), String.format("%c%d", c, r));
            }
        }
    }

    /**
     * 测试 {@link HashBasedTable} 的创建及各类操作
     *
     * <p>
     * 本例中, 创建了类似如下的一个表格对象, 测试表格对象的各类操作
     *
     * <table>
     * <tr>
     * <th></th>
     * <th>A</th>
     * <th>B</th>
     * <th>C</th>
     * <th>D</th>
     * </tr>
     * <tr>
     * <th>0</th>
     * <td>A0</td>
     * <td>B0</td>
     * <td>C0</td>
     * <td>D0</td>
     * </tr>
     * <tr>
     * <th>1</th>
     * <td>A1</td>
     * <td>B1</td>
     * <td>C1</td>
     * <td>D1</td>
     * </tr>
     * <tr>
     * <th>2</th>
     * <td>A2</td>
     * <td>B2</td>
     * <td>C2</td>
     * <td>D2</td>
     * </tr>
     * <tr>
     * <th>3</th>
     * <td>A3</td>
     * <td>B3</td>
     * <td>C3</td>
     * <td>D3</td>
     * </tr>
     * <tr>
     * <th>4</th>
     * <td>A4</td>
     * <td>B4</td>
     * <td>C4</td>
     * <td>D4</td>
     * </tr>
     * </table>
     * </p>
     *
     * <p>
     * {@link com.google.common.collect.TreeBasedTable TreeBasedTable} 除存储方式不同外, 其余操作和 {@link HashBasedTable}
     * 一致
     * </p>
     */
    @Test
    void hashBasedTable_shouldCreateAndUseTable() {
        // 创建 Hash Table 对象
        var table = HashBasedTable.<Integer, String, String>create();

        // 将单元格值填充入 Table 对象, 确认填充正确
        fillTable(table, 0, 5, 'A', 'E');
        then(table.size()).isEqualTo(20);

        // 确认根据行列标识获取的单元格值
        then(table.get(1, "B")).isEqualTo("B1");
        // 确认根据行标识获取指定行内容的 Map 结果
        then(table.row(2)).containsExactly(
            entry("A", "A2"),
            entry("B", "B2"),
            entry("C", "C2"),
            entry("D", "D2"));
        // 确认根据列标识获取指定列内容的 Map 结果
        then(table.column("D")).containsExactly(
            entry(0, "D0"),
            entry(1, "D1"),
            entry(2, "D2"),
            entry(3, "D3"),
            entry(4, "D4"));

        // 根据行列标识确认对应的单元格存在
        then(table.contains(0, "A")).isTrue();
        // 根据行标识确认指定的行存在
        then(table.containsRow(3)).isTrue();
        // 根据列标识确认指定的列存在
        then(table.containsColumn("D")).isTrue();
        // 确认指定值的单元格存在
        then(table.containsValue("B2")).isTrue();

        // 获取所有的行标识符
        then(table.rowKeySet()).containsExactly(0, 1, 2, 3, 4);
        // 获取所有的列标识符
        then(table.columnKeySet()).containsExactly("A", "B", "C", "D");

        // 获取所有的单元格, 为一个 Table.Cell 类型对象, 这里展开成为 Tuple 类型对象进行测试
        then(table.cellSet()).extracting("rowKey", "columnKey", "value").containsExactly(
            tuple(0, "A", "A0"), tuple(0, "B", "B0"), tuple(0, "C", "C0"), tuple(0, "D", "D0"),
            tuple(1, "A", "A1"), tuple(1, "B", "B1"), tuple(1, "C", "C1"), tuple(1, "D", "D1"),
            tuple(2, "A", "A2"), tuple(2, "B", "B2"), tuple(2, "C", "C2"), tuple(2, "D", "D2"),
            tuple(3, "A", "A3"), tuple(3, "B", "B3"), tuple(3, "C", "C3"), tuple(3, "D", "D3"),
            tuple(4, "A", "A4"), tuple(4, "B", "B4"), tuple(4, "C", "C4"), tuple(4, "D", "D4"));

        // 获取所有行对应的内容 Map, 为 Map<C, V> 类型, 即列标识和单元格值对应关系
        then(table.rowMap()).containsExactly(
            entry(0, ImmutableMap.of("A", "A0", "B", "B0", "C", "C0", "D", "D0")),
            entry(1, ImmutableMap.of("A", "A1", "B", "B1", "C", "C1", "D", "D1")),
            entry(2, ImmutableMap.of("A", "A2", "B", "B2", "C", "C2", "D", "D2")),
            entry(3, ImmutableMap.of("A", "A3", "B", "B3", "C", "C3", "D", "D3")),
            entry(4, ImmutableMap.of("A", "A4", "B", "B4", "C", "C4", "D", "D4")));
        // 获取所有列对应的内容 Map, 为 Map<R, V> 类型, 即行标识符和单元格值对应关系
        then(table.columnMap()).containsExactly(
            entry("A", ImmutableMap.of(0, "A0", 1, "A1", 2, "A2", 3, "A3", 4, "A4")),
            entry("B", ImmutableMap.of(0, "B0", 1, "B1", 2, "B2", 3, "B3", 4, "B4")),
            entry("C", ImmutableMap.of(0, "C0", 1, "C1", 2, "C2", 3, "C3", 4, "C4")),
            entry("D", ImmutableMap.of(0, "D0", 1, "D1", 2, "D2", 3, "D3", 4, "D4")));

        // 获取 Table 的所有单元格值, 组成集合
        then(table.values()).containsExactly(
            "A0", "B0", "C0", "D0",
            "A1", "B1", "C1", "D1",
            "A2", "B2", "C2", "D2",
            "A3", "B3", "C3", "D3",
            "A4", "B4", "C4", "D4");

        // 根据行列标识符删除对应单元格
        then(table.remove(2, "B")).isEqualTo("B2");
        // 确认删除成功
        then(table.contains(2, "B")).isFalse();
    }

    /**
     * 测试 {@link HashBasedTable} 的创建及各类操作
     *
     * <p>
     * 本测试中使用的用例和前一个测试一致, 参考 {@link #hashBasedTable_shouldCreateAndUseTable()} 测试方法
     * </p>
     *
     * <p>
     * 注意, 基于数组的 {@link ArrayTable} 对象一旦实例化后, 其行数, 列数以及行列标识符都已经确定, 后续无法更改, 只能基于已定义的行列标识
     * 符读写对应单元格内容
     * </p>
     *
     * <p>
     * 同样, 基于数组的 {@link ArrayTable} 对象一旦实例化后, 也不支持删除单元格的操作
     * </p>
     */
    @Test
    void arrayBaseTable_shouldCreateAndUseTable() {
        // 创建数组 Table 对象
        // 基于数组的 Table 对象需要在初始化时就明确行数和列数, 指定所有的行标识符和列标识
        var table = ArrayTable.<Integer, String, String>create(
            ImmutableList.of(0, 1, 2, 3, 4),
            ImmutableList.of("A", "B", "C", "D"));

        // 将单元格值填充入 Table 对象, 确认填充正确
        fillTable(table, 0, 5, 'A', 'E');
        then(table.size()).isEqualTo(20);

        // 除初始化时指定的行列标识符外, 使用其它的标识符会导致异常, 即基于数组的 Table 无法扩充行列
        // 这里抛出的异常为参数异常, 即所给的行列标识符参数不合法
        thenThrownBy(() -> table.put(3, "E", "E3")).isInstanceOf(IllegalArgumentException.class);

        // 确认根据行列标识获取的单元格值
        then(table.get(1, "B")).isEqualTo("B1");
        // 确认根据行标识获取指定行内容的 Map 结果
        then(table.row(2)).containsExactly(
            entry("A", "A2"),
            entry("B", "B2"),
            entry("C", "C2"),
            entry("D", "D2"));
        // 确认根据列标识获取指定列内容的 Map 结果
        then(table.column("D")).containsExactly(
            entry(0, "D0"),
            entry(1, "D1"),
            entry(2, "D2"),
            entry(3, "D3"),
            entry(4, "D4"));

        // 根据行列标识确认对应的单元格存在
        then(table.contains(0, "A")).isTrue();
        // 根据行标识确认指定的行存在
        then(table.containsRow(3)).isTrue();
        // 根据列标识确认指定的列存在
        then(table.containsColumn("D")).isTrue();
        // 确认指定值的单元格存在
        then(table.containsValue("B2")).isTrue();

        // 获取所有的行标识符
        then(table.rowKeySet()).containsExactly(0, 1, 2, 3, 4);
        // 获取所有的列标识符
        then(table.columnKeySet()).containsExactly("A", "B", "C", "D");

        // 获取所有的单元格, 为一个 Table.Cell 类型对象, 这里展开成为 Tuple 类型对象进行测试
        then(table.cellSet()).extracting("rowKey", "columnKey", "value").containsExactly(
            tuple(0, "A", "A0"), tuple(0, "B", "B0"), tuple(0, "C", "C0"), tuple(0, "D", "D0"),
            tuple(1, "A", "A1"), tuple(1, "B", "B1"), tuple(1, "C", "C1"), tuple(1, "D", "D1"),
            tuple(2, "A", "A2"), tuple(2, "B", "B2"), tuple(2, "C", "C2"), tuple(2, "D", "D2"),
            tuple(3, "A", "A3"), tuple(3, "B", "B3"), tuple(3, "C", "C3"), tuple(3, "D", "D3"),
            tuple(4, "A", "A4"), tuple(4, "B", "B4"), tuple(4, "C", "C4"), tuple(4, "D", "D4"));

        // 获取所有行对应的内容 Map, 为 Map<C, V> 类型
        then(table.rowMap()).containsExactly(
            entry(0, ImmutableMap.of("A", "A0", "B", "B0", "C", "C0", "D", "D0")),
            entry(1, ImmutableMap.of("A", "A1", "B", "B1", "C", "C1", "D", "D1")),
            entry(2, ImmutableMap.of("A", "A2", "B", "B2", "C", "C2", "D", "D2")),
            entry(3, ImmutableMap.of("A", "A3", "B", "B3", "C", "C3", "D", "D3")),
            entry(4, ImmutableMap.of("A", "A4", "B", "B4", "C", "C4", "D", "D4")));
        // 获取所有列对应的内容 Map, 为 Map<R, V> 类型, 即行标识符和单元格值对应关系
        then(table.columnMap()).containsExactly(
            entry("A", ImmutableMap.of(0, "A0", 1, "A1", 2, "A2", 3, "A3", 4, "A4")),
            entry("B", ImmutableMap.of(0, "B0", 1, "B1", 2, "B2", 3, "B3", 4, "B4")),
            entry("C", ImmutableMap.of(0, "C0", 1, "C1", 2, "C2", 3, "C3", 4, "C4")),
            entry("D", ImmutableMap.of(0, "D0", 1, "D1", 2, "D2", 3, "D3", 4, "D4")));

        // 获取 Table 的所有单元格值, 组成集合
        then(table.values()).containsExactly(
            "A0", "B0", "C0", "D0",
            "A1", "B1", "C1", "D1",
            "A2", "B2", "C2", "D2",
            "A3", "B3", "C3", "D3",
            "A4", "B4", "C4", "D4");

        // 基于数组的 Table 无法删除单元格
        thenThrownBy(() -> table.remove(2, "B")).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * 测试 {@link ImmutableTable} 的创建及各类操作
     *
     * <p>
     * 本测试中使用的用例和前一个测试一致, 参考 {@link #hashBasedTable_shouldCreateAndUseTable()} 测试方法
     * </p>
     *
     * <p>
     * 因为 {@link ImmutableTable} 对象一旦创建, 就无法对单元格进行修改, 所以需要通过 {@link ImmutableTable.Builder} 对象来构建
     * {@link ImmutableTable} 对象
     * </p>
     *
     * <p>
     * 注意, 基于数组的 {@link ImmutableTable} 对象一旦实例化后, 不能对其进行任何修改 (包括添加, 删除等), 会导致异常抛出
     * </p>
     * sy
     */
    @Test
    void immutableTable_shouldCreateAndUseTable() {
        // 实例化 Builder 对象用于创建不可变 Table 对象
        var builder = ImmutableTable.builder();

        // 通过 Builder 对象添加单元格值
        // 遍历行标识范围内的所有值
        for (var r = 0; r < 5; r++) {
            // 遍历列标识范围内的所有值
            for (var c = 'A'; c < 'E'; c++) {
                // 组成单元格内容, 并填充到 Table 对象中
                builder.put(r, String.valueOf(c), String.format("%c%d", c, r));
            }
        }

        // 通过 Builder 对象构建不可变 Table 对象
        var table = builder.build();
        then(table.size()).isEqualTo(20);

        // 不可变对象无法添加或修改单元格值
        // 这里给出的异常时不支持该操作异常
        thenThrownBy(() -> table.put(3, "E", "E3")).isInstanceOf(UnsupportedOperationException.class);

        // 确认根据行列标识获取的单元格值
        then(table.get(1, "B")).isEqualTo("B1");
        // 确认根据行标识获取指定行内容的 Map 结果
        then(table.row(2)).containsExactly(
            entry("A", "A2"),
            entry("B", "B2"),
            entry("C", "C2"),
            entry("D", "D2"));
        // 确认根据列标识获取指定列内容的 Map 结果
        then(table.column("D")).containsExactly(
            entry(0, "D0"),
            entry(1, "D1"),
            entry(2, "D2"),
            entry(3, "D3"),
            entry(4, "D4"));

        // 根据行列标识确认对应的单元格存在
        then(table.contains(0, "A")).isTrue();
        // 根据行标识确认指定的行存在
        then(table.containsRow(3)).isTrue();
        // 根据列标识确认指定的列存在
        then(table.containsColumn("D")).isTrue();
        // 确认指定值的单元格存在
        then(table.containsValue("B2")).isTrue();

        // 获取所有的行标识符
        then(table.rowKeySet()).containsExactly(0, 1, 2, 3, 4);
        // 获取所有的列标识符
        then(table.columnKeySet()).containsExactly("A", "B", "C", "D");

        // 获取所有的单元格, 为一个 Table.Cell 类型对象, 这里展开成为 Tuple 类型对象进行测试
        then(table.cellSet()).extracting("rowKey", "columnKey", "value").containsExactly(
            tuple(0, "A", "A0"), tuple(0, "B", "B0"), tuple(0, "C", "C0"), tuple(0, "D", "D0"),
            tuple(1, "A", "A1"), tuple(1, "B", "B1"), tuple(1, "C", "C1"), tuple(1, "D", "D1"),
            tuple(2, "A", "A2"), tuple(2, "B", "B2"), tuple(2, "C", "C2"), tuple(2, "D", "D2"),
            tuple(3, "A", "A3"), tuple(3, "B", "B3"), tuple(3, "C", "C3"), tuple(3, "D", "D3"),
            tuple(4, "A", "A4"), tuple(4, "B", "B4"), tuple(4, "C", "C4"), tuple(4, "D", "D4"));

        // 获取所有行对应的内容 Map, 为 Map<C, V> 类型
        then(table.rowMap()).containsExactly(
            entry(0, ImmutableMap.of("A", "A0", "B", "B0", "C", "C0", "D", "D0")),
            entry(1, ImmutableMap.of("A", "A1", "B", "B1", "C", "C1", "D", "D1")),
            entry(2, ImmutableMap.of("A", "A2", "B", "B2", "C", "C2", "D", "D2")),
            entry(3, ImmutableMap.of("A", "A3", "B", "B3", "C", "C3", "D", "D3")),
            entry(4, ImmutableMap.of("A", "A4", "B", "B4", "C", "C4", "D", "D4")));
        // 获取所有列对应的内容 Map, 为 Map<R, V> 类型, 即行标识符和单元格值对应关系
        then(table.columnMap()).containsExactly(
            entry("A", ImmutableMap.of(0, "A0", 1, "A1", 2, "A2", 3, "A3", 4, "A4")),
            entry("B", ImmutableMap.of(0, "B0", 1, "B1", 2, "B2", 3, "B3", 4, "B4")),
            entry("C", ImmutableMap.of(0, "C0", 1, "C1", 2, "C2", 3, "C3", 4, "C4")),
            entry("D", ImmutableMap.of(0, "D0", 1, "D1", 2, "D2", 3, "D3", 4, "D4")));
        // 获取 Table 的所有单元格值, 组成集合
        then(table.values()).containsExactly(
            "A0", "B0", "C0", "D0",
            "A1", "B1", "C1", "D1",
            "A2", "B2", "C2", "D2",
            "A3", "B3", "C3", "D3",
            "A4", "B4", "C4", "D4");

        // 不可变 Table 无法删除单元格
        thenThrownBy(() -> table.remove(2, "B")).isInstanceOf(UnsupportedOperationException.class);
    }
}
