/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic;

import com.fazecast.jSerialComm.SerialPort;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author SpongeBob
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here

        InputStream inputStream = new FileInputStream(args[0]);
        byte[] fileData = new byte[5000];
        inputStream.read(fileData);

        // Chunk chunk = new Chunk(fileData);
        ChunkArray chunkArray = new ChunkArray(fileData);
    }
}
