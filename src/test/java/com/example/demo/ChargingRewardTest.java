package com.example.demo;

import com.example.demo.entity.RewardResult;
import com.example.demo.service.ChargingRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ChargingRewardTest {

    @Autowired
    private ChargingRewardService chargingRewardService;

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

    @Test
    public void testPointIndexBoundary() throws Exception {
        System.out.println("========== 时段索引边界测试 ==========");
        System.out.println();

        java.lang.reflect.Method method = ChargingRewardService.class.getDeclaredMethod(
            "getPointIndex", LocalDateTime.class);
        method.setAccessible(true);

        LocalDateTime time1 = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0);
        int index1 = (int) method.invoke(chargingRewardService, time1);
        System.out.printf("10:00 时段索引: %d (预期: 39, 对应 09:45-10:00)%n", index1);
        assert index1 == 39 : "10:00 应该属于第39点(09:45-10:00)，实际是: " + index1;

        LocalDateTime time2 = LocalDateTime.now().withHour(10).withMinute(15).withSecond(0);
        int index2 = (int) method.invoke(chargingRewardService, time2);
        System.out.printf("10:15 时段索引: %d (预期: 40, 对应 10:00-10:15)%n", index2);
        assert index2 == 40 : "10:15 应该属于第40点(10:00-10:15)，实际是: " + index2;

        LocalDateTime time3 = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int index3 = (int) method.invoke(chargingRewardService, time3);
        System.out.printf("00:00 时段索引: %d (预期: 95, 对应 23:45-00:00)%n", index3);
        assert index3 == 95 : "00:00 应该属于第95点(23:45-00:00)，实际是: " + index3;

        LocalDateTime time4 = LocalDateTime.now().withHour(10).withMinute(14).withSecond(59);
        int index4 = (int) method.invoke(chargingRewardService, time4);
        System.out.printf("10:14:59 时段索引: %d (预期: 40, 对应 10:00-10:15)%n", index4);
        assert index4 == 40 : "10:14:59 应该属于第40点(10:00-10:15)，实际是: " + index4;

        LocalDateTime time5 = LocalDateTime.now().withHour(10).withMinute(16).withSecond(0);
        int index5 = (int) method.invoke(chargingRewardService, time5);
        System.out.printf("10:16 时段索引: %d (预期: 41, 对应 10:15-10:30)%n", index5);
        assert index5 == 41 : "10:16 应该属于第41点(10:15-10:30)，实际是: " + index5;

        System.out.println();
        System.out.println("========== 边界测试通过! ==========");
    }
}
