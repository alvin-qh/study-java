package alvin.study.springboot.mybatis.infra.mapper;

import alvin.study.springboot.mybatis.IntegrationTest;
import alvin.study.springboot.mybatis.builder.UserBuilder;
import alvin.study.springboot.mybatis.conf.MyBatisConfig;
import alvin.study.springboot.mybatis.infra.entity.User;
import alvin.study.springboot.mybatis.infra.entity.UserType;
import alvin.study.springboot.mybatis.infra.mapper.method.DeleteAllMethod;
import alvin.study.springboot.mybatis.infra.mapper.method.InsertAllBatchMethod;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * 测试 {@link UserMapper} 类型
 */
class UserMapperTest extends IntegrationTest {
    // 注入 Mapper 对象
    @Autowired
    private UserMapper mapper;

    /**
     * 测试 {@link UserMapper#insert(Object) UserMapper.insert(User)} 方法
     *
     * <p>
     * {@link UserMapper#insert(Object) UserMapper.insert(User)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     *
     * <p>
     * {@link UserMapper#selectById(java.io.Serializable) UserMapper.selectById( Serializable )} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     */
    @Test
    @Transactional
    void insert_shouldAddNewRecord() {
        // 创建一个非持久化的 User 实体对象
        var expectedUser = newBuilder(UserBuilder.class).build();

        // 将非持久化对象进行持久化 (插入操作), 确认操作成功
        var effectLines = mapper.insert(expectedUser);
        then(effectLines).isOne();

        // 清除缓存
        clearSessionCache();

        // 确认可以根据 id 查询到插入的数据
        var mayUser = Optional.ofNullable(mapper.selectById(expectedUser.getId()));
        then(mayUser).isPresent();

        // 确认查询的结果和插入的结果一致
        var actualUser = mayUser.get();
        then(actualUser.getId()).isEqualTo(expectedUser.getId());
        then(actualUser.getType()).isEqualTo(UserType.NORMAL);
    }

    /**
     * 测试 {@link UserMapper#updateById(Object) UserMapper.updateById(User)} 方法
     *
     * <p>
     * {@link UserMapper#updateById(Object) UserMapper.updateById(User)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     *
     * <p>
     * {@link UserMapper#selectById(java.io.Serializable) UserMapper.selectById(Serializable)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     */
    @Test
    @Transactional
    void update_shouldUpdateRecord() {
        // 创建一个 User 实体对象
        var expectedUser = newBuilder(UserBuilder.class).create();

        // 修改实体对象属性值
        expectedUser.setAccount("alvin-001");
        expectedUser.setType(UserType.ADMIN);

        // 更新实体对象, 确认操作执行成功
        var effectLines = mapper.updateById(expectedUser);
        then(effectLines).isOne();

        // 清除缓存
        clearSessionCache();

        // 确认可以根据 id 查询到插入的数据
        var mayUser = Optional.ofNullable(mapper.selectById(expectedUser.getId()));
        then(mayUser).isPresent();

        // 确认查询的结果和修改的结果一致
        var actualUser = mayUser.get();
        then(actualUser.getAccount()).isEqualTo("alvin-001");
        then(actualUser.getType()).isEqualTo(UserType.ADMIN);
    }

    /**
     * 测试 {@link UserMapper#deleteById(Object) UserMapper.deleteById(User)} 方法
     *
     * <p>
     * {@link UserMapper#deleteById(Object) UserMapper.deleteById(User)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     *
     * <p>
     * {@link UserMapper#selectById(java.io.Serializable)
     * UserMapper.selectById(Serializable)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     */
    @Test
    @Transactional
    void delete_shouldDeleteRecord() {
        // 创建一个 User 实体对象
        var expectedUser = newBuilder(UserBuilder.class).create();

        // 更新实体对象, 确认操作执行成功
        var effectLines = mapper.deleteById(expectedUser);
        then(effectLines).isOne();

        // 清除缓存
        clearSessionCache();

        // 确认查询结果为空, 表示实体已被删除
        var mayUser = Optional.ofNullable(mapper.selectById(expectedUser.getId()));
        then(mayUser).isEmpty();
    }

