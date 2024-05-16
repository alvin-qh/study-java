package alvin.study.springboot.security.app.endpoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单信息类型
 */
@Data
@NoArgsConstructor(access = AccessLevel.MODULE)
@AllArgsConstructor
public class MenuDto implements Serializable {
    // 菜单项集合
    private List<MenuItemDto> items;

    /**
     * 菜单项类型
     */
    @Data
    @NoArgsConstructor(access = AccessLevel.MODULE)
    @AllArgsConstructor
    public static class MenuItemDto implements Serializable {
        /**
         * 菜单 id
         */
        private long id;

        /**
         * 菜单文本
         */
        private String text;

        /**
         * 菜单图标
         */
        private String icon;

        /**
         * 菜单序列
         */
        private int order;

        /**
         * 子菜单项
         */
        private List<MenuItemDto> items = List.of();

        /**
         * 上一级菜单 id
         */
        @JsonIgnore
        private Long parentId;

        /**
         * 菜单角色
         */
        @JsonIgnore
        private String role;

        /**
         * 菜单权限
         */
        @JsonIgnore
        private String permission;

        /**
         * 添加子菜单项
         *
         * @param item 子菜单项
         * @return 当前对象
         */
        public MenuItemDto addSubMenuItem(MenuItemDto item) {
            if (this.items.isEmpty()) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);

            return this;
        }
    }
}
