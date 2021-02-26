package com.api.document.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author xzp
 * @description 本地接口实体类
 * @date 2021/2/22
 **/
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "LocalInterfaceDTO", description = "本地接口实体类")
public class LocalInterfaceDTO implements Serializable {

    /**
     * 接口名称
     */
    @ApiModelProperty(value = "接口名称")
    private String interfaceName;

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码")
    private String interfaceCode;

    /**
     * 请求地址
     */
    @ApiModelProperty(value = "请求地址")
    private String requestUri;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求类型")
    private String requestType;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private List<InterfaceParamDTO> requestParam;

    /**
     * 请求参数是否是实体
     */
    @ApiModelProperty(value = "请求参数是否是实体")
    private Boolean requestIsEntity;

    /**
     * 接口描述
     */
    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;

    /**
     * 响应参数
     */
    @ApiModelProperty(value = "响应参数")
    private List<InterfaceParamDTO> responseParam;

    /**
     * 响应参数是否是集合
     */
    @ApiModelProperty(value = "响应参数是否是集合")
    private Boolean responseIsList;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数")
    private Integer current;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数")
    private Integer size;

    /**
     * 在DTO中新增并自定义字段，需要覆盖验证的字段，请新建DTO。Entity中的验证规则可以自行修改，但下次生成代码时，记得同步代码！！
     */
    private static final long serialVersionUID = 1L;

    public static LocalInterfaceDTO build() {
        return new LocalInterfaceDTO();
    }

}
