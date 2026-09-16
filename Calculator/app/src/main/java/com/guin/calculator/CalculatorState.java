package com.guin.calculator;

import java.math.BigDecimal;
import java.math.MathContext;

/** State input dipisahkan dari Activity supaya mudah dipahami dan diuji. */
public final class CalculatorState {
    public String expression = "";
    public String answer = "0";
    public String memory = "0";
    public boolean degrees = true;
    public boolean justEvaluated = false;
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    public BigDecimal value() { return evaluator.evaluate(expression, degrees); }
    public String preview() { return ExpressionEvaluator.format(value()); }

    public String calculate() {
        if (expression.isEmpty()) expression = "0";
        answer = preview();
        expression = answer;
        justEvaluated = true;
        return answer;
    }

    public void clear() { expression = ""; justEvaluated = false; }

    public void input(String key) {
        if (key.equals("AC")) { clear(); return; }
        if (key.equals("DEL")) {
            if (justEvaluated) { clear(); return; }
            if (expression.endsWith("(")) {
                int start = expression.length() - 1;
                while (start > 0 && Character.isLetter(expression.charAt(start - 1))) start--;
                expression = expression.substring(0, start);
            } else if (!expression.isEmpty()) expression = expression.substring(0, expression.length() - 1);
            return;
        }
        if (key.equals("SIGN")) { toggleSign(); justEvaluated = false; return; }
        if (key.equals("SQUARE")) { wrapLast("(", ")^2"); justEvaluated = false; return; }
        if (key.equals("INV")) { wrapLast("(1/(", "))"); justEvaluated = false; return; }
        if (key.equals("Ans")) { insertValue(answer); return; }
        if (key.equals("MC")) { memory = "0"; return; }
        if (key.equals("MR")) { insertValue(memory); return; }
        if (key.equals("M+") || key.equals("M-")) {
            BigDecimal old = new BigDecimal(memory);
            BigDecimal next = key.equals("M+") ? old.add(value(), MathContext.DECIMAL64)
                    : old.subtract(value(), MathContext.DECIMAL64);
            String formatted = ExpressionEvaluator.format(next);
            evaluator.evaluate(formatted, degrees);
            memory = formatted;
            return;
        }
        boolean operator = "+-*/^".contains(key) && key.length() == 1;
        boolean postfix = key.equals("%") || key.equals("!");
        if (justEvaluated && !operator && !postfix && !key.equals(")")) expression = "";
        justEvaluated = false;
        if (expression.length() >= 240) throw new IllegalArgumentException("Maksimal 240 karakter input");
        if (operator) {
            if (expression.isEmpty()) { if (key.equals("-")) expression = "-"; return; }
            char last = last();
            if (last == '(') { if (key.equals("-")) expression += key; return; }
            if (isOperator(last)) {
                if (key.equals("-") && last != '-') expression += key;
                else {
                    while (!expression.isEmpty() && isOperator(last())) expression = expression.substring(0, expression.length() - 1);
                    if (!expression.isEmpty() && last() != '(') expression += key;
                    else if (key.equals("-")) expression += key;
                }
            } else expression += key;
            return;
        }
        if (key.equals(")")) {
            int open = 0, close = 0;
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') open++;
                if (expression.charAt(i) == ')') close++;
            }
            if (open > close && canEnd()) expression += ")";
            return;
        }
        if (postfix) { if (canEnd() && last() != '%' && last() != '!') expression += key; return; }
        if (key.equals(".")) {
            int index = expression.length() - 1;
            while (index >= 0 && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
                if (expression.charAt(index) == '.') return;
                index--;
            }
            if (endsSymbol()) expression += "*";
            if (expression.isEmpty() || !Character.isDigit(last())) expression += "0";
            expression += ".";
            return;
        }
        if (key.matches("[0-9]")) {
            if (endsSymbol()) expression += "*";
            // Hindari deretan nol di awal angka.
            if (expression.endsWith("0") && (expression.length() == 1 || isOperator(expression.charAt(expression.length() - 2))
                    || expression.charAt(expression.length() - 2) == '(')) expression = expression.substring(0, expression.length() - 1);
        } else if (canEnd()) expression += "*";
        expression += key;
    }

    private void insertValue(String value) {
        if (justEvaluated) expression = "";
        justEvaluated = false;
        if (canEnd()) expression += "*";
        expression += value.startsWith("-") ? "(" + value + ")" : value;
    }

    private void toggleSign() {
        if (!canEnd()) { expression += "-"; return; }
        int start = lastOperandStart();
        String operand = expression.substring(start);
        if (operand.startsWith("(-") && operand.endsWith(")")) operand = operand.substring(2, operand.length() - 1);
        else operand = "(-" + operand + ")";
        expression = expression.substring(0, start) + operand;
    }

    private void wrapLast(String before, String after) {
        if (!canEnd()) throw new IllegalArgumentException("Masukkan angka terlebih dahulu");
        int start = lastOperandStart();
        expression = expression.substring(0, start) + before + expression.substring(start) + after;
    }

    private int lastOperandStart() {
        int end = expression.length() - 1;
        while (end > 0 && (expression.charAt(end) == '%' || expression.charAt(end) == '!')) end--;
        if (expression.charAt(end) == ')') {
            int depth = 1;
            int i = end - 1;
            while (i >= 0 && depth > 0) {
                if (expression.charAt(i) == ')') depth++;
                else if (expression.charAt(i) == '(') depth--;
                i--;
            }
            int start = i + 1;
            while (start > 0 && Character.isLetter(expression.charAt(start - 1))) start--;
            return includeUnarySign(start);
        }
        int start = end;
        while (start > 0) {
            char c = expression.charAt(start - 1);
            if (Character.isLetterOrDigit(c) || c == '.' || c == 'π') start--;
            else if ((c == '-' || c == '+') && start > 1 && expression.charAt(start - 2) == 'E') start--;
            else break;
        }
        return includeUnarySign(start);
    }

    private int includeUnarySign(int start) {
        if (start > 0 && expression.charAt(start - 1) == '-'
                && (start == 1 || isOperator(expression.charAt(start - 2)) || expression.charAt(start - 2) == '('))
            return start - 1;
        return start;
    }

    private boolean endsSymbol() { return !expression.isEmpty() && ")%!πe".indexOf(last()) >= 0; }
    private boolean canEnd() { return !expression.isEmpty() && (Character.isDigit(last()) || endsSymbol() || last() == '.'); }
    private char last() { return expression.charAt(expression.length() - 1); }
    private boolean isOperator(char c) { return "+-*/^".indexOf(c) >= 0; }
}
