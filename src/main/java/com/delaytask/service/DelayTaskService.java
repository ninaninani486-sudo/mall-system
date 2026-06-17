package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.entity.DelayTask;
import java.time.LocalDateTime;
import java.util.List;

public interface DelayTaskService extends IService<DelayTask> {

    DelayTask createTask(String taskName, String taskType, String taskParam, LocalDateTime executeTime);

    void updateTaskStatus(Long taskId, String status);

    List<DelayTask> getPendingTasks(LocalDateTime executeTime, int limit);

    void cancelTask(Long taskId);
}
