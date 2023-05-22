package com.luke.darktalk.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.luke.darktalk.model.request.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class BuddyDto extends PageRequest implements Serializable {

    /**
     *
     */
    private Long id;

    /**
     * 用户A的id
     */
    private Integer mineId;

    /**
     * 用户B的id
     */
    private Integer friendId;

    /**
     * 好友名称
     */
    private String friendName;

    /**
     * 好友头像连接
     */
    private Integer friendAvatarUrl;

    /**
     * 备注
     */
    private Integer noteName;

    /**
     * 搜索文本
     */
    private String searchText;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
