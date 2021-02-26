package com.api.document.service;

import com.api.document.dto.ApiDTO;
import com.api.document.dto.InterfaceParamDTO;
import com.api.document.dto.LocalInterfaceDTO;
import com.api.document.util.ClassUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author xzp
 * @description
 * @date 2021/2/4
 **/
@Service("WordService")
public class WordServiceImpl {

    private XWPFDocument xwpfDocument;

    /**
     * 基础类型
     */
    private static String[] baseTypes = {
            "com.mars.demo.base.bean.ResponseResult",
            "java.lang.Integer",
            "java.lang.Double",
            "java.lang.Float",
            "java.lang.Long",
            "java.lang.Short",
            "java.lang.Byte",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "int","double","long","short","byte","boolean","char","float"
    };

    /**
     * 获取响应列表
     * @return
     */
    private static List<InterfaceParamDTO> responseList = new ArrayList<InterfaceParamDTO>(8){{
        add(new InterfaceParamDTO().setParamName("200").setParamCode("响应成功"));
        add(new InterfaceParamDTO().setParamName("401").setParamCode("未授权"));
        add(new InterfaceParamDTO().setParamName("403").setParamCode("被禁止"));
        add(new InterfaceParamDTO().setParamName("404").setParamCode("找不到资源"));
        add(new InterfaceParamDTO().setParamName("500").setParamCode("服务器内部错误"));
    }};

    /**
     * 生成接口文档
     *
     * @param page 包路径
     */
    public void apiWord(String page) throws IOException {
        XWPFDocument document = null;
        FileOutputStream out = null;
        try{
            document = new XWPFDocument();
            apiWord(document, page);
            String fileName = "API-" + new Random().nextInt(9999);
            System.err.println("文件名称：" + fileName);
            out = new FileOutputStream(new File("../" + fileName + ".docx"));
            document.write(out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != document){
                document.close();
            }
            if(null != out){
                out.close();
            }
        }
    }

    /**
     * 生成接口文档
     *
     * @param document
     * @param page 包路径
     */
    private void apiWord(XWPFDocument document, String page) {
        xwpfDocument = document;
        // 获取所有controller类
        Set<Class<?>> objs = ClassUtil.getClassList(page);
        int number = 1;
        for (Object obj : objs) {
            // 根据类获取接口信息
            ApiDTO apiDTO = queryLocalInterfaceList((Class) obj);
            if (null == apiDTO) {
                continue;
            }
            List<LocalInterfaceDTO> list = apiDTO.getList();
            if (null == list) {
                continue;
            }
            oneLevel(number + "." + apiDTO.getClassNote());
            int num = 1;
            for (LocalInterfaceDTO info : list) {
                String str = number + "." + num;
                twoLevel(new StringBuffer().append(str).append("接口名称：").append(info.getInterfaceName()).append("（").append(info.getInterfaceCode()).append("）"));
                threeLevel(new StringBuffer(str).append(".1").append("接口说明：").append(info.getInterfaceDesc()));
                threeLevel(new StringBuffer(str).append(".2").append("请求方式：").append(info.getRequestType()));
                threeLevel(new StringBuffer(str).append(".3").append("接口路径：").append(info.getRequestUri()));
                threeLevel(new StringBuffer(str).append(".4").append("编码方式：UTF-8"));
                int serialNumber = 5;
                List<InterfaceParamDTO> requestParam = info.getRequestParam(), responseParam = info.getResponseParam();
                if (null != requestParam && requestParam.size() > 0) {
                    String requestString = apiWordGenerateTable(true, serialNumber, str, "请求参数", new String[]{"参数名称", "参数编码", "参数类型"}, info.getRequestParam());
                    serialNumber ++;
                    if (null != requestString) {
                        threeLevel(new StringBuffer(str).append(".").append(serialNumber).append("请求示例："));
                        threeLevel(requestString, ParagraphAlignment.LEFT);
                        serialNumber ++;
                    }
                }
                if (null != responseParam && responseParam.size() > 0) {
                    apiWordGenerateTable(serialNumber, str, "返回参数", new String[]{"参数名称", "参数编码", "参数类型"}, info.getResponseParam());
                    serialNumber ++;
                }
                apiWordGenerateTable(serialNumber, str, "响应状态", new String[]{"状态码", "说明"}, responseList);
                num ++;
                newline();
            }
            number ++;
        }
    }

    /**
     * API创建表格
     * @param isJoin 是否拼接
     * @param number 序号
     * @param str 序号
     * @param note 说明
     * @param title 标题
     * @param list 列表值
     */
    private String apiWordGenerateTable(boolean isJoin, int number, String str, String note, String[] title, List<InterfaceParamDTO> list) {
        StringBuffer stringBuffer = new StringBuffer("{\r");
        if (null != list) {
            int paramSize = list.size();
            if(paramSize > 0) {
                threeLevel(new StringBuffer(str).append(".").append(number).append(note).append(":"));
                String[][] paramArray = new String[paramSize + 1][title.length];
                paramArray[0] = title;
                for (int p = 0; p < paramSize; p ++) {
                    InterfaceParamDTO interfaceParamDTO = list.get(p);
                    String paramType = interfaceParamDTO.getParamType(), paramValue, paramCode = interfaceParamDTO.getParamCode(), paramName = interfaceParamDTO.getParamName();
                    paramArray[p + 1] = new String[]{paramName, paramCode, paramType};
                    if (isJoin) {
                        switch (paramType) {
                            case "java.lang.Integer":
                            case "java.lang.Long":
                            case "java.lang.Byte":
                            case "int":
                            case "double":
                            case "float":
                            case "long":
                            case "short":
                            case "byte":
                            case "java.lang.Double":
                            case "java.lang.Float":
                            case "java.lang.Short":
                                paramValue = "0";
                                break;
                            case "boolean":
                            case "java.lang.Boolean":
                                paramValue = "false";
                                break;
                            default: paramValue = null;
                                break;
                        }
                        stringBuffer.append("   \"").append(paramCode).append("\":");
                        if(null == paramValue) {
                            stringBuffer.append("\"\"");
                        } else {
                            stringBuffer.append(paramValue);
                        }
                        stringBuffer.append(",\r");
                    }
                }
                generateTable(paramArray);
                if (isJoin) {
                    return new StringBuffer(stringBuffer.substring(0, stringBuffer.length() - 2)).append("\r}").toString();
                }
            }
        }
        return null;
    }
    private String apiWordGenerateTable(int number, String str, String note, String[] title, List<InterfaceParamDTO> list) {
        return apiWordGenerateTable(false, number, str, note, title, list);
    }

    /**
     * 初始化段落
     */
    private XWPFParagraph initParagraph(ParagraphAlignment paragraphAlignment){
        XWPFParagraph paragraph = xwpfDocument.createParagraph();
        paragraph.createRun();
        paragraph.setAlignment(paragraphAlignment);
        paragraph.setBorderBottom(Borders.DOUBLE);
        paragraph.setBorderTop(Borders.DOUBLE);
        paragraph.setBorderRight(Borders.DOUBLE);
        paragraph.setBorderLeft(Borders.DOUBLE);
        paragraph.setBorderBetween(Borders.SINGLE);
        paragraph.setFirstLineIndent(1);
        return paragraph;
    }

    /**
     * 创建表格
     * @param list
     */
    private void generateTable(String[][] list) {
        generateTable(null, list, 0);
    }

    /**
     * 创建表格,自定义首行
     * @param list
     * @param fistRow
     */
    private void generateTable(String[][] list, String[] fistRow){
        if (null != list && null != fistRow) {
            int rows = list.length, cols = list[0].length, number = 0;
            XWPFTable table = xwpfDocument.createTable(rows, cols);
            for (String s : fistRow) {
                table.getRow(0).getCell(number).setText(s);
                number ++;
            }
            generateTable(table, list, 1);
        }
    }

