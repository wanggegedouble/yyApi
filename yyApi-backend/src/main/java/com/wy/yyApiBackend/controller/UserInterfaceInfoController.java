package com.wy.yyApiBackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.UserInterfaceInfo;
import com.wy.common.result.RestResult;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.annotation.AuthCheck;
import com.wy.yyApiBackend.constant.UserConstant;
import com.wy.yyApiBackend.model.DTO.UserInterfaceCancelDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfaceDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfacePageDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfaceUpdateDTO;
import com.wy.yyApiBackend.service.UserInterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @Author: wy
 * @CreateTime: 2023-12-18  21:02
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/userInterface")
@Api(tags = "用户调用管理接口")
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceService userInterfaceService;


    @ApiOperation(value = "为用户分配接口")
    @GetMapping("/distribute")
    public RestResult<String> distributeInterface(UserInterfaceDTO userInterfaceDTO) {
        if (userInterfaceDTO == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.userInterfaceService.distributeInterface(userInterfaceDTO);
        return RestResult.of("为用户分配成功");
    }

    @DeleteMapping("/cancel")
    @ApiOperation(value = "取消用户调用接口关系")
    public RestResult<String> cancelInterface(@RequestBody UserInterfaceCancelDTO userInterfaceCancelDTO, HttpServletRequest request) {
        if (userInterfaceCancelDTO == null || userInterfaceCancelDTO.getId() <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.userInterfaceService.cancelInterface(userInterfaceCancelDTO.getId(),request);
        return RestResult.of("取消成功");
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新用户接口信息")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<String> updateUserInterface(@RequestBody UserInterfaceUpdateDTO userInterfaceUpdateDTO) {
        if (userInterfaceUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        this.userInterfaceService.updateUserInterface(userInterfaceUpdateDTO);
        return RestResult.of("更新成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<UserInterfaceInfo> getInfoById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        return RestResult.of("获取成功",this.userInterfaceService.getInfoById(id));
    }

    @GetMapping("/pageList")
    @ApiOperation(value = "分页获取")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public RestResult<Page<UserInterfaceInfo>> pageList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        UserInterfacePageDTO userInterfacePageDTO) {
        if (userInterfacePageDTO == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        if (pageSize < 50) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        return RestResult.of("获取成功",this.userInterfaceService.pageList(pageNo,pageSize,userInterfacePageDTO));
    }

}
