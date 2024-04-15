package com.api.springbootinit;

import com.api.springbootinit.config.WxOpenConfig;
import javax.annotation.Resource;

import com.api.springbootinit.mapper.UserMapper;
import com.api.springbootinit.model.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Resource
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }

    @Test
    void testprint(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", "li");
        userMapper.selectOne(queryWrapper);
    }

}
