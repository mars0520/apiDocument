package com.api.document.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xzp
 * @description 接口参数DTO
 * @date 2021/2/22
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "InterfaceParamDTO", description = "接口参数DTO")
public class InterfaceParamDTO implements Serializable {

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    private String paramName;

    /**
     * 参数编码
     */
    @ApiModelProperty(value = "参数编码")
    private String paramCode;

    /**
     * 参数编码
     */
    @ApiModelProperty(value = "参数类型")
    private String paramType;

    /**
     * 参数值
     */
    @ApiModelProperty(value = "参数值")
    private String paramValue;

    /**
     * 在DTO中新增并自定义字段，需要覆盖验证的字段，请新建DTO。Entity中的验证规则可以自行修改，但下次生成代码时，记得同步代码！！
     */
    private static final long serialVersionUID = 1L;

    public static InterfaceParamDTO build() {
        return new InterfaceParamDTO();
    }

}
