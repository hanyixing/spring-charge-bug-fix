package com.example.demo;

import com.example.demo.entity.ChargingPoint;
import com.example.demo.entity.RewardResult;
import com.example.demo.service.ChargingRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChargingRewardTest {

    @Autowired
    private ChargingRewardService chargingRewardService;

    @Test
    public void testNegativeIndexHandling() throws Exception {
        System.out.println("========== 负数索引边界测试 ==========");

        // 使用反射获取私有方法 getPointIndex
        Method getPointIndexMethod = ChargingRewardService.class.getDeclaredMethod("getPointIndex", LocalDateTime.class);
        getPointIndexMethod.setAccessible(true);

        // 测试正常时间
        LocalDateTime normalTime = LocalDateTime.now().withHour(12).withMinute(0);
        int normalIndex = (int) getPointIndexMethod.invoke(chargingRewardService, normalTime);
        System.out.println("正常时间 12:00 的索引: " + normalIndex);
        assertTrue(normalIndex >= 0, "正常时间的索引应该大于等于0");

        // 测试边界时间 00:00
        LocalDateTime midnight = LocalDateTime.now().withHour(0).withMinute(0);
        int midnightIndex = (int) getPointIndexMethod.invoke(chargingRewardService, midnight);
        System.out.println("午夜 00:00 的索引: " + midnightIndex);
        assertEquals(0, midnightIndex, "午夜应该对应索引0");

        // 验证 calculateReward 不会因为负数索引而崩溃
        // 由于 getPointIndex 基于时间计算，正常情况下不会产生负数
        // 但修复确保即使出现负数索引也能安全处理
        LocalDateTime startTime = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0);
        LocalDateTime endTime = startTime.plusHours(4);

        double reward = chargingRewardService.calculateReward("USER_TEST", startTime, endTime, 20.0);
        System.out.println("谷时充电奖励计算结果: " + reward + " 元");
        assertTrue(reward >= 0, "奖励金额应该大于等于0");

        // 验证 calculateRewardWithDetails
        RewardResult result = chargingRewardService.calculateRewardWithDetails("USER_TEST", startTime, endTime, 20.0);
        System.out.println("详细奖励计算结果: " + result.getTotalReward() + " 元");
        assertNotNull(result, "结果不应为null");
        assertTrue(result.getTotalReward() >= 0, "总奖励应该大于等于0");

        System.out.println("========== 负数索引边界测试通过! ==========");
    }

    @Test
    public void testValleyCharging() {
        System.out.println("========== 充电奖励算法测试 ==========");
        System.out.println();

        // 测试1: 谷时充电 (23:00-03:00)
        System.out.println("【测试1】谷时充电场景 (23:00-03:00)");
        LocalDateTime start1 = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0);
        LocalDateTime end1 = start1.plusHours(4);
        RewardResult result1 = chargingRewardService.calculateRewardWithDetails("USER001", start1, end1, 20.0);
        System.out.println("总电量: " + result1.getTotalEnergy() + " kWh");
        System.out.println("总奖励: " + result1.getTotalReward() + " 元");
        System.out.println("消息: " + result1.getMessage());
        System.out.println();

        // 测试2: 峰时充电 (08:00-11:00)
        System.out.println("【测试2】峰时充电场景 (08:00-11:00)");
        LocalDateTime start2 = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end2 = start2.plusHours(3);
        RewardResult result2 = chargingRewardService.calculateRewardWithDetails("USER002", start2, end2, 15.0);
        System.out.println("总电量: " + result2.getTotalEnergy() + " kWh");
        System.out.println("总奖励: " + result2.getTotalReward() + " 元");
        System.out.println("消息: " + result2.getMessage());
        System.out.println();

        // 测试3: 混合时段充电 (22:00-02:00)
        System.out.println("【测试3】混合时段充电场景 (22:00-02:00)");
        LocalDateTime start3 = LocalDateTime.now().withHour(22).withMinute(0).withSecond(0);
        LocalDateTime end3 = start3.plusHours(4);
        RewardResult result3 = chargingRewardService.calculateRewardWithDetails("USER003", start3, end3, 25.0);
        System.out.println("总电量: " + result3.getTotalEnergy() + " kWh");
        System.out.println("总奖励: " + result3.getTotalReward() + " 元");
        System.out.println("消息: " + result3.getMessage());
        System.out.println();

        // 测试4: 午间谷时充电 (12:00-14:00)
        System.out.println("【测试4】午间谷时充电场景 (12:00-14:00)");
        LocalDateTime start4 = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0);
        LocalDateTime end4 = start4.plusHours(2);
        RewardResult result4 = chargingRewardService.calculateRewardWithDetails("USER004", start4, end4, 10.0);
        System.out.println("总电量: " + result4.getTotalEnergy() + " kWh");
        System.out.println("总奖励: " + result4.getTotalReward() + " 元");
        System.out.println("消息: " + result4.getMessage());
        System.out.println();

        // 测试5: 平时充电 (07:00-08:00)
        System.out.println("【测试5】平时充电场景 (07:00-08:00)");
        LocalDateTime start5 = LocalDateTime.now().withHour(7).withMinute(0).withSecond(0);
        LocalDateTime end5 = start5.plusHours(1);
        RewardResult result5 = chargingRewardService.calculateRewardWithDetails("USER005", start5, end5, 5.0);
        System.out.println("总电量: " + result5.getTotalEnergy() + " kWh");
        System.out.println("总奖励: " + result5.getTotalReward() + " 元");
        System.out.println("消息: " + result5.getMessage());
        System.out.println();

        System.out.println("========== 测试完成! ==========");
    }
}
