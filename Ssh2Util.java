package com.zkcm.hydrobiologicasinica.common.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.zkcm.hydrobiologicasinica.common.config.SshConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: 玲
 * @Description: 远程连接服务器
 * @create 2020-07-24 17:58
 * @Modified By:
 */
public class Ssh2Util {

   public static final String encodeSet = "export LC_CTYPE=zh_CN.UTF-8;";

    public static Connection getConn(SshConfig sshConfig) {
        Connection conn = null;
        try {
            conn = new Connection(sshConfig.getIp());
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(sshConfig.getUsername(), sshConfig.getPassword());
            if (isAuthenticated) {
                return conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return conn;
    }

    public static String getResult(InputStream in) {
        InputStream inputStream = new StreamGobbler(in);
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                builder.append(readLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String getError(InputStream in) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                builder.append(readLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static boolean executeOne(SshConfig sshConfig, String command) {
        Connection conn = null;
        Session session = null;
        try {
            conn = getConn(sshConfig);
            session = conn.openSession();
            session.execCommand(encodeSet + command);
            // session.execCommand(command);
            getResult(session.getStdout());
            String result = getError(session.getStderr());
            if (StringUtils.isNotBlank(result)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return true;
    }
}
