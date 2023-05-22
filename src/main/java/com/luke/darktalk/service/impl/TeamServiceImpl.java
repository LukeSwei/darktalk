package com.luke.darktalk.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.contant.TeamConstant;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.mapper.TeamMapper;
import com.luke.darktalk.model.domain.Team;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.domain.UserTeam;
import com.luke.darktalk.model.dto.TeamDto;
import com.luke.darktalk.model.enums.TeamStatusEnum;
import com.luke.darktalk.model.request.TeamJoinRequest;
import com.luke.darktalk.model.request.TeamQuitRequest;
import com.luke.darktalk.model.request.TeamUpdateRequest;
import com.luke.darktalk.model.vo.TeamUserVo;
import com.luke.darktalk.model.vo.UserVo;
import com.luke.darktalk.service.TeamService;
import com.luke.darktalk.service.UserService;
import com.luke.darktalk.service.UserTeamService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author caolu
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-04-14 23:28:36
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //用户登录时，获取用户id
        long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        String name = team.getName();
        if (StringUtils.isEmpty(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长或为空");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByValue) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍过期时间设置错误");
        }
        //TODO 同时点击创建100个队伍
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", userId);
        long dbTeamNum = this.count(wrapper);
        if (dbTeamNum >= TeamConstant.TEAM_MAX_NUM) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean userTeams = userTeamService.save(userTeam);
        if (!userTeams) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败或加入队伍失败");
        }
        return teamId;
    }

    //TODO 查询已加入用户信息
    @Override
    public List<TeamUserVo> listTeams(TeamDto teamDto, boolean loginUser) {
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        String searchText = teamDto.getSearchText();
        wrapper.and(StringUtils.isNotBlank(searchText), qw -> qw.like("name", searchText).or().like("description", searchText));
        Long id = teamDto.getId();
        wrapper.eq(id != null && id > 0, "id", id);
        String name = teamDto.getName();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);
        String description = teamDto.getDescription();
        wrapper.like(StringUtils.isNotBlank(description), "description", description);
        Integer maxNum = teamDto.getMaxNum();
        wrapper.eq(maxNum != null && maxNum > 0, "maxNum", maxNum);
        Long userId = teamDto.getUserId();
        wrapper.eq(userId != null && userId > 0, "userId", userId);
        Integer status = teamDto.getStatus();
        wrapper.eq(status != null && status > -1, "status", status);
        //不查询过期的
        wrapper.and(c -> c.ge("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> teamList = this.list(wrapper);
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        } else {
            for (Team team : teamList) {
                Long captain = team.getUserId();
                if (captain == null) {
                    continue;
                }
                User user = userService.getById(captain);
                TeamUserVo teamUserVo = new TeamUserVo();
                BeanUtils.copyProperties(team, teamUserVo);
                if (user != null) {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    teamUserVo.setCreateUser(userVo);
                }
                teamUserVoList.add(teamUserVo);
            }
            return teamUserVoList;
        }
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 只有管理员或者队伍的创建者可以修改
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 加入队伍请求参数
     * @param loginUser       登录用户
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 该用户已加入的队伍数量
        long userId = loginUser.getId();
        //设置只有一个线程可以获取锁
        RLock lock = redissonClient.getLock("darktalk:join_team");
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
                    wrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(wrapper);
                    if (hasJoinNum > TeamConstant.TEAM_MAX_NUM) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
                    }
                    wrapper = new QueryWrapper<>();
                    wrapper.eq("userId", userId);
                    wrapper.eq("teamId", teamId);
                    long hasUserJoinTeam = userTeamService.count(wrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                    }
                    // 已加入队伍的人数
                    long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    // 修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest 退出队伍请求参数
     * @param loginUser       登录用户
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择要操作的队伍");
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getById(teamId);
        long quitId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(quitId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍队伍中");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum == 1) {
            //当队伍只剩一人时解散队伍
            this.removeById(teamId);
        }else {
            //当队伍还剩至少两人时，为队长时
            if (team.getUserId() == quitId) {
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍队长失败");
                }
            }
        }
        // 移除关系
        return userTeamService.remove(wrapper);
    }

    /**
     * 删除解散队伍
     *
     * @param id        id
     * @param loginUser 登录用户
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        // 校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        // 校验你是不是队伍的队长
        if (team.getUserId() != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        // 移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);

    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

}




