package com.sshInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONObject;

import java.util.List;

@RestController
public class Controller {
    @Autowired
    Connection connection;

    @PostMapping(value = "/writeConfig")
    private JSONObject writeConfig(@RequestBody Data data) {
        JSONObject response = null;
        return connection.execWriteCommands(data);
    }

    @GetMapping(value = "/getConfig")
    private JSONObject getConfig(@RequestBody Data data) {
        JSONObject response = null;
        return connection.execGetCommands(data);
    }
    @PostMapping(value = "/writeMultipleConfig")
    private JSONObject writeMultipleConfig(@RequestBody List<Data> data) {
        JSONObject response = new JSONObject();
        for (int i = 0; i < data.size(); i++) {
            Data d = data.get(i);
            response.put(d.getHost(),connection.execWriteCommands(d));

        }
        return response;
    }
}
