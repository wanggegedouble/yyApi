package com.wy.yyApiBackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wy.common.model.UserInterfaceInfo;
import com.wy.yyApiBackend.model.DTO.UserInterfaceDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfacePageDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfaceUpdateDTO;

import javax.servlet.http.HttpServletRequest;

public interface UserInterfaceService {
    void distributeInterface(UserInterfaceDTO userInterfaceDTO);

    void cancelInterface(Long id, HttpServletRequest request);

    void updateUserInterface(UserInterfaceUpdateDTO userInterfaceUpdateDTO);

    UserInterfaceInfo getInfoById(Long id);

    Page<UserInterfaceInfo> pageList(Integer pageNo, Integer pageSize, UserInterfacePageDTO userInterfacePageDTO);

    void invokeCount(long interfaceId, long userId);
}