    /**
     * 创建表格
     * @param table
     * @param list
     * @param i 第几行
     */
    private void generateTable(XWPFTable table, String[][] list, int i){
        if (null != list) {
            int rows = list.length, cols = list[0].length;
            if (null == table) {
                table = xwpfDocument.createTable(rows, cols);
            }
            table.getCTTbl().addNewTblPr().addNewTblW().setType(STTblWidth.DXA);
            table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(9072));
            for (int r = i; r < rows; r ++) {
                for (int l = 0; l < cols; l ++) {
                    table.getRow(r).getCell(l).setText(list[r][l]);
                }
            }
        }
    }

    /**
     * 一级目录
     * @param text
     */
    private void oneLevel(String text){
        XWPFRun xwpfRun = initParagraph(ParagraphAlignment.CENTER).createRun();
        xwpfRun.setBold(true);
        xwpfRun.addBreak();
        xwpfRun.setFontFamily("黑体");
        xwpfRun.setText(text);
        xwpfRun.setFontSize(16);
        xwpfRun.removeTab();
    }

    /**
     * 二级目录
     * @param text 内容
     */
    private void twoLevel(StringBuffer text){
        twoLevel(text.toString(), ParagraphAlignment.LEFT);
    }
    private void twoLevel(StringBuffer text, ParagraphAlignment paragraphAlignment){
        twoLevel(text.toString(), paragraphAlignment);
    }
    private void twoLevel(String text, ParagraphAlignment paragraphAlignment){
        XWPFRun xwpfRun = initParagraph(paragraphAlignment).createRun();
        xwpfRun.setBold(true);
        xwpfRun.setFontFamily("黑体");
        xwpfRun.setText(text);
        xwpfRun.setFontSize(14);
        xwpfRun.removeTab();
    }

    /**
     * 三级目录
     * @param text 内容
     */
    private void threeLevel(StringBuffer text){
        threeLevel(text.toString(), ParagraphAlignment.LEFT);
    }
    private void threeLevel(StringBuffer text, ParagraphAlignment paragraphAlignment){
        threeLevel(text.toString(), paragraphAlignment);
    }
    private void threeLevel(String text, ParagraphAlignment paragraphAlignment){
        XWPFRun xwpfRun = initParagraph(paragraphAlignment).createRun();
        xwpfRun.setFontFamily("宋体 (正文)");
        xwpfRun.setText(text);
        xwpfRun.setFontSize(12);
        xwpfRun.removeTab();
    }

    /**
     * 换行
     */
    private void newline(){
        xwpfDocument.createParagraph().createRun().setText("\r");
    }


    /**
     * 根据类查询接口信息
     * @param c
     * @return
     * @throws ClassNotFoundException
     */
    private ApiDTO queryLocalInterfaceList(Class c) {
        // 创建本地接口列表集合
        List<LocalInterfaceDTO> localInterfaceDTOList = new ArrayList<>();
        // 请求地址头 类说明
        String requestMapping = "", classNote = "";
        // 判断存在RequestMapping注解
        if(!c.isAnnotationPresent(RequestMapping.class)){
            return null;
        }
        // 获取注解
        RequestMapping requestMappingAnnotation= (RequestMapping) c.getAnnotation(RequestMapping.class);
        // 判断注解对象存在 获取值
        if(requestMappingAnnotation != null && requestMappingAnnotation.value().length > 0){
            requestMapping = requestMappingAnnotation.value()[0];
        }
        Api apiAnnotation = (Api) c.getAnnotation(Api.class);
        // 判断注解对象存在 获取值
        if(apiAnnotation != null){
            classNote = apiAnnotation.tags()[0];
        }
        // 获取方法列表
        Method[] methods = c.getDeclaredMethods();
        // 判断不为空
        if(!ArrayUtils.isEmpty(methods)){
            // 遍历方法列表
            for(Method method : methods){
                // 创建本地接口列表对象
                LocalInterfaceDTO localInterfaceDTO = new LocalInterfaceDTO();
                // 设置接口编码
                localInterfaceDTO.setInterfaceCode(method.getName());
                // 判断当前方法存在ApiOperation注解
                if(method.isAnnotationPresent(ApiOperation.class)){
                    // 获取注解对象
                    ApiOperation annotation = method.getAnnotation(ApiOperation.class);
                    // 设置接口名称
                    localInterfaceDTO.setInterfaceName(annotation.value());
                    // 设置接口描述
                    localInterfaceDTO.setInterfaceDesc(annotation.notes());
                }
                // 请求地址
                String requestURI = "";
                // 请求类型
                String requestType = "";
                // 判断当前方法存在GetMapping注解
                if(method.isAnnotationPresent(GetMapping.class)){
                    // 获取注解对象
                    GetMapping annotation = method.getAnnotation(GetMapping.class);
                    // 判断注解对象存在 获取值
                    if(annotation != null && annotation.value().length > 0){
                        requestURI = annotation.value()[0];
                    }
                    requestType = "get";
                    // 判断当前方法存在PostMapping注解
                }else if(method.isAnnotationPresent(PostMapping.class)){
                    // 获取注解对象
                    PostMapping annotation = method.getAnnotation(PostMapping.class);
                    // 判断注解对象存在 获取值
                    if(annotation != null && annotation.value().length > 0){
                        requestURI = annotation.value()[0];
                    }
                    requestType = "post";
                }else if(method.isAnnotationPresent(PutMapping.class)){
                    // 获取注解对象
                    PutMapping annotation = method.getAnnotation(PutMapping.class);
                    // 判断注解对象存在 获取值
                    if(annotation != null && annotation.value().length > 0){
                        requestURI = annotation.value()[0];
                    }
                    requestType = "put";
                }else if(method.isAnnotationPresent(DeleteMapping.class)){
                    // 获取注解对象
                    DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
                    // 判断注解对象存在 获取值
                    if(annotation != null && annotation.value().length > 0){
                        requestURI = annotation.value()[0];
                    }
                    requestType = "delete";
                }else {
                    continue;
                }
                // 设置请求地址
                localInterfaceDTO.setRequestUri(requestMapping + requestURI);
                // 设置请求类型
                localInterfaceDTO.setRequestType(requestType);
                // 创建请求对象集合
                List<InterfaceParamDTO> requestParamList = new ArrayList<>();
                // 获取参数列表
                Parameter[] params = method.getParameters();
                // 判断不为空
                if(params != null && params.length > 0){
                    // 遍历参数列表
                    for(Parameter param:params){
                        // 获取参数类型
                        String paramType = param.getType().getName();
                        // 如果当前属于基本类型则直接添加到请求参数中
                        if(ArrayUtils.contains(baseTypes, paramType)){
                            // 创建参数对象
                            InterfaceParamDTO requestParam = new InterfaceParamDTO();
                            // 设参数编码
                            requestParam.setParamCode(param.getName());
                            // 设置参数名称
                            requestParam.setParamName(param.getName());
                            // 设置参数类型
                            requestParam.setParamType(paramType);
                            // 添加参数到列表
                            requestParamList.add(requestParam);
                            // 调用方法获取该对象的参数列表集合
                        } else {
                            // 根据类型Class获取参数列表集合
                            List<InterfaceParamDTO> tempRequestParamList = parseParamDTOListForClass(param.getType());
                            // 合并集合
                            requestParamList.addAll(tempRequestParamList);
                        }
                    }
                }
                // 设置请求参数
                localInterfaceDTO.setRequestParam(requestParamList);
                // 创建响应对象集合
                List<InterfaceParamDTO> responseParamList = new ArrayList<>();
                // 响应参数是否是集合
                Boolean responseIsList = false;
                // 获取返回类型
                Type t = method.getGenericReturnType();
                String typeName = t.getTypeName();
                if(ArrayUtils.contains(baseTypes, typeName)){
                    List<InterfaceParamDTO> tempRequestParamList = parseParamDTOListForClass((Class) t);
                    responseParamList.addAll(tempRequestParamList);
                }
                // 解析第一层R对象 判断是否是参数化类型
                else if(t instanceof ParameterizedType){
                    // 转换参数化类型
                    ParameterizedType parameterizedType = (ParameterizedType) t;
                    // 获取实际对象值
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    // 遍历实际对象
                    for(Type type : actualTypeArguments) {
                        // 解析第二层对象 如果是Map List等则还是参数化对象
                        // 判断是否是参数化类型
                        if(type instanceof ParameterizedType){
                            ParameterizedType parameterizedType1 = (ParameterizedType) type;
                            // 获取类型名称
                            String tempTypeName=parameterizedType1.getRawType().getTypeName();
                            // 当前是集合 则继续解析
                            if("java.util.List".equals(tempTypeName)){
                                // 获取实际对象值
                                Type[] actualTypeArguments1 = parameterizedType1.getActualTypeArguments();
                                // 遍历实际对象
                                for(Type type1:actualTypeArguments1) {
                                    // 获取实际对象类
                                    Class<?> classTemp= (Class<?>) type1;
                                    // 调用方法获取参数列表集合
                                    List<InterfaceParamDTO> tempResponseParamList = parseParamDTOListForClass(classTemp);
                                    // 合并集合
                                    responseParamList.addAll(tempResponseParamList);
                                }
                                // 设置为true
                                responseIsList = true;
                            }
                            // 否则是具体对象类
                        } else {
                            Class<?> classTemp = (Class<?>) type;
                            // 调用方法获取参数列表集合
                            List<InterfaceParamDTO> tempResponseParamList = parseParamDTOListForClass(classTemp);
                            // 合并集合
                            responseParamList.addAll(tempResponseParamList);
                        }
                    }
                }
                // 设置响应参数
                localInterfaceDTO.setResponseParam(responseParamList);
                // 设置响应参数是否是集合标志
                localInterfaceDTO.setResponseIsList(responseIsList);
                // 添加到集合中
                localInterfaceDTOList.add(localInterfaceDTO);
            }
        }
        return new ApiDTO(localInterfaceDTOList, requestMapping, classNote);
    }

    /**
     * 根据类转换参数列表集合
     * @param cls
     * @return
     */
    private List<InterfaceParamDTO> parseParamDTOListForClass(Class cls) {
        // 创建参数列表集合
        List<InterfaceParamDTO> paramDTOList = new ArrayList<>();
        // 获取class中的全部字段
        Field[] fields = cls.getDeclaredFields();
        // 遍历字段
        for(Field field : fields){
            // 当前为private修饰
            if(2 == field.getModifiers()){
                // 字段名称
                String fieldName = "";
                // 字段编码
                String fieldCode = field.getName();
                // 判断当前字段是否有ApiModelProperty注解
                if(field.isAnnotationPresent(ApiModelProperty.class)){
                    // 获取注解
                    ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                    // 设置字段名称
                    fieldName = annotation.value();
                }
                // 创建参数对象
                InterfaceParamDTO param = new InterfaceParamDTO();
                // 设置参数名称
                param.setParamName(fieldName);
                // 设置参数编码
                param.setParamCode(fieldCode);
                // 设置参数类型
                param.setParamType(field.getType().getName());
                // 添加到集合中
                paramDTOList.add(param);
            }
        }
        return paramDTOList;
    }

}
