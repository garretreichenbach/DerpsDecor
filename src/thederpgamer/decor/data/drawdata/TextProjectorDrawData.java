package thederpgamer.decor.data.drawdata;

import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.forms.gui.GUITextOverlay;
import thederpgamer.decor.utils.SegmentPieceUtils;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/06/2021
 */
public class TextProjectorDrawData {

    public long indexAndOrientation;
    public Vector3i offset;
    public Vector3i rotation;
    public int scale;
    public boolean changed;
    public String text;
    public String color;

    public transient Transform transform;
    public transient GUITextOverlay textOverlay;

    public TextProjectorDrawData(long indexAndOrientation, Vector3i offset, Vector3i rotation, int scale, boolean changed, String text, String color) {
        this.indexAndOrientation = indexAndOrientation;
        this.offset = offset;
        this.rotation = rotation;
        this.scale = scale;
        this.changed = changed;
        this.text = text;
        this.color = color;
        this.transform = new Transform();
    }

    public TextProjectorDrawData(SegmentPiece segmentPiece) {
        scale = 1;
        offset = new Vector3i();
        rotation = new Vector3i();
        changed = true;
        text = "";
        color = "FFFFFF";
        if(segmentPiece != null) {
            indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
            transform = SegmentPieceUtils.getProjectorTransform(segmentPiece, offset, rotation);
        }
    }
}
