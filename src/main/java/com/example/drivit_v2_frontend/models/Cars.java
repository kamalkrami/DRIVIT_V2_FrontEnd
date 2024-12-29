package com.example.drivit_v2_frontend.models;

import android.net.Uri;

import com.example.drivit_v2_frontend.enums.Status_add;
import com.example.drivit_v2_frontend.enums.Status_dispo;

public class Cars {
    private String id_car;
    private Users users;
    private String id_user;
    private String carName;
    private String carPrix;
    private String carModel;
    private String carMatricul;
    private String carImage;
    private Status_dispo statusDipo;
    private Status_add statusAdd;

    public Cars() {
    }

    public Cars(String id_car, Users users, String id_user, String carName,String carPrix, String carModel, String carMatricul, String carImage, Status_dispo statusDipo, Status_add statusAdd) {
        this.id_car = id_car;
        this.users = users;
        this.id_user = id_user;
        this.carName = carName;
        this.carModel = carModel;
        this.carMatricul = carMatricul;
        this.carImage = carImage;
        this.statusDipo = statusDipo;
        this.statusAdd = statusAdd;
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

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }


    public String getCarPrix() {
        return carPrix;
    }

    public void setCarPrix(String carPrix) {
        this.carPrix = carPrix;
    }


    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarMatricul() {
        return carMatricul;
    }

    public void setCarMatricul(String carMatricul) {
        this.carMatricul = carMatricul;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public Status_dispo getStatusDipo() {
        return statusDipo;
    }

    public void setStatusDipo(Status_dispo statusDipo) {
        this.statusDipo = statusDipo;
    }

    public Status_add getStatusAdd() {
        return statusAdd;
    }

    public void setStatusAdd(Status_add statusAdd) {
        this.statusAdd = statusAdd;
    }
}
