package com.luke.darktalk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.mapper.MomentMapper;
import com.luke.darktalk.mapper.PictureMapper;
import com.luke.darktalk.model.domain.Moment;
import com.luke.darktalk.model.domain.Picture;
import com.luke.darktalk.model.dto.MomentDto;
import com.luke.darktalk.model.request.PageRequest;
import com.luke.darktalk.model.vo.PictureVo;
import com.luke.darktalk.service.MomentService;
import com.luke.darktalk.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 朋友圈
 *
 * @author caolu
 * @date 2023/05/02
 */
@Slf4j
@RestController
@RequestMapping("/moment")
public class MomentController {

    @Resource
    private MomentService momentService;

    @Resource
    private PictureMapper pictureMapper;

    @Resource
    private MomentMapper momentMapper;

    @PostMapping("/add")
    public BaseResponse addMoment(Moment moment){
        return momentService.saveMoment(moment);
    }

    @PostMapping("/list/page")
    public BaseResponse<PageInfo<Moment>> getMomentPage(@RequestBody MomentDto momentDto){
        PageInfo<Moment> pageInfo= momentService.listPage(momentDto);
        return ResultUtils.success(pageInfo);
    }

    /**
     * 查询某位用户的发表的朋友圈
     * @param momentDto 用户编号
     */
    @PostMapping("/list/page/byUser")
    public BaseResponse<PageInfo<Moment>> getMomentByUserId(@RequestBody MomentDto momentDto){

        PageInfo<Moment> pageInfo= momentService.momentByUserPage(momentDto);
        return ResultUtils.success(pageInfo);
    }

    /**
     * 取消点赞
     *
     * @param id id
     * @return {@link BaseResponse}
     */
    @GetMapping("/cancelLike")
    public BaseResponse cancelLike(@RequestParam Long id){
        boolean cancelLike = momentMapper.cancelLike(id);
        if(!cancelLike){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"点赞取消失败");
        }
        return ResultUtils.success("");
    }

    @GetMapping("/incrLike")
    public BaseResponse incrLike(@RequestParam Long id){
        boolean incrLike = momentMapper.incrLike(id);
        if(!incrLike){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"点赞失败");
        }
        return ResultUtils.success("");
    }
}
