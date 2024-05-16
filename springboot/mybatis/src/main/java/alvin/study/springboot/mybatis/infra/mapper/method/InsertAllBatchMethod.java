package alvin.study.springboot.mybatis.infra.mapper.method;

import alvin.study.springboot.mybatis.conf.MyBatisConfig;
import alvin.study.springboot.mybatis.infra.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Collection;

/**
 * 定义 {@code deleteAll} 方法
 *
 * <p>
 * 定义通用方法需要完成三个部分:
 * <ol>
 * <li>
 * 定义通用方法, 参考 {@link InsertAllBatchMethod} 类型
 * </li>
 * <li>
 * 注入通用方法, 参考
 * {@link MyBatisConfig#getMethodList(Class, com.baomidou.mybatisplus.core.metadata.TableInfo)
 * MyBatisConfig.getMethodList(Class, TableInfo)} 方法
 * </li>
 * <li>
 * 声明通用方法, 参考
 * {@link BaseMapper#insertAllBatch(Collection)} BaseMapper.insertAllBatch(Collection)} 方法
 * </li>
 * </ol>
 * </p>
 */
public class InsertAllBatchMethod extends AbstractMethod {
    private static final String METHOD_ID = "insertAllBatch";

    /**
     * 构造器, 传入方法名称, {@link AbstractMethod} 默认构造器已经过期, 需要调用 {@link AbstractMethod#AbstractMethod(String)}
     * 构造器
     */
    public InsertAllBatchMethod() {
        super(METHOD_ID);
    }

    /**
     * 注入该方法相关的 SQL 模板
     *
     * <p>
     * 将 {@code insertAllBatch} 方法注入到 Mapper 中, 该方法需要一个注解为
     * {@link org.apache.ibatis.annotations.Param @Param("list")} 的参数, 即
     * {@code foreach} 标签 {@code collection} 属性的值, 最终生成如下的
     * SQL 模板
     *
     * <pre>
     * &lt;script&gt;
     * insert into [table name] (field1,field2,field3,...) values
     * &lt;foreach collection="list" item="item" index="index" open="(" separator="),(" close="close"&gt;
     *    #{item.[属性参数1]},
     *    #{item.[属性参数2]},
     *    #{item.[属性参数3]},
     *    ...
     * &lt;/foreach&gt;
     * &lt;/script&gt;
     * </pre>
     * </p>
     *
     * @param mapperClass 所有从 {@link BaseMapper BaseMapper}
     *                    继承的的 Mapper 类型
     * @param modelClass  所有和 Mapper 相关的实体类型
     * @param tableInfo   所有和 Mapper 相关的数据表信息
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        // 定义整体插入模板
        var sql = "<script>insert into %s %s values %s</script>";

        // 拼装插入字段列表
        var fieldSql = new StringBuilder("(")
            .append(tableInfo.getKeyColumn())
            .append(",");

        // 拼装 foreach 部分
        var valueSql = new StringBuilder("""
            <foreach collection="list" item="item" index="index" open="(" separator="),(" close=")">""")
            .append("#{item.")
            .append(tableInfo.getKeyProperty())
            .append("},");

        // 拼装表示要插入值的参数列表
        tableInfo.getFieldList().forEach(field -> {
            fieldSql.append(field.getColumn()).append(",");
            valueSql.append("#{item.").append(field.getProperty()).append("},");
        });

        // 收尾
        fieldSql.deleteCharAt(fieldSql.length() - 1).append(")");
        valueSql.deleteCharAt(valueSql.length() - 1).append("</foreach>");

        // 产生 SQL 模板
        sql = String.format(sql, tableInfo.getTableName(), fieldSql, valueSql);

        // 生成 sql 源
        var sqlSource = languageDriver.createSqlSource(super.configuration, sql, modelClass);

        // 添加插入模板
        return super.addInsertMappedStatement(
            mapperClass, modelClass, METHOD_ID, sqlSource, new NoKeyGenerator(), null, null);
    }
}
