package com.example.demo;

import com.example.demo.service.ChargingRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class BoundaryTest {

    @Autowired
    private ChargingRewardService chargingRewardService;

    @Test
    public void testMidnightCrossing() {
        System.out.println("========== 午夜边界测试 ==========");
        
        LocalDateTime start = LocalDateTime.now().withHour(23).withMinute(45).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(45);
        System.out.println("开始时间: " + start);
        System.out.println("结束时间: " + end);
        
        try {
            double reward = chargingRewardService.calculateReward("TEST_USER", start, end, 10.0);
            System.out.println("计算成功，奖励: " + reward + " 元");
        } catch (Exception e) {
            System.out.println("出现异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========== 测试完成! ==========");
    }
}
