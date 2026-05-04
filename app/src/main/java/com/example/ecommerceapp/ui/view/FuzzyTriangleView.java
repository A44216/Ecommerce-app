package com.example.ecommerceapp.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class FuzzyTriangleView extends View {

    private Paint gridPaint;
    private Paint valuePaint;
    private Paint valueBorderPaint;
    private Paint textPaint;

    private float ratingScore = 0.5f;
    private float soldScore = 0.5f;
    private float priceScore = 0.5f;

    public FuzzyTriangleView(Context context) {
        super(context);
        init();
    }

    public FuzzyTriangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Lưới biểu đồ (Màu xám nhạt, nét đứt)
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2f);

        // Phần diện tích tam giác (Quay lại màu Xanh của dự án)
        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.parseColor("#802563EB")); // Màu xanh có độ trong suốt (80 alpha)
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setPathEffect(new CornerPathEffect(15)); 

        // Viền của tam giác giá trị
        valueBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valueBorderPaint.setColor(Color.parseColor("#2563EB"));
        valueBorderPaint.setStyle(Paint.Style.STROKE);
        valueBorderPaint.setStrokeWidth(5f);
        valueBorderPaint.setPathEffect(new CornerPathEffect(15));

        // Text nhãn
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#424242"));
        textPaint.setTextSize(40f); // Tăng kích thước chữ lên 40f
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true); // Làm đậm chữ cho rõ
    }

    public void setScores(float rating, float sold, float price) {
        // Đảm bảo tối thiểu là 0.05 để không bị xẹp thành đường thẳng
        this.ratingScore = Math.max(0.05f, rating);
        this.soldScore = Math.max(0.05f, sold);
        this.priceScore = Math.max(0.05f, price);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 30;
        int maxRadius = Math.min(getWidth(), getHeight()) / 2 - 110;
        float midRadius = maxRadius * 0.5f; // Khoảng cách từ tâm đến trung điểm cạnh

        // 1. Vẽ lưới và trục (Grid)
        float[] levels = {0.25f, 0.5f, 0.75f, 1.0f};
        for (float level : levels) {
            drawTriangle(canvas, centerX, centerY, maxRadius * level, gridPaint);
        }
        drawAxes(canvas, centerX, centerY, maxRadius, gridPaint);

        // 2. Tính toán các điểm của đa giác giá trị (7 điểm)
        // Lấy score thực tế (giá tốt đảo ngược)
        float s1 = ratingScore;
        float s2 = soldScore;
        float s3 = 1 - priceScore;

        // Tọa độ các đỉnh (V) và trung điểm (M)
        // V1: 90°, V2: 210°, V3: 330°
        // M12: 150°, M23: 270°, M31: 30°
        
        Path polyPath = new Path();
        
        // Bắt đầu từ điểm M31 (giữa Price và Rating)
        float rM31 = midRadius * Math.max(s1, s3);
        float xM31 = (float)(centerX + rM31 * Math.cos(Math.toRadians(30)));
        float yM31 = (float)(centerY - rM31 * Math.sin(Math.toRadians(30)));
        polyPath.moveTo(xM31, yM31);

        // Đỉnh V1 (Rating)
        float rV1 = maxRadius * s1;
        polyPath.lineTo(centerX, centerY - rV1);

        // Điểm M12 (giữa Rating và Sold)
        float rM12 = midRadius * Math.max(s1, s2);
        polyPath.lineTo((float)(centerX + rM12 * Math.cos(Math.toRadians(150))), 
                        (float)(centerY - rM12 * Math.sin(Math.toRadians(150))));

        // Đỉnh V2 (Sold)
        float rV2 = maxRadius * s2;
        polyPath.lineTo((float)(centerX + rV2 * Math.cos(Math.toRadians(210))), 
                        (float)(centerY - rV2 * Math.sin(Math.toRadians(210))));

        // Điểm M23 (giữa Sold và Price)
        float rM23 = midRadius * Math.max(s2, s3);
        polyPath.lineTo(centerX, centerY + rM23); 

        // Đỉnh V3 (Price)
        float rV3 = maxRadius * s3;
        polyPath.lineTo((float)(centerX + rV3 * Math.cos(Math.toRadians(330))), 
                        (float)(centerY - rV3 * Math.sin(Math.toRadians(330))));

        polyPath.close();

        // 3. Vẽ đa giác
        canvas.drawPath(polyPath, valuePaint);
        canvas.drawPath(polyPath, valueBorderPaint);

        // 4. Vẽ nhãn (Labels)
        drawLabels(canvas, centerX, centerY, maxRadius);
    }

    private void drawTriangle(Canvas canvas, int cx, int cy, float radius, Paint paint) {
        Path path = new Path();
        path.moveTo(cx, cy - radius);
        path.lineTo((float) (cx + radius * Math.cos(Math.toRadians(210))), (float) (cy - radius * Math.sin(Math.toRadians(210))));
        path.lineTo((float) (cx + radius * Math.cos(Math.toRadians(330))), (float) (cy - radius * Math.sin(Math.toRadians(330))));
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawAxes(Canvas canvas, int cx, int cy, float radius, Paint paint) {
        canvas.drawLine(cx, cy, cx, cy - radius, paint);
        canvas.drawLine(cx, cy, (float) (cx + radius * Math.cos(Math.toRadians(210))), (float) (cy - radius * Math.sin(Math.toRadians(210))), paint);
        canvas.drawLine(cx, cy, (float) (cx + radius * Math.cos(Math.toRadians(330))), (float) (cy - radius * Math.sin(Math.toRadians(330))), paint);
    }

    private void drawLabels(Canvas canvas, int cx, int cy, float radius) {
        float offset = 65f;
        textPaint.setTextSize(34f);

        // Label: Đánh giá
        int rPercent = (int) (ratingScore * 100);
        String ratingText = "Đánh giá (" + rPercent + "%)";
        canvas.drawText(ratingText, cx, cy - radius - offset, textPaint);

        // Label: Lượt bán
        int sPercent = (int) (soldScore * 100);
        String soldText = "Lượt bán (" + sPercent + "%)";
        float x2 = (float) (cx + radius * Math.cos(Math.toRadians(210))) - 80;
        float y2 = (float) (cy - radius * Math.sin(Math.toRadians(210))) + offset;
        canvas.drawText(soldText, x2, y2, textPaint);

        // Label: Giá tốt
        // Invert priceScore: Low price = High "Good Price" score
        int pPercent = (int) ((1 - priceScore) * 100);
        String priceText = "Giá tốt (" + pPercent + "%)";
        float x3 = (float) (cx + radius * Math.cos(Math.toRadians(330))) + 80;
        float y3 = (float) (cy - radius * Math.sin(Math.toRadians(330))) + offset;
        canvas.drawText(priceText, x3, y3, textPaint);
    }
}
