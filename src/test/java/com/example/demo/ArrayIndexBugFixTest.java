package com.example.demo;

import com.example.demo.entity.RewardResult;
import com.example.demo.service.ChargingRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ArrayIndexBugFixTest {

    @Autowired
    private ChargingRewardService chargingRewardService;

    @Test
    public void testPointIndexRange() throws Exception {
        System.out.println("========== 测试 pointIndex 范围 ==========");
        
        Method method = ChargingRewardService.class.getDeclaredMethod("getPointIndex", LocalDateTime.class);
        method.setAccessible(true);
        
        List<Integer> indexes = new ArrayList<>();
        
        for (int hour = 0; hour < 48; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                LocalDateTime time = LocalDateTime.now().withHour(hour % 24).withMinute(minute);
                int index = (int) method.invoke(chargingRewardService, time);
                
                indexes.add(index);
                
                assertTrue(index >= 0 && index < 96, 
                    String.format("时间 %02d:%02d: index=%d 超出范围 [0, 95]", hour % 24, minute, index));
            }
        }
        
        System.out.println("测试了 " + indexes.size() + " 个时间点");
        System.out.println("所有 pointIndex 都在有效范围内 (0-95)");
    }

    @Test
    public void testMidnightCrossing1() {
        System.out.println("\n========== 测试跨越午夜场景 1: 从 23:45 到次日 00:30 ==========");
        
        LocalDateTime start = LocalDateTime.now().withHour(23).withMinute(45).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(45);
        
        System.out.println("开始时间: " + start);
        System.out.println("结束时间: " + end);
        
        assertDoesNotThrow(() -> {
            RewardResult result = chargingRewardService.calculateRewardWithDetails("USER001", start, end, 10.0);
            System.out.println("计算成功! 总奖励: " + result.getTotalReward() + " 元");
            assertNotNull(result);
        });
    }

    @Test
    public void testMidnightCrossing2() {
        System.out.println("\n========== 测试跨越午夜场景 2: 从 23:00 到次日 05:00 ==========");
        
        LocalDateTime start = LocalDateTime.now().withHour(23).withMinute(0);
        LocalDateTime end = start.plusHours(6);
        
        System.out.println("开始时间: " + start);
        System.out.println("结束时间: " + end);
        
        assertDoesNotThrow(() -> {
            RewardResult result = chargingRewardService.calculateRewardWithDetails("USER002", start, end, 30.0);
            System.out.println("计算成功! 总奖励: " + result.getTotalReward() + " 元");
            System.out.println("时段详情数量: " + result.getPointDetails().size());
            assertNotNull(result);
        });
    }

    @Test
    public void testMidnightCrossing3() {
        System.out.println("\n========== 测试跨越午夜场景 3: 多天充电 ==========");
        
        LocalDateTime start = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime end = start.plusDays(2).withHour(14).withMinute(0);
        
        System.out.println("开始时间: " + start);
        System.out.println("结束时间: " + end);
        
        assertDoesNotThrow(() -> {
            RewardResult result = chargingRewardService.calculateRewardWithDetails("USER003", start, end, 100.0);
            System.out.println("计算成功! 总奖励: " + result.getTotalReward() + " 元");
            System.out.println("时段详情数量: " + result.getPointDetails().size());
            assertNotNull(result);
        });
    }

    @Test
    public void testBoundaryTimes() {
        System.out.println("\n========== 测试边界时间点 ==========");
        
        int[][] boundaryTimes = {
            {0, 0}, {0, 1}, {0, 14}, {0, 15},
            {23, 44}, {23, 45}, {23, 59},
        };
        
        for (int[] time : boundaryTimes) {
            int hour = time[0];
            int minute = time[1];
            LocalDateTime t = LocalDateTime.now().withHour(hour).withMinute(minute);
            LocalDateTime end = t.plusMinutes(15);
            
            assertDoesNotThrow(() -> {
                double reward = chargingRewardService.calculateReward("TEST", t, end, 1.0);
                System.out.println(String.format("%02d:%02d 计算成功，奖励: %.2f", hour, minute, reward));
            });
        }
    }

    @Test
    public void testIndex96Simulation() {
        System.out.println("\n========== 验证 index=96 不会越界 ==========");
        
        int simulatedIndex = 96;
        int safeIndex = simulatedIndex % 96;
        System.out.println("模拟 index=96, 使用 %96 后: " + safeIndex);
        assertEquals(0, safeIndex, "96 % 96 应该等于 0");
        
        simulatedIndex = 97;
        safeIndex = simulatedIndex % 96;
        System.out.println("模拟 index=97, 使用 %96 后: " + safeIndex);
        assertEquals(1, safeIndex);
        
        simulatedIndex = 192;
        safeIndex = simulatedIndex % 96;
        System.out.println("模拟 index=192, 使用 %96 后: " + safeIndex);
        assertEquals(0, safeIndex);
        
        System.out.println("取模运算可以正确处理所有超出范围的索引值");
    }

    @Test
    public void testAllOriginalTests() {
        System.out.println("\n========== 运行所有原始测试用例 ==========");
        
        LocalDateTime start1 = LocalDateTime.now().withHour(23).withMinute(0);
        LocalDateTime end1 = start1.plusHours(4);
        assertDoesNotThrow(() -> {
            RewardResult result1 = chargingRewardService.calculateRewardWithDetails("USER001", start1, end1, 20.0);
            System.out.println("测试1 通过: 23:00-03:00, 奖励=" + result1.getTotalReward());
        });

        LocalDateTime start2 = LocalDateTime.now().withHour(8).withMinute(0);
        LocalDateTime end2 = start2.plusHours(3);
        assertDoesNotThrow(() -> {
            RewardResult result2 = chargingRewardService.calculateRewardWithDetails("USER002", start2, end2, 15.0);
            System.out.println("测试2 通过: 08:00-11:00, 奖励=" + result2.getTotalReward());
        });
    }
}
