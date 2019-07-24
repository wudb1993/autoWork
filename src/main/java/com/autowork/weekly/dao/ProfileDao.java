package com.autowork.weekly.dao;

import com.autowork.weekly.domain.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProfileDao {

    @Select("select * from auto_work_profile where name = #{name} limit 1")
    Profile select(String name);
}
