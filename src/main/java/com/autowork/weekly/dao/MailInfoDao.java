package com.autowork.weekly.dao;

import com.autowork.weekly.domain.MailInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MailInfoDao {

    @Insert("insert into mail_info(project_type,mail_subject,mail_content,create_time,update_time) values(#{projectType},#{mailSubject},#{mailContent},#{createTime},#{updateTime})")
    void insert(MailInfo mailInfo);

    @Select("select * from mail_info where project_type = #{project}")
    List<MailInfo> select(int projectId);

    @Delete("delete from mail_info where project_type = #{project}")
    void delete(int project);
}
