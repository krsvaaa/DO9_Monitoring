package com.s21.devops.sample.reportservice.Statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s21.devops.sample.reportservice.Communication.BookingStatisticsMessage;
import com.s21.devops.sample.reportservice.Service.BookingStatsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    @Autowired
    private BookingStatsService bookingStatsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Counter processedMessagesCounter;

    @Autowired
    public QueueConsumer(MeterRegistry meterRegistry) {
        this.processedMessagesCounter = Counter.builder("rabbitmq_messages_processed_total")
                .description("Total RabbitMQ messages processed")
                .register(meterRegistry);
    }

    public void receiveMessage(String message) throws JsonProcessingException {
        System.out.println("Received !!! (String) " + message);
        processMessage(message);
    }

    public void receiveMessage(byte[] message) throws JsonProcessingException {
        String strMessage = new String(message);
        System.out.println("Received !!! (No String) " + strMessage);
        processMessage(strMessage);
    }

    private void processMessage(String message) throws JsonProcessingException {
        BookingStatisticsMessage bsm =
                objectMapper.readValue(message, BookingStatisticsMessage.class);
        bookingStatsService.postBookingStatsMessage(bsm);
        processedMessagesCounter.increment();
    }
}
