package com.sshInterface;

import java.util.ArrayList;

@lombok.Data
public class Data {
    private String user;
    private String password;
    private String host;
    private ArrayList<String> commands;
}
