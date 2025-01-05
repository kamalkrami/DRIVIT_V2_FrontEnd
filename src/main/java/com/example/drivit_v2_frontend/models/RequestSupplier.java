package com.example.drivit_v2_frontend.models;

import com.example.drivit_v2_frontend.enums.Status_Request_Supplier;

import java.io.Serializable;
import java.time.LocalDate;


public class RequestSupplier implements Serializable {
    private String id_request;
    private Users users;
    private String id_user;
    private String requestDate;
    private Status_Request_Supplier status;

    public RequestSupplier(String id_request, Users users, String id_user, String requestDate, Status_Request_Supplier status) {
        this.id_request = id_request;
        this.users = users;
        this.id_user = id_user;
        this.requestDate = requestDate;
        this.status = status;
    }

    public String getId_request() {
        return id_request;
    }

    public void setId_request(String id_request) {
        this.id_request = id_request;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public Status_Request_Supplier getStatus() {
        return status;
    }

    public void setStatus(Status_Request_Supplier status) {
        this.status = status;
    }
}
