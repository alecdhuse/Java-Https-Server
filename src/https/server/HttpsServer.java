package https.server;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.time.ZoneId;
import java.time.ZonedDateTime;
 
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
 
public class HttpsServer {
    private int port = 8443;
    private boolean isServerDone = false;
     
    public static void main(String[] args){
        HttpsServer server = new HttpsServer();
        server.run();
    }
     
    HttpsServer(){      
    }
     
    HttpsServer(int port){
        this.port = port;
    }
     
    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keystore.jks"),"passphrase".toCharArray());
             
            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "passphrase".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();
             
            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();
             
            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(km,  null, null);
             
            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }
         
        return null;
    }
     
    // Start to run the server
    public void run(){
        SSLContext sslContext = this.createSSLContext();
         
        try{
            // Create server socket factory
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
             
            // Create server socket
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port);
             
            System.out.println("SSL server started");
            while(!isServerDone){
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                 
                // Start the server thread
                new ServerThread(sslSocket).start();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
     
    // Thread handling the socket from client
    static class ServerThread extends Thread {
        private SSLSocket sslSocket = null;
         
        ServerThread(SSLSocket sslSocket){
            this.sslSocket = sslSocket;
        }
         
        public void run(){
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
             
            try{
                // Start handshake
                sslSocket.startHandshake();
                 
                // Get session after the connection is established
                SSLSession sslSession = sslSocket.getSession();
                 
                System.out.println("SSLSession :");
                System.out.println("\tProtocol : "+sslSession.getProtocol());
                System.out.println("\tCipher suite : "+sslSession.getCipherSuite());
                 
                // Start handling application content
                InputStream inputStream = sslSocket.getInputStream();
                OutputStream outputStream = sslSocket.getOutputStream();
                 
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
                 
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    System.out.println("Inut : "+line);
                     
                    if(line.trim().isEmpty()){
                        break;
                    }
                }
                 
                // Write data
                printWriter.print("HTTP/1.1 200 OK\r\n");
                printWriter.print("Server: Java HTTS Webserver\r\n");
                printWriter.print("Date: " + java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT"))) + "\r\n");
                printWriter.print("Content-Type: text/html\r\n");
                
                String htmlString = "<html>Hello World!</html>";
                
                printWriter.print("Content-Length: " + htmlString.getBytes().length + "\r\n");
                printWriter.print("Connection: close\r\n");
                printWriter.print(htmlString);
                printWriter.flush();
                 
                sslSocket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}