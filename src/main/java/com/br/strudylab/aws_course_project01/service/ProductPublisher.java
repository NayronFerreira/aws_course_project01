package com.br.strudylab.aws_course_project01.service;

import com.br.strudylab.aws_course_project01.enums.EventType;
import com.br.strudylab.aws_course_project01.model.Envelope;
import com.br.strudylab.aws_course_project01.model.Product;
import com.br.strudylab.aws_course_project01.model.ProductEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.Topic;

@Service
public class ProductPublisher {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ProductPublisher.class);

    private final SnsClient snsClient;
    private final Topic productEventsTopic;
    private  final ObjectMapper objectMapper;

    public ProductPublisher(SnsClient snsClient, @Qualifier("productEventsTopic") Topic productEventsTopic, ObjectMapper objectMapper) {
        this.snsClient = snsClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public void publishProductEvent(Product product, EventType eventType, String username) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(product.getId());
        productEvent.setUsername(username);
        productEvent.setCode(product.getCode());

        Envelope envelope = new Envelope();
        envelope.setEventType(eventType);

        try {
            envelope.setData(objectMapper.writeValueAsString(productEvent));
            String productEventJson = objectMapper.writeValueAsString(productEvent);
            snsClient.publish(builder -> {
                        try {
                            builder
                                    .topicArn(productEventsTopic.topicArn())
                                    .message(objectMapper.writeValueAsString(productEvent));
                        } catch (JsonProcessingException e) {
                            log.error("Error publishing product event", e);
                        }
                    }
            );
            log.info("Product event published: {}", productEventJson);
        } catch (Exception e) {
            log.error("Error publishing product event", e);
        }
    }

}
