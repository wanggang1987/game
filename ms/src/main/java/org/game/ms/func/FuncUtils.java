/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.func;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author wanggang
 */
public class FuncUtils {

    private static final Random randomSeed = new Random(currentTime().getTime());

    public static double randomPersentage() {
        return randomSeed.nextDouble();
    }

    public static int randomZeroToRange(int range) {
        return randomSeed.nextInt(range);
    }

    public static double randomInRange(double base, double range) {
        return randomSeed.nextBoolean() == true ? base + randomSeed.nextDouble() * range : base - randomSeed.nextDouble() * range;
    }

    public static double randomInRangeByPersentage(double base, int persentRange) {
        double range = base * persentRange / 100;
        return randomInRange(base, range);
    }

    public static int randomInRangeByPersentage(int base, int persentRange) {
        double range = base * persentRange / 100;
        Double ret = randomInRange(base, range);
        return ret.intValue();
    }

    private static final double precision = 0.001;

    public static int numberCompare(double a, double b) {
        if (a - b > precision) {
            return 1;
        } else if (b - a > precision) {
            return -1;
        }
        return 0;
    }

    public static Timestamp currentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static boolean notEquals(Object a, Object b) {
        return !equals(a, b);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof Collection)) {
            return ((Collection) obj).isEmpty();
        }
        if ((obj instanceof Map)) {
            return ((Map) obj).isEmpty();
        }
        if ((obj instanceof String)) {
            return ((String) obj).trim().equals("");
        }
        return false;
    }

    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static void copyProperties(Object from, Object to) {
        BeanUtils.copyProperties(from, to);
    }

}
