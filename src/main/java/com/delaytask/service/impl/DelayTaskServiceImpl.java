package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.DelayTask;
import com.delaytask.mapper.DelayTaskMapper;
import com.delaytask.mq.TaskProducer;
import com.delaytask.service.DelayTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DelayTaskServiceImpl extends ServiceImpl<DelayTaskMapper, DelayTask>
        implements DelayTaskService {

    @Autowired
    private TaskProducer taskProducer;

    @Override
    @Transactional
    public DelayTask createTask(String taskName, String taskType, String taskParam, LocalDateTime executeTime) {
        DelayTask task = new DelayTask();
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setTaskParam(taskParam);
        task.setTaskStatus("PENDING");
        task.setRetryCount(0);
        task.setMaxRetryCount(3);
        task.setRetryInterval(5);
        task.setExecuteTime(executeTime);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        task.setDeleted(0);

        save(task);
        log.info("延迟任务创建成功, taskId={}, executeTime={}", task.getId(), executeTime);

        long delayMillis = executeTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();
        if (delayMillis > 0) {
            taskProducer.sendDelayTask(task, delayMillis);
        }

        return task;
    }

    @Override
    @Transactional
    public void updateTaskStatus(Long taskId, String status) {
        DelayTask task = getById(taskId);
        if (task != null) {
            task.setTaskStatus(status);
            task.setUpdateTime(LocalDateTime.now());
            updateById(task);
        }
    }

    @Override
    public List<DelayTask> getPendingTasks(LocalDateTime executeTime, int limit) {
        return baseMapper.selectPendingTasks(executeTime, limit);
    }

    @Override
    @Transactional
    public void cancelTask(Long taskId) {
        DelayTask task = getById(taskId);
        if (task != null && "PENDING".equals(task.getTaskStatus())) {
            task.setTaskStatus("CANCELLED");
            task.setUpdateTime(LocalDateTime.now());
            updateById(task);
            log.info("任务已取消, taskId={}", taskId);
        }
    }
}
