package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.rpcprotocol.AppClientRpcReflectionWorker;
import org.example.service.IAppServices;

import java.net.Socket;

public class AppRpcConcurrentServer extends AbsConcurrentServer{
    private IAppServices appServices;
    private static Logger logger= LogManager.getLogger(AppRpcConcurrentServer.class);

    public AppRpcConcurrentServer(int port, IAppServices appServices){
        super(port);
        this.appServices=appServices;
        logger.info("AppRpcConcurrentServer constructor");
    }

    @Override
    protected Thread createWorker(Socket client) {
        AppClientRpcReflectionWorker worker = new AppClientRpcReflectionWorker(appServices, client);
        return new Thread(worker);
    }

    @Override
    public void stop(){
        logger.info("AppRpcConcurrentServer stop");
    }
}
