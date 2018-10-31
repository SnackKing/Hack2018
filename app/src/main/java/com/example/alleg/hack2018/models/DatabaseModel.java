package com.example.alleg.hack2018.models;

import android.content.ContentValues;

import java.io.Serializable;

public interface DatabaseModel extends Serializable {
    ContentValues getContentValues();
    String getID();
}
