package com.delaytask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delaytask.entity.DelayTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DelayTaskMapper extends BaseMapper<DelayTask> {

    @Select("SELECT * FROM delay_task WHERE task_status = 'PENDING' AND execute_time <= #{executeTime} AND deleted = 0 LIMIT #{limit}")
    List<DelayTask> selectPendingTasks(@Param("executeTime") LocalDateTime executeTime, @Param("limit") int limit);

    @Select("SELECT * FROM delay_task WHERE task_status = 'PENDING' AND execute_time <= #{executeTime} AND task_type = #{taskType} AND deleted = 0 LIMIT #{limit}")
    List<DelayTask> selectPendingTasksByType(@Param("executeTime") LocalDateTime executeTime, @Param("taskType") String taskType, @Param("limit") int limit);
}
