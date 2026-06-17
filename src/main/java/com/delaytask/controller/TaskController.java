package com.delaytask.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delaytask.entity.DelayTask;
import com.delaytask.entity.TaskLog;
import com.delaytask.service.DelayTaskService;
import com.delaytask.service.TaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@Tag(name = "延迟任务管理接口")
public class TaskController {

    @Autowired
    private DelayTaskService delayTaskService;

    @Autowired
    private TaskLogService taskLogService;

    @PostMapping("/create")
    @Operation(summary = "创建延迟任务")
    public Map<String, Object> createTask(
            @Parameter(description = "任务名称") @RequestParam String taskName,
            @Parameter(description = "任务类型") @RequestParam String taskType,
            @Parameter(description = "任务参数") @RequestParam String taskParam,
            @Parameter(description = "执行时间") @RequestParam String executeTime) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDateTime executeDateTime = LocalDateTime.parse(executeTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            DelayTask task = delayTaskService.createTask(taskName, taskType, taskParam, executeDateTime);
            result.put("code", 200);
            result.put("message", "任务创建成功");
            result.put("data", task);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "任务创建失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/cancel/{taskId}")
    @Operation(summary = "取消延迟任务")
    public Map<String, Object> cancelTask(@PathVariable Long taskId) {
        Map<String, Object> result = new HashMap<>();
        try {
            delayTaskService.cancelTask(taskId);
            result.put("code", 200);
            result.put("message", "任务已取消");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "任务取消失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    @Operation(summary = "查询任务列表")
    public Map<String, Object> listTasks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<DelayTask> taskPage = delayTaskService.page(new Page<>(page, size));
            result.put("code", 200);
            result.put("data", taskPage);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/log/{taskId}")
    @Operation(summary = "查询任务执行日志")
    public Map<String, Object> getTaskLog(@PathVariable Long taskId) {
        Map<String, Object> result = new HashMap<>();
        try {
            IPage<TaskLog> logPage = taskLogService.page(
                    new Page<>(1, 10),
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TaskLog>()
                            .eq(TaskLog::getTaskId, taskId)
                            .orderByDesc(TaskLog::getCreateTime)
            );
            result.put("code", 200);
            result.put("data", logPage);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }
}
