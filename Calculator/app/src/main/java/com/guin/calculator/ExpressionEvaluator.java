package com.guin.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** Parser matematika tanpa library eksternal. Urutan: kurung, pangkat, kali/bagi, tambah/kurang. */
public final class ExpressionEvaluator {
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private String input;
    private int position;
    private boolean degrees;

    public BigDecimal evaluate(String expression, boolean degreeMode) {
        input = expression.replace("×", "*").replace("÷", "/")
                .replace("−", "-").replace("π", "pi").replace(" ", "");
        if (input.length() > 500) throw error("Ekspresi terlalu panjang");
        position = 0;
        degrees = degreeMode;
        if (input.isEmpty()) return BigDecimal.ZERO;
        BigDecimal value = expression();
        if (position != input.length()) throw error("Periksa susunan angka dan operator");
        return checked(value);
    }

    private BigDecimal expression() {
        BigDecimal left = term();
        while (position < input.length()) {
            if (eat('+')) {
                int start = position;
                BigDecimal right = term();
                left = left.add(isSinglePercent(start, position) ? left.multiply(right, MC) : right, MC);
            } else if (eat('-')) {
                int start = position;
                BigDecimal right = term();
                left = left.subtract(isSinglePercent(start, position) ? left.multiply(right, MC) : right, MC);
            } else break;
        }
        return checked(left);
    }

    // Perilaku kalkulator umum: 200 + 10% = 220; 200 × 10% = 20.
    private boolean isSinglePercent(int start, int end) {
        return input.substring(start, end).matches("[+-]?[0-9]+(?:\\.[0-9]*)?%");
    }

    private BigDecimal term() {
        BigDecimal value = unary();
        while (position < input.length()) {
            if (eat('*')) value = value.multiply(unary(), MC);
            else if (eat('/')) {
                BigDecimal divisor = unary();
                if (divisor.signum() == 0) throw error("Tidak bisa membagi dengan nol");
                value = value.divide(divisor, MC);
            } else if (peek() == '(' || Character.isLetter(peek())) {
                value = value.multiply(unary(), MC); // 2pi, 2(3+4)
            } else break;
            checked(value);
        }
        return value;
    }

    private BigDecimal unary() {
        if (eat('+')) return unary();
        if (eat('-')) return unary().negate(MC);
        return power();
    }

    private BigDecimal power() {
        BigDecimal base = postfix();
        if (!eat('^')) return base;
        BigDecimal exponent = unary(); // Pangkat bersifat right-associative: 2^3^2 = 512.
        if (base.signum() == 0 && exponent.signum() < 0) throw error("Tidak bisa membagi dengan nol");
        if (exponent.stripTrailingZeros().scale() <= 0 && exponent.abs().compareTo(new BigDecimal("999")) <= 0) {
            int n = exponent.intValue();
            if (n >= 0) return checked(base.pow(n, MC));
            return checked(BigDecimal.ONE.divide(base.pow(-n, MC), MC));
        }
        return finite(Math.pow(base.doubleValue(), exponent.doubleValue()));
    }

    private BigDecimal postfix() {
        BigDecimal value = atom();
        while (true) {
            if (eat('%')) value = value.divide(new BigDecimal("100"), MC);
            else if (eat('!')) {
                if (value.signum() < 0 || value.stripTrailingZeros().scale() > 0
                        || value.compareTo(new BigDecimal("170")) > 0)
                    throw error("Faktorial hanya untuk bilangan bulat 0–170");
                int n = value.intValue();
                value = BigDecimal.ONE;
                for (int i = 2; i <= n; i++) value = value.multiply(BigDecimal.valueOf(i), MC);
            } else break;
        }
        return checked(value);
    }

