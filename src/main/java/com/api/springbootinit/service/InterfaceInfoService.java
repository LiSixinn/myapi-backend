package com.api.springbootinit.service;

import com.api.springbootinit.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lii
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-04-13 15:38:45
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
