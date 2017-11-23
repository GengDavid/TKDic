package com.example.abnervictor.tkdic;

/**
 * Created by abnervictor on 2017/11/21.
 */



//目前没有作用


public class countryInfo {
    public String countryName;//国号
    public String year;//建国～亡国
    public String leader;//国君
    public String nativeplace;//都城
    public String knownCtr;//知名人物
    public String story;

    public countryInfo(String countryName){
        this.countryName = countryName;
        year = "";
        leader = "";
        nativeplace = "";
        knownCtr = "";
        story = "";
        getData();
    }
    private void getData(){
        //从数据库获取剩下的信息
    }


}