    private BigDecimal atom() {
        if (eat('(')) {
            BigDecimal value = expression();
            if (!eat(')')) throw error("Kurung tutup belum lengkap");
            return value;
        }
        if (Character.isLetter(peek())) {
            int start = position;
            while (Character.isLetter(peek())) position++;
            String name = input.substring(start, position);
            if (name.equals("pi")) return BigDecimal.valueOf(Math.PI);
            if (name.equals("e")) return BigDecimal.valueOf(Math.E);
            if (!eat('(')) throw error("Fungsi membutuhkan tanda kurung");
            BigDecimal argument = expression();
            if (!eat(')')) throw error("Kurung tutup belum lengkap");
            return function(name, argument);
        }
        int start = position;
        boolean dot = false;
        while (position < input.length()) {
            char c = peek();
            if (Character.isDigit(c)) position++;
            else if (c == '.' && !dot) { dot = true; position++; }
            else break;
        }
        // Notasi E hanya untuk hasil besar/kecil, bukan konstanta e.
        if (position > start && peek() == 'E') {
            position++;
            if (peek() == '+' || peek() == '-') position++;
            int exponentStart = position;
            while (Character.isDigit(peek())) position++;
            if (exponentStart == position) throw error("Eksponen belum lengkap");
        }
        if (start == position) throw error("Ekspresi belum lengkap");
        try { return checked(new BigDecimal(input.substring(start, position), MC)); }
        catch (NumberFormatException ex) { throw error("Format angka tidak valid"); }
    }

    private BigDecimal function(String name, BigDecimal value) {
        double x = value.doubleValue();
        double angle = degrees ? Math.toRadians(x) : x;
        switch (name) {
            case "sin": return trig(Math.sin(angle));
            case "cos": return trig(Math.cos(angle));
            case "tan":
                if (Math.abs(Math.cos(angle)) < 1e-14) throw error("Tangen tidak terdefinisi pada sudut ini");
                return trig(Math.tan(angle));
            case "asin": return finite(degrees ? Math.toDegrees(Math.asin(x)) : Math.asin(x));
            case "acos": return finite(degrees ? Math.toDegrees(Math.acos(x)) : Math.acos(x));
            case "atan": return finite(degrees ? Math.toDegrees(Math.atan(x)) : Math.atan(x));
            case "log":
                if (x <= 0) throw error("Log hanya untuk angka positif");
                return finite(Math.log10(x));
            case "ln":
                if (x <= 0) throw error("Ln hanya untuk angka positif");
                return finite(Math.log(x));
            case "sqrt":
                if (x < 0) throw error("Akar negatif tidak memiliki hasil real");
                return finite(Math.sqrt(x));
            case "abs": return value.abs(MC);
            case "exp": return finite(Math.exp(x));
            default: throw error("Fungsi tidak dikenal");
        }
    }

    private BigDecimal trig(double value) {
        return finite(Math.abs(value) < 1e-15 ? 0 : value);
    }

    private BigDecimal finite(double value) {
        if (Double.isNaN(value)) throw error("Operasi tidak memiliki hasil real");
        if (Double.isInfinite(value)) throw error("Hasil terlalu besar");
        return checked(BigDecimal.valueOf(value));
    }

    private BigDecimal checked(BigDecimal value) {
        if (value.signum() != 0) {
            long exponent = (long) value.precision() - value.scale() - 1;
            if (exponent > 308 || exponent < -300) throw error("Hasil di luar rentang kalkulator (10⁻³⁰⁰–10³⁰⁸)");
        }
        return value;
    }

    private char peek() { return position < input.length() ? input.charAt(position) : '\0'; }
    private boolean eat(char c) {
        if (peek() != c) return false;
        position++;
        return true;
    }
    private IllegalArgumentException error(String message) { return new IllegalArgumentException(message); }

    public static String format(BigDecimal value) {
        BigDecimal result = value.round(new MathContext(14, RoundingMode.HALF_UP)).stripTrailingZeros();
        if (result.signum() == 0) return "0";
        int exponent = result.precision() - result.scale() - 1;
        return exponent > 14 || exponent < -8 ? result.toString() : result.toPlainString();
    }
}
