/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package casic;

import java.nio.ByteBuffer;

/**
 *
 * @author SpongeBob
 */
public class Chunk {

    /* Chunk header */
    private byte[] type = new byte[4];
    private byte[] length = new byte[2];
    private byte[] aux = new byte[2];

    /* Chunk data */
    private byte[] data;

    /* Chunk length including data and header */
    private short entireChunkLength;

    /* Can create Chunk */
    private boolean creationResult = false;

    public byte[] getType() {
        return type;
    }

    public short getLength() {
        return ByteBuffer.wrap(length).getShort();
    }

    public short getAux() {
        return ByteBuffer.wrap(aux).getShort();
    }

    public byte[] getData() {
        return data;
    }

    public short getEntireChunkLength() {
        return entireChunkLength;
    }

    public Chunk(ByteBuffer buffer) {
        creationResult = false;
        if (buffer.remaining() >= 8) {
            buffer.get(type);
            buffer.get(length);
            length = Utils.LittleToBigEndian(length);
            buffer.get(aux);
            aux = Utils.LittleToBigEndian(aux);
            if (buffer.remaining() >= getLength()) {
                data = new byte[getLength()];
                buffer.get(data);
                creationResult = true;
                entireChunkLength = (short) (getLength() + 8);
            }
        }
    }

    public boolean getCreationResult() {
        return creationResult;
    }
}
