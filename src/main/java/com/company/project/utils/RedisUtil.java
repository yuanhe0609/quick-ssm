package com.company.project.utils;

import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2024/10/14
 */
public class RedisUtil {
    public static final String host = "localhost";
    public static final Integer port = 6379;
    private static Jedis jedis = new Jedis(host,port);
    public static Jedis getJedis(){
        return jedis;
    }

}
