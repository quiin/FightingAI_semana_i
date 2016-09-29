import commandcenter.CommandCenter;
import enumerate.Action;
import gameInterface.AIInterface;
import structs.CharacterData;
import structs.FrameData;
import structs.GameData;
import structs.Key;

public class KebabAI implements AIInterface {
	Key inputKey;
	boolean playerNumber;
	CommandCenter cc;

	GameData gameData;
	FrameData frameData;

	CharacterData opponent;
	CharacterData myCharacter;
	
	// Global status variables
	boolean finishedJumpingAfterStepBack;
	int womboCombo1;
	boolean isInWomboCombo1;

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		this.inputKey = new Key();
		cc = new CommandCenter();

		frameData = new FrameData();
		this.gameData = gameData;
		
		womboCombo1 = 0;
		isInWomboCombo1 = false;

		return 0;
	}

	@Override
	public void getInformation(FrameData frameData) {
		this.frameData = frameData;
		cc.setFrameData(this.frameData, playerNumber);

		if (playerNumber) {
			myCharacter = frameData.getP1();
			opponent = frameData.getP2();
		} else {
			myCharacter = frameData.getP2();
			opponent = frameData.getP1();
		}
	}

	@Override
	public void processing() {
		if (!frameData.getEmptyFlag() && frameData.getRemainingTime() > 0) {
			if (cc.getskillFlag()) {
				inputKey = cc.getSkillKey();
			} else {
				inputKey.empty();
				cc.skillCancel();
				
				if (isInWomboCombo1) {
					womboCombo1();
					return;
				}

				if (!stepBackIfNeeded()) {
					if (myCharacter.getEnergy() >= 300) {
						cc.commandCall("2 3 6 _ C");
					} else {
						cc.commandCall("2 1 4 _ A");
					}
				}
				
				int distance = cc.getDistanceX();
				int enemyVerticalDistance = cc.getEnemyY();

			}
		}
	}

	@Override
	public Key input() {
		return inputKey;
	}

	@Override
	public void close() {
	}

	@Override
	public String getCharacter() {
		return CHARACTER_ZEN;
	}

	/* Combat methods */
	// With how much they USE and how much is OBTAINED when
	// it connects after method name.
	
	// Combos
	
	public void womboCombo1() {
		cc.skillCancel();
		switch (womboCombo1) {
		case 0:
			cc.commandCall("A");
			break;
		case 1:
			cc.commandCall("B");
			break;
		case 2:
			cc.commandCall("3 _ B");
			womboCombo1 = -1;
			isInWomboCombo1 = false;
			break;
		}
		womboCombo1++;
	}

	// Blocking/avoiding

	public boolean stepBackIfNeeded() {
		// 4 states: cornered, cornering, face-to-face, free (no stepback
		// needed)
		
		if (characterCornered(myCharacter)) {
			// MyCharacter is cornered.
			cc.commandCall(Action.FOR_JUMP.name());
			return true;
		} else if (characterCornered(opponent)) {
			// Opponent is cornered.
			System.out.println("Opponent cornered");
			if (Math.random()*10 < 5) {
				cc.commandCall("A");
			} else {
				cc.commandCall("B");
			}
			return true;
		} else if (characterFaceToFace()) {
			// Face to face but not cornered
			System.out.println("Starting wombo combo 1");
			womboCombo1();
//			le pican 8 -> salto
//			el pedo es q tienen q checar hacia donde está viendo el dude
//			porque si le ponen q se vaya para atras pero no está viendo balh blah -> pendeje
//			8 6 -> salto y atrás
			//cc.commandCall(Action.BACK_STEP.name());
			System.out.println("We are face to face");
			return true;
		}
		
		System.out.println("not in the danger zone");
		return false;
	}

	public boolean characterFaceToFace() {
		int characterHitBoxX = myCharacter.right - myCharacter.left;
		return cc.getDistanceX() < characterHitBoxX*3;
	}

	public boolean characterCornered(CharacterData c) {
		int characterHitBoxX = c.right - c.left;
		int cornerThreshold = characterHitBoxX * 3;
		if ((characterFaceToFace() && c.isFront() && c.left <= cornerThreshold) || (characterFaceToFace() && !c.isFront() && this.gameData.getStageXMax()- c.right <= characterHitBoxX)) {
			return true;
		}  
		return false;
	}

	// Ground attacks

	public void throwA_5_5() {
		cc.commandCall("4 _ A");
	}

	public void throwB_20_50() {
		cc.commandCall("4 _ B");
	}

	public void standA_0_2() {
		cc.commandCall("A");
	}

	public void standB_0_5() {
		cc.commandCall("B");
	}

	public void crouchA_0_2() {
		cc.commandCall("2 _ A");
	}

	public void crouchB_0_5() {
		cc.commandCall("2 _ B");
	}

	public void standFA_0_2() {
		cc.commandCall("6 _ A");
	}

	public void standFB_0_10() {
		cc.commandCall("6 _ B");
	}

	public void crouchFA_0_2() {
		cc.commandCall("3 _ A");
	}

	public void crouchFB_0_5() {
		cc.commandCall("3 _ B");
	}

	public void projectileA_0_2() {
		cc.commandCall("2 3 6 _ A");
	}

	public void projectileB_30_15() {
		cc.commandCall("2 3 6 _ B");
	}

	public void ultimate_300_30() {
		cc.commandCall("2 3 6 _ C");
	}

	public void upperCut_0_5() {
		cc.commandCall("6 2 3 _ A");
	}

	public void lowKick_50_15() {
		cc.commandCall("6 2 3 _ B");
	}

	public void forwardJump_0_5() {
		cc.commandCall("2 1 4 _ A");
	}

	public void standUNCLEAR_50_15() {
		cc.commandCall("2 1 4 _ B");
	}

	// Air attacks

	public void airA_0_5() {
		cc.commandCall("A");
	}

	public void airB_0_10() {
		cc.commandCall("B");
	}

	public void airDA_5_5() {
		cc.commandCall("2 _ A");
	}

	public void airDB_5_10() {
		cc.commandCall("2 _ B");
	}

	public void airFA_0_5() {
		cc.commandCall("6 _ A");
	}

	public void airFB_0_10() {
		cc.commandCall("6 _ B");
	}

	public void airUA_0_5() {
		cc.commandCall("8 _ A");
	}

	public void airUB_0_10() {
		cc.commandCall("8 _ B");
	}

	public void airProjectileA_0_0() {
		cc.commandCall("2 3 6 _ A");
	}

	public void airProjectileB_50_15() {
		cc.commandCall("2 3 6 _ B");
	}

	public void airDFA_10_5() {
		cc.commandCall("6 2 3 _ A");
	}

	public void airDFB_40_15() {
		cc.commandCall("6 2 3 _ B");
	}

	public void airBA_10_5() {
		cc.commandCall("2 1 4 _ A");
	}

	public void airBB_50_15() {
		cc.commandCall("2 1 4 _ B");
	}

}