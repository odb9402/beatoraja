package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.List;

import bms.model.Note;

public class NoMurioshiShuffle extends NoteShuffleAction {

	@Override
	public int[] shuffle(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration1, int duration2) {
		List<Integer> assignedLane = new ArrayList<Integer>(keys.length);
		List<Integer> noAssignedLane = new ArrayList<Integer>(keys.length);
		List<Integer> originalLane = new ArrayList<Integer>(keys.length);

		int max = 0;
		int[] result = new int[max + 1];
		
		initLanes(keys, noAssignedLane, originalLane, max, result);

		// LN�걣�궋�궚�깇�궍�깣�겒�꺃�꺖�꺍�굮�궋�궢�궎�꺍�걮�겍�걢�굢�솮鸚�
		for (int lane = 0; lane < keys.length; lane++) {
			if (activeln != null && activeln[keys[lane]] != -1) {
				result[keys[lane]] = activeln[keys[lane]];
				assignedLane.remove((Integer) keys[lane]);
				originalLane.remove((Integer) activeln[keys[lane]]);
				if(noAssignedLane != null)
					noAssignedLane.remove((Integer) keys[lane]);
			}
		}
		
		List<Integer> noteLane, otherLane;
		noteLane = new ArrayList<Integer>(keys.length);
		otherLane = new ArrayList<Integer>(keys.length);

		classifyOriginalLane(notes, originalLane, noteLane, otherLane, true);

		//�꽒�릤�듉�걮�겓�겒�굢�겒�걚�굠�걝�겓�꺀�꺍���깲�겓營��걚�겍�걚�걦
		//7�뗦듉�걮餓δ툓�겎�겘�꽒�릤�듉�걮�걮�걢耶섇쑉�걮�겒�걚�겗�겎�솮鸚�
		if(assignedLane.size() + noteLane.size() <= 6) {
			preventMoreThanSevenKeys(keys, lastNoteTime, now, duration1, assignedLane, noAssignedLane, max, result,
					noteLane);
		}

		laneRemover(noAssignedLane, result, noteLane);

		// 餘뗣굤�굮�꺀�꺍���깲�겓營��걚�겍�걚�걦
		laneRemover(noAssignedLane, result, otherLane);

		return result;
	}

	@Override
	void laneRandaCountChange() {
		// TODO Auto-generated method stub

	}

	@Override
	boolean removeCondition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	void removeNoassignedLane() {
		// TODO Auto-generated method stub

	}

}
