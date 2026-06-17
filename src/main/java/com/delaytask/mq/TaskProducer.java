package com.delaytask.mq;

import com.delaytask.entity.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TaskProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendDelayTask(DelayTask task, long delayMillis) {
        Map<String, Object> message = new HashMap<>();
        message.put("taskId", task.getId());
        message.put("taskName", task.getTaskName());
        message.put("taskType", task.getTaskType());
        message.put("taskParam", task.getTaskParam());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                message,
                msg -> {
                    msg.getMessageProperties().setDelay((int) delayMillis);
                    return msg;
                }
        );
        log.info("延迟任务已发送, taskId={}, delay={}ms", task.getId(), delayMillis);
    }

    public void sendRetryTask(Long taskId, String taskName, String taskType, String taskParam, int retryCount) {
        Map<String, Object> message = new HashMap<>();
        message.put("taskId", taskId);
        message.put("taskName", taskName);
        message.put("taskType", taskType);
        message.put("taskParam", taskParam);
        message.put("retryCount", retryCount);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                message
        );
        log.info("重试任务已发送, taskId={}, retryCount={}", taskId, retryCount);
    }

    public void sendOrderTimeoutTask(String orderNo, long delayMillis) {
        Map<String, Object> message = new HashMap<>();
        message.put("taskType", "ORDER_TIMEOUT");
        message.put("taskParam", orderNo);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                message,
                msg -> {
                    msg.getMessageProperties().setDelay((int) delayMillis);
                    return msg;
                }
        );
        log.info("订单超时任务已发送, orderNo={}, delay={}ms", orderNo, delayMillis);
    }
}
