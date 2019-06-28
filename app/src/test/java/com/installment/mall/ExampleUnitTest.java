package com.installment.mall;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 *
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        double rate = 0.18885d;

        double repay = 1000 / (2 + 1) + 1000 * rate;

        BigDecimal rate1 = new BigDecimal("0.18885");
        BigDecimal amount = new BigDecimal("1000");
        BigDecimal total = amount.divide(new BigDecimal("1"),5,BigDecimal.ROUND_HALF_UP);
        total = total.add(amount.multiply(rate1));
        System.err.println("repay:"+total.doubleValue());

        BigDecimal total2 = amount.divide(new BigDecimal("2"),5,BigDecimal.ROUND_HALF_UP);
        total2 = total2.add(amount.multiply(rate1));
        System.err.println("repay 2:"+total2.doubleValue());
        BigDecimal total3 = amount.divide(new BigDecimal("3"),5,BigDecimal.ROUND_HALF_UP);
        total3 = total3.add(amount.multiply(rate1));
        System.err.println("repay3:"+total3.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
        BigDecimal total4 = amount.divide(new BigDecimal("4"),5,BigDecimal.ROUND_HALF_UP);
        total4 = total4.add(amount.multiply(rate1));
        System.err.println("repay4 :"+total4.doubleValue());
    }


}