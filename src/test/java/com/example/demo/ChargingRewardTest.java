package com.example.demo;

import com.example.demo.entity.ChargingRecord;
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
    public void testUserIdWithTestString() {
        System.out.println();
        System.out.println("========== 测试用户ID包含_test_场景 ==========");
        System.out.println();

        LocalDateTime start = LocalDateTime.now().withHour(23).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(4);

        System.out.println("【测试A】普通用户ID USER_normal");
        ChargingRecord record1 = chargingRewardService.createChargingRecord("USER_normal", start, end, 20.0);
        System.out.println("用户ID: " + record1.getUserId());
        System.out.println("充电电量: " + record1.getEnergyKwh() + " kWh");
        System.out.println("奖励金额: " + record1.getRewardAmount() + " 元");
        System.out.println();

        System.out.println("【测试B】测试用户ID USER_test_001 (包含_test_)");
        ChargingRecord record2 = chargingRewardService.createChargingRecord("USER_test_001", start, end, 20.0);
        System.out.println("用户ID: " + record2.getUserId());
        System.out.println("充电电量: " + record2.getEnergyKwh() + " kWh");
        System.out.println("奖励金额: " + record2.getRewardAmount() + " 元");
        System.out.println();

        System.out.println("【测试C】测试用户ID test_user_123 (包含_test_)");
        ChargingRecord record3 = chargingRewardService.createChargingRecord("test_user_123", start, end, 20.0);
        System.out.println("用户ID: " + record3.getUserId());
        System.out.println("充电电量: " + record3.getEnergyKwh() + " kWh");
        System.out.println("奖励金额: " + record3.getRewardAmount() + " 元");
        System.out.println();

        System.out.println("验证结果: 普通用户与测试用户奖励金额应该一致");
        System.out.println("USER_normal奖励: " + record1.getRewardAmount());
        System.out.println("USER_test_001奖励: " + record2.getRewardAmount());
        System.out.println("test_user_123奖励: " + record3.getRewardAmount());

        assert record1.getRewardAmount() > 0 : "普通用户奖励应该大于0";
        assert record2.getRewardAmount() > 0 : "测试用户奖励应该大于0，不应该被强制置零";
        assert record3.getRewardAmount() > 0 : "测试用户奖励应该大于0，不应该被强制置零";
        assert record1.getRewardAmount() == record2.getRewardAmount() : "相同条件下普通用户与测试用户奖励应该一致";
        assert record1.getRewardAmount() == record3.getRewardAmount() : "相同条件下普通用户与测试用户奖励应该一致";

        System.out.println();
        System.out.println("========== 测试用户ID验证通过! ==========");
    }
}
