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
    public void testUserWithTestSuffix() {
        System.out.println("========== 测试用户ID包含_test_的奖励计算 ==========");
        System.out.println();

        System.out.println("【测试】包含_test_的用户ID奖励计算 (00:00-04:00 谷时)");
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(4);

        System.out.println("普通用户 USER_normal:");
        ChargingRecord recordNormal = chargingRewardService.createChargingRecord("USER_normal", start, end, 20.0);
        System.out.println("  用户ID: " + recordNormal.getUserId());
        System.out.println("  充电电量: " + recordNormal.getEnergyKwh() + " kWh");
        System.out.println("  奖励金额: " + recordNormal.getRewardAmount() + " 元");

        System.out.println("测试用户 USER_test_001:");
        ChargingRecord recordTest = chargingRewardService.createChargingRecord("USER_test_001", start, end, 20.0);
        System.out.println("  用户ID: " + recordTest.getUserId());
        System.out.println("  充电电量: " + recordTest.getEnergyKwh() + " kWh");
        System.out.println("  奖励金额: " + recordTest.getRewardAmount() + " 元");

        System.out.println("测试用户 test_user_123:");
        ChargingRecord recordTest2 = chargingRewardService.createChargingRecord("test_user_123", start, end, 20.0);
        System.out.println("  用户ID: " + recordTest2.getUserId());
        System.out.println("  充电电量: " + recordTest2.getEnergyKwh() + " kWh");
        System.out.println("  奖励金额: " + recordTest2.getRewardAmount() + " 元");

        System.out.println();
        System.out.println("验证结果: 所有用户在相同条件下应获得相同奖励");
        System.out.println("普通用户奖励: " + recordNormal.getRewardAmount() + " 元");
        System.out.println("测试用户1奖励: " + recordTest.getRewardAmount() + " 元");
        System.out.println("测试用户2奖励: " + recordTest2.getRewardAmount() + " 元");

        assert recordNormal.getRewardAmount() > 0 : "普通用户奖励应大于0";
        assert recordTest.getRewardAmount() > 0 : "测试用户奖励应大于0 (Bug修复验证)";
        assert recordTest2.getRewardAmount() > 0 : "测试用户奖励应大于0 (Bug修复验证)";
        assert recordNormal.getRewardAmount() == recordTest.getRewardAmount() : "普通用户和测试用户奖励应相等";
        assert recordNormal.getRewardAmount() == recordTest2.getRewardAmount() : "普通用户和测试用户奖励应相等";

        System.out.println();
        System.out.println("========== _test_ 用户ID测试通过! ==========");
    }

    @Test
    public void testNullUserId() {
        System.out.println("========== 测试 userId == null 边界情况 ==========");
        System.out.println();

        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(4);

        System.out.println("有效用户ID:");
        ChargingRecord recordWithUser = chargingRewardService.createChargingRecord("VALID_USER", start, end, 20.0);
        System.out.println("  用户ID: " + recordWithUser.getUserId());
        System.out.println("  奖励金额: " + recordWithUser.getRewardAmount() + " 元");

        System.out.println("null 用户ID:");
        ChargingRecord recordNullUser = chargingRewardService.createChargingRecord(null, start, end, 20.0);
        System.out.println("  用户ID: " + recordNullUser.getUserId());
        System.out.println("  奖励金额: " + recordNullUser.getRewardAmount() + " 元");

        System.out.println();
        System.out.println("验证结果:");
        System.out.println("有效用户奖励: " + recordWithUser.getRewardAmount() + " 元");
        System.out.println("null用户奖励: " + recordNullUser.getRewardAmount() + " 元");

        assert recordWithUser.getRewardAmount() > 0 : "有效用户应该获得奖励";
        assert recordNullUser.getRewardAmount() == 0.0 : "null用户ID奖励应为0 (边界情况验证)";

        System.out.println();
        System.out.println("========== null 用户ID测试通过! ==========");
    }
}
