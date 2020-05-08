package com.xiaoniu.asmhelp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



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
public class PhoneConfig implements Serializable {


    public String model;
    public String rom;
    public String manufacturer;
    public int api;
    public List<ASBase> data;

}
