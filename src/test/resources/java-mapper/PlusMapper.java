package com.igaozp.dsd.test;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface PlusMapper extends BaseMapper<Object> {

    @Select("SELECT * FROM table")
    void customMethod();
}
