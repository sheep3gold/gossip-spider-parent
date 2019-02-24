package cn.itcast.dubboxdemo.service.impl;

import cn.itcast.dubboxdemo.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;

//注意：Service注解与原来不同，需要引入com.alibaba包下的
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String getName() {
        return "itcast";
    }
}
