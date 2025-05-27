package org.example;

import org.example.rpcprotocol.AppServicesRpcProxy;
import org.example.service.IAppServices;

import java.util.Properties;

public class StartRpcClient {
    private static int defaultChatPort=55555;
    private static String defaultServer="localhost";
    public static void main(String[] args) {
        Properties clientProps=new Properties();
        try{
            clientProps.load(StartRpcClient.class.getResourceAsStream("/swimmingclient.properties"));
            System.out.println("Client properties loaded");
            clientProps.list(System.out);
        }catch(Exception e){
            System.err.println("Cannot find swimmingclient.properties "+e);
            return;
        }
        String serverIP=clientProps.getProperty("swimming.server.host",defaultServer);
        int serverPort=defaultChatPort;
        try{
            serverPort=Integer.parseInt(clientProps.getProperty("swimming.server.port"));
        }catch(NumberFormatException e){
            System.err.println("Wrong port number "+e.getMessage());
            System.err.println("Default port number is "+defaultChatPort);
        }
        System.out.println("Using server IP "+serverIP);
        System.out.println("Using server port "+serverPort);
        IAppServices services=new AppServicesRpcProxy(serverIP,serverPort);
        AppClientCtrl ctrl=new AppClientCtrl(services);
    }
}
