package com.delaytask.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.TaskLog;
import com.delaytask.mapper.TaskLogMapper;
import com.delaytask.service.TaskLogService;
import org.springframework.stereotype.Service;

@Service
public class TaskLogServiceImpl extends ServiceImpl<TaskLogMapper, TaskLog>
        implements TaskLogService {
}
