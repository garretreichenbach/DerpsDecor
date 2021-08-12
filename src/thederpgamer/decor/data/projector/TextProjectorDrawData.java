package thederpgamer.decor.data.projector;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
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
        text = "";
        color = "FFFFFF";
    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        super.onTagSerialize(packetWriteBuffer);
        packetWriteBuffer.writeString(text);
        packetWriteBuffer.writeString(color);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        super.onTagDeserialize(packetReadBuffer);
        text = packetReadBuffer.readString();
        color = packetReadBuffer.readString();
    }
}