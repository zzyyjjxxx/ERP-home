package com.erp.system.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.system.entity.SysEventRecord;
import com.erp.system.mapper.SysEventRecordMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventRetryTask {

    private final SysEventRecordMapper eventRecordMapper;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 */5 * * * ?")
    public void retryFailedEvents() {
        LambdaQueryWrapper<SysEventRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysEventRecord::getStatus, 0)
                .lt(SysEventRecord::getRetryCount, SysEventRecord::getMaxRetry);
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
