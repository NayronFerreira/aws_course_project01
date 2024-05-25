package com.br.strudylab.aws_course_project01.config.local;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.Topic;

import java.net.URI;

@Configuration
@Profile("local")
public class SnsCreated {


    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SnsCreated.class);

    private final String productEventsTopic;

    private final SnsClient snsClient;

    public SnsCreated(){
        this.snsClient = SnsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.of("us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                .name("product-events")
                .build();

        this.productEventsTopic = snsClient.createTopic(createTopicRequest).topicArn();
        log.info("SNS topic created: {}", this.productEventsTopic);
    }

    @Bean
    public SnsClient snsClient() {
        return this.snsClient;
    }

    @Bean(name = "productEventsTopic")
    public Topic snsProductEventsTopic() {
        return Topic.builder()
                .topicArn(productEventsTopic)
                .build();
    }
}
