package org.the.husky.web;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.the.husky.client.RedisNodeClient;
import org.the.husky.config.Config;
import org.the.husky.util.HashGenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebServerTest {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String PHONE = "380671231231";
    private static final int PORT = 7070;

    private static RedisNodeClient redisClient;
    private static RequestValidator requestValidator;


    @BeforeAll
    static void startServer() throws InterruptedException, ExecutionException, TimeoutException {
        Config config = mock(Config.class);
        requestValidator = mock(RequestValidator.class);
        redisClient = mock(RedisNodeClient.class);

        when(config.getUsername()).thenReturn("Guchi");
        when(config.getPassword()).thenReturn("TheHusky");
        when(config.getHashAlgorithm()).thenReturn("SHA-256");
        when(config.getSalt()).thenReturn("theHuskySalt");
        when(config.getServerPort()).thenReturn(PORT);

        HashGenerator.init(config);

        WebServer server = new WebServer(redisClient, requestValidator, config);

        Future<?> future = executor.submit(server::start);
        future.get(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopServer() {
        executor.shutdown();
    }

    @Test
    void shouldReturnHashForPhone_whenAuthorized() throws Exception {
        String token = Base64.getEncoder().encodeToString("Guchi:TheHusky".getBytes());

        URL url = new URL("http://localhost:" + PORT + "/hash?phone=" + PHONE);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + token);

        assertEquals(200, connection.getResponseCode());

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                .readLine();

        assertTrue(response.contains("hash"));
    }

    @Test
    void shouldReturnUnauthorized_whenNoAuthHeader() throws Exception {
        URL url = new URL("http://localhost:" + PORT + "/hash?phone=" + PHONE);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        when(requestValidator.isNotAuthorized(null)).thenReturn(true);

        assertEquals(401, connection.getResponseCode());

        String response = new BufferedReader(new InputStreamReader(connection.getErrorStream()))
                .readLine();

        assertTrue(response.contains("Unauthorized"));
    }

    @Test
    void shouldReturnPhone_whenHashIsFound() throws Exception {
        String hash = HashGenerator.generate(PHONE);

        when(redisClient.findPhoneByHash(hash)).thenReturn(PHONE);

        String token = Base64.getEncoder().encodeToString("Guchi:TheHusky".getBytes());
        URL url = new URL("http://localhost:" + PORT + "/phone?hash=" + hash);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + token);

        assertEquals(200, connection.getResponseCode());

        String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                .readLine();

        assertTrue(response.contains(PHONE));
    }
}
