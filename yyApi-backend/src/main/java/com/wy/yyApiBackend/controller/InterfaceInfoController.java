package com.wy.yyApiBackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.InterfaceInfo;
import com.wy.common.result.RestResult;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.annotation.AuthCheck;
import com.wy.yyApiBackend.model.DTO.IdDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInfoAddDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInvokeDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceQueryDTO;
import com.wy.yyApiBackend.service.InterfaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.wy.yyApiBackend.constant.UserConstant.ADMIN_ROLE;


/**
 * @Author: wy
 * @CreateTime: 2023-12-16  21:35
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/interfaceInfo")
@Api(tags = "平台接口管理")
public  class InterfaceInfoController {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @PostMapping("/")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "添加用户接口调用",notes = "管理员")
    public RestResult<String> addInterfaceInfo(@RequestBody InterfaceInfoAddDTO infoAddDTO, HttpServletRequest request) {
        if (infoAddDTO == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        interfaceInfoService.addInterfaceInfo(infoAddDTO,request);
        return RestResult.of("添加成功");
    }

    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "删除用户接口调用",notes = "管理员")
    public RestResult<String> deleteInterfaceInfo(@PathVariable Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        interfaceInfoService.deleteInterfaceById(id,request);
        return RestResult.of("删除成功");
    }

    @GetMapping("/{id}")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "根据id获取",notes = "管理员")
    public RestResult<InterfaceInfo> getInterfaceInfo(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = this.interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"无此接口");
        }
        return RestResult.of("获取成功",interfaceInfo);
    }

    @GetMapping("/list}")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "分页获取用户接口列表",notes = "管理员")
    public RestResult<Page<InterfaceInfo>> pageList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    InterfaceQueryDTO interfaceQueryDTO) {
        if (interfaceQueryDTO == null || pageSize > 20) {
            // pageSize >50 可以现在爬虫
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        return RestResult.of("获取成功",this.interfaceInfoService.getPageList(pageNo,pageSize,interfaceQueryDTO));
    }

    @PostMapping("/online")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "上线接口",notes = "管理员")
    public RestResult<String> interfaceOnline(@RequestBody IdDTO idDTO,HttpServletRequest request) {
        if (idDTO == null || idDTO.getId() <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.interfaceInfoService.interfaceOnline(idDTO.getId(),request);
        return RestResult.of("上线成功");
    }

    @PostMapping("/offline")
    @AuthCheck(mustRole = ADMIN_ROLE)
    @ApiOperation(value = "下线接口",notes = "管理员")
    public RestResult<String> interfaceOffline(@RequestBody IdDTO idDTO,HttpServletRequest request) {
        if (idDTO == null || idDTO.getId() <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.interfaceInfoService.offlineInterface(idDTO,request);
        return RestResult.of("下线成功");
    }

    @PostMapping("/invokeInterface")
    @ApiOperation(value = "测试调用")
    public RestResult<String> invokeInterface(@RequestBody InterfaceInvokeDTO interfaceInvokeDTO, HttpServletRequest request) {
        if (interfaceInvokeDTO == null || interfaceInvokeDTO.getId() <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = this.interfaceInfoService.getById(interfaceInvokeDTO.getId());
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        return RestResult.of("调用成功",this.interfaceInfoService.invokeInterface(interfaceInvokeDTO,request,interfaceInfo));
    }
}
