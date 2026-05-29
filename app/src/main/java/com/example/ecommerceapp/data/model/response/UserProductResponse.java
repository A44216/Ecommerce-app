package com.example.ecommerceapp.data.model.response;

import com.example.ecommerceapp.data.enums.ProductStatus;
import java.math.BigDecimal;
import java.util.List;

public class UserProductResponse {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String description;
    private List<UserProductImageResponse> images;
    private String categoryName;
    private Integer categoryId;
    private Integer shopId;
    private String shopName;
    private Integer shopOwnerId;

    // --- Các thông số để hiển thị thống kê ---
    private Float ratingAvg;   // Đã đổi thành Float để dễ truyền qua Intent (Hoặc bạn có thể dùng BigDecimal cũng được nhưng cần ép kiểu)
    private Integer soldCount; // Dùng Integer thay vì int để tránh lỗi null
    private Integer stock;     // ĐÃ THÊM BIẾN STOCK
    private ProductStatus status;

    // --- GETTERS ---
    public Integer getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public List<UserProductImageResponse> getImages() { return images; }
    public String getCategoryName() { return categoryName; }
    public Integer getCategoryId() { return categoryId; }
    public Integer getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public Integer getShopOwnerId() { return shopOwnerId; }

    // Đã đổi kiểu trả về cho khớp với adapter
    public Float getRatingAvg() { return ratingAvg; }
    public Integer getSoldCount() { return soldCount; }
    public Integer getStock() { return stock; }

    public ProductStatus getStatus() { return status; }

    // --- SETTERS (Rất quan trọng để thư viện Gson đẩy dữ liệu vào) ---
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImages(List<UserProductImageResponse> images) { this.images = images; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setShopId(Integer shopId) { this.shopId = shopId; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setShopOwnerId(Integer shopOwnerId) { this.shopOwnerId = shopOwnerId; }
    public void setRatingAvg(Float ratingAvg) { this.ratingAvg = ratingAvg; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setStatus(ProductStatus status) { this.status = status; }
}