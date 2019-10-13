package com.andrewn.userapi.model.jwt;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private String jwtToken;

    public JwtResponse() { }

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public void setToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
