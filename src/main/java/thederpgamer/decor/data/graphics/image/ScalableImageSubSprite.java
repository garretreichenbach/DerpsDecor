package thederpgamer.decor.data.graphics.image;

import com.bulletphysics.linearmath.Transform;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.TransformableSubSprite;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/16/2021
 */
public class ScalableImageSubSprite implements TransformableSubSprite {
	private float scale;
	private Transform transform;

	public ScalableImageSubSprite(float scale, Transform transform) {
		this.scale = scale;
		this.transform = transform;
	}

	@Override
	public float getScale(long l) {
		return scale;
	}

	@Override
	public int getSubSprite(Sprite sprite) {
		return 0;
	}

	@Override
	public boolean canDraw() {
		return true;
	}

	@Override
	public Transform getWorldTransform() {
		return transform;
	}
}
