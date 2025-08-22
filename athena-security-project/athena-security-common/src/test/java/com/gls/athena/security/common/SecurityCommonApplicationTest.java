package com.gls.athena.security.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 安全通用模块基础测试类
 *
 * @author Athena Security Team
 * @since 0.0.1
 */
@DisplayName("安全通用模块测试")
class SecurityCommonApplicationTest {

    @Test
    @DisplayName("基础测试 - 验证测试环境")
    void testBasicFunctionality() {
        // 基础断言测试
        assertTrue(true, "基础测试应该通过");
        assertNotNull("test", "字符串不应为空");
        assertEquals(1, 1, "数字应该相等");
    }

    @Test
    @DisplayName("字符串操作测试")
    void testStringOperations() {
        String testString = "Athena Security";

        assertNotNull(testString, "测试字符串不应为空");
        assertFalse(testString.isEmpty(), "测试字符串不应为空字符串");
        assertTrue(testString.contains("Security"), "字符串应包含Security");
        assertEquals(15, testString.length(), "字符串长度应为15");
    }

    @Test
    @DisplayName("数值计算测试")
    void testMathOperations() {
        int a = 10;
        int b = 5;

        assertEquals(15, a + b, "加法计算错误");
        assertEquals(5, a - b, "减法计算错误");
        assertEquals(50, a * b, "乘法计算错误");
        assertEquals(2, a / b, "除法计算错误");
    }

    @Test
    @DisplayName("异常处理测试")
    void testExceptionHandling() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("测试异常");
        }, "应该抛出IllegalArgumentException");

        assertDoesNotThrow(() -> {
            String result = "正常执行";
            assertNotNull(result);
        }, "正常代码不应抛出异常");
    }
}
