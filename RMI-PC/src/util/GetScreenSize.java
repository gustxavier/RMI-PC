/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author jonathan
 */
public class GetScreenSize {
    private static Toolkit tk = Toolkit.getDefaultToolkit();
    
    public static Dimension getDimension(){
        return tk.getScreenSize();
    }
    public static int getX(Dimension d){
        return (tk.getScreenSize().width)/2 - d.width/2;
    }
    public static int getY(Dimension d){
        return (tk.getScreenSize().height)/2 - d.height/2;
    }
}
