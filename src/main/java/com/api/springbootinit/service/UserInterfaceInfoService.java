package com.api.springbootinit.service;

import com.api.springbootinit.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.api.springbootinit.model.entity.UserInterfaceInfo;

/**
* @author Lii
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-04-15 15:18:20
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userinterfaceInfo, boolean add);
}
