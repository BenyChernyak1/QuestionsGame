package com.ebay.game;

import com.ebay.game.model.api.AnswerQuestionResponse;
import com.ebay.game.model.api.GetQuestionResponse;
import com.ebay.game.service.GameService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBrokerConfig {

    @Value("${service.rabbitmq.exchange.name}")
    private String topicExchangeName;

    @Value("${service.rabbitmq.questions.queue.name}")
    private String questionsQueueName;

    @Value("${service.rabbitmq.questions.routing.key}")
    private String questionsRoutingKey;

    @Value("${service.rabbitmq.answers.queue.name}")
    private String answersQueueName;

    @Value("${service.rabbitmq.answers.routing.key}")
    private String answersRoutingKey;

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${spring.rabbitmq.port}")
    private String rabbitPort;

    @Value("${broker.container.prefetch.count}")
    private int prefetchCount;

    @Value("${broker.container.concurrent.consumers}")
    private int concurrentConsumers;

    @Value("${broker.container.receive.timeout}")
    private long receiveTimeout;

    @Autowired
    private GameService gameService;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitHost);
        connectionFactory.setUsername(rabbitUsername);
        connectionFactory.setPassword(rabbitPassword);
        connectionFactory.setPort(Integer.parseInt(rabbitPort));
        try {
            connectionFactory.createConnection();
        } catch (AmqpException e) {
            e.printStackTrace();
        }
        return connectionFactory;
    }

    @Bean
    Queue questionsQueue() {
        return new Queue(questionsQueueName, false);
    }

    @Bean
    Queue answersQueue() {
        return new Queue(answersQueueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding bindingQuestionsQueue(Queue questionsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(questionsQueue).to(exchange).with(questionsRoutingKey);
    }

    @Bean
    Binding bindingAnswersQueue(Queue answersQueue, TopicExchange exchange) {
        return BindingBuilder.bind(answersQueue).to(exchange).with(answersRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @RabbitListener(queues = "${service.rabbitmq.questions.queue.name}")
    public GetQuestionResponse processQuestion(Message message) throws Exception {
        return gameService.getQuestion(message);
    }

    @RabbitListener(queues = "${service.rabbitmq.answers.queue.name}")
    public AnswerQuestionResponse processAnswer(Message message) throws Exception {
        return gameService.answerQuestion(message);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory containerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(concurrentConsumers);
        factory.setPrefetchCount(prefetchCount);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setReceiveTimeout(receiveTimeout);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
