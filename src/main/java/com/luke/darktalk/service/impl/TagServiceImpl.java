package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.model.domain.Tag;
import com.luke.darktalk.service.TagService;
import com.luke.darktalk.mapper.TagMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【tag(标签)】的数据库操作Service实现
 * @createDate 2023-04-24 21:58:55
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

}




