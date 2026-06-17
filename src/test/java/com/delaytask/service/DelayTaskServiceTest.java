package com.delaytask.service;

import com.delaytask.entity.DelayTask;
import com.delaytask.mapper.DelayTaskMapper;
import com.delaytask.mq.TaskProducer;
import com.delaytask.service.impl.DelayTaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelayTaskServiceTest {

    @Mock
    private DelayTaskMapper delayTaskMapper;

    @Mock
    private TaskProducer taskProducer;

    @InjectMocks
    private DelayTaskServiceImpl delayTaskService;

    @Test
    void createTask_shouldSucceed() {
        LocalDateTime executeTime = LocalDateTime.now().plusMinutes(5);
        when(delayTaskMapper.insert(any(DelayTask.class))).thenReturn(1);

        DelayTask task = delayTaskService.createTask("测试任务", "ORDER_TIMEOUT", "ORD001", executeTime);

        assertNotNull(task);
        assertEquals("PENDING", task.getTaskStatus());
        assertEquals("测试任务", task.getTaskName());
        assertEquals(0, task.getRetryCount());
        assertEquals(3, task.getMaxRetryCount());
        verify(taskProducer).sendDelayTask(any(DelayTask.class), anyLong());
    }

    @Test
    void cancelTask_shouldSucceed() {
        DelayTask task = new DelayTask();
        task.setId(1L);
        task.setTaskStatus("PENDING");
        when(delayTaskMapper.selectById(1L)).thenReturn(task);
        when(delayTaskMapper.updateById(any(DelayTask.class))).thenReturn(1);

        delayTaskService.cancelTask(1L);

        assertEquals("CANCELLED", task.getTaskStatus());
    }

    @Test
    void cancelTask_shouldNotUpdate_whenAlreadyExecuted() {
        DelayTask task = new DelayTask();
        task.setId(1L);
        task.setTaskStatus("SUCCESS");
        when(delayTaskMapper.selectById(1L)).thenReturn(task);

        delayTaskService.cancelTask(1L);

        assertEquals("SUCCESS", task.getTaskStatus());
        verify(delayTaskMapper, never()).updateById(any());
    }

    @Test
    void updateTaskStatus_shouldSucceed() {
        DelayTask task = new DelayTask();
        task.setId(1L);
        task.setTaskStatus("PENDING");
        when(delayTaskMapper.selectById(1L)).thenReturn(task);
        when(delayTaskMapper.updateById(any(DelayTask.class))).thenReturn(1);

        delayTaskService.updateTaskStatus(1L, "SUCCESS");

        assertEquals("SUCCESS", task.getTaskStatus());
    }

    @Test
    void getPendingTasks_shouldReturnTasks() {
        LocalDateTime now = LocalDateTime.now();
        delayTaskService.getPendingTasks(now, 10);
        verify(delayTaskMapper).selectPendingTasks(now, 10);
    }
}
