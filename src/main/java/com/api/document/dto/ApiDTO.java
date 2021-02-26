package com.api.document.dto;

import java.util.List;

/**
 * @author xzp
 * @description api文档参数
 * @date 2021/2/23
 **/
public class ApiDTO {

    /**
     * 接口信息
     */
    private List<LocalInterfaceDTO> list;

    /**
     * 请求地址头
     */
    private String requestMapping;

    /**
     * 类说明
     */
    private String classNote;

    public List<LocalInterfaceDTO> getList() {
        return list;
    }

    public void setList(List<LocalInterfaceDTO> list) {
        this.list = list;
    }

    public String getRequestMapping() {
        return requestMapping;
    }

    public void setRequestMapping(String requestMapping) {
        this.requestMapping = requestMapping;
    }

    public String getClassNote() {
        return classNote;
    }

    public void setClassNote(String classNote) {
        this.classNote = classNote;
    }

    public ApiDTO() {
    }

    public ApiDTO(List<LocalInterfaceDTO> list, String requestMapping, String classNote) {
        this.list = list;
        this.requestMapping = requestMapping;
        this.classNote = classNote;
    }
}
