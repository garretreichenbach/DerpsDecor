package thederpgamer.decor.utils;

import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import it.unimi.dsi.fastutil.longs.LongIterator;
import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.PositionControl;
import org.schema.game.common.controller.SegmentBufferInterface;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ControlElementMapper;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SegmentData;
import org.schema.game.common.util.FastCopyLongOpenHashSet;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/10/2021
 */
public class SegmentPieceUtils {

	// Rotation Helpers
	private static final Matrix3f mY = new Matrix3f();
	private static final Matrix3f mYB = new Matrix3f();
	private static final Matrix3f mYC = new Matrix3f();
	private static final Matrix3f mX = new Matrix3f();
	private static final Matrix3f mXB = new Matrix3f();
	private static final Matrix3f mXC = new Matrix3f();

	public static void initialize() {
		mY.setIdentity();
		mY.rotY(FastMath.HALF_PI);
		mYB.setIdentity();
		mYB.rotY(-FastMath.HALF_PI);
		mYC.setIdentity();
		mYC.rotY(FastMath.PI);

		mX.setIdentity();
		mX.rotX(FastMath.HALF_PI);
		mXB.setIdentity();
		mXB.rotX(-FastMath.HALF_PI);
		mXC.setIdentity();
		mXC.rotX(FastMath.PI);
	}

	public static int getDistance(SegmentPiece pieceA, SegmentPiece pieceB) {
		return (int) Math.ceil(Vector3fTools.distance(pieceA.x, pieceA.y, pieceA.z, pieceB.x, pieceB.y, pieceB.z));
	}

	public static boolean withinSameAxisAndAngle(SegmentPiece pieceA, SegmentPiece pieceB, float maxAngle) {
		Transform pieceATransform = new Transform();
		pieceA.getTransform(pieceATransform);

		Transform pieceBTransform = new Transform();
		pieceB.getTransform(pieceBTransform);

		Vector3f pieceAForward = new Vector3f();
		Vector3f pieceBForward = new Vector3f();

		Element.getRelativeForward(pieceA.getOrientation(), Element.FRONT, pieceAForward);
		Element.getRelativeForward(pieceB.getOrientation(), Element.FRONT, pieceBForward);

		return pieceAForward.dot(pieceBForward) <= 90 - maxAngle;
	}

	/**
	 * Gets the position of the specified face from the provided SegmentPiece.
	 *
	 * @param segmentPiece The SegmentPiece
	 * @param face The face to find the position of (NOT the orientation of the piece itself)
	 *
	 * @return The position of the specified face
	 */
	public static Vector3f getPieceFacePos(SegmentPiece segmentPiece, int face) {
		Vector3f piecePos = new Vector3f();
		ElementCollection.getPosFromIndex(segmentPiece.getAbsoluteIndex(), piecePos);
		piecePos.x -= SegmentData.SEG_HALF;
		piecePos.y -= SegmentData.SEG_HALF;
		piecePos.z -= SegmentData.SEG_HALF;
		segmentPiece.getSegmentController().getWorldTransform().transform(piecePos);
		Vector3f forward = new Vector3f();
		Element.getRelativeForward(segmentPiece.getOrientation(), face, forward);
		forward.add(piecePos);
		return forward;
	}

	/**
	 * Gets the full Transform of a Projector.
	 *
	 * @param segmentPiece The SegmentPiece
	 *
	 * @return The full transform of the Projector
	 */
	public static Transform getProjectorTransform(SegmentPiece segmentPiece, Vector3i offset, Vector3i rotation, Transform out) {
		if(out == null) out = new Transform();
		out.setIdentity();
		segmentPiece.getTransform(out);
		ElementCollection.getPosFromIndex(segmentPiece.getAbsoluteIndex(), out.origin);
		out.origin.x -= SegmentData.SEG_HALF;
		out.origin.y -= SegmentData.SEG_HALF;
		out.origin.z -= SegmentData.SEG_HALF;

		float sNormalDir = 0.51f;
		float sVertical = 0.5f;
		float sHorizontal = 0.5f;

		out.origin.add(offset.toVector3f());
		Quat4f currentRot = new Quat4f();
		out.getRotation(currentRot);
		Quat4f addRot = new Quat4f();
		QuaternionUtil.setEuler(addRot, rotation.y / 100.0f, rotation.z / 100.0f, rotation.x / 100.0f);
		currentRot.mul(addRot);
		MathUtils.roundQuat(currentRot);
		out.setRotation(currentRot);

		int orientation = segmentPiece.getOrientation();
		switch(orientation) {
			case (Element.FRONT):
				out.basis.mul(mYC);
				out.origin.x -= sHorizontal;
				out.origin.y += sVertical;
				out.origin.z += sNormalDir;
				break;
			case (Element.BACK):
				out.origin.x += sHorizontal;
				out.origin.y += sVertical;
				out.origin.z -= sNormalDir;
				break;
			case (Element.TOP):
				out.basis.mul(mX);
				out.origin.x += sHorizontal;
				out.origin.y += sNormalDir;
				out.origin.z += sVertical;
				break;
			case (Element.BOTTOM):
				out.basis.mul(mYC);
				out.basis.mul(mXB);
				out.origin.x -= sHorizontal;
				out.origin.y -= sNormalDir;
				out.origin.z += sVertical;
				break;
			case (Element.RIGHT):
				out.basis.mul(mY);
				out.origin.x -= sNormalDir;
				out.origin.y += sVertical;
				out.origin.z -= sHorizontal;
				break;
			case (Element.LEFT):
				out.basis.mul(mYB);
				out.origin.x += sNormalDir;
				out.origin.y += sVertical;
				out.origin.z += sHorizontal;
				break;
		}
		segmentPiece.getSegmentController().getWorldTransform().transform(out.origin);
		return out;
	}

