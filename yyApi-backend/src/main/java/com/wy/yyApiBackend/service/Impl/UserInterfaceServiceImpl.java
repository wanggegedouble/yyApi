package com.wy.yyApiBackend.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.common.exception.BusinessException;
import com.wy.common.model.UserInterfaceInfo;
import com.wy.common.result.ResultCode;
import com.wy.yyApiBackend.constant.CommonConstant;
import com.wy.yyApiBackend.mapper.UserInterfaceMapper;
import com.wy.yyApiBackend.model.DTO.UserInterfaceDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfacePageDTO;
import com.wy.yyApiBackend.model.DTO.UserInterfaceUpdateDTO;
import com.wy.yyApiBackend.service.UserInterfaceService;
import com.wy.yyApiBackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: wy
 * @CreateTime: 2023-12-18  21:04
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class UserInterfaceServiceImpl extends ServiceImpl<UserInterfaceMapper, UserInterfaceInfo> implements UserInterfaceService {

    @Resource
    private UserService userService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeInterface(UserInterfaceDTO userInterfaceDTO) {
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceDTO,userInterfaceInfo);
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        int insert = this.baseMapper.insert(userInterfaceInfo);
        if (insert != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void cancelInterface(Long id, HttpServletRequest request) {
        boolean admin = this.userService.isAdmin(request);
        if (!admin) {
            throw new BusinessException(ResultCode.NO_AUTH_ERROR);
        }
        validatedDataExist(id);
        int i = this.baseMapper.deleteById(id);
        if (i != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"取消失败");
        }
    }

    @Override
    public void updateUserInterface(UserInterfaceUpdateDTO userInterfaceUpdateDTO) {
        this.validatedDataExist(userInterfaceUpdateDTO.getId());
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceUpdateDTO,userInterfaceInfo);
        int i = this.baseMapper.updateById(userInterfaceInfo);
        if (i != 1) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

    }

    @Override
    public UserInterfaceInfo getInfoById(Long id) {
        UserInterfaceInfo userInterfaceInfo = this.baseMapper.selectById(id);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"无此记录");
        }
        return userInterfaceInfo;
    }

    @Override
    public Page<UserInterfaceInfo> pageList(Integer pageNo, Integer pageSize, UserInterfacePageDTO userInterfacePageDTO) {
        Page<UserInterfaceInfo> pageParam = new Page<>(pageNo, pageSize);
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        String sortOrder = userInterfacePageDTO.getSortOrder();
        String sortField = userInterfacePageDTO.getSortField();
        BeanUtils.copyProperties(userInterfacePageDTO,userInterfaceInfo);
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfo);
        queryWrapper.orderBy(StringUtils.isNotBlank(userInterfacePageDTO.getSortField()),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        Page<UserInterfaceInfo> page = this.baseMapper.selectPage(pageParam, queryWrapper);
        List<UserInterfaceInfo> records = page.getRecords();
        if (records.isEmpty()){
            return new Page<>();
        }
        return page;
    }

    @Override
    public void invokeCount(long interfaceId, long userId) {
        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInterfaceInfo::getInterfaceInfoId,interfaceId);
        updateWrapper.eq(UserInterfaceInfo::getUserId,userId);
        updateWrapper.setSql(true,"leftNum = leftNum - 1,totalNum = totalNum + 1");
        this.update(updateWrapper);
    }
    /**
     * @Description: 校验UserInterface 是否有此id记录
     * @param id UserInterfaceInfo 主键id
     */
    private void validatedDataExist(Long id) {
        UserInterfaceInfo userInterfaceInfo = this.baseMapper.selectById(id);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR,"无此接口调用信息");
        }
    }


}
