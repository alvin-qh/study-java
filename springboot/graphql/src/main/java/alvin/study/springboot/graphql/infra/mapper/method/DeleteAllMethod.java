package alvin.study.springboot.graphql.infra.mapper.method;

import org.apache.ibatis.mapping.MappedStatement;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import alvin.study.springboot.graphql.conf.MyBatisConfig;

/**
 * 定义 {@code deleteAll} 方法
 *
 * <p>
 * 定义通用方法需要完成三个部分:
 * <ol>
 * <li>
 * 定义通用方法, 参考 {@link DeleteAllMethod} 类型
 * </li>
 * <li>
 * 注入通用方法, 参考
 * {@link MyBatisConfig#getMethodList(Class, com.baomidou.mybatisplus.core.metadata.TableInfo)
 * MyBatisConfig.getMethodList(Class, TableInfo)} 方法
 * </li>
 * <li>
 * 声明通用方法, 参考 {@link BaseMapper#deleteAll()
 * BaseMapper.deleteAll()} 方法
 * </li>
 * </ol>
 * </p>
 */
public class DeleteAllMethod extends AbstractMethod {
    /**
     * 方法名, 必须和 {@link BaseMapper BaseMapper} 中添加的方法同名
     *
     * @see BaseMapper#deleteAll() BaseMapper.deleteAll()
     */
    private static final String METHOD_ID = "deleteAll";

    /**
     * 构造器, 传入方法名称, {@link AbstractMethod} 默认构造器已经过期, 需要调用 {@link AbstractMethod#AbstractMethod(String)}
     * 构造器
     */
    public DeleteAllMethod() {
        super(METHOD_ID);
    }

    /**
     * 注入该方法相关的 SQL 模板
     *
     * <p>
     * 将 {@code deleteAll} 方法注入到 Mapper 中, 该方法无需参数 (因为 SQL 模板中没有引用参数), 最终生成如下的 SQL
     * 模板
     *
     * <pre>
     * delete from [table name]
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
        var sql = "delete from " + tableInfo.getTableName();
        var sqlSource = languageDriver.createSqlSource(super.configuration, sql, modelClass);
        return super.addDeleteMappedStatement(mapperClass, METHOD_ID, sqlSource);
    }
}
