package com.delaytask.mq;

import com.delaytask.entity.DelayTask;
import com.delaytask.entity.TaskLog;
import com.delaytask.redis.RedisLock;
import com.delaytask.service.DelayTaskService;
import com.delaytask.service.PaymentService;
import com.delaytask.service.TaskLogService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class TaskConsumer {

    @Autowired
    private DelayTaskService delayTaskService;

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private TaskProducer taskProducer;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.DELAY_QUEUE)
    public void handleDelayTask(Message message, Channel channel) throws IOException {
        Map<String, Object> messageBody = objectMapper.readValue(message.getBody(), Map.class);

        Long taskId = Long.valueOf(messageBody.get("taskId").toString());
        String taskName = messageBody.get("taskName").toString();
        String taskType = messageBody.get("taskType").toString();
        String taskParam = messageBody.get("taskParam").toString();
        int retryCount = messageBody.get("retryCount") != null ?
                Integer.parseInt(messageBody.get("retryCount").toString()) : 0;

        String lockKey = "task:execute:" + taskId;
        if (!redisLock.tryLock(lockKey, "1", 30000)) {
            log.warn("任务正在执行中, taskId={}", taskId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setTaskName(taskName);
        taskLog.setExecuteStatus("PROCESSING");
        taskLog.setStartTime(LocalDateTime.now());
        taskLog.setExecuteNode(getNodeName());
        taskLogService.save(taskLog);

        try {
            boolean success = executeTask(taskType, taskParam);

            if (success) {
                taskLog.setExecuteStatus("SUCCESS");
                taskLog.setExecuteResult("任务执行成功");
                delayTaskService.updateTaskStatus(taskId, "SUCCESS");
                log.info("任务执行成功, taskId={}", taskId);
            } else {
                throw new RuntimeException("任务执行失败");
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("任务执行异常, taskId={}", taskId, e);

            DelayTask task = delayTaskService.getById(taskId);
            if (task != null && retryCount < task.getMaxRetryCount()) {
                taskProducer.sendRetryTask(taskId, taskName, taskType, taskParam, retryCount + 1);
                delayTaskService.updateTaskStatus(taskId, "RETRY");
                taskLog.setExecuteStatus("RETRY");
                taskLog.setErrorMsg("任务执行失败，已加入重试队列，第" + (retryCount + 1) + "次重试");
            } else {
                delayTaskService.updateTaskStatus(taskId, "FAILED");
                taskLog.setExecuteStatus("FAILED");
                taskLog.setErrorMsg(e.getMessage());
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } finally {
            taskLog.setEndTime(LocalDateTime.now());
            taskLogService.updateById(taskLog);
            redisLock.unlock(lockKey);
        }
    }

    private boolean executeTask(String taskType, String taskParam) {
        switch (taskType) {
            case "ORDER_TIMEOUT":
                return handleOrderTimeout(taskParam);
            case "SMS_REMINDER":
                return handleSmsReminder(taskParam);
            case "DATA_CLEANUP":
                return handleDataCleanup(taskParam);
            default:
                log.warn("未知的任务类型: {}", taskType);
                return false;
        }
    }

    private boolean handleOrderTimeout(String param) {
        log.info("处理订单超时任务, orderNo={}", param);
        paymentService.handleOrderTimeout(param);
        return true;
    }

    private boolean handleSmsReminder(String param) {
        log.info("处理短信提醒任务, param={}", param);
        return true;
    }

    private boolean handleDataCleanup(String param) {
        log.info("处理数据清理任务, param={}", param);
        return true;
    }

    private String getNodeName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
