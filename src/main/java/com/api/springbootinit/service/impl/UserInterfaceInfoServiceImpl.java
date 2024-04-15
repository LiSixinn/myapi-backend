package com.api.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.springbootinit.model.entity.UserInterfaceInfo;
import com.api.springbootinit.mapper.UserInterfaceInfoMapper;
import com.api.springbootinit.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
* @author Lii
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2024-04-15 15:18:20
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

}




