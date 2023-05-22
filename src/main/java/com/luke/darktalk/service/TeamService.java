package com.luke.darktalk.service;

import com.luke.darktalk.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.dto.TeamDto;
import com.luke.darktalk.model.request.TeamJoinRequest;
import com.luke.darktalk.model.request.TeamQuitRequest;
import com.luke.darktalk.model.request.TeamUpdateRequest;
import com.luke.darktalk.model.vo.TeamUserVo;

import java.util.List;

/**
* @author caolu
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-04-14 23:28:36
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team      团队
     * @param loginUser 登录用户
     * @return long
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍
     *
     * @param teamDto   队伍传输对象
     * @param loginUser
     * @return {@link List}<{@link TeamUserVo}>
     */
    List<TeamUserVo> listTeams(TeamDto teamDto, boolean loginUser);

    /**
     * 更新队伍
     *
     * @param team      团队
     * @param loginUser 登录用户
     * @return boolean
     */
    boolean updateTeam(TeamUpdateRequest team, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 加入队伍请求参数
     * @param loginUser       登录用户
     * @return boolean
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest 退出队伍请求参数
     * @param loginUser       登录用户
     * @return boolean
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    boolean deleteTeam(long id, User loginUser);
}
