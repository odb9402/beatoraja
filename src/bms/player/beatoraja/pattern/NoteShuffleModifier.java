package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import bms.model.BMSModel;
import bms.model.LongNote;
import bms.model.Mode;
import bms.model.NormalNote;
import bms.model.Note;
import bms.model.TimeLine;
import bms.player.beatoraja.PlayerConfig;

/**
 * �궭�궎�깲�꺀�궎�꺍�뜕鵝띲겎�깕�꺖�깂�굮�뀯�굦�쎘�걟�굥�걼�굙�겗�궚�꺀�궧竊�
 *
 * @author exch
 */
public class NoteShuffleModifier extends PatternModifier {
	private static final PlayerConfig config = playerConfig;
	/**
	 * �궭�궎�깲�꺀�궎�꺍驪롢겓�깕�꺖�깂�굮�꺀�꺍���깲�겓�뀯�굦�쎘�걟�굥
	 */
	public static final int S_RANDOM = 0;
	/**
	 * �닜�쐿�겗訝╉겧�쎘�걟�굮�깧�꺖�궧�겓�곮왉�뿃�듁�겓訝╉겧�쎘�걟�굥
	 */
	public static final int SPIRAL = 1;
	/**
	 * �깕�꺖�깂�굮�궧�궚�꺀�긿�긽�꺃�꺖�꺍�겓�썓榮꾠걲�굥
	 */
	public static final int ALL_SCR = 2;
	/**
	 * S-RANDOM�겓潁��ｃ걣璵드뒟�씎�겒�걚�굠�걝�겓�뀓營��걲�굥
	 */
	public static final int H_RANDOM = 3;
	/**
	 * �궧�궚�꺀�긿�긽�꺃�꺖�꺍�굮�맜�굙�걼S-RANDOM
	 */
	public static final int S_RANDOM_EX = 4;

	/**
	 * 7to9
	 */
	public static final int SEVEN_TO_NINE = 100;

	private NoteShuffleAction noteShuffleAction;
	
	private int modifyType;
	/**
	 * 轝▲겗TimeLine罌쀥뒥�늽(SPIRAL�뵪)
	 */
	private int inc;

	/**
	 * �ｆ돀�걮�걤�걚��(ms)(H-RANDOM�뵪)
	 */
	private int hranThreshold = 125;

	public NoteShuffleModifier(int type) {
		super(type >= ALL_SCR ? 1 : 0);
		this.modifyType = type;
	}

	/**
	 * �ｆ돀�썮�빊(PMS ALLSCR�뵪)
	 */
	private static int[] laneRendaCount;

