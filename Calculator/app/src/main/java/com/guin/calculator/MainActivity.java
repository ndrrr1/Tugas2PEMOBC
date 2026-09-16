package com.guin.calculator;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Semua tombol dipanggil melalui atribut android:onClick di XML layout. */
public class MainActivity extends Activity {
    private final CalculatorState calculator = new CalculatorState();
    private SharedPreferences preferences;
    private JSONArray history = new JSONArray();
    private TextView expressionView, resultView, messageView, statusView;
    private RelativeLayout root, sciencePad, keypad;
    private View historyPanel;
    private boolean scientific, dark;
    private String lastEquation = "";

    @Override protected void attachBaseContext(Context base) {
        boolean useDark = base.getSharedPreferences("calculator", MODE_PRIVATE).getBoolean("dark", true);
        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.uiMode = (config.uiMode & ~Configuration.UI_MODE_NIGHT_MASK)
                | (useDark ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO);
        super.attachBaseContext(base.createConfigurationContext(config));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("calculator", MODE_PRIVATE);
        restoreState(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        sciencePad = findViewById(R.id.sciencePad);
        keypad = findViewById(R.id.keypad);
        expressionView = findViewById(R.id.expression);
        resultView = findViewById(R.id.result);
        messageView = findViewById(R.id.message);
        statusView = findViewById(R.id.status);
        if (Build.VERSION.SDK_INT >= 26) {
            resultView.setAutoSizeTextTypeUniformWithConfiguration(18, 48, 1, android.util.TypedValue.COMPLEX_UNIT_SP);
        } else resultView.setTextSize(30);
        configureInsets();
        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            sizeKeys(keypad, false);
            if (scientific) sizeKeys(sciencePad, true);
        });
        render();
    }

    private void configureInsets() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (!dark) flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (!dark && Build.VERSION.SDK_INT >= 26) flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        if (Build.VERSION.SDK_INT >= 30) getWindow().setDecorFitsSystemWindows(false);
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        root.requestApplyInsets();
    }

    /** Lebar tombol mengikuti perangkat; container tetap RelativeLayout. */
    private void sizeKeys(RelativeLayout pad, boolean small) {
        int width = pad.getWidth();
        if (width <= 0) return;
        int gap = dp(8);
        int buttonWidth = (width - gap * 3) / 4;
        int availableHeight = root.getHeight() - root.getPaddingTop() - root.getPaddingBottom();
        int buttonHeight = dp(small || availableHeight < dp(780) ? 48 : 56);
        int rows = (pad.getChildCount() + 3) / 4;
        ViewGroup.LayoutParams groupParams = pad.getLayoutParams();
        int desiredHeight = rows * buttonHeight + (rows - 1) * gap;
        if (groupParams.height != desiredHeight) {
            groupParams.height = desiredHeight;
            pad.setLayoutParams(groupParams);
        }
        for (int i = 0; i < pad.getChildCount(); i++) {
            View button = pad.getChildAt(i);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
            int start = (i % 4) * (buttonWidth + gap);
            int top = (i / 4) * (buttonHeight + gap);
            if (params.width != buttonWidth || params.height != buttonHeight
                    || params.getMarginStart() != start || params.topMargin != top) {
                params.width = buttonWidth;
                params.height = buttonHeight;
                params.setMarginStart(start);
                params.topMargin = top;
                button.setLayoutParams(params);
            }
        }
    }

    /** Signature ini wajib public void dengan satu parameter View untuk XML onClick. */
    public void onButtonClick(View view) {
        String key = String.valueOf(view.getTag());
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        try {
            switch (key) {
                case "MODE": scientific = !scientific; render(); break;
                case "THEME": dark = !dark; persist(); recreate(); return;
                case "ANGLE": calculator.degrees = !calculator.degrees; render(); break;
                case "HISTORY": showHistory(); break;
                case "CLOSE_HISTORY": closeHistory(); break;
                case "CLEAR_HISTORY": history = new JSONArray(); persist(); renderHistory(); break;
                case "COPY": copyResult(); break;
                case "=":
                    if (calculator.justEvaluated) return;
                    String equation = calculator.expression.isEmpty() ? "0" : calculator.expression;
                    String answer = calculator.calculate();
                    lastEquation = equation;
                    addHistory(equation, answer);
                    render();
                    break;
                default:
                    calculator.input(key);
                    render();
                    if (key.equals("MC") || key.equals("M+") || key.equals("M-"))
                        toast("Memori: " + calculator.memory);
                    break;
            }
        } catch (IllegalArgumentException | ArithmeticException ex) {
            resultView.setText("Error");
            messageView.setText(ex.getMessage());
            messageView.setTextColor(getColor(R.color.danger));
            toast(ex.getMessage());
        }
        persist();
    }

    private void render() {
        sciencePad.setVisibility(scientific ? View.VISIBLE : View.GONE);
        ((Button) findViewById(R.id.modeButton)).setText(scientific ? R.string.standard : R.string.scientific);
        ((Button) findViewById(R.id.angleButton)).setText(calculator.degrees ? "DEG" : "RAD");
        ((Button) findViewById(R.id.themeButton)).setText(dark ? "☀" : "☾");
        String mode = (scientific ? "SCIENTIFIC" : "STANDAR") + "  ·  " + (calculator.degrees ? "DEG" : "RAD");
        if (!calculator.memory.equals("0")) mode += "  ·  M";
        statusView.setText(mode);
        String display = calculator.justEvaluated ? lastEquation + " =" : calculator.expression;
        expressionView.setText(display.isEmpty() ? "0" : pretty(display));
        messageView.setTextColor(getColor(R.color.muted));
        try {
            resultView.setText(calculator.preview());
            messageView.setText(calculator.expression.isEmpty() ? getString(R.string.hint)
                    : calculator.justEvaluated ? "Tersimpan di riwayat · Siap untuk hitungan berikutnya" : "Pratinjau hasil · Tekan = untuk menyimpan");
        } catch (IllegalArgumentException | ArithmeticException ignored) {
            resultView.setText("…");
            messageView.setText("Lengkapi ekspresi dan tanda kurung, lalu tekan =");
        }
        HorizontalScrollView scroll = findViewById(R.id.expressionScroll);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_RIGHT));
    }

    private String pretty(String text) {
        return text.replace("sqrt", "√").replace("asin", "sin⁻¹").replace("acos", "cos⁻¹")
                .replace("atan", "tan⁻¹").replace("*", "×").replace("/", "÷").replace("-", "−");
    }

    private void copyResult() {
        String value = calculator.preview();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Hasil Calculator", value));
        toast("Hasil disalin: " + value);
    }

    private void addHistory(String expression, String answer) {
        try {
            JSONObject item = new JSONObject();
            item.put("expression", expression);
            item.put("answer", answer);
            item.put("mode", calculator.degrees ? "DEG" : "RAD");
            JSONArray updated = new JSONArray();
            updated.put(item);
            for (int i = 0; i < Math.min(history.length(), 49); i++) updated.put(history.getJSONObject(i));
            history = updated;
        } catch (JSONException ignored) { toast("Riwayat tidak dapat disimpan"); }
    }

    private void showHistory() {
        if (historyPanel != null) return;
        historyPanel = getLayoutInflater().inflate(R.layout.history_panel, root, false);
        root.addView(historyPanel, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // Cegah TalkBack mengakses tombol yang tertutup panel.
        root.getChildAt(0).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        renderHistory();
    }

    private void closeHistory() {
        if (historyPanel != null) {
            root.removeView(historyPanel);
            historyPanel = null;
            root.getChildAt(0).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        }
    }

    private void renderHistory() {
        RelativeLayout rows = historyPanel.findViewById(R.id.historyRows);
        rows.removeAllViews();
        if (history.length() == 0) {
            TextView empty = new TextView(this);
            empty.setText(R.string.history_empty);
            empty.setTextColor(getColor(R.color.muted));
            empty.setTextSize(16);
            empty.setPadding(dp(8), dp(30), dp(8), dp(30));
            rows.addView(empty);
            return;
        }
        int previousId = 0;
        for (int i = 0; i < history.length(); i++) {
            JSONObject item = history.optJSONObject(i);
            if (item == null) continue;
            View row = getLayoutInflater().inflate(R.layout.history_row, rows, false);
            row.setId(View.generateViewId());
            Button entry = row.findViewById(R.id.historyEntry);
            String answer = item.optString("answer", "0");
            entry.setText(item.optString("mode", "DEG") + "  ·  " + pretty(item.optString("expression")) + "\n= " + answer);
            entry.setTag(answer);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (previousId != 0) params.addRule(RelativeLayout.BELOW, previousId);
            params.bottomMargin = dp(10);
            rows.addView(row, params);
            previousId = row.getId();
        }
    }

    public void onHistoryResultClick(View view) {
        calculator.expression = String.valueOf(view.getTag());
        calculator.answer = calculator.expression;
        calculator.justEvaluated = true;
        lastEquation = calculator.expression;
        closeHistory();
        render();
        persist();
    }

    @Override public void onBackPressed() {
        if (historyPanel != null) closeHistory(); else super.onBackPressed();
    }

    private void restoreState(Bundle state) {
        calculator.expression = preferences.getString("expression", "");
        calculator.answer = preferences.getString("answer", "0");
        calculator.memory = preferences.getString("memory", "0");
        calculator.degrees = preferences.getBoolean("degrees", true);
        calculator.justEvaluated = preferences.getBoolean("evaluated", false);
        scientific = preferences.getBoolean("scientific", false);
        dark = preferences.getBoolean("dark", true);
        lastEquation = preferences.getString("lastEquation", "");
        try { history = new JSONArray(preferences.getString("history", "[]")); }
        catch (JSONException ignored) { history = new JSONArray(); }
        if (state != null) {
            calculator.expression = state.getString("expression", calculator.expression);
            calculator.justEvaluated = state.getBoolean("evaluated", calculator.justEvaluated);
            lastEquation = state.getString("lastEquation", lastEquation);
        }
    }

    private void persist() {
        preferences.edit().putString("expression", calculator.expression).putString("answer", calculator.answer)
                .putString("memory", calculator.memory).putBoolean("degrees", calculator.degrees)
                .putBoolean("evaluated", calculator.justEvaluated).putBoolean("scientific", scientific)
                .putBoolean("dark", dark).putString("lastEquation", lastEquation)
                .putString("history", history.toString()).apply();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putString("expression", calculator.expression);
        outState.putBoolean("evaluated", calculator.justEvaluated);
        outState.putString("lastEquation", lastEquation);
        persist();
        super.onSaveInstanceState(outState);
    }
    @Override protected void onPause() { persist(); super.onPause(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    private void toast(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
}
