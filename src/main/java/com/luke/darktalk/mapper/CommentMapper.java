package com.luke.darktalk.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.luke.darktalk.model.domain.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author luke
* @description 针对表【comment(标签)】的数据库操作Mapper
* @createDate 2023-05-15 17:34:31
* @Entity com.luke.darktalk.domain.Comment
*/
public interface CommentMapper extends BaseMapper<Comment> {

}




