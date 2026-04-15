package com.example.demo;

import com.example.demo.entity.ChargingPoint;
import com.example.demo.entity.RewardResult;
import com.example.demo.service.ChargingRewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChargingRewardTest {

    @Autowired
    private ChargingRewardService chargingRewardService;

    /**
     * 测试 getPointIndex 方法的边界条件
     * 验证分钟数为15的倍数时是否正确计算时段索引
     */
    @Test
    public void testGetPointIndexBoundary() {
        List<ChargingPoint> points = chargingRewardService.getChargingPoints();

        // 测试10:00 - 应该属于第40点 (10:00-10:15)
        LocalDateTime time1 = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0);
        ChargingPoint point1 = points.get(getPointIndex(time1));
        assertEquals(10, point1.getHour());
        assertEquals(0, point1.getMinute());
        System.out.println("10:00 -> 第" + getPointIndex(time1) + "点 (" + point1.getHour() + ":" + point1.getMinute() + ")");

        // 测试10:15 - 应该属于第41点 (10:15-10:30)
        LocalDateTime time2 = LocalDateTime.now().withHour(10).withMinute(15).withSecond(0);
        ChargingPoint point2 = points.get(getPointIndex(time2));
        assertEquals(10, point2.getHour());
        assertEquals(15, point2.getMinute());
        System.out.println("10:15 -> 第" + getPointIndex(time2) + "点 (" + point2.getHour() + ":" + point2.getMinute() + ")");

        // 测试10:30 - 应该属于第42点 (10:30-10:45)
        LocalDateTime time3 = LocalDateTime.now().withHour(10).withMinute(30).withSecond(0);
        ChargingPoint point3 = points.get(getPointIndex(time3));
        assertEquals(10, point3.getHour());
        assertEquals(30, point3.getMinute());
        System.out.println("10:30 -> 第" + getPointIndex(time3) + "点 (" + point3.getHour() + ":" + point3.getMinute() + ")");

        // 测试10:45 - 应该属于第43点 (10:45-11:00)
        LocalDateTime time4 = LocalDateTime.now().withHour(10).withMinute(45).withSecond(0);
        ChargingPoint point4 = points.get(getPointIndex(time4));
        assertEquals(10, point4.getHour());
        assertEquals(45, point4.getMinute());
        System.out.println("10:45 -> 第" + getPointIndex(time4) + "点 (" + point4.getHour() + ":" + point4.getMinute() + ")");

        // 测试00:00 - 应该属于第0点 (00:00-00:15)
        LocalDateTime time5 = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        ChargingPoint point5 = points.get(getPointIndex(time5));
        assertEquals(0, point5.getHour());
        assertEquals(0, point5.getMinute());
        System.out.println("00:00 -> 第" + getPointIndex(time5) + "点 (" + point5.getHour() + ":" + point5.getMinute() + ")");

        // 测试23:45 - 应该属于第95点 (23:45-00:00)
        LocalDateTime time6 = LocalDateTime.now().withHour(23).withMinute(45).withSecond(0);
        ChargingPoint point6 = points.get(getPointIndex(time6));
        assertEquals(23, point6.getHour());
        assertEquals(45, point6.getMinute());
        System.out.println("23:45 -> 第" + getPointIndex(time6) + "点 (" + point6.getHour() + ":" + point6.getMinute() + ")");

        System.out.println("边界测试通过!");
    }

    private int getPointIndex(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int minutesPerPoint = 15;
        int pointsPerDay = 96;

        int pointIndex = (hour * 60 + minute) / minutesPerPoint;
        pointIndex = pointIndex % pointsPerDay;
        if (pointIndex < 0) {
            pointIndex = pointIndex + pointsPerDay;
        }
        return pointIndex;
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
