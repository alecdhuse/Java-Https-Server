/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package https.server;

/**
 *
 * @author adhuse
 */
public class Main {
    public static int port = 8443;
    
    public static void main(String[] args) {
        LittleHttpsServer httpsServer = new LittleHttpsServer();
        httpsServer.Start(port);    
        
    }
}
