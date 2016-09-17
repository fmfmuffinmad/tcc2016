package com.tcc.tccpinut.tccpinut.classes;

import android.provider.ContactsContract;

/**
 * Created by muffinmad on 14/09/2016.
 */
public class Amigo {
    // TODO: Criar todos os atributos e funções da classe
    private int id;
    private String username;
    private ContactsContract.CommonDataKinds.Phone phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ContactsContract.CommonDataKinds.Phone getPhone() {
        return phone;
    }

    public void setPhone(ContactsContract.CommonDataKinds.Phone phone) {
        this.phone = phone;
    }
}
