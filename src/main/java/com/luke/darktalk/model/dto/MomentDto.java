package com.luke.darktalk.model.dto;

import com.luke.darktalk.model.request.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class MomentDto extends PageRequest implements Serializable {

    private String tagCode;

    private long userId;
}
