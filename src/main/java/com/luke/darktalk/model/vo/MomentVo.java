package com.luke.darktalk.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luke.darktalk.model.domain.Picture;
import com.luke.darktalk.model.request.PageRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class MomentVo extends PageRequest {
    private long id;

    private long userId;

    private String tagCode;

    private String nickName;

    private String avatarUrl;

    private String content;

    private Integer likes;

    private Integer isDelete;

    private Double latitude;

    private Double longitude;

    private String momentPlace;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Date updateTime;

    private MultipartFile[] files;

    private List<Picture> pictureList;

}
