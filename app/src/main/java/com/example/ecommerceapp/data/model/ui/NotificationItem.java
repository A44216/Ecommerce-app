package com.example.ecommerceapp.data.model.ui;

public class NotificationItem {
    private int iconResId;
    private String title;
    private String description;
    private int badgeCount; // Số lượng thông báo mới

    public NotificationItem(int iconResId, String title, String description, int badgeCount) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.badgeCount = badgeCount;
    }

    public int getIconResId() { return iconResId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getBadgeCount() { return badgeCount; }
    public void setBadgeCount(int badgeCount) { this.badgeCount = badgeCount; }
    public void setDescription(String description) { this.description = description; }
}