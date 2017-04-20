/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log.system;

import authentication.authentication;
import java.util.ArrayList;

/**
 *
 * @author adhuse
 */
public class LogWritter extends Thread {
    private static ArrayList<String> write_queue = new ArrayList<String>();
    
    public static boolean add_log_https(String authToken, String data) {
        boolean success = false;
        
        //check auth
        if (authentication.validateToken(authToken)) {
            success = write_queue.add(data);
        } else {
            success = false;
        }
        
        return success;
    }
}
