package com.luke.darktalk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luke.darktalk.model.domain.BuddyList;
import com.luke.darktalk.service.BuddyListService;
import com.luke.darktalk.mapper.BuddyListMapper;
import org.springframework.stereotype.Service;

/**
* @author caolu
* @description 针对表【buddy_list(好友关系表)】的数据库操作Service实现
* @createDate 2023-04-20 12:14:53
*/
@Service
public class BuddyListServiceImpl extends ServiceImpl<BuddyListMapper, BuddyList>
    implements BuddyListService{

}




