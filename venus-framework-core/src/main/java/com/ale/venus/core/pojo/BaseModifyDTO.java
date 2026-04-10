package com.ale.venus.core.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 修改实体DTO基类
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseModifyDTO {

    /**
     * ID
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 备注
     */
    private String remark;
}
