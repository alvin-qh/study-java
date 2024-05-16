package alvin.study.springboot.ds.app.endpoint;

import alvin.study.springboot.ds.app.domain.model.DataDto;
import alvin.study.springboot.ds.app.domain.model.DataForm;
import alvin.study.springboot.ds.app.domain.service.DataService;
import alvin.study.springboot.ds.core.http.common.ResponseDto;
import alvin.study.springboot.ds.infra.entity.DataEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 数据 Controller 类
 *
 * <p>
 * 数据信息存储在不同组织代码对应数据源的数据库中, 需要切换到各个不同的数据源进行操作
 * </p>
 */
@Validated
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {
    // 注入 Data 持久化操作对象
    private final DataService dataService;

    /**
     * 创建 Data 数据信息
     *
     * @param form 传递 Data 数据的 Form 对象
     * @return Data 数据 Dto 对象
     */
    @PostMapping
    @ResponseBody
    ResponseDto<DataDto> createData(@Valid @RequestBody DataForm form) {
        var entity = new DataEntity();
        entity.setName(form.getName());
        entity.setValue(form.getValue());

        // 创建实体对象
        var dto = dataService.createData(entity);

        // 返回结果
        return ResponseDto.success(dto);
    }

    /**
     * 获取 Data 数据信息
     *
     * @param id Data 数据对应的 {@code id} 属性
     * @return Data 数据 Dto 对象
     */
    @GetMapping("/{id}")
    @ResponseBody
    ResponseDto<DataDto> getData(@NotNull @PathVariable("id") Long id) {
        try {
            // 获取实体对象
            var dto = dataService.getData(id);

            // 返回结果
            return ResponseDto.success(dto);
        } catch (Exception e) {
            throw HttpClientErrorException.create(
                "Cannot found resource",
                HttpStatus.NOT_FOUND,
                "resource_not_exist",
                HttpHeaders.EMPTY,
                null,
                null);
        }
    }
}
