package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luke.darktalk.model.domain.Comment;
import com.luke.darktalk.model.domain.User;
import com.luke.darktalk.model.vo.UserCommentVo;
import com.luke.darktalk.service.CommentService;
import com.luke.darktalk.mapper.CommentMapper;
import com.luke.darktalk.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Administrator
* @description 针对表【comment(标签)】的数据库操作Service实现
* @createDate 2023-05-15 17:34:31
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserService userService;

    @Override
    public PageInfo<Comment> listCommentParent(Long momentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getIsDelete,false);
        wrapper.orderByDesc(Comment::getLikeNum);
        //0L-父级评论
        wrapper.eq(Comment::getRootParentId,0L);
        //获取对应的评论列表
        List<Comment> commentList = this.list(wrapper);
        for (Comment comment : commentList) {
            User user = userService.getById(comment.getUserId());
            UserCommentVo userCommentVo = new UserCommentVo();
            BeanUtils.copyProperties(user, userCommentVo);
            comment.setUserCommentVo(userCommentVo);
        }
        return new PageInfo<>(commentList);
    }

    @Override
    public List<Comment> getCommentList(Long commentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Comment::getLikeNum);
        wrapper.eq(Comment::getIsDelete,false);
        wrapper.eq(Comment::getRootParentId,commentId);
        List<Comment> commentList = this.list(wrapper);
        for (Comment comment : commentList){
            User user = userService.getById(comment.getUserId());
            UserCommentVo userCommentVo = new UserCommentVo();
            BeanUtils.copyProperties(user, userCommentVo);
            if(comment.getToUserId()!=null&&comment.getToUserId()!=0){
                User user2 = userService.getById(comment.getToUserId());
                userCommentVo.setToUserId(comment.getToUserId());
                userCommentVo.setToUserName(user2.getUsername());
            }
        }
        PageInfo<Comment> pageInfo = new PageInfo<>(commentList);
        return pageInfo.getList();
    }
}




