package thederpgamer.decor.data.drawdata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.manager.LogManager;
import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData extends ProjectorDrawData {

    public String text;
    public String color;
    public transient GUITextOverlay textOverlay;

    public TextProjectorDrawData(SegmentPiece segmentPiece) {
        super(segmentPiece);
    }

    public TextProjectorDrawData(PacketReadBuffer packetReadBuffer) {
        super();
        try {
            onTagDeserialize(packetReadBuffer);
        } catch(IOException exception) {
            LogManager.logException("Using default values because something went wrong while trying to deserialize text projector data", exception);
            scale = 1;
            offset = new Vector3i();
            rotation = new Vector3i();
            changed = true;
        }
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        //super.onTagSerialize(packetWriteBuffer);
        packetWriteBuffer.writeLong(indexAndOrientation);
        packetWriteBuffer.writeVector(offset);
        packetWriteBuffer.writeVector(rotation);
        packetWriteBuffer.writeInt(scale);
        packetWriteBuffer.writeBoolean(changed);
        packetWriteBuffer.writeString(text);
        packetWriteBuffer.writeString(color);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        //super.onTagDeserialize(packetReadBuffer);
        offset = packetReadBuffer.readVector();
        rotation = packetReadBuffer.readVector();
        scale = packetReadBuffer.readInt();
        changed = packetReadBuffer.readBoolean();
        text = packetReadBuffer.readString();
        color = packetReadBuffer.readString();
    }
}