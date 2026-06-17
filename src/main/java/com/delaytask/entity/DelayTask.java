package com.delaytask.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("delay_task")
public class DelayTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private String taskType;

    private String taskParam;

    private String taskStatus;

    private Integer retryCount;

    private Integer maxRetryCount;

    private Integer retryInterval;

    private LocalDateTime executeTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
