package com.luke.darktalk.controller;

import com.github.pagehelper.PageInfo;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.model.domain.Comment;
import com.luke.darktalk.model.vo.CommentVo;
import com.luke.darktalk.service.CommentService;
import com.luke.darktalk.service.impl.CommentServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "评论功能接口",tags = "评论功能接口")
@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("/{momentId}")
    public BaseResponse<List<Comment>> listCommentParent(@PathVariable Long momentId,@RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize){
        PageInfo<Comment> commentList = commentService.listCommentParent(momentId,pageNum,pageSize);
        return ResultUtils.success(commentList.getList());
    }

    @GetMapping("/comment/{commentId}")
    public BaseResponse<List<Comment>> getCommentList(@PathVariable Long commentId,@RequestParam Integer pageNum,
                                                      @RequestParam Integer pageSize) {
        List<Comment> list = commentService.getCommentList(commentId,pageNum,pageSize);
        return ResultUtils.success(list);
    }

    @PostMapping("addComment")
    public BaseResponse addComment(@RequestBody CommentVo commentVo){
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentVo,comment);
        boolean save = commentService.save(comment);
        if(!save){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"评论失败");
        }
        return ResultUtils.success("评论成功");
    }
}
