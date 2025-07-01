package org.the.husky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.service.impl.PreloadServiceImpl;
import org.the.husky.util.HashGenerator;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PreloadServiceImplTest {

    private RedisNodeClient redisClient;
    private Config config;

    @BeforeEach
    void setUp() {
        redisClient = mock(RedisNodeClient.class);
        config = mock(Config.class);

        when(config.getCodes()).thenReturn(List.of("38067", "38097"));
        when(config.getNumbersPerPrefix()).thenReturn(2_000);
        when(config.getBatchSize()).thenReturn(1000);
        when(config.getPoolSize()).thenReturn(2);

        HashGenerator.init("SHA3-256", "testSalt");
    }

    @Test
    void shouldCallPutBatchMultipleTimes() throws InterruptedException {
        PreloadServiceImpl service = new PreloadServiceImpl(redisClient, config);
        service.preload();

        verify(redisClient, times(4)).putBatch(any(Map.class));
    }
}
