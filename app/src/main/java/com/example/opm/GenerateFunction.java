package com.example.opm;

import android.annotation.SuppressLint;

import java.util.Random;

public class GenerateFunction {
    public static String generateFunction(Random random) {
        int a, b, c, d;
        int functionType = random.nextInt(4);

        switch (functionType) {
            case 0:
                a = random.nextInt(6) + 1;
                b = random.nextInt(11) - 5;
                return formatLinearFunction(a, b);
            case 1:
                a = random.nextInt(4) - 1;
                b = random.nextInt(6) - 2;
                c = random.nextInt(11) - 5;
                return formatQuadraticFunction(a, b, c);
            case 2:
                a = random.nextInt(3) - 1;
                b = random.nextInt(6) - 2;
                c = random.nextInt(6) - 2;
                d = random.nextInt(11) - 5;
                return formatCubicFunction(a, b, c, d);
            case 3:
                a = random.nextInt(6);
                b = (int) (random.nextDouble() * 2 * Math.PI);
                c = random.nextInt(11) - 5;
                return formatSineFunction(a, b, c);
            default:
                return "";
        }
    }

    @SuppressLint("DefaultLocale")
    private static String formatLinearFunction(int a, int b) {
        if (b == 0) {
            return String.format("%dx", a);
        } else if (b > 0) {
            return String.format("%dx + %d", a, b);
        } else {
            return String.format("%dx - %d", a, -b);
        }
    }

    private static String formatQuadraticFunction(int a, int b, int c) {
        StringBuilder sb = new StringBuilder();
        if (a != 0) {
            sb.append(a).append("x^2");
        }
        if (b != 0) {
            sb.append(b > 0 ? " + " : " - ").append(Math.abs(b)).append("x");
        }
        if (c != 0) {
            sb.append(c > 0 ? " + " : " - ").append(Math.abs(c));
        }
        return sb.toString();
    }

    private static String formatCubicFunction(int a, int b, int c, int d) {
        StringBuilder sb = new StringBuilder();
        if (a != 0) {
            sb.append(a).append("x^3");
        }
        if (b != 0) {
            sb.append(b > 0 ? " + " : " - ").append(Math.abs(b)).append("x^2");
        }
        if (c != 0) {
            sb.append(c > 0 ? " + " : " - ").append(Math.abs(c)).append("x");
        }
        if (d != 0) {
            sb.append(d > 0 ? " + " : " - ").append(Math.abs(d));
        }
        return sb.toString();
    }

    @SuppressLint("DefaultLocale")
    private static String formatSineFunction(int a, double b, int c) {
        if (c == 0) {
            return String.format("%d * sin(%.2fx)", a, b);
        } else if (c > 0) {
            return String.format("%d * sin(%.2fx) + %d", a, b, c);
        } else {
            return String.format("%d * sin(%.2fx) - %d", a, b, -c);
        }
    }
}
