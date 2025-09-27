package com.example.tp0gym.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionsResponse {
    @SerializedName("items")
    private List<SessionDto> items;
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("page")
    private int page;
    
    @SerializedName("pageSize")
    private int pageSize;
    
    @SerializedName("hasMore")
    private boolean hasMore;

    // Constructors
    public SessionsResponse() {}

    // Getters and Setters
    public List<SessionDto> getItems() {
        return items;
    }

    public void setItems(List<SessionDto> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
