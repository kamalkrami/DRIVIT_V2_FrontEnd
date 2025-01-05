package com.example.drivit_v2_frontend.models;



import com.example.drivit_v2_frontend.enums.Status_rental;

import java.io.Serializable;
import java.time.LocalDate;

public class CarRental implements Serializable {
    private String id_carRental;
    private Cars cars;
    private String id_car;
    private Users users;
    private String id_user;
    private String rentalTime;
    private Status_rental statusRental;

    public CarRental(String id_carRental, Cars cars, String id_car, Users users, String id_user, String rentalTime, Status_rental statusRental) {
        this.id_carRental = id_carRental;
        this.cars = cars;
        this.id_car = id_car;
        this.users = users;
        this.id_user = id_user;
        this.rentalTime = rentalTime;
        this.statusRental = statusRental;
    }

    public String getId_carRental() {
        return id_carRental;
    }

    public void setId_carRental(String id_carRental) {
        this.id_carRental = id_carRental;
    }

    public Cars getCars() {
        return cars;
    }

    public void setCars(Cars cars) {
        this.cars = cars;
    }

    public String getId_car() {
        return id_car;
    }

    public void setId_car(String id_car) {
        this.id_car = id_car;
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

    public String getRentalTime() {
        return rentalTime;
    }

    public void setRentalTime(String rentalTime) {
        this.rentalTime = rentalTime;
    }

    public Status_rental getStatusRental() {
        return statusRental;
    }

    public void setStatusRental(Status_rental statusRental) {
        this.statusRental = statusRental;
    }
}
