import com.guin.calculator.CalculatorState;
import com.guin.calculator.ExpressionEvaluator;
import java.math.BigDecimal;

/** Jalankan tanpa Android SDK; contoh perintah ada di README. */
public class CalculatorTests {
    static int passed;
    static final ExpressionEvaluator e = new ExpressionEvaluator();
    static void equal(String label, String actual, String expected) {
        if (!actual.equals(expected)) throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        passed++;
    }
    static void calc(String input, String expected) { equal(input, ExpressionEvaluator.format(e.evaluate(input, true)), expected); }
    static void bad(String input) {
        try { e.evaluate(input, true); throw new AssertionError("Should reject " + input); }
        catch (IllegalArgumentException | ArithmeticException expected) { passed++; }
    }
    static CalculatorState type(String... keys) {
        CalculatorState s = new CalculatorState();
        for (String k : keys) s.input(k);
        return s;
    }
    public static void main(String[] args) {
        calc("", "0"); calc("2+3*4", "14"); calc("(2+3)*4", "20"); calc("0.1+0.2", "0.3");
        calc("1/3", "0.33333333333333"); calc("12/4", "3"); calc("-2^2", "-4"); calc("(-2)^2", "4");
        calc("2^-3", "0.125"); calc("2^3^2", "512"); calc("2(3+4)", "14");
        calc("sqrt(81)", "9"); calc("5!", "120"); calc("0!", "1"); calc("10!", "3628800");
        calc("200+10%", "220"); calc("200-10%", "180"); calc("200*10%", "20"); calc("50%", "0.5");
        calc("sin(30)", "0.5"); calc("cos(60)", "0.5"); calc("sin(180)", "0"); calc("tan(45)", "1");
        calc("asin(0.5)", "30"); calc("acos(0.5)", "60"); calc("atan(1)", "45");
        calc("log(1000)", "3"); calc("ln(e)", "1"); calc("exp(0)", "1"); calc("abs(-6)", "6");
        calc("2*pi/pi", "2"); calc("1E-5*10", "0.0001"); calc("1E+20/1E+19", "10");
        calc(".5+.25", "0.75"); calc("9^0.5", "3"); calc("2*-3", "-6"); calc("3--2", "5");
        equal("radian", ExpressionEvaluator.format(e.evaluate("sin(pi/2)", false)), "1");
        for (String input : new String[]{"1/0", "sqrt(-1)", "log(0)", "ln(-1)", "tan(90)", "asin(2)",
                "(-1)!", "1.5!", "171!", "2+", "((2)", "2..3", "1E+", "(-2)^0.5", "0^-1", "1E3010"}) bad(input);
        CalculatorState s = type("0", ".", "1", "+", "0", ".", "2"); equal("decimal keys", s.calculate(), "0.3");
        s.input("+"); s.input("2"); equal("continue result", s.calculate(), "2.3");
        s.input("7"); equal("fresh result", s.calculate(), "7");
        s = type("2", "+", "3", "SIGN"); equal("sign operand", s.calculate(), "-1");
        s = type("2", "+", "3", "SIGN", "SIGN"); equal("toggle twice", s.calculate(), "5");
        s = type("0", "0", "5"); equal("leading zeros", s.calculate(), "5");
        s = type("1", ".", ".", "2"); equal("duplicate dot", s.calculate(), "1.2");
        s = type("2", "*", "-", "3"); equal("negative factor", s.calculate(), "-6");
        s = type("2", "+", "*", "3"); equal("replace operator", s.calculate(), "6");
        s = type("2", "(", "3", "+", "4", ")"); equal("implicit product input", s.calculate(), "14");
        s = type("sin(", "3", "0", ")"); equal("function input", s.calculate(), "0.5");
        s = type("5", "SQUARE"); equal("square input", s.calculate(), "25");
        s = type("4", "INV"); equal("inverse input", s.calculate(), "0.25");
        s = type("3", "M+"); equal("M+", s.memory, "3"); s.input("AC"); s.input("2"); s.input("M-"); equal("M-", s.memory, "1");
        s.input("AC"); s.input("MR"); equal("MR", s.calculate(), "1"); s.input("MC"); equal("MC", s.memory, "0");
        s = type("6"); s.calculate(); s.input("AC"); s.input("Ans"); s.input("*"); s.input("2"); equal("Ans", s.calculate(), "12");
        s = type("sin(", "DEL"); equal("delete function", s.expression, "");
        s = type("8", "9", "DEL"); equal("delete digit", s.expression, "8");
        s = new CalculatorState(); // State can restore a negative saved answer.
        s.expression = "-3"; s.input("SQUARE"); equal("square negative result", s.calculate(), "9");
        s = type("2", "+", "-", "3", "SIGN"); equal("negative operand sign", s.calculate(), "5");
        System.out.println("PASS: " + passed + " checks");
    }
}
