package com.luke.darktalk.service;

import com.github.pagehelper.PageInfo;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.model.domain.Moment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luke.darktalk.model.dto.MomentDto;

/**
* @author Administrator
* @description 针对表【moment(迷你朋友圈)】的数据库操作Service
* @createDate 2023-05-02 18:02:43
*/
public interface MomentService extends IService<Moment> {

    BaseResponse saveMoment(Moment moment);

    PageInfo<Moment> momentByUserPage(MomentDto momentDto);

    PageInfo<Moment> listPage(MomentDto momentDto);
}
