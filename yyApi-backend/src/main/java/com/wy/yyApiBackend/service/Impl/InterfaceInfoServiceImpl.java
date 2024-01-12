package com.wy.yyApiBackend.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.client.yyClinet.YyApiClient;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.InterfaceInfo;
import com.wy.common.model.User;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.constant.CommonConstant;
import com.wy.yyApiBackend.mapper.InterfaceInfoMapper;
import com.wy.yyApiBackend.model.DTO.IdDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInfoAddDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceInvokeDTO;
import com.wy.yyApiBackend.model.DTO.InterfaceQueryDTO;
import com.wy.yyApiBackend.service.InterfaceInfoService;
import com.wy.yyApiBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.wy.yyApiBackend.constant.InterfaceStatus.INTERFACE_OPEN;

/**
 * 接口信息服务实现类

 */
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {
    @Resource
    private UserService userService;
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isBlank(name)) {
                throw new BusinessException(ResultCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public void addInterfaceInfo(InterfaceInfoAddDTO infoAddDTO, HttpServletRequest request) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(infoAddDTO,interfaceInfo);
        // 接口详细校验
        this.validInterfaceInfo(interfaceInfo,true);
        User loginUser = this.userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        int insert = this.baseMapper.insert(interfaceInfo);
        if (insert != 1) {
            throw new BusinessException(ResultCode.INTERFACE_ADD_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInterfaceById(Long id, HttpServletRequest request) {
        InterfaceInfo interfaceInfo = this.baseMapper.selectById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.PARAMS_ERROR,"无此信息");
        }
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(interfaceInfo.getUserId()) || !userService.isAdmin(request)) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        int delete = this.baseMapper.deleteById(id);
        if (delete != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"删除失败");
        }
    }

    @Override
    public Page<InterfaceInfo> getPageList(Integer pageNo, Integer pageSize, InterfaceQueryDTO interfaceQueryDTO) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfo, interfaceQueryDTO);
        String description = interfaceQueryDTO.getDescription();
        String sortField = interfaceQueryDTO.getSortField();
        String sortOrder = interfaceQueryDTO.getSortOrder();
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfo);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = this.baseMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<InterfaceInfo> records = interfaceInfoPage.getRecords();
        if (records.isEmpty()) {
            return new Page<>();
        }
        return interfaceInfoPage;

    }

    @Override
    public void interfaceOnline(Long id, HttpServletRequest request) {
        //接口是否存在
        InterfaceInfo interfaceInfo = this.baseMapper.selectById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"无此接口");
        }
        //测试接口是否正常
        String requestParams = interfaceInfo.getRequestParams();
        String url = interfaceInfo.getUrl();
        String method = interfaceInfo.getMethod();
        YyApiClient yyApiclient = this.userService.getYyApiclient(request);
        String result = null;
        synchronized (InterfaceInfoServiceImpl.class) {
            try {
                result = yyApiclient.invokeInterface(requestParams, url, method);
            } catch (Exception e) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR,"接口测试失效");
            }
            if (StringUtils.isBlank(result)) {
                throw new BusinessException(ResultCode.SYSTEM_ERROR,"接口返回值为空");
            }
        }

    }

    @Override
    public void offlineInterface(IdDTO idDTO, HttpServletRequest request) {
        Long id = idDTO.getId();
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getId,id);
        queryWrapper.eq(InterfaceInfo::getStatus, INTERFACE_OPEN);
        InterfaceInfo interfaceInfo = this.baseMapper.selectOne(queryWrapper);
        if (interfaceInfo == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"下线异常");
        }
        int i = this.baseMapper.deleteById(id);
        if (i != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"删除失败");
        }
    }

    @Override
    public String invokeInterface(InterfaceInvokeDTO interfaceInvokeDTO, HttpServletRequest request, InterfaceInfo interfaceInfo) {
        String url = interfaceInfo.getUrl();
        String requestParams = interfaceInvokeDTO.getRequestParams();
        String method = interfaceInfo.getMethod();
        User loginUser = this.userService.getLoginUser(request);
        String secretKey = loginUser.getSecretKey();
        String accessKey = loginUser.getAccessKey();
        YyApiClient yyApiClient = new YyApiClient(accessKey, secretKey);
        String invokeResult = null;
        try {
            invokeResult = yyApiClient.invokeInterface(requestParams, url, method);
        } catch (Exception e) {
            log.error("测试调用失败",e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"调用失败");
        }
        return invokeResult;
    }
}




