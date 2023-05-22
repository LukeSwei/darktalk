package com.luke.darktalk.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.darktalk.model.domain.Moment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【moment(迷你朋友圈)】的数据库操作Mapper
* @createDate 2023-05-02 18:02:43
* @Entity com.luke.darktalk.model.domain.Moment
*/
public interface MomentMapper extends BaseMapper<Moment> {

    boolean cancelLike(@Param("id") Long id);

    boolean incrLike(@Param("id") Long id);
}




