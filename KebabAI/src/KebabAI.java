import structs.FrameData;
import structs.GameData;
import structs.Key;
import gameInterface.AIInterface; 
import commandcenter.CommandCenter;

public class KebabAI implements AIInterface {
	Key inputKey;
	boolean playerNumber;
	FrameData frameData;
	CommandCenter cc;
	
	@Override
	public int initialize(GameData gameData, boolean playerNumber){
		this.playerNumber = playerNumber;
		this.inputKey = new Key();
		cc = new CommandCenter();
		frameData = new FrameData();
		return 0;
	}
	
	@Override
	public void getInformation(FrameData frameData){ 
		this.frameData = frameData;
		cc.setFrameData(this.frameData, playerNumber);
	}
	
	@Override
	public void processing() {
		if (!frameData.getEmptyFlag() && frameData.getRemainingTime() > 0) { 
			if (cc.getskillFlag()) {
				inputKey = cc.getSkillKey();
			} else {
			inputKey.empty();
				cc.skillCancel();
				if (cc.getDistanceY() == 0) {
					// If enemy is on the ground.
					if (cc.getDistanceX() <= 50) {
						cc.commandCall("4 _ B");
						System.out.println("punching");
					} else if (cc.getDistanceX() <= 100) {
						cc.commandCall("CROUCH_FB");
						System.out.println("Crouch punch");
					} else if (cc.getDistanceX() <= 300) { 
						cc.commandCall("6");
						System.out.println("Forward dash");
					} else if (cc.getDistanceX() <= 500) { 
						cc.commandCall("2 3 6 _ A");
						System.out.println("Simple projectile");
					} else if (cc.getDistanceX() > 500 ) { 
						cc.commandCall("9");
						System.out.println("Jump");
					}
				} else {
					if (cc.getDistanceX() <= 100) {
						cc.commandCall("6 2 3 _ A");
						System.out.println("high kick?");
					} else if (cc.getDistanceX() > 100) { 
						cc.commandCall("6 _ A");
						System.out.println("Stand");
					}
				}
				 
			}
		}
	}
	
	@Override
	public Key input(){ 
		return inputKey;
	}
	
	@Override
	public void close(){
	}
	
	@Override
	public String getCharacter(){
		return CHARACTER_ZEN;
	}
}