package com.sshInterface;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sshInterface.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;

@Service
public class Connection {
    Logger logger = LoggerFactory.getLogger(Connection.class);

    /**
     * Authenticates the user, establishes an SSH connection to a remote server,
     * and executes a series of commands.
     *
     * @param datas the Data object containing the authentication and command information
     * @return a JSONObject containing the response data
     */
    public JSONObject execWriteCommands(Data datas) {

        String username = datas.getUser();
        String ip = datas.getHost();
        String password = datas.getPassword();
        ArrayList<String> commands = datas.getCommands();
        JSONObject response = new JSONObject();
        try {
            // Establish SSH connection
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, ip, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("shell");
            OutputStream ops = channel.getOutputStream();
            PrintStream ps = new PrintStream(ops, true);

            channel.connect();
            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            for (String command : commands) {
                ps.println(command);
                ps.flush();

                String line;
                ArrayList<String> data = new ArrayList<>();
                while ((line = reader.readLine()) != null && !line.contains("#")) {
                    data.add(line);
                }
                response.put(command, data);
            }

            ps.close();
            reader.close();
            in.close();
            channel.disconnect();
            session.disconnect();
            logger.info("Commands executed");
            return response;
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        return null;
    }

    public JSONObject execGetCommands(Data datas) {

        String username = datas.getUser();
        String ip = datas.getHost();
        String password = datas.getPassword();
        ArrayList<String> commands = datas.getCommands();
        JSONObject response = new JSONObject();
        try {
            for (String command : commands) {

                // Establish SSH connection
                JSch jsch = new JSch();
                Session session = jsch.getSession(username, ip, 22);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                // Get input stream from the channel
                InputStream in = channel.getInputStream();
                channel.connect();
                // Read the command output
                BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

                String line;
                ArrayList<String> data = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    data.add(line);
                }
                //Added the json object
                response.put(command,data);

                //Cleanup the resource
                reader.close();
                in.close();
                channel.disconnect();
                session.disconnect();
                logger.info("Command : "+command);
            }
            return response;
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        return null;
    }
}
