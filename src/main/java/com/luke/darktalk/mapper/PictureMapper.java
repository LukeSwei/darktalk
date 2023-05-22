package com.luke.darktalk.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.luke.darktalk.model.domain.Picture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luke.darktalk.model.vo.PictureVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【picture(朋友圈图片表)】的数据库操作Mapper
* @createDate 2023-05-02 18:05:09
* @Entity com.luke.darktalk.model.domain.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {

    List<PictureVo> listUrl(@Param(Constants.WRAPPER) QueryWrapper<Picture> wrapper1);
}