	/**
	 * Gets the full Transform of a SegmentPiece.
	 *
	 * @param segmentPiece The SegmentPiece
	 *
	 * @return The full transform of the SegmentPiece
	 */
	public static Transform getPieceTransform(SegmentPiece segmentPiece, Vector3i offset, Vector3i rotation) {
		Transform transform = new Transform();
		transform.setIdentity();
		segmentPiece.getTransform(transform);
		ElementCollection.getPosFromIndex(segmentPiece.getAbsoluteIndex(), transform.origin);
		transform.origin.x -= SegmentData.SEG_HALF;
		transform.origin.y -= SegmentData.SEG_HALF;
		transform.origin.z -= SegmentData.SEG_HALF;

		transform.origin.add(offset.toVector3f());
		Quat4f currentRot = new Quat4f();
		transform.getRotation(currentRot);
		Quat4f addRot = new Quat4f();
		QuaternionUtil.setEuler(addRot, rotation.y / 100.0f, rotation.z / 100.0f, rotation.x / 100.0f);
		currentRot.mul(addRot);
		MathUtils.roundQuat(currentRot);
		transform.setRotation(currentRot);

		int orientation = segmentPiece.getOrientation();
		switch(orientation) {
			case (Element.FRONT):
				transform.basis.mul(mYC);
				break;
			case (Element.BACK):
				break;
			case (Element.TOP):
				transform.basis.mul(mX);
				break;
			case (Element.BOTTOM):
				transform.basis.mul(mYC);
				transform.basis.mul(mXB);
				break;
			case (Element.RIGHT):
				transform.basis.mul(mY);
				break;
			case (Element.LEFT):
				transform.basis.mul(mYB);
				break;
		}
		segmentPiece.getSegmentController().getWorldTransform().transform(transform.origin);
		return transform;
	}

	public static boolean isControlling(SegmentPiece controller, SegmentPiece controlled) {
		return getControlledPieces(controller).contains(controlled);
	}

	public static ArrayList<SegmentPiece> getControlledPiecesMatching(SegmentPiece segmentPiece, short type) {
		ArrayList<SegmentPiece> controlledPieces = new ArrayList<>();
		PositionControl control = segmentPiece.getSegmentController().getControlElementMap().getDirectControlledElements(type, segmentPiece.getAbsolutePos(new Vector3i()));
		if(control != null) {
			for(long l : control.getControlMap().toLongArray()) {
				SegmentPiece p = segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(l);
				if(p != null && p.getType() == type) controlledPieces.add(p);
			}
		}
		return controlledPieces;
	}

	public static ArrayList<SegmentPiece> getControlledPieces(SegmentPiece segmentPiece) {
		ArrayList<SegmentPiece> controlledPieces = new ArrayList<>();
		ControlElementMapper controlElementMapper = segmentPiece.getSegmentController().getControlElementMap().getControllingMap();
		if(controlElementMapper.containsKey(segmentPiece.getAbsoluteIndex())) {
			for(FastCopyLongOpenHashSet longs : controlElementMapper.get(segmentPiece.getAbsoluteIndex()).values()) {
				LongIterator longIterator = longs.iterator();
				while(longIterator.hasNext()) {
					try {
						controlledPieces.add(segmentPiece.getSegmentController().getSegmentBuffer().getPointUnsave(longIterator.nextLong()));
					} catch(Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
		return controlledPieces;
	}

	public static ArrayList<SegmentPiece> getControllingPieces(SegmentPiece segmentPiece) { // Todo
		ArrayList<SegmentPiece> controllingPieces = new ArrayList<>();
		return controllingPieces;
	}

	public static SegmentPiece getFirstMatchingAdjacent(SegmentPiece segmentPiece, short type) {
		ArrayList<SegmentPiece> matching = getMatchingAdjacent(segmentPiece, type);
		if(matching.isEmpty()) return null;
		else return matching.get(0);
	}

	public static ArrayList<SegmentPiece> getMatchingAdjacent(SegmentPiece segmentPiece, short type) {
		ArrayList<SegmentPiece> matchingAdjacent = new ArrayList<>();
		SegmentBufferInterface buffer = segmentPiece.getSegmentController().getSegmentBuffer();
		Vector3i pos = new Vector3i(segmentPiece.getAbsolutePos(new Vector3i()));
		Vector3i[] offsets = getAdjacencyOffsets(pos);
		for(Vector3i offset : offsets) {
			if(buffer.existsPointUnsave(offset)) {
				SegmentPiece piece = buffer.getPointUnsave(offset);
				if(piece.getType() == type) matchingAdjacent.add(piece);
			}
		}
		return matchingAdjacent;
	}

	private static Vector3i[] getAdjacencyOffsets(Vector3i absPos) {
		return new Vector3i[] {new Vector3i(absPos.x - 1, absPos.y, absPos.z), new Vector3i(absPos.x + 1, absPos.y, absPos.z), new Vector3i(absPos.x, absPos.y - 1, absPos.z), new Vector3i(absPos.x, absPos.y + 1, absPos.z), new Vector3i(absPos.x, absPos.y, absPos.z - 1), new Vector3i(absPos.x, absPos.y, absPos.z + 1)};
	}
}
