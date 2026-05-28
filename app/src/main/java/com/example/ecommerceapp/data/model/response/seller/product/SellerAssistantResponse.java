package com.example.ecommerceapp.data.model.response.seller.product;

import com.google.gson.annotations.SerializedName;

public class SellerAssistantResponse {

    @SerializedName("productId")
    private Integer productId;

    @SerializedName("score")
    private Double score;

    @SerializedName("priceScore")
    private Double priceScore;

    @SerializedName("soldScore")
    private Double soldScore;

    @SerializedName("ratingScore")
    private Double ratingScore;

    @SerializedName("analysis")
    private String analysis;

    @SerializedName("recommendation")
    private String recommendation;

    public SellerAssistantResponse() {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getPriceScore() {
        return priceScore;
    }

    public void setPriceScore(Double priceScore) {
        this.priceScore = priceScore;
    }

    public Double getSoldScore() {
        return soldScore;
    }

    public void setSoldScore(Double soldScore) {
        this.soldScore = soldScore;
    }

    public Double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
