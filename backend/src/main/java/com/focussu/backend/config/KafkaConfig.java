package com.focussu.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Kafka 관련 추가 설정이 있다면 여기에 작성 (기본 설정은 application.yml 활용)
}
