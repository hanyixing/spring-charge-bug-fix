package com.example.demo;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class DebugPointIndexTest {

    private static final int MINUTES_PER_POINT = 15;

    private int getPointIndex(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        
        System.out.println(String.format("时间: %02d:%02d, hour=%d, minute=%d", hour, minute, hour, minute));
        
        if (minute % MINUTES_PER_POINT == 0 && minute > 0) {
            int result = (hour * 60 + minute) / MINUTES_PER_POINT;
            System.out.println("  if分支: " + result);
            return result;
        }
        
        int result = (hour * 60 + minute) / MINUTES_PER_POINT;
        System.out.println("  else分支: " + result);
        return result;
    }

    @Test
    public void testDebugPointIndex() {
        System.out.println("========== 调试 getPointIndex ==========");
        
        // 测试一系列边界时间
        LocalDateTime[] times = {
            LocalDateTime.now().withHour(23).withMinute(0),
            LocalDateTime.now().withHour(23).withMinute(15),
            LocalDateTime.now().withHour(23).withMinute(30),
            LocalDateTime.now().withHour(23).withMinute(45),
            LocalDateTime.now().withHour(23).withMinute(59),
            LocalDateTime.now().plusDays(1).withHour(0).withMinute(0),
            LocalDateTime.now().plusDays(1).withHour(0).withMinute(1),
        };
        
        for (LocalDateTime time : times) {
            int index = getPointIndex(time);
            if (index >= 96) {
                System.out.println("  !!! 数组越界风险: index=" + index);
            }
        }
        
        // 测试 hour=24 的情况（虽然不可能，但我们手动算）
        System.out.println("\n模拟 hour=24 的情况:");
        int hour = 24;
        int minute = 0;
        int result = (hour * 60 + minute) / MINUTES_PER_POINT;
        System.out.println(String.format("hour=24, minute=0: result=%d", result));
        
        System.out.println("========== 完成 ==========");
    }
}
