package com.s21.devops.sample.bookingservice.Statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.s21.devops.sample.bookingservice.Communication.BookingStatisticsMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {

    @Value("${fanout.exchange}")
    private String fanoutExchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Counter sentMessagesCounter;



    private final Counter bookingsCounter;

    @Autowired
    public QueueProducer(MeterRegistry meterRegistry) {

        this.sentMessagesCounter = Counter.builder("rabbitmq_messages_sent_total")
                .description("Total messages sent to RabbitMQ")
                .register(meterRegistry);

        this.bookingsCounter = Counter.builder("booking_total")
                .description("Total number of bookings created")
                .register(meterRegistry);
    }

    public void putStatistics(BookingStatisticsMessage bookingStatisticsMessage) throws JsonProcessingException {
        System.out.println("Sending message...");
        rabbitTemplate.setExchange(fanoutExchange);
        rabbitTemplate.convertAndSend(objectMapper.writeValueAsString(bookingStatisticsMessage));

        sentMessagesCounter.increment();

        bookingsCounter.increment();

        System.out.println("Message was sent successfully!");
    }

}
