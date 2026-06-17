package com.delaytask.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_log")
public class TaskLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private String taskName;

    private String executeStatus;

    private String executeResult;

    private String errorMsg;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String executeNode;

    private LocalDateTime createTime;
}
