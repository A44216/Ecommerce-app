package com.example.ecommerceapp.data.model.ui;

import com.example.ecommerceapp.data.model.response.ProductBaseResponse;
import com.example.ecommerceapp.data.model.response.ProductEvaluationResponse;
import com.example.ecommerceapp.data.model.response.seller.RecommendationResponse;

import java.math.BigDecimal;

public class SuggestionDisplayItem {
    private final Object originalData;
    private final ProductBaseResponse product;
    private final BigDecimal score;
    private final String reason;
    private final BigDecimal ratingScore;
    private final BigDecimal soldScore;
    private final BigDecimal priceScore;

    public SuggestionDisplayItem(ProductEvaluationResponse eval) {
        this.originalData = eval;
        this.product = eval.getProduct();
        this.score = eval.getScore();
        this.reason = eval.getReason();
        this.ratingScore = eval.getRatingScore();
        this.soldScore = eval.getSoldScore();
        this.priceScore = eval.getPriceScore();
    }

    public SuggestionDisplayItem(RecommendationResponse rec) {
        this.originalData = rec;
        this.product = rec.getProduct();
        this.score = rec.getScore();
        this.reason = rec.getReason();
        // Recommendations don't have sub-scores in current schema, defaulting to 0 or mapping if available
        this.ratingScore = BigDecimal.ZERO;
        this.soldScore = BigDecimal.ZERO;
        this.priceScore = BigDecimal.ZERO;
    }

    public Object getOriginalData() { return originalData; }
    public ProductBaseResponse getProduct() { return product; }
    public BigDecimal getScore() { return score; }
    public String getReason() { return reason; }
    public BigDecimal getRatingScore() { return ratingScore; }
    public BigDecimal getSoldScore() { return soldScore; }
    public BigDecimal getPriceScore() { return priceScore; }

    public boolean isFuzzy() {
        return originalData instanceof ProductEvaluationResponse;
    }
}
