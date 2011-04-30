package com.bytegames.prevent;

/**
 * A variety of functions to perform mathematical calculations.
 * 
 * @author byte
 */
public class MathHelper {

    /**
     * Translates degrees to radians.
     * 
     * @param angle An angle.
     * @return The radians for the given angle.
     */
    public static double degreeToRadian(double angle) {
        return Math.PI * (double)angle / 180;
    }

    /**
     * Translates rise-over-run into degrees.
     * 
     * @param slope A slope.
     * @return The angle degree of the slope.
     */
    public static double slopeToDegree(double slope) {
        return (180 * Math.atan(slope)) / Math.PI;
    }

    /**
     * Clamps (wraps) an angle to a value between 0 and 360 degrees.
     * 
     * @param angle The angle to clamp.
     * @return The clamped angle.
     */
    public static double clampAngle(double angle) {

        double result = angle % 360;
        if (result < 0)
            result = result + 360;

        return result;

    }
}
