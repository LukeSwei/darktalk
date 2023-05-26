package com.luke.darktalk.service;

import com.github.pagehelper.PageInfo;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.model.domain.Moment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.darktalk.model.dto.MomentDto;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【moment(迷你朋友圈)】的数据库操作Service
* @createDate 2023-05-02 18:02:43
*/
public interface MomentService extends IService<Moment> {

    /**
     * 保存帖子
     *
     * @param moment  时刻
     * @param request 请求
     * @return {@code BaseResponse}
     */
    BaseResponse saveMoment(Moment moment, HttpServletRequest request);

    /**
     * 根据用户查询发布的帖子
     *
     * @param momentDto 时刻dto
     * @return {@code PageInfo<Moment>}
     */
    PageInfo<Moment> momentByUserPage(MomentDto momentDto);

    /**
     * 帖子总列表
     *
     * @param momentDto 时刻dto
     * @return {@code PageInfo<Moment>}
     */
    PageInfo<Moment> listPage(MomentDto momentDto);
}
