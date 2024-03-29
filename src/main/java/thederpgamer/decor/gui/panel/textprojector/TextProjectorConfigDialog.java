package thederpgamer.decor.gui.panel.textprojector;

import api.common.GameClient;
import api.utils.gui.GUIInputDialog;
import api.utils.gui.GUIInputDialogPanel;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;
import thederpgamer.decor.data.drawdata.TextProjectorDrawData;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.TextProjectorModule;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/15/2021
 */
public class TextProjectorConfigDialog extends GUIInputDialog {
	private SegmentPiece segmentPiece;

	public void setSegmentPiece(SegmentPiece segmentPiece) {
		this.segmentPiece = segmentPiece;
		ManagedUsableSegmentController<?> segmentController = (ManagedUsableSegmentController<?>) segmentPiece.getSegmentController();
		TextProjectorModule module = (TextProjectorModule) segmentController.getManagerContainer().getModMCModule(ElementManager.getBlock("Text Projector").getId());
		TextProjectorDrawData drawData = (TextProjectorDrawData) module.getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
		setDefaults(drawData);
		getConfigPanel().setText(drawData.text);
		getConfigPanel().setColor(drawData.color);
		getConfigPanel().setXOffset(drawData.offset.x);
		getConfigPanel().setYOffset(drawData.offset.y);
		getConfigPanel().setZOffset(drawData.offset.z);
		getConfigPanel().setXRot(drawData.rotation.x);
		getConfigPanel().setYRot(drawData.rotation.y);
		getConfigPanel().setZRot(drawData.rotation.z);
		getConfigPanel().setScaleSetting(drawData.scale);
		getConfigPanel().setHolographic(drawData.holographic);
	}

	private void setDefaults(TextProjectorDrawData drawData) {
		if(drawData.offset == null) drawData.offset = new Vector3i();
		if(drawData.rotation == null) drawData.rotation = new Vector3i();
		if(drawData.text == null) drawData.text = "";
		if(drawData.color == null) drawData.color = "FFFFFF";
	}

	private TextProjectorConfigPanel getConfigPanel() {
		return (TextProjectorConfigPanel) getInputPanel();
	}

	@Override
	public GUIInputDialogPanel createPanel() {
		return new TextProjectorConfigPanel(getState(), this);
	}

	@Override
	public void callback(GUIElement callingElement, MouseEvent mouseEvent) {
		if(!isOccluded() && mouseEvent.pressedLeftMouse()) {
			if(callingElement.getUserPointer() != null) {
				switch((String) callingElement.getUserPointer()) {
					case "X":
					case "CANCEL":
						deactivate();
						break;
					case "OK":
						TextProjectorDrawData drawData = getModule().getDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()));
						drawData.text = getConfigPanel().getText();
						drawData.color = getConfigPanel().getColor();
						drawData.offset = new Vector3i(getConfigPanel().getXOffset(), getConfigPanel().getYOffset(), getConfigPanel().getZOffset());
						drawData.rotation = new Vector3i(getConfigPanel().getXRot(), getConfigPanel().getYRot(), getConfigPanel().getZRot());
						drawData.scale = getConfigPanel().getScaleSetting();
						drawData.holographic = getConfigPanel().getHolographic();
						drawData.changed = true;
						getModule().setDrawData(ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation()), drawData);
						deactivate();
						break;
				}
			}
		}
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		GameClient.getClientState().getGlobalGameControlManager().getIngameControlManager().getPlayerGameControlManager().getPlayerIntercationManager().suspend(false);
	}

	private TextProjectorModule getModule() {
		if(segmentPiece.getSegmentController().getType().equals(SimpleTransformableSendableObject.EntityType.SHIP)) {
			return (TextProjectorModule) ((Ship) segmentPiece.getSegmentController()).getManagerContainer().getModMCModule(ElementManager.getBlock("Text Projector").getId());
		} else if(segmentPiece.getSegmentController().getType().equals(SimpleTransformableSendableObject.EntityType.SPACE_STATION)) {
			return (TextProjectorModule) ((SpaceStation) segmentPiece.getSegmentController()).getManagerContainer().getModMCModule(ElementManager.getBlock("Text Projector").getId());
		} else return null;
	}
}
