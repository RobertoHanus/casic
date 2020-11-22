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
                    i++;
                }
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

                for (int i = 0; i < Integer.parseInt(args[3]); i++) {   // Stages
                    while (true) {
                        chunk = new Chunk();
                        chunk.setType("data");
                        // chunk.setLength(132);
                        long startIRG = System.currentTimeMillis();
                        while (commPort.bytesAvailable() == 0) {
                            Thread.sleep(1);
                        };
                        short elapsedIRG = (short) (System.currentTimeMillis() - startIRG);
                        chunk.setAux(elapsedIRG);
                        // while (commPort.bytesAvailable() < chunk.getLength());
                        
                        byte[] buffer = new byte[1000];
                        // commPort.readBytes(buffer, chunk.getLength());
                        
                        int j;
                        for (j = 0; j < 1000; j++) {
                            byte[] single = new byte[1];
                            commPort.readBytes(single, 1);
                            buffer[j] = single[0];
                            long startInterByte = System.currentTimeMillis();
                            short elapsedEndOfStage = 0;
                            while (commPort.bytesAvailable() == 0) {
                                Thread.sleep(1);
                                elapsedEndOfStage = (short) (System.currentTimeMillis() - startInterByte);
                                if(elapsedEndOfStage > 5000)
                                {
                                    break;
                                }
                            }
                            short elapsedInterByte = (short) (System.currentTimeMillis() - startInterByte);
                            if(elapsedInterByte > 50)
                            {
                                break;
                            }
                        }
                        
                        chunk.setLength(j + 1);
                        // commPort.readBytes(buffer, chunk.getLength());
                        chunk.setData(buffer);
                        chunkList.add(chunk);
                        System.out.println(String.format("0x%01X", chunk.getData()[2]));
                        if (chunk.getData()[2] == (byte) 0xFE) {
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
