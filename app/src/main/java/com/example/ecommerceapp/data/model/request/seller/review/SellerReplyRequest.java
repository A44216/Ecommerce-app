package com.example.ecommerceapp.data.model.request.seller.review;

public class SellerReplyRequest {

    private String sellerReply;

    public SellerReplyRequest() {
    }

    public SellerReplyRequest(String sellerReply) {
        this.sellerReply = sellerReply;
    }

    public String getSellerReply() {
        return sellerReply;
    }

    public void setSellerReply(String sellerReply) {
        this.sellerReply = sellerReply;
    }
}