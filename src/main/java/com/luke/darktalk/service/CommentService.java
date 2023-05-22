package com.luke.darktalk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.luke.darktalk.model.domain.Comment;

import java.util.List;

/**
* @author Administrator
* @description 针对表【comment(标签)】的数据库操作Service
* @createDate 2023-05-15 17:34:31
*/
public interface CommentService extends IService<Comment> {

    /**
     * 查询以及评论列表
     *
     * @param momentId 帖子Id
     * @param pageNum 当前页码
     * @param pageSize 分页数量
     * @return {@link List}<{@link Comment}>
     */
    PageInfo<Comment> listCommentParent(Long momentId, Integer pageNum, Integer pageSize);

    /**
     * 得到评论列表
     *
     * @param commentId 评论id
     * @param pageNum
     * @param pageSize
     * @return {@code List<Comment>}
     */
    List<Comment> getCommentList(Long commentId, Integer pageNum, Integer pageSize);
}
