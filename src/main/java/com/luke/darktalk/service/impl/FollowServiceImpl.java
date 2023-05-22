package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.model.domain.Follow;
import com.luke.darktalk.service.FollowService;
import com.luke.darktalk.mapper.FollowMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【follow(标签)】的数据库操作Service实现
* @createDate 2023-05-07 21:06:01
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{

}




