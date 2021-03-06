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
    @SuppressWarnings({"SleepWhileInLoop", "empty-statement"})
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        // TODO code application logic here

        /* COM Port initialization */
        SerialPort commPort = SerialPort.getCommPort(args[1].toUpperCase());
        commPort.openPort();
        commPort.setComPortParameters(600, 8, 1, 0);

        /* Select mode PLAY or REC */
        switch (args[0]) {
            case "play": {
                /* Get file size and create a byte array with data */
                InputStream inputStream = new FileInputStream(args[2]);
                int fileSize = (int) (new File(args[2])).length();
                byte[] fileData = new byte[fileSize];
                inputStream.read(fileData);

                /* Creates an array of Chunks */
                ChunkList chunkList = new ChunkList(fileData);
                               
                int i = 1;
                int length = chunkList.list().size();
                for (Chunk chunk : chunkList.list()) {
                    System.out.println("Chunk Type: " + chunk.toString() + " Chunk " + i + " of " + length);
                    if (chunk.toString().equals("data")) {
                        Thread.sleep(chunk.getAux());
                        commPort.writeBytes(chunk.getData(), chunk.getLength());
                        Thread.sleep((int) (chunk.getLength() * 16.666));
                    }
                    /*
                    if(chunk.getData()!= null) {
                        if(chunk.getData().length >= 3) {
                            if(chunk.getData()[2] == (byte)0xFE)
                            {
                                System.out.println("Reached end of stage. Waiting...");
                                System.out.println("Press any key to continue with next stage.");
                                System.in.read();
                            }
                        }
                    }*/
                    
                    i++;
                }
            }
            break;
            case "info": {
                InputStream inputStream = new FileInputStream(args[1]);
                int fileSize = (int) (new File(args[1])).length();
                byte[] fileData = new byte[fileSize];
                inputStream.read(fileData);

                /* Creates an array of Chunks */
                ChunkList chunkList = new ChunkList(fileData);
                
                System.out.println("Chunks count: " + chunkList.list().size());
                
                int i=0;
                for(Chunk chunk: chunkList.list())
                {
                    try {
                    if(chunk.getData()[2] == (byte)0xFE) i++;
                    }
                    catch(Exception ex) {
                        
                    }
                }
                
                System.out.println("File stages: " + i);
                
            } 
            break;
            case "rec": {
                ChunkList chunkList = new ChunkList();

                Chunk chunk = new Chunk();
                chunk.setType("FUJI");
                chunk.setLength(0);
                chunk.setAux(0);
                chunk.setData(null);
                chunkList.add(chunk);

                chunk = new Chunk();
                chunk.setType("baud");
                chunk.setLength(0);
                chunk.setAux(600);
                chunkList.add(chunk);

                short elapsedInterByte = 0;
                short elapsedIRG = 0;
                boolean endOfFile = false;
                while(elapsedIRG < 30000) {   
                    while (true) {
                        chunk = new Chunk();
                        chunk.setType("data");
                        // chunk.setLength(132);
                        long startIRG = System.currentTimeMillis();
                        
                        elapsedIRG = 0;
                        while (commPort.bytesAvailable() == 0) {
                            Thread.sleep(1);
                            elapsedIRG = (short) (System.currentTimeMillis() - startIRG);
                            if(elapsedIRG > 30000)
                            {
                                endOfFile = true;
                                break;
                            }
                        };                        
                        if(endOfFile) break;
                        
                        chunk.setAux(elapsedIRG + elapsedInterByte);
                        // while (commPort.bytesAvailable() < chunk.getLength());

                        byte[] buffer = new byte[1000];
                        // commPort.readBytes(buffer, chunk.getLength());

                        int j;
                        for (j = 0; j < 1000; j++) {
                            byte[] single = new byte[1];
                            commPort.readBytes(single, 1);
                            buffer[j] = single[0];
                            long startInterByte = System.currentTimeMillis();
                            elapsedInterByte = 0;
                            while (commPort.bytesAvailable() == 0) {
                                Thread.sleep(1);
                                elapsedInterByte = (short) (System.currentTimeMillis() - startInterByte);
                                if (elapsedInterByte > 5000) {
                                    break;
                                }
                            }
                            if (elapsedInterByte > 16.666 * 5) {
                                break;
                            }
                        }

                        chunk.setLength(j + 1);
                        // commPort.readBytes(buffer, chunk.getLength());
                        chunk.setData(buffer);
                        chunkList.add(chunk);
                        System.out.println(String.format("0x%01X", chunk.getData()[2]));
                        // if (chunk.getData()[2] == (byte) 0xFE) {
                        if(elapsedInterByte > 5000) {
                            break;
                        }
                    }
                }

                OutputStream outputStream = new FileOutputStream(args[2]);

                for (Chunk chunk_ : chunkList.list()) {
                    outputStream.write(chunk_.array());
                }
            }
            break;
        }

        commPort.closePort();
    }
}
