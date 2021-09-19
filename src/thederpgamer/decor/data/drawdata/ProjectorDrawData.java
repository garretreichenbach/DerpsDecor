package thederpgamer.decor.data.drawdata;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ByteArrayTagSerializable;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.io.IOException;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/19/2021
 */
public abstract class ProjectorDrawData implements ByteArrayTagSerializable {

    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public boolean changed;
    public transient Transform transform = new Transform();

    public ProjectorDrawData(SegmentPiece segmentPiece) {
        if(segmentPiece != null) {
            indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
            transform = SegmentPieceUtils.getFullPieceTransform(segmentPiece);
        }
        scale = 1;
        offset = new Vector3i();
        rotation = new Vector3i();
        changed = true;
    }

    public ProjectorDrawData() {

    }

    public abstract void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException;
    public abstract void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException;
}