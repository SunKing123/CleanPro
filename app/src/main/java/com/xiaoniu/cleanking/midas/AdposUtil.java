package com.xiaoniu.cleanking.midas;

/**
 * @author zhengzhihao
 * @date 2020/7/7 16
 * @mail：zhengzhihao@hellogeek.com
 */
public class AdposUtil {
    /**
     *
     * @param localNum
     * @param adIndex
     * @return
     */
    public static String getAdPos(int localNum,int adIndex){
        String adpos="";
        switch (localNum){
            case 1://1-左上
                adpos = MidasConstants.HOMELTTOP_LIST.get(adIndex);
                break;
            case 2://1-右上
                adpos = MidasConstants.HOMERTTOP_LIST.get(adIndex);
                break;
            case 3://左下
                adpos = MidasConstants.HOMELFBOTTOM_LIST.get(adIndex);
                break;
            case 4://右下
                adpos = MidasConstants.HOMERTBOTTOM_LIST.get(adIndex);
                break;
        }
        return adpos;
    }
}
