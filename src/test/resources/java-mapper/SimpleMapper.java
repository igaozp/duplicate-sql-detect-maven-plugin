package com.igaozp.dsd.test;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

public interface SimpleMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    Object selectById(Long id);

    @Insert("INSERT INTO users (name) VALUES (#{name})")
    void insertUser(String name);
}
