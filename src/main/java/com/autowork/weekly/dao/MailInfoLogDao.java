package com.autowork.weekly.dao;

import com.autowork.weekly.domain.MailInfo;
import com.autowork.weekly.domain.MailInfoLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MailInfoLogDao {

    @Insert("insert into mail_info_log(project_type,mail_subject,mail_content,create_time,update_time) values(#{projectType},#{mailSubject},#{mailContent},#{createTime},#{updateTime})")
    void insert(MailInfoLog mailInfoLog);

    @Select("select * from mail_info_info where project_type = #{project}")
    List<MailInfo> select(int projectId);
}
