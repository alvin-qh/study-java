package alvin.study.springboot.shiro.app.endpoint.mapper;

import alvin.study.springboot.shiro.app.endpoint.model.MenuDto;
import alvin.study.springboot.shiro.app.endpoint.model.MenuDto.MenuItemDto;
import alvin.study.springboot.shiro.infra.entity.Menu;
import com.google.common.base.Functions;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 菜单对象类型转换
 */
@Component
@RequiredArgsConstructor
public class MenuEntityMapper {
    // 注入对象转换器
    private final ModelMapper modelMapper;

    /**
     * 将菜单实体对象集合转为菜单 to 对象
     *
     * @param menuCollection 菜单实体对象集合
     * @return 菜单 dto 对象
     */
    public MenuDto toDto(Collection<Menu> menuCollection) {
        // 读取数据表中的菜单记录并转为 MenuItemDto 对象, 并以 id 为 Key 产生 Map 对象
        var menuMap = menuCollection.stream()
            .map(m -> modelMapper.map(m, MenuItemDto.class))
            .collect(Collectors.toMap(MenuItemDto::getId, Functions.identity()));

        // 将子菜单和父菜单进行关联
        var result = new ArrayList<MenuItemDto>(menuMap.size());
        for (var item : menuMap.values()) {
            if (item.getParentId() == null) {
                result.add(item);
            } else {
                var parent = menuMap.get(item.getParentId());
                if (parent != null) {
                    parent.addSubMenuItem(item);
                }
            }
        }
        return new MenuDto(Collections.unmodifiableList(result));
    }
}
