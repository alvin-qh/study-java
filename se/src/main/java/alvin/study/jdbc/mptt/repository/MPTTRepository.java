package alvin.study.jdbc.mptt.repository;

import java.sql.SQLException;
import java.sql.Statement;

import alvin.study.jdbc.datasource.ConnectionHolder;
import alvin.study.jdbc.mptt.model.MPTT;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MPTTRepository {
    private final ConnectionHolder connectionHolder = new ConnectionHolder();

    public void createNode(MPTT parent, MPTT child) throws SQLException {
        var conn = connectionHolder.get();

        if (parent != null) {
            try (var stat = conn.prepareStatement("update mptt m set m.left = m.left + 2 where m.left >= ?")) {
                stat.setLong(1, parent.getRht());
                stat.executeUpdate();
            }

            try (var stat = conn.prepareStatement("update mptt m set m.right = m.right + 2 where m.right >= ?")) {
                stat.setLong(1, parent.getRht());
                stat.executeUpdate();
            }

            child.setLft(parent.getRht());
            child.setRht(parent.getRht() + 1);
        }

        try (var stat = conn.prepareStatement(
            "insert into mptt (name, lft, rht) values (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS)) {
            stat.setString(1, child.getName());
            stat.setLong(2, child.getLft());
            stat.setLong(3, child.getRht());
            if (stat.executeUpdate() > 0) {
                try (var rs = stat.getGeneratedKeys()) {
                    if (rs.next()) {
                        child.setId(rs.getLong(1));
                    }
                }
            }
        }
    }
}
