package com.autowork.weekly.domain.emun;

public enum ProjectType {


    OP_ADMIN(1,"XX神仙项目");

    int id;
    String code;

     private ProjectType(int id, String code){
        this.id = id;
        this.code = code;
    }

    public int getId(){
        return this.id;
    }

    public String getCode(){
        return this.code;
    }
}
