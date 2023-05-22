package com.luke.darktalk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.exception.BusinessException;
import com.luke.darktalk.model.domain.Team;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.domain.UserTeam;
import com.luke.darktalk.model.dto.TeamDto;
import com.luke.darktalk.model.request.*;
import com.luke.darktalk.model.vo.TeamUserVo;
import com.luke.darktalk.service.TeamService;
import com.luke.darktalk.service.UserService;
import com.luke.darktalk.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author caolu
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 新增队伍
     *
     * @param teamAddRequest 队伍请求参数
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }


    /**
     * 更新队伍
     *
     * @param teamUpdateRequest 团队更新请求
     * @param request           请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/update")
    public BaseResponse<Long> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(teamUpdateRequest.getId());
    }

    /**
     * 根据Id获取队伍信息
     *
     * @param id id
     * @return {@link BaseResponse}<{@link Team}>
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeam(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表
     *
     * @param teamDto 团队dto
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link TeamUserVo}>>
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVo>> getTeamList(TeamDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean loginUser = userService.isAdmin(request);
        List<TeamUserVo> list = teamService.listTeams(teamDto, loginUser);


        return ResultUtils.success(list);
    }

    /**
     * 分页查询队伍信息
     *
     * @param teamDto 团队dto
     * @return {@link BaseResponse}<{@link List}<{@link Team}>>
     */
    @GetMapping("/list/page")
    public BaseResponse<List<Team>> getTeamListPage(TeamDto teamDto) {
        int pageSize = teamDto.getPageSize();
        int pageNum = teamDto.getPageNum();
        if (teamDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamDto, team);
        Page<Team> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        Page<Team> teamPage = teamService.page(page, wrapper);
        return ResultUtils.success(teamPage.getRecords());
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 团队加入请求
     * @param request         请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest 团队辞职请求
     * @param request         请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 删除队伍
     *
     * @param id      删除请求
     * @param request 请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(deleteRequest.getId(), loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     *
     * @param teamDto
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVo>> listMyCreateTeams(TeamDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamDto.setUserId(loginUser.getId());
        List<TeamUserVo> teamList = teamService.listTeams(teamDto, true);
        return ResultUtils.success(teamList);
    }


    /**
     * 查询我加入的队伍
     *
     * @param teamDto 团队dto
     * @param request 请求
     * @return {@link BaseResponse}<{@link List}<{@link TeamUserVo}>>
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVo>> listMyJoinTeams(TeamDto teamDto, HttpServletRequest request) {
        if (teamDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);

        Map<Long, List<UserTeam>> collect = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idList = new ArrayList<>(collect.keySet());
        teamDto.setIdList(idList);
        List<TeamUserVo> teamList = teamService.listTeams(teamDto, true);
        return ResultUtils.success(teamList);
    }


}
