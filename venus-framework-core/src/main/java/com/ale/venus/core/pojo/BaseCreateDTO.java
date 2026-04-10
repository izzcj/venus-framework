package com.ale.venus.core.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 创建实体DTO基类
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseCreateDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 备注
     */
    private String remark;
}
