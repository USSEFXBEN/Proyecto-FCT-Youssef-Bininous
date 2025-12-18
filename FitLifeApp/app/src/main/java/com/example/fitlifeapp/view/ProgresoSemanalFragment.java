package com.example.fitlifeapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitlifeapp.R;

public class ProgresoSemanalFragment extends Fragment {

    private TextView tvRacha, tvTotalRutinas, tvLogros;

    private View[] bars = new View[7];
    private int[] weeklyValues = new int[]{8, 6, 9, 7, 10, 5, 4}; // ejemplo
    private int maxBarValue = 10; // para escalar las barras

    private int[] monthlyValues = new int[]{65, 72, 68, 80}; // porcentajes ejemplo

    public ProgresoSemanalFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_progreso_semanal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRacha = view.findViewById(R.id.tvRacha);
        tvTotalRutinas = view.findViewById(R.id.tvTotalRutinas);
        tvLogros = view.findViewById(R.id.tvLogros);

        // barras
        bars[0] = view.findViewById(R.id.bar0);
        bars[1] = view.findViewById(R.id.bar1);
        bars[2] = view.findViewById(R.id.bar2);
        bars[3] = view.findViewById(R.id.bar3);
        bars[4] = view.findViewById(R.id.bar4);
        bars[5] = view.findViewById(R.id.bar5);
        bars[6] = view.findViewById(R.id.bar6);

        cargarResumen();
        renderWeeklyBars();
        addTrendView(view);
    }

    private void cargarResumen() {
        // ejemplo de datos (luego vendrán de base/datos)
        tvRacha.setText("7 días");
        tvTotalRutinas.setText("156");
        tvLogros.setText("12");
    }

    private void renderWeeklyBars() {
        // altura máxima disponible (en dp) para la barra
        float maxBarDp = 120f; // corresponderá a value == maxBarValue
        int maxBarPx = dpToPx(requireContext(), maxBarDp);

        for (int i = 0; i < bars.length; i++) {
            View bar = bars[i];
            int value = 0;
            if (i < weeklyValues.length) value = weeklyValues[i];

            int heightPx = (int) ((value / (float) maxBarValue) * maxBarPx);
            if (heightPx < dpToPx(requireContext(), 6)) heightPx = dpToPx(requireContext(), 6); // mínimo visible

            ViewGroup.LayoutParams lp = bar.getLayoutParams();
            lp.height = heightPx;
            bar.setLayoutParams(lp);
        }
    }

    private void addTrendView(View root) {
        FrameLayout container = root.findViewById(R.id.trend_card);
        TrendView trend = new TrendView(requireContext(), monthlyValues);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        trend.setLayoutParams(lp);
        container.addView(trend);
    }

    private static int dpToPx(Context c, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    /**
     * View personalizado que dibuja una línea simple y puntos (tendencia mensual).
     */
    private static class TrendView extends View {
        private Paint linePaint;
        private Paint pointPaint;
        private Paint gridPaint;
        private Paint textPaint;
        private Path path;
        private int[] values;

        public TrendView(Context context, int[] values) {
            super(context);
            this.values = values != null ? values : new int[]{0,0,0,0};
            init();
        }

        private void init() {
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(dpToPx(getContext(), 2));
            linePaint.setColor(0xFF2F8EF8); // azul

            pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            pointPaint.setStyle(Paint.Style.FILL);
            pointPaint.setColor(0xFF2F8EF8);

            gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setStrokeWidth(1f);
            gridPaint.setColor(0xFFE0E6EF);

            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(dpToPx(getContext(), 11));
            textPaint.setColor(0xFF666666);

            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();

            int paddingLeft = dpToPx(getContext(), 12);
            int paddingRight = dpToPx(getContext(), 12);
            int paddingTop = dpToPx(getContext(), 16);
            int paddingBottom = dpToPx(getContext(), 28);

            int drawW = w - paddingLeft - paddingRight;
            int drawH = h - paddingTop - paddingBottom;

            // grid horizontal (4 líneas)
            for (int i = 0; i <= 4; i++) {
                float y = paddingTop + (i * (drawH / 4f));
                canvas.drawLine(paddingLeft, y, paddingLeft + drawW, y, gridPaint);
            }

            // points and line
            int n = values.length;
            if (n == 0) return;

            path.reset();
            for (int i = 0; i < n; i++) {
                float fractionX = (i / (float) (Math.max(1, n - 1)));
                float x = paddingLeft + fractionX * drawW;
                float val = values[i] / 100f; // percentages
                float y = paddingTop + (1f - val) * drawH;

                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }

            // draw line and points
            canvas.drawPath(path, linePaint);
            for (int i = 0; i < n; i++) {
                float fractionX = (i / (float) (Math.max(1, n - 1)));
                float x = paddingLeft + fractionX * drawW;
                float val = values[i] / 100f;
                float y = paddingTop + (1f - val) * drawH;
                canvas.drawCircle(x, y, dpToPx(getContext(), 3), pointPaint);
            }

            // labels W1...Wn
            for (int i = 0; i < n; i++) {
                float fractionX = (i / (float) (Math.max(1, n - 1)));
                float x = paddingLeft + fractionX * drawW;
                String label = "W" + (i + 1);
                float textWidth = textPaint.measureText(label);
                canvas.drawText(label, x - textWidth / 2f, h - dpToPx(getContext(), 8), textPaint);
            }

            // simple percentage labels
            canvas.drawText("0", paddingLeft - dpToPx(getContext(), 6), paddingTop + drawH, textPaint);
            canvas.drawText("100", paddingLeft - dpToPx(getContext(), 22), paddingTop + dpToPx(getContext(), 6), textPaint);
        }
    }
}
