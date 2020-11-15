/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic;

import com.fazecast.jSerialComm.SerialPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
        
        /* Get file size and create a byte array with data */
        InputStream inputStream = new FileInputStream(args[0]);
        int fileSize =(int)(new File(args[0])).length();
        byte[] fileData = new byte[fileSize];
        inputStream.read(fileData);
                
        /* Creates an array of Chunks */
        ChunkArray chunkArray = new ChunkArray(fileData);
        
        int i=1;
        int length = chunkArray.getArray().size();
        for(Chunk chunk : chunkArray.getArray())
        {
            System.out.println("Chunk Type: " + chunk.toString() + " Chunk " + i + " of " + length);
            i++;
        }
    }
}
