package com.erp.system.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.system.entity.SysEventRecord;
import com.erp.system.mapper.SysEventRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventRetryTask {

    private static final Logger log = LoggerFactory.getLogger(EventRetryTask.class);

    private final SysEventRecordMapper eventRecordMapper;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    public EventRetryTask(SysEventRecordMapper eventRecordMapper, ApplicationEventPublisher publisher, ObjectMapper objectMapper) {
        this.eventRecordMapper = eventRecordMapper;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void retryFailedEvents() {
        LambdaQueryWrapper<SysEventRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysEventRecord::getStatus, 0)
                .apply("retry_count < max_retry");
        List<SysEventRecord> events = eventRecordMapper.selectList(wrapper);

        for (SysEventRecord record : events) {
            try {
                Object event = objectMapper.readValue(record.getEventData(), Class.forName(record.getEventName()));
                publisher.publishEvent(event);
                record.setStatus(1);
            } catch (Exception e) {
                record.setRetryCount(record.getRetryCount() + 1);
                record.setLastError(e.getMessage());
                if (record.getRetryCount() >= record.getMaxRetry()) {
                    record.setStatus(2);
                    log.error("事件重试最终失败 eventId={} eventName={}", record.getId(), record.getEventName(), e);
                } else {
                    log.warn("事件重试失败 eventId={} retry={}", record.getId(), record.getRetryCount());
                }
            }
            eventRecordMapper.updateById(record);
        }
    }
}
