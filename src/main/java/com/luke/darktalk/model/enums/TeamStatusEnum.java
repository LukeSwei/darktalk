package com.luke.darktalk.model.enums;

/**
 * 团队状态枚举
 *
 * @author caolu
 * @date 2023/04/16
 */
public enum TeamStatusEnum {


    /**
     * 公共
     */
    PUBLIC(0,"公开"),
    /**
     * 私有
     */
    PRIVATE(1,"私有"),
    /**
     * 加密
     */
    SECRET(2,"加密");


    private int value;

    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value){
        if(value==null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values){
            if(teamStatusEnum.getValue() == value){
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
