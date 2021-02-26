package com.api.document.controller;

import com.api.document.service.WordServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author xzp
 * @description
 * @date 2021/1/21
 **/
@Api(tags = "生成word文档")
@RestController
@RequestMapping("/api/word")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
public class ApiWordController {

    @Autowired private WordServiceImpl wordService;

    @GetMapping("/apiDocument")
    @ApiOperation(value = "生成接口文档", notes = "生成接口文档,path包路径")
    public void writeApiDocx(String page) throws IOException {
        wordService.apiWord(page);
    }

}
