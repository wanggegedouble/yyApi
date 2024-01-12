package com.wy.yyApiBackend.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wy.common.model.InterfaceInfo;
import com.wy.yyApiBackend.model.DTO.IdDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInfoAddDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInvokeDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceQueryDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口信息服务

 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    void addInterfaceInfo(InterfaceInfoAddDTO infoAddDTO, HttpServletRequest request);

    void deleteInterfaceById(Long id, HttpServletRequest request);

    Page<InterfaceInfo> getPageList(Integer pageNo, Integer pageSize, InterfaceQueryDTO interfaceQueryDTO);

    void interfaceOnline(Long id, HttpServletRequest request);

    void offlineInterface(IdDTO idDTO, HttpServletRequest request);

    String invokeInterface(InterfaceInvokeDTO interfaceInvokeDTO, HttpServletRequest request, InterfaceInfo interfaceInfo);
}
