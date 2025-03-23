package alvin.study.se.jdbc.mptt.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import alvin.study.se.jdbc.datasource.ConnectionManager;
import alvin.study.se.jdbc.mptt.model.MPTT;

/**
 * 对 MPTT 表进行增删改查操作的持久化类
 */
@RequiredArgsConstructor
public class MPTTRepository {
    // 数据库连接管理器对象
    private final ConnectionManager connectionManager = new ConnectionManager();

    /**
     * 将 {@link java.sql.ResultSet ResultSet} 结果集转为 {@link MPTT} 类型对象集合
     *
     * @param rs {@link java.sql.ResultSet ResultSet} 结果集对象
     * @return {@link MPTT} 类型对象集合
     */
    private List<MPTT> resultSetToList(ResultSet rs) throws SQLException {
        // 对于空结果集, 返回空集合
        if (!rs.next()) {
            return List.of();
        }

        var results = new ArrayList<MPTT>();
        // 遍历结果集, 转为 MPTT 类型对象
        do {
            results.add(new MPTT(
                rs.getLong(1),
                rs.getString(2),
                rs.getLong(3),
                rs.getLong(4),
                rs.getLong(5)));
        } while (rs.next());

        return results;
    }

    /**
     * 根据 {@code id} 字段的值查询 {@link MPTT} 类型对象
     *
     * @param id {@code id} 字段值
     * @return {@link MPTT} 对象的 {@link Optional} 包装
     */
    public Optional<MPTT> findById(long id) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 执行查询语句
        try (var stat = conn.prepareStatement("SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` WHERE `id` = ?")) {
            stat.setLong(1, id);

            try (var rs = stat.executeQuery()) {
                var results = resultSetToList(rs);
                return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
            }
        }
    }

    /**
     * 根据 {@code name} 字段的值查询 {@link MPTT} 类型对象
     *
     * @param name {@code name} 字段值
     * @return {@link MPTT} 对象的 {@link Optional} 包装
     */
    public Optional<MPTT> findByName(String name) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 执行查询语句
        try (var stat = conn
                .prepareStatement("SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` WHERE `name` = ?")) {
            stat.setString(1, name);

            try (var rs = stat.executeQuery()) {
                var results = resultSetToList(rs);
                return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
            }
        }
    }

    /**
     * 向数据表中插入一个 {@link MPTT} 类型实体对象
     *
     * @param conn 数据库连接对象
     * @param mptt {@link MPTT} 类型实体对象
     */
    private void insert(Connection conn, MPTT mptt) throws SQLException {
        // 执行插入语句, 要求返回新纪录的自增 ID
        try (var stat = conn.prepareStatement(
            "INSERT INTO `mptt` (`name`, `pid`, `lft`, `rht`) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {
            stat.setString(1, mptt.getName());
            stat.setLong(2, mptt.getPid());
            stat.setLong(3, mptt.getLft());
            stat.setLong(4, mptt.getRht());

            if (stat.executeUpdate() > 0) {
                // 获取插入记录的自增 ID
                try (var rs = stat.getGeneratedKeys()) {
                    if (rs.next()) {
                        mptt.setId(rs.getLong(1));
                    }
                }
            }
        }
    }

    /**
     * 将所给的 {@link MPTT} 实体对象存储为根节点
     *
     * <p>
     * 如果数据表中只有根节点, 则其"父记录 ID ({@code pid})" 为 {@code 0}, 其"左记录编号 ({@code lft})" 为
     * {@code 1},
     * 其"右记录编号 ({@code rht})" 为 {@code 2}
     * </p>
     *
     * @param mptt {@link MPTT} 实体对象
     * @return {@link MPTT} 实体对象
     */
    public MPTT createAsRoot(MPTT mptt) throws SQLException {
        // 设置表示根节点的 pid, lft 和 rht 值
        mptt.setPid(0L);
        mptt.setLft(1L);
        mptt.setRht(2L);

        // 将实体对象插入数据表
        insert(connectionManager.get(), mptt);
        return mptt;
    }

    /**
     * 将所给的 {@link MPTT} 实体对象存储为所给记录对象的子节点
     *
     * <p>
     * 如若要向树中某个记录节点添加子记录节点, 则要为该子节点记录空出"左右"编号, 算法如下:
     * <ul>
     * <li>
     * 将作为"父"记录节点"之后"的节点 {@code lft} 统一增加 {@code 2}, 即
     * {@code update mptt m set m.lft = m.lft + 2 where m.lft > [父节点左记录编号]"}
     * </li>
     * <li>
     * 将作为"父"记录节点"之后"的节点 {@code rht} 统一增加 {@code 2}, 即
     * {@code update mptt m set m.rht = m.rht + 2 where m.rht > [父节点左记录编号]"}
     * </li>
     * <li>
     * 所以, 在记录中, 在指定记录"之后"的节点, 其 {@code lft} 和 {@code rht} 编号值都会大于指定节点的 {@code lft}
     * 编号值
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 上述操作会在指定节点"之后"空出一个位置, 也就是该指定节点子节点的编号位置, 此时将子节点的左右编号进行计算即可:
     *
     * <pre>
     * 子节点.lft = 父节点.lft + 1;
     * 子节点.rht = 父节点.rht + 2;
     * </pre>
     * </p>
     *
     * @param mptt     {@link MPTT} 实体对象
     * @param parentId 父节点记录 ID
     * @return {@link MPTT} 实体对象
     */
    public MPTT createAsChild(MPTT mptt, long parentId) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 获取父节点记录
        var parent = findById(parentId).orElseThrow();

        // 将父节点记录之后的节点 lft 全部 +2
        try (var stat = conn.prepareStatement("UPDATE `mptt` SET `lft` = `lft` + 2 WHERE `lft` > ?")) {
            stat.setLong(1, parent.getLft());
            stat.executeUpdate();
        }

        // 将父节点记录之后的节点 rht 全部 +2
        try (var stat = conn.prepareStatement("UPDATE `mptt` SET `rht` = `rht` + 2 WHERE `rht` > ?")) {
            stat.setLong(1, parent.getLft());
            stat.executeUpdate();
        }

        // 设置子节点的父节点 id 和左右编号值
        mptt.setPid(parentId);
        mptt.setLft(parent.getLft() + 1);
        mptt.setRht(parent.getLft() + 2);

        // 插入节点记录
        insert(conn, mptt);

        return mptt;
    }

    /**
     * 将所给的 {@link MPTT} 实体对象存储为所给记录对象的兄弟节点
     *
     * <p>
     * 如若要向树中某个记录节点添加兄弟记录节点, 则要为该兄弟节点记录空出"左右"编号, 算法如下:
     * <ul>
     * <li>
     * 将作为"父"记录节点"之后"的节点 {@code lft} 统一增加 {@code 2}, 即
     * {@code update mptt m set m.lft = m.lft + 2 where m.lft > [父节点左记录编号]"}
     * </li>
     * <li>
     * 将作为"父"记录节点"之后"的节点 {@code rht} 统一增加 {@code 2}, 即
     * {@code update mptt m set m.rht = m.rht + 2 where m.rht > [父节点左记录编号]"}
     * </li>
     * <li>
     * 所以, 在记录中, 在指定记录"之后"的节点, 其 {@code lft} 和 {@code rht} 编号值都会大于指定节点的 {@code lft}
     * 编号值
     * </li>
     * </ul>
     * </p>
     *
     * <p>
     * 上述操作会在指定节点"之后"空出一个位置, 也就是该指定节点子节点的编号位置, 此时将子节点的左右编号进行计算即可:
     *
     * <pre>
     * 子节点.lft = 父节点.lft + 1;
     * 子节点.rht = 父节点.rht + 2;
     * </pre>
     * </p>
     *
     * @param mptt      {@link MPTT} 实体对象
     * @param siblingId 父节点记录 ID
     */
    public void createAsSibling(MPTT mptt, long siblingId) throws SQLException {
        var conn = connectionManager.get();

        var sibling = findById(siblingId).orElseThrow();

        try (var stat = conn.prepareStatement("UPDATE `mptt` SET `lft` = `lft` + 2 WHERE `lft` > ?")) {
            stat.setLong(1, sibling.getRht());
            stat.executeUpdate();
        }

        try (var stat = conn.prepareStatement("UPDATE `mptt` SET `rht` = `rht` + 2 WHERE `rht` > ?")) {
            stat.setLong(1, sibling.getRht());
            stat.executeUpdate();
        }

        // 相当对父节点的 lft + 1, rht + 1
        mptt.setPid(sibling.getPid());
        mptt.setLft(sibling.getRht() + 1);
        mptt.setRht(sibling.getRht() + 2);

        insert(conn, mptt);
    }

    /**
     * 根据所给父节点记录, 查询其所有的子孙节点记录
     *
     * <p>
     * 所谓父节点的子孙节点记录, 即 {@code lft} 大于父节点以及 {@code rht} 小于父节点的所有记录
     * </p>
     *
     * @param parent 表示父节点记录的 {@link MPTT} 实体对象
     * @return 父节点之下的所有子孙节点 {@link MPTT} 实体对象集合
     */
    public List<MPTT> findChildren(MPTT parent) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 查询父节点记录的子节点记录集合
        try (var stat = conn.prepareStatement(
            "SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` WHERE `lft` > ? AND `rht` < ? ORDER BY `lft`")) {
            stat.setLong(1, parent.getLft());
            stat.setLong(2, parent.getRht());

            try (var rs = stat.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    /**
     * 根据所给两个节点记录, 查询连接这两个节点会经过的节点记录集合
     *
     * <p>
     * 设两个节点为 {@code A} 和 {@code B}, 则其路径上的节点 {@code lft} 和 {@code rht} 均满足
     * {@code A.lft <= lft <= B.lft and B.rht <= rht <= A.rht}
     * </p>
     *
     * @param first 表示路径起始节点的 {@link MPTT} 实体对象
     * @param last  表示路径结束节点的 {@link MPTT} 实体对象
     * @return {@code first} 和 {@code last} 之间节点的 {@link MPTT} 实体对象集合
     */
    public List<MPTT> findPath(MPTT first, MPTT last) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 查询路径节点记录集合
        try (var stat = conn.prepareStatement(
            "SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` " +
                                              "WHERE `lft` BETWEEN ? AND ? AND `rht` BETWEEN ? AND ? ORDER BY `lft`")) {
            stat.setLong(1, first.getLft());
            stat.setLong(2, last.getLft());
            stat.setLong(3, last.getRht());
            stat.setLong(4, first.getRht());

            try (var rs = stat.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    /**
     * 查询表示树中叶子节点 (即没有子节点的那些节点) 的记录集合
     *
     * <p>
     * 在 MPTT 树中, 一条记录的 {@code lft} 与 {@code rht} 相差 {@code 1}, 即该记录表示叶子节点
     * </p>
     *
     * @return 所有表示叶子节点的 {@link MPTT} 实体对象集合
     */
    public List<MPTT> findLeaves() throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 查询叶子节点记录集合
        try (var stat = conn.prepareStatement(
            "SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` WHERE `rht` - `lft` = 1 ORDER BY `lft`")) {

            try (var rs = stat.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    /**
     * 查询直属子节点 (即只包含子节点, 不包含孙节点)
     *
     * <p>
     * 查询所有 {@code pid} 为父节点记录 {@code id} 的记录即可
     * </p>
     *
     * @param parentId 父节点记录 {@code id}
     * @return 表示直属子节点的 {@link MPTT} 实体对象集合
     */
    public List<MPTT> findImmediateChildren(long parentId) throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 查询直属子节点记录
        try (var stat = conn.prepareStatement(
            "SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` WHERE `pid` = ? ORDER BY `lft`")) {

            stat.setLong(1, parentId);
            try (var rs = stat.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    /**
     * 从 MPTT 数据表中查询所有记录
     *
     * @return MPTT 数据表中的所有记录实体集合
     */
    public List<MPTT> findAll() throws SQLException {
        // 从连接管理器中获取当前线程的数据库连接
        var conn = connectionManager.get();

        // 执行查询语句
        try (var stat = conn.prepareStatement(
            "SELECT `id`, `name`, `pid`, `lft`, `rht` FROM `mptt` ORDER BY `lft`")) {
            try (var rs = stat.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }
}
