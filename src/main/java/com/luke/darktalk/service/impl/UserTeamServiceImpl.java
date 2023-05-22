package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.luke.darktalk.mapper.UserTeamMapper;
import com.luke.darktalk.model.domain.UserTeam;
import com.luke.darktalk.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author caolu
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-04-14 23:39:07
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