	@Override
	public List<PatternModifyLog> modify(BMSModel model) {
		List<PatternModifyLog> log = new ArrayList<PatternModifyLog>();
		Mode mode = model.getMode();
		int lanes = mode.key;
		int[] random = new int[0];
		int[] ln = new int[lanes];
		int[] lastNoteTime = new int[lanes];
		int[] endLnNoteTime = new int[lanes];
		int scratchIndex = 0;
		
		Arrays.fill(ln, -1);
		Arrays.fill(lastNoteTime, -100);
		Arrays.fill(endLnNoteTime, -1);
		laneRendaCount = new int[lanes];
		Arrays.fill(laneRendaCount, 0);
		
		calculateHranThreshold();
		
		for (TimeLine tl : model.getAllTimeLines()) {
			if (tl.existNote() || tl.existHiddenNote()) {
				Note[] notes = new Note[lanes];
				Note[] hnotes = new Note[lanes];
				for (int i = 0; i < lanes; i++) {
					notes[i] = tl.getNote(i);
					hnotes[i] = tl.getHiddenNote(i);
				}
				int[] keys;
				
				switch (modifyType) {
				case S_RANDOM:
					random = modifySRANDOM(mode, ln, lastNoteTime, tl, notes);
					break;
					
				case SPIRAL:
					random = modifySPIRAL(mode, random, ln);
					break;
					
				case ALL_SCR:
					if(mode == Mode.POPN_9K) {
						noteShuffleAction = new RendaShuffle();
						keys = getKeys(mode, false);
						random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln, notes, lastNoteTime, tl.getTime(), hranThreshold, 60, laneRendaCount)
								: keys;
						break;
					}
					// �궧�궚�꺀�긿�긽�꺃�꺖�꺍�걣�꽒�걚�겒�굢鵝뺛굚�걮�겒�걚
					if (mode.scratchKey.length == 0) {
						break;
					}
					
					if (mode.player == 2 && mode == Mode.KEYBOARD_24K_DOUBLE) {
						// TODO 24k-DP�겓野얍퓶
						break;
					}
					
					random = new int[mode.key];
					for (int i = 0; i < random.length; i++) {
						random[i] = i;
					}

					/*
					 * �ｇ슼�걮�걤�걚��
					 */
					int scratchInterval = 40;

					scratchIndex = modifyALLSCR(mode, random, ln, lastNoteTime, scratchIndex, tl, notes,
							scratchInterval);
					break;

				case H_RANDOM:
					random = modifyHRANDOM(mode, ln, lastNoteTime, tl, notes);
					break;
				case S_RANDOM_EX:
					random = modifySRANDOMEX(mode, ln, lastNoteTime, tl, notes);
					break;
				case SEVEN_TO_NINE:
					random = modifySEVENTONINE(mode, ln, lastNoteTime, tl, notes);
					break;

				}

				for (int i = 0; i < lanes; i++) {
					final int mod = i < random.length ? random[i] : i;
					Note n = notes[mod];
					Note hn = hnotes[mod];
					if (n instanceof LongNote) {
						LongNote ln2 = (LongNote) n;
						if (ln2.isEnd() && tl.getTime() == endLnNoteTime[i]) {
							tl.setNote(i, n);
							ln[i] = -1;
							endLnNoteTime[i] = -1;
						} else {
							tl.setNote(i, n);
							ln[i] = mod;
							if (!ln2.isEnd()) {
								endLnNoteTime[i] = ln2.getPair().getTime();
							}
							lastNoteTime[i] = tl.getTime();
						}
					} else {
						tl.setNote(i, n);
						if (n != null) {
							lastNoteTime[i] = tl.getTime();
						}
					}
					tl.setHiddenNote(i, hn);
				}
				
				log.add(new PatternModifyLog(tl.getSection(), random));
			}
		}
		return log;
	}
	
	private int[] modifySEVENTONINE(Mode mode, int[] ln, int[] lastNoteTime, TimeLine tl, Note[] notes) {
		int[] random;
		int[] keys;
		keys = getKeys(mode, true);
		random = keys.length > 0 ? sevenToNine(keys, ln,
				notes, lastNoteTime, tl.getTime(), hranThreshold)
				: keys;
		return random;
	}


	private int[] modifyHRANDOM(Mode mode, int[] ln, int[] lastNoteTime, TimeLine tl, Note[] notes) {
		int[] random;
		int[] keys;
		keys = getKeys(mode, false);
		noteShuffleAction = new TimeBasedShuffle();
		random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln, notes, lastNoteTime, tl.getTime(), hranThreshold, -1, laneRendaCount)
				: keys;
		return random;
	}


	private int[] modifySRANDOMEX(Mode mode, int[] ln, int[] lastNoteTime, TimeLine tl, Note[] notes) {
		int[] random;
		int[] keys;
		keys = getKeys(mode, true);
		if(mode == Mode.POPN_9K) {
			noteShuffleAction = new NoMurioshiShuffle();
			random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln,
					notes, lastNoteTime, tl.getTime(), hranThreshold, -1, laneRendaCount)
					: keys;
		} else {
			noteShuffleAction = new TimeBasedShuffle();
			random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln,
					notes, lastNoteTime, tl.getTime(), 40, -1, laneRendaCount)
					: keys;
		}
		return random;
	}


	private int modifyALLSCR(Mode mode, int[] random, int[] ln, int[] lastNoteTime, int scratchIndex, TimeLine tl,
			Note[] notes, int scratchInterval) {
		int[] keys;
		// Scratch�꺃�꺖�꺍�걣筽뉑빊�걗�굥�졃�릦�겘�젂濚겹굤�겓�뀓營��걬�굦�굥�굠�걝�겓 (24key野얍퓶)
		if (mode.player == 1) {
			// �궥�꺍�궛�꺂�깤�꺃�꺖�셽
			keys = getKeys(mode, true);
			int keyInterval = hranThreshold;
			ArrayList<Integer> original, assign, note, other, primary, tate, sckey;
			original = new ArrayList<Integer>(keys.length);
			assign = new ArrayList<Integer>(keys.length);
			note = new ArrayList<Integer>(keys.length);
			other = new ArrayList<Integer>(keys.length);
			primary = new ArrayList<Integer>(keys.length);
			tate = new ArrayList<Integer>(keys.length);
			sckey = new ArrayList<Integer>(mode.scratchKey.length);

			for (int lane = 0; lane < keys.length; lane++) {
				original.add(keys[lane]);
				assign.add(keys[lane]);
			}
			
			for (int sc = 0; sc < mode.scratchKey.length; sc++) {
				sckey.add(mode.scratchKey[sc]);
			}
			
			noteShuffleAction = new ExtraShuffle();
			noteShuffleAction.removeActivatedLane( keys, ln, assign, null, original, random);

			// �뀇�겗�꺃�꺖�꺍�굮�깕�꺖�깂�겗耶섇쑉�겎�늽窈�
			noteShuffleAction.classifyOriginalLane(notes, original, note, other, false);
			
			// 

			// �쑋�궋�궢�궎�꺍�꺃�꺖�꺍�굮�늽窈� 1.轝▲겓�뀓營��걲�굥�궧�궚�꺀�긿�긽�꺃�꺖�꺍�겎�겒�걚 2.潁��ｃ걣�쇇�뵟�걲�굥
			while (!assign.isEmpty()) {
				if ((
						sckey.contains(assign.get(0)) && assign.get(0) != sckey.get(scratchIndex))
						|| tl.getTime() - lastNoteTime[assign.get(0)]
								< (sckey.contains(assign.get(0)) ? scratchInterval : keyInterval)) {
					tate.add(assign.get(0));
				} else {
					primary.add(assign.get(0));
				}
				
				assign.remove(0);
			}
			
			// primary�겓�궧�궚�꺀�긿�긽�꺃�꺖�꺍�걣�걗�굦�겙�깕�꺖�깂�걣�걗�굥�꺃�꺖�꺍�굮�뀓營�
			if (primary.contains(sckey.get(scratchIndex)) && !note.isEmpty()) {
				random[sckey.get(scratchIndex)] = note.get(0);
				primary.remove(sckey.get(scratchIndex));
				note.remove(0);
				// �궧�궚�꺀�긿�긽�꺃�꺖�꺍�굮�젂濚겹굤�겓
				scratchIndex = ++scratchIndex == sckey.size() ? 0 : scratchIndex;
			}

			// �깕�꺖�깂�걣�걗�굥�꺃�꺖�꺍�굮潁��ｃ걣�쇇�뵟�걮�겒�걚�꺃�꺖�꺍�겓�꺀�꺍���깲�겓�뀓營�
			while (!(note.isEmpty() || primary.isEmpty())) 
				noteShuffleAction.makeOtherLaneRandom(random, note, primary, laneRendaCount);

			// noteLane�걣令뷩겎�겒�걢�겂�걼�굢
			// lastNoteTime�걣弱뤵걬�걚�꺃�꺖�꺍�걢�굢�젂�빁�겓營��걚�겍�걚�걦
			leaveLastNoteTime(random, lastNoteTime, note, tate);

			primary.addAll(tate);
			// 餘뗣굤�굮�꺀�꺍���깲�겓
			while (!other.isEmpty())
				noteShuffleAction.makeOtherLaneRandom(random, other, primary, laneRendaCount);


		} else if (mode.player == 2) {
			// ���깣�꺂�깤�꺃�꺖�셽
			// �궧�궚�꺀�긿�긽�겢�겗�뜷�썶�겓�꽛�뀍�쉪�겓�궋�궢�궎�꺍�걬�굦�굥�굠�걝�겓�걲�굥
			// �ｆ돀�겘�눣�씎�겒�걚�굠�걝�겓 sc:40ms key:�궠�꺍�깢�궍�궛�걢�굢沃��겳�눣�걮
			keys = getKeys(mode, true);
			int keyInterval = hranThreshold;
			boolean isRightSide = (getModifyTarget() == SIDE_2P);
			int scLane = isRightSide ? mode.scratchKey[1] : mode.scratchKey[0];
			ArrayList<Integer> original, assign, note, other, primary, tate;
			original = new ArrayList<Integer>(keys.length);
			assign = new ArrayList<Integer>(keys.length);
			note = new ArrayList<Integer>(keys.length);
			other = new ArrayList<Integer>(keys.length);
			primary = new ArrayList<Integer>(keys.length);
			tate = new ArrayList<Integer>(keys.length);

			for (int lane = 0; lane < keys.length; lane++) {
				original.add(keys[lane]);
				if (isRightSide) {
					assign.add(keys[keys.length - lane - 1]);
				} else {
					assign.add(keys[lane]);
				}
			}

			// scLane�굮�뀍�젺�겓
			if (!isRightSide) {
				assign.remove((Integer) scLane);
				assign.add(0, scLane);
			}

			// LN�걣�궋�궚�깇�궍�깣�겒�꺃�꺖�꺍�굮�궋�궢�궎�꺍�걮�겍�걢�굢�솮鸚�
			//removeActivatedLane(random, ln, keys, original, assign);
			noteShuffleAction = new ExtraShuffle();
			noteShuffleAction.removeActivatedLane( keys, ln, assign, null, original, random);

			
			noteShuffleAction.classifyOriginalLane(notes, original, note, other, false);

			// �쑋�궋�궢�궎�꺍�꺃�꺖�꺍�굮潁��ｇ쇇�뵟�걢�겑�걝�걢�겎�늽窈�
			while (!assign.isEmpty()) {
				if (tl.getTime() - lastNoteTime[assign.get(0)] < (assign.get(0) == scLane ? scratchInterval
						: keyInterval)) {
					tate.add(assign.get(0));
				} else {
					primary.add(assign.get(0));
				}
				assign.remove(0);
			}

			// �깕�꺖�깂�걣�걗�굥�꺃�꺖�꺍�굮潁��ｃ걣�쇇�뵟�걮�겒�걚�꺃�꺖�꺍�겓�뀓營�
			while (!(note.isEmpty() || primary.isEmpty())) {
				random[primary.get(0)] = note.get(0);
				primary.remove(0);
				note.remove(0);
			}

			leaveLastNoteTime(random, lastNoteTime, note, tate);

			primary.addAll(tate);
			// 餘뗣굤�굮營��걚�겍�걚�걦
			while (!other.isEmpty()) {
				random[primary.get(0)] = other.get(0);
				primary.remove(0);
				other.remove(0);
			}

		}
		return scratchIndex;
	}


	private int[] modifySRANDOM(Mode mode, int[] ln, int[] lastNoteTime, TimeLine tl, Note[] notes) {
		int[] random;
		int[] keys;
		keys = getKeys(mode, false);
		noteShuffleAction = new TimeBasedShuffle();
		
		if(mode == Mode.POPN_9K)
			random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln, notes, lastNoteTime, tl.getTime(), 0, -1, laneRendaCount): keys;
		else
			random = keys.length > 0 ? noteShuffleAction.shuffle(keys, ln, notes, lastNoteTime, tl.getTime(), 40, -1, laneRendaCount): keys;
		return random;
	}


	private int[] modifySPIRAL(Mode mode, int[] random, int[] ln) {
		int[] keys;
		keys = getKeys(mode, false);
		if (random.length == 0) {
			// �닜�쐿�ㅳ겗鵝쒏닇
			int max = 0;
			for (int key : keys) {
				max = Math.max(max, key);
			}
			random = new int[max + 1];
			for (int i = 0; i < random.length; i++) {
				random[i] = i;
			}

			int index = (int) (Math.random() * keys.length);
			int j = (int) (Math.random() * 2) >= 1 ? 1 : keys.length - 1;
			for (int i = 0; i < keys.length; i++) {
				random[keys[i]] = keys[index];
				index = (index + j) % keys.length;
			}
			inc = (int) (Math.random() * (keys.length - 1)) + 1;
			Logger.getGlobal().info("SPIRAL - �뼀冶뗤퐤營�:" + index + " 罌쀥늽:" + inc);
		} else {
			boolean cln = false;
			for (int lane = 0; lane < keys.length; lane++) {
				if (ln[keys[lane]] != -1) {
					cln = true;
				}
			}
			if (!cln) {
				int[] nrandom = Arrays.copyOf(random, random.length);
				int index = inc;
				for (int i = 0; i < keys.length; i++) {
					nrandom[keys[i]] = random[keys[index]];
					index = (index + 1) % keys.length;
				}
				random = nrandom;
			}
		}
		return random;
	}


	//7to9
	private static int[] sevenToNine(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration) {
		/**
		 * 7to9 �궧�궚�꺀�긿�긽�뜷�썶鵝띸쉰�뼟岳� 0:OFF 1:SC1KEY2~8 2:SC1KEY3~9 3:SC2KEY3~9 4:SC8KEY1~7 5:SC9KEY1~7 6:SC9KEY2~8
		 */
		int keyLane = 2;
		int scLane = 1;
		int restLane = 0;
		switch(config.getSevenToNinePattern()) {
			case 1:
				scLane = 1 - 1;
				keyLane = 2 - 1;
				restLane = 9 - 1;
				break;
			case 2:
				scLane = 1 - 1;
				keyLane = 3 - 1;
				restLane = 2 - 1;
				break;
			case 4:
				scLane = 8 - 1;
				keyLane = 1 - 1;
				restLane = 9 - 1;
				break;
			case 5:
				scLane = 9 - 1;
				keyLane = 1 - 1;
				restLane = 8 - 1;
				break;
			case 6:
				scLane = 9 - 1;
				keyLane = 2 - 1;
				restLane = 1 - 1;
				break;
			case 3:
			default:
				scLane = 2 - 1;
				keyLane = 3 - 1;
				restLane = 1 - 1;
				break;
		}

		int[] result = new int[9];
		for (int i = 0; i < 7; i++) {
			result[i + keyLane] = i;
		}

		if (activeln != null && (activeln[scLane] != -1 || activeln[restLane] != -1)) {
			if(activeln[scLane] == 7) {
				result[scLane] = 7;
				result[restLane] = 8;
			} else {
				result[scLane] = 8;
				result[restLane] = 7;
			}
		} else {
			/**
			 * 7to9�궧�궚�꺀�긿�긽�눇�릤�궭�궎�깤 0:�걹�겗�겲�겲 1:�ｆ돀�썮�겳 2:雅ㅴ틨
			 */
			switch(config.getSevenToNineType()) {
				case 1:
					if(now - lastNoteTime[scLane] > duration || now - lastNoteTime[scLane] >= now - lastNoteTime[restLane]) {
						result[scLane] = 7;
						result[restLane] = 8;
					} else {
						result[scLane] = 8;
						result[restLane] = 7;
					}
					break;
				case 2:
					if(now - lastNoteTime[scLane] >= now - lastNoteTime[restLane]) {
						result[scLane] = 7;
						result[restLane] = 8;
					} else {
						result[scLane] = 8;
						result[restLane] = 7;
					}
					break;
				case 0:
				default:
					result[scLane] = 7;
					result[restLane] = 8;
					break;
			}
		}
		return result;
	}
	
	
	private void calculateHranThreshold() {
		if(config.getHranThresholdBPM() <= 0)
			hranThreshold = 0;
		else
			hranThreshold = (int) (Math.ceil(15000.0f / config.getHranThresholdBPM()));
	}

	private void leaveLastNoteTime(int[] random, int[] lastNoteTime, ArrayList<Integer> note, ArrayList<Integer> tate) {
		while (!note.isEmpty()) {
			int min = Integer.MAX_VALUE;
			int minLane = tate.get(0);
			for (int i = 0; i < tate.size(); i++) {
				if (min > lastNoteTime[tate.get(i)]) {
					min = lastNoteTime[tate.get(i)];
					minLane = tate.get(i);
				}
			}
			random[minLane] = note.get(0);
			tate.remove((Integer) minLane);
			note.remove(0);
		}
	}

	
}
