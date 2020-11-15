/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author SpongeBob
 */
public class ChunkArray {

    private ArrayList<Chunk> array = new ArrayList<Chunk>();

    public ChunkArray(byte[] stream) {
        ByteBuffer buffer = ByteBuffer.wrap(stream);

        Chunk chunk;
        chunk = new Chunk(buffer);
        while (chunk.getCreationResult()) {
            array.add(chunk);
            chunk = new Chunk(buffer);
        }
    }

    public ArrayList<Chunk> getArray() {
        return array;
    }
}
