package com.xiaoniu.cleanking.selfdebug;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengzhihao
 * @date 2019/12/19 20
 * @mail：zhengzhihao@hellogeek.com
 */
public class JavaTest {

    public static void main(String[] args) {
        List<String> a =new ArrayList<>();
        a.add("a");
        a.add("b");
        System.out.println(a.remove(0));
    }
}