    /**
     * 测试
     * {@link UserMapper#selectList(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * UserMapper.selectList(Wrapper)} 方法
     *
     * <p>
     * {@link UserMapper#selectList(com.baomidou.mybatisplus.core.conditions.Wrapper)
     * UserMapper.selectList(Wrapper)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     *
     * <p>
     * 在查询操作中, {@link com.baomidou.mybatisplus.core.conditions.Wrapper Wrapper} 类型应为
     * {@link QueryWrapper} 子类型对象, 表示一个查询条件. 该类型提供了一组条件表达式方法, 以产生 {@code where} 语句
     * </p>
     *
     * <p>
     * {@link UserMapper#selectById(java.io.Serializable)
     * UserMapper.selectById(Serializable)} 方法继承自
     * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper BaseMapper} 超类
     * </p>
     */
    @Test
    @Transactional
    void selectList_shouldGetSelectResult() {
        var accountPrefix = "alvin-";

        // 创建 10 个 User 实体对象
        for (var i = 0; i < 10; i++) {
            newBuilder(UserBuilder.class).withAccount(accountPrefix + i).create();
        }

        // 清除缓存
        clearSessionCache();

        // 查询符合条件的 User 实体对象集合, 并确认查询正确
        var users = mapper.selectList(new QueryWrapper<User>().likeRight("account", accountPrefix));
        then(users).hasSize(10);
    }

    /**
     * 测试通过 {@link UserMapper#selectByAccount(String)} 方法查询 {@link User} 实体
     */
    @Test
    @Transactional
    void selectByAccount_shouldGetSelectResult() {
        // 根据当前登录的 account 查询该用户实体信息
        var mayUser = mapper.selectByAccount(currentUser().getAccount());
        then(mayUser).isPresent();

        var user = mayUser.get();
        then(user.getId()).isEqualTo(currentUser().getId());
    }

    /**
     * 测试通过 {@link UserMapper#selectAllByAccount(String)} 方法批量查询 {@link User} 实体集合
     */
    @Test
    @Transactional
    void selectAllByAccount_shouldGetSelectResult() {
        var accountPrefix = "alvin";

        // 创建 10 个用户实体对象, 具备相同的账号前缀
        for (var i = 0; i < 10; i++) {
            newBuilder(UserBuilder.class).withAccount(accountPrefix + "_" + i).create();
        }

        // 清除缓存
        clearSessionCache();

        // 根据账号前缀批量查询
        var users = mapper.selectAllByAccount(accountPrefix);
        then(users).hasSize(10);
    }

    /**
     * 测试定义的通用的 {@code deleteAll} 方法
     *
     * <p>
     * 定义通用方法需要完成三个部分:
     * <ol>
     * <li>
     * 定义通用方法, 参考
     * {@link DeleteAllMethod DeleteAllMethod} 类型
     * </li>
     * <li>
     * 注入通用方法, 参考
     * {@link MyBatisConfig#getMethodList(Class, com.baomidou.mybatisplus.core.metadata.TableInfo)
     * MyBatisConfig.getMethodList(Class, TableInfo)} 方法
     * </li>
     * <li>
     * 声明通用方法, 参考 {@link BaseMapper#deleteAll()} 方法
     * </li>
     * </ol>
     * </p>
     */
    @Test
    @Transactional
    void deleteAll_shouldDeleteAllRecords() {
        // 持久化 10 个用户实体 (连同 IntegrationTest 中共 11 个)
        for (var i = 0; i < 10; i++) {
            newBuilder(UserBuilder.class).create();
        }
        clearSessionCache();

        // 确认共 11 个实体类型可被查询
        var users = mapper.selectList(null);
        then(users).hasSize(11);

        // 调用通用方法, 删除所有记录
        mapper.deleteAll();
        clearSessionCache();

        // 确认所有用户实体已被删除
        users = mapper.selectList(null);
        then(users).isEmpty();
    }

    /**
     * 测试定义的通用的 {@code insertAllBatch} 方法
     *
     * <p>
     * 定义通用方法需要完成三个部分:
     * <ol>
     * <li>
     * 定义通用方法, 参考
     * {@link InsertAllBatchMethod
     * InsertAllBatchMethod} 类型
     * </li>
     * <li>
     * 注入通用方法, 参考
     * {@link MyBatisConfig#getMethodList(Class, com.baomidou.mybatisplus.core.metadata.TableInfo)
     * MyBatisConfig.getMethodList(Class, TableInfo)} 方法
     * </li>
     * <li>
     * 声明通用方法, 参考 {@link BaseMapper#insertAllBatch(java.util.Collection)
     * BaseMapper.insertAllBatch(Collection)} 方法
     * </li>
     * </ol>
     * </p>
     */
    @Test
    @Transactional
    void insertAllBatch_shouldInsertEntitiesBatch() {
        // 产生 10 个 User 对象, 不进行持久化
        var newUsers = new ArrayList<User>();
        for (var i = 0; i < 10; i++) {
            newUsers.add(newBuilder(UserBuilder.class).build());
        }

        // 进行批量持久化操作
        mapper.insertAllBatch(newUsers);

        clearSessionCache();

        // 确认批量持久化正确
        var users = mapper.selectList(null);
        then(users).hasSize(11);
    }
}
