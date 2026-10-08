package com.streamingsystem.cloudservice.config.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        String redisAddress = String.format("redis://%s:%d", host, port);

        config.setCodec(new SerializationCodec());

        config.useSingleServer()
                .setAddress(redisAddress)
                .setConnectTimeout(3000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setConnectionPoolSize(32)
                .setConnectionMinimumIdleSize(8);

        return Redisson.create(config);
    }
}
