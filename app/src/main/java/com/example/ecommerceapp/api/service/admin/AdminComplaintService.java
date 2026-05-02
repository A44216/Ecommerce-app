package com.example.ecommerceapp.api.service.admin;

import com.example.ecommerceapp.data.enums.ComplaintStatus;
import com.example.ecommerceapp.data.model.request.admin.management.AdminReplyComplaintRequest;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintDetailResponse;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintResponse;
import com.example.ecommerceapp.data.model.response.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface AdminComplaintService {

    // LIST + FILTER + SEARCH
    @GET("admin/complaints")
    Call<PageResponse<AdminComplaintResponse>> getComplaints(
            @Query("page") int page,
            @Query("size") int size,
            @Query("status") ComplaintStatus status,
            @Query("keyword") String keyword,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction
    );

    // DETAIL
    @GET("admin/complaints/{id}")
    Call<AdminComplaintDetailResponse> getComplaintById(
            @Path("id") int id
    );

    // FINAL ACTION: RESOLVE / REJECT + RESPONSE
    @POST("admin/complaints/{id}/reply")
    Call<Void> replyComplaint(
            @Path("id") int id,
            @Body AdminReplyComplaintRequest request
    );

    // AUTOCOMPLETE
    @GET("admin/complaints/autocomplete")
    Call<List<String>> autocompleteComplaints(
            @Query("keyword") String keyword
    );
}