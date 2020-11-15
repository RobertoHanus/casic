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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author SpongeBob
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        // TODO code application logic here
        
        /* COM Port initialization */
        SerialPort commPort = SerialPort.getCommPort(args[1].toUpperCase());
        commPort.openPort();
        commPort.setComPortParameters(600, 8, 1, 0);
        
        /* Select mode PLAY or REC */
        switch (args[0]) {
            case "play":
                /* Get file size and create a byte array with data */
                InputStream inputStream = new FileInputStream(args[2]);
                int fileSize = (int) (new File(args[2])).length();
                byte[] fileData = new byte[fileSize];
                inputStream.read(fileData);

                /* Creates an array of Chunks */
                ChunkArray chunkArray = new ChunkArray(fileData);

                int i = 1;
                int length = chunkArray.getArray().size();
                for (Chunk chunk : chunkArray.getArray()) {
                    System.out.println("Chunk Type: " + chunk.toString() + " Chunk " + i + " of " + length);
                    if (chunk.toString().equals("data")) {
                        Thread.sleep(chunk.getAux());
                        commPort.writeBytes(chunk.getData(), chunk.getLength());
                        Thread.sleep((int) (chunk.getLength() * 16.666));
                    }
                    i++;
                }
                break;
            case "rec":
                OutputStream outputStream = new FileOutputStream(args[2]);
                
                break;
        }
    }
}
