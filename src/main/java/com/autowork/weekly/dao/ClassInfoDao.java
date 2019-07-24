package com.autowork.weekly.dao;

import com.autowork.weekly.domain.ClassInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClassInfoDao {

    @Insert("insert into class_info(project_type,class_message,create_time,update_time) values(#{projectType},#{classMessage},#{createTime},#{updateTime})")
    void insert(ClassInfo classInfo);

    @Select("select * from class_info where project_type = #{projectType} order by create_time desc limit 1")
    ClassInfo selectLastOne(int projectType);
}
