package alvin.study.springboot.ds.app.domain.service;

import alvin.study.springboot.ds.app.domain.model.DataDto;
import alvin.study.springboot.ds.app.domain.service.common.BaseService;
import alvin.study.springboot.ds.infra.entity.DataEntity;
import alvin.study.springboot.ds.infra.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据库服务类
 *
 * <p>
 * 数据信息存储在不同组织代码对应数据源的数据库中, 需要切换到各个不同的数据源进行操作
 * </p>
 */
@Service
@RequiredArgsConstructor
public class DataService extends BaseService {
    // 注入 data 表持久化操作对象
    private final DataRepository dataRepository;

    /**
     * 创建 Data 实体
     *
     * @param entity Data 实体对象
     * @return Data 实体对象
     */
    @Transactional
    public DataDto createData(DataEntity entity) {
        dataRepository.insert(entity);
        return new DataDto(
            entity.getId(),
            entity.getName(),
            entity.getValue(),
            entity.getUpdatedAt(),
            entity.getCreatedAt()
        );
    }

    /**
     * 根据 {@code id} 获取 Data 实体
     *
     * @param id 实体 {@code id} 属性
     * @return Data 实体对象
     */
    @Transactional(readOnly = true)
    public DataDto getData(Long id) {
        var entity = dataRepository.selectById(id).orElseThrow(() -> new DataNotExistException(id));
        return new DataDto(
            entity.getId(),
            entity.getName(),
            entity.getValue(),
            entity.getUpdatedAt(),
            entity.getCreatedAt()
        );
    }
}
