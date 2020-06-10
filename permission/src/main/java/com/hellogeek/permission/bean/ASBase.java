package com.hellogeek.permission.bean;

import com.hellogeek.permission.Integrate.Permission;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/8
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class ASBase implements Serializable {


    /**
     * 行为延迟时间
     */
    public int delay_time;
    /**
     * 大分类ID
     */
    public int type_id;
    /**
     * 描述
     */
    public String describe;
    public Permission permission;
    public boolean isNecessary;

    public ASIntentBean intent;
    public ArrayList<ASStepBean> step;
    public  boolean isAllow;
    public  int executeNumber;
    /**
     * 弹窗引导
     */
    public int alterWindowGuide;
}
