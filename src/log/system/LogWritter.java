/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log.system;

import java.util.ArrayList;

/**
 *
 * @author adhuse
 */
public class LogWritter extends Thread {
    private static ArrayList<String> write_queue = new ArrayList<String>();
    
    public static boolean add_log_https(String authToken, String data) {
        boolean success = false;
        
        //TODO: check auth
        success = write_queue.add(data);
        
        return success;
    }
}
