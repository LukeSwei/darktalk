package com.luke.darktalk.service.impl;

import cn.xuyanwu.spring.file.storage.FileInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.luke.darktalk.common.BaseResponse;
import com.luke.darktalk.common.ErrorCode;
import com.luke.darktalk.common.ResultUtils;
import com.luke.darktalk.mapper.PictureMapper;
import com.luke.darktalk.model.domain.Moment;
import com.luke.darktalk.model.domain.Picture;
import com.luke.darktalk.model.dto.MomentDto;
import com.luke.darktalk.model.vo.PictureVo;
import com.luke.darktalk.service.MomentService;
import com.luke.darktalk.mapper.MomentMapper;
import com.luke.darktalk.service.PictureService;
import com.luke.darktalk.strategy.StorageService;
import com.luke.darktalk.strategy.StorageStrategy;
import com.luke.darktalk.utils.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author Administrator
* @description 针对表【moment(迷你朋友圈)】的数据库操作Service实现
* @createDate 2023-05-02 18:02:43
*/
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService{

    @Resource
    private MomentMapper momentMapper;

    @Autowired
    private StorageService storageService;

    @Resource
    private PictureService pictureService;

    @Resource
    private PictureMapper pictureMapper;

    @Override
    @Transactional
    public BaseResponse saveMoment(Moment moment) {
        int momentId = momentMapper.insert(moment);
        MultipartFile[] files = moment.getFiles();
        Picture picture = new Picture();
        picture.setMomentId(momentId);
        picture.setUserId(moment.getUserId());
        ArrayList<Picture> list = new ArrayList<>();
        if(null!= files && files.length > 0){
            for (int i = 0; i < files.length; i++){
                String url = storageService.uploadFile(files[i],files[i].getName(),"qiniu");
                picture.setPictureUrl(url);
                list.add(picture);
            }
        }
        if(null!= list &&!list.isEmpty()){
            boolean saveBatch = pictureService.saveBatch(list);
            if(!saveBatch){
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"发布失败");
            }
        }
        return ResultUtils.success("","发布成功");
    }

    /**
     * 查询用户发表记录
     *
     * @param momentDto 时刻dto
     * @return {@link PageInfo}<{@link Moment}>
     */
    @Override
    public PageInfo<Moment> momentByUserPage(MomentDto momentDto) {
        int pageNum = momentDto.getPageNum();
        int pageSize = momentDto.getPageSize();
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Moment> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", momentDto.getUserId());
        List<Moment> records = this.list(wrapper);
        List<Moment> momentList = assembleMomentAndPicture(records);
        return new PageInfo<>(momentList);
    }

    /**
     * 根据code查询文章
     *
     * @param momentDto 时刻dto
     * @return {@link PageInfo}<{@link Moment}>
     */
    @Override
    public PageInfo<Moment> listPage(MomentDto momentDto) {
        int pageNum = momentDto.getPageNum();
        int pageSize = momentDto.getPageSize();
        PageHelper.startPage(pageNum,pageSize);
        String tagCode = momentDto.getTagCode();
        QueryWrapper<Moment> wrapper = new QueryWrapper<>();
        wrapper.eq("tagCode",tagCode);
        List<Moment> records = this.list(wrapper);
        List<Moment> momentList = assembleMomentAndPicture(records);
        return new PageInfo<>(momentList);
    }

    /**
     * 组装朋友圈和图片
     *
     * @param momentList 时刻列表
     * @return {@link List}<{@link Picture}>
     */
    private List<Moment> assembleMomentAndPicture(List<Moment> momentList){
        QueryWrapper<Picture> wrapper1 = new QueryWrapper<>();
        for (Moment item : momentList){
            long id = item.getId();
            wrapper1.eq("momentId",id);
            List<PictureVo> pictures = pictureMapper.listUrl(wrapper1);
            item.setPictureList(pictures);
        }
        return momentList;
    }
}




