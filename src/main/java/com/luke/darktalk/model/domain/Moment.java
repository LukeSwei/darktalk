package com.luke.darktalk.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luke.darktalk.model.request.PageRequest;
import com.luke.darktalk.model.vo.PictureVo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 迷你朋友圈
 * @TableName moment
 * @author caolu
 */
@TableName(value ="moment")
@Data
public class Moment implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "userId")
    private long userId;

    /**
     * 标签
     */
    @TableField(value = "tagCode")
    private String tagCode;

    /**
     * 用户名
     */
    @TableField(value = "nickName")
    private String nickName;

    /**
     * 头像连接
     */
    @TableField(value = "avatarUrl")
    private String avatarUrl;

    /**
     * 朋友圈内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 朋友圈点赞数
     */
    @TableField(value = "likes")
    private Integer likes;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    private Double latitude;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    private Double longitude;

    /**
     * 位置描述
     */
    @TableField(value = "momentPlace")
    private String momentPlace;

    /**
     * 发布时间，默认为当前时间
     */
    @TableField(value = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    @TableField(exist = false)
    private MultipartFile[] files;

    @TableField(exist = false)
    private List<PictureVo> pictureList;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}