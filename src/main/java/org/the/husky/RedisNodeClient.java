package org.the.husky;

import org.redisson.Redisson;
import org.redisson.api.RBatch;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisNodeClient {
    private final RedissonClient redisson;

    public RedisNodeClient(String redisUrl) {
        waitUntilRedisReady(redisUrl);           // unchanged
        this.redisson = createClient(redisUrl);
    }

    // ★ CHANGED: key = "h:" + hash , value = phone
    public void putBatch(Map<String,String> phoneToHash){
        RBatch batch = redisson.createBatch();
        phoneToHash.forEach((phone,hash) -> batch.getBucket("h:"+hash).setAsync(phone));
        batch.execute();
    }

    // GET phone by hash – single point lookup
    public String findPhoneByHash(String hash){
        Object v = redisson.getBucket("h:"+hash).get();
        return v==null?null:v.toString();
    }

    private RedissonClient createClient(String redisUrl){
        Config cfg=new Config(); cfg.useSingleServer().setAddress(redisUrl); return Redisson.create(cfg);}

    // waitUntilRedisReady kept for robustness
    private void waitUntilRedisReady(String redisUrl){
        String host=redisUrl.split("//")[1].split(":")[0];
        int port=Integer.parseInt(redisUrl.split(":")[2]);
        System.out.println("⏳ Waiting for Redis at "+host+":"+port);
        for(int i=0;i<30;i++){
            try(Socket s=new Socket()){s.connect(new InetSocketAddress(host,port),800);
                try(PrintWriter out=new PrintWriter(s.getOutputStream(),true);
                    BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()))){
                    out.println("PING"); if("+PONG".equals(in.readLine())){System.out.println("✔ Redis "+host+":"+port+" is ready.");return;}}
            }catch(Exception ignore){}
            try{Thread.sleep(1000);}catch(InterruptedException e){Thread.currentThread().interrupt();}}
        throw new RuntimeException("Redis "+host+":"+port+" not ready");}
}

