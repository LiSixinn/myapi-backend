package com.api.springbootinit.controller;

import com.api.myapiclientsdk.client.MyApiClient;
import com.api.springbootinit.annotation.AuthCheck;
import com.api.springbootinit.common.*;
import com.api.springbootinit.constant.CommonConstant;
import com.api.springbootinit.exception.BusinessException;
import com.api.springbootinit.exception.ThrowUtils;
import com.api.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import com.api.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoInvokeRequest;
import com.api.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.api.springbootinit.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import com.api.springbootinit.model.entity.UserInterfaceInfo;
import com.api.springbootinit.model.entity.User;
import com.api.springbootinit.model.enums.UserInterfaceInfoStatusEnum;
import com.api.springbootinit.service.UserInterfaceInfoService;
import com.api.springbootinit.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口管理
 *
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private MyApiClient myApiClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        // 校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newUserInterfaceInfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param userInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }


    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        String description = userInterfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        userInterfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInterfaceInfoPage);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(userInterfaceInfoList);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        return ResultUtils.success(userInterfaceInfo);
    }

    /**
     * 上线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineUserInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        // 如果id为null或者id小于等于0
        if (idRequest == null || idRequest.getId() <= 0) {
            // 抛出业务异常，表示请求参数错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.校验该接口是否存在
        // 获取idRequest对象的id属性值
        long id = idRequest.getId();
        // 根据id查询接口信息数据
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        // 如果查询结果为空
        if (oldUserInterfaceInfo == null) {
            // 抛出业务异常，表示未找到数据
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2.判断该接口是否可以调用
        com.api.myapiclientsdk.model.User user = new com.api.myapiclientsdk.model.User();
        user.setUsername("test");

        String username = myApiClient.getUserNameByPost(user);
        if(StringUtils.isBlank(username)){
            //接口验证失败
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Interface validation failed");
        }

        //3.online
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        //调用数据库，设为可用
        userInterfaceInfo.setId(id);
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.ONLINE.getValue());

        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineUserInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        // 如果id为null或者id小于等于0
        if (idRequest == null || idRequest.getId() <= 0) {
            // 抛出业务异常，表示请求参数错误
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.校验该接口是否存在
        // 获取idRequest对象的id属性值
        long id = idRequest.getId();
        // 根据id查询接口信息数据
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        // 如果查询结果为空
        if (oldUserInterfaceInfo == null) {
            // 抛出业务异常，表示未找到数据
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }


        // 2.offline
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        //调用数据库，设为可用
        userInterfaceInfo.setId(id);
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.OFFLINE.getValue());

        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 测试调用
     *
     * @param userInterfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
// 这里给它新封装一个参数UserInterfaceInfoInvokeRequest
// 返回结果把对象发出去就好了，因为不确定接口的返回值到底是什么
    public BaseResponse<Object> invokeUserInterfaceInfo(@RequestBody UserInterfaceInfoInvokeRequest userInterfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {

        // 检查请求对象是否为空或者接口id是否小于等于0
        if (userInterfaceInfoInvokeRequest == null || userInterfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口id
        long id = userInterfaceInfoInvokeRequest.getId();
        // 获取用户请求参数
        String userRequestParams = userInterfaceInfoInvokeRequest.getUserRequestParams();
        // 判断用户是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (oldUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 检查接口状态是否为下线状态
        if (oldUserInterfaceInfo.getStatus() == UserInterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Interface closed");
        }

        // 获取当前登录用户的ak和sk，这样相当于用户自己的这个身份去调用，
        // 也不会担心它刷接口，因为知道是谁刷了这个接口，会比较安全
        User loginUser = userService.getLoginUser(request);

        // todo 需要修改为读取所输入的
//        String accessKey = loginUser.getAccessKey();
//        String secretKey = loginUser.getSecretKey();
//        MyApiClient tempClient = new MyApiClient(accessKey, secretKey);
        // 我们只需要进行测试调用，所以我们需要解析传递过来的参数。
        Gson gson = new Gson();
        // 将用户请求参数转换为com.api.myapiclientsdk.model.User对象
        com.api.myapiclientsdk.model.User user = gson.fromJson(userRequestParams, com.api.myapiclientsdk.model.User.class);
        // 调用MyApiClient的getUsernameByPost方法，传入用户对象，获取用户名
        String userNameByPost = myApiClient.getUserNameByPost(user);
        // 返回成功响应，并包含调用结果
        return ResultUtils.success(userNameByPost);
    }


}
