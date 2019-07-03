package com.example.ex8;

class ClientInfo {
    private String username;
    private String imageURL;
    private String pretty_name;
    private String token;

    ClientInfo(){
        username = null;
        pretty_name = null;
        token = null;
        imageURL = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPretty_name() {
        return pretty_name;
    }

    public void setPretty_name(String pretty_name) {
        this.pretty_name = pretty_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

