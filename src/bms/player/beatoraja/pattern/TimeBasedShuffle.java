package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.List;

import bms.model.Note;

public class TimeBasedShuffle extends NoteShuffleAction {

	@Override
	int[] shuffle(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration1, int duration2, int[] laneRendaCount) {
		List<Integer> assignLane = new ArrayList<Integer>(keys.length);
		List<Integer> originalLane = new ArrayList<Integer>(keys.length);
		int max = 0;
		int[] result = new int[max + 1];
		
		initLanes(keys, assignLane, originalLane, max, result);

		removeActivatedLane(keys, activeln, assignLane, originalLane, null, result);
		
		List<Integer> noteLane, otherLane;
		noteLane = new ArrayList<Integer>(keys.length);
		otherLane = new ArrayList<Integer>(keys.length);

		classifyOriginalLane(notes, originalLane, noteLane, otherLane, false);
		

		// �쑋�궋�궢�궎�꺍�꺃�꺖�꺍�굮潁��ｇ쇇�뵟�걢�겑�걝�걢�겎�늽窈�
		List<Integer> rendaLane, primaryLane;
		rendaLane = new ArrayList<Integer>(keys.length);
		primaryLane = new ArrayList<Integer>(keys.length);
		
		while (!assignLane.isEmpty()) {
			if (now - lastNoteTime[assignLane.get(0)] < duration1) {
				rendaLane.add(assignLane.get(0));
			} else {
				primaryLane.add(assignLane.get(0));
			}
			assignLane.remove(0);
		}

		// �깕�꺖�깂�걣�걗�굥�꺃�꺖�꺍�굮潁��ｃ걣�쇇�뵟�걮�겒�걚�꺃�꺖�꺍�겓�뀓營�
		while (!(noteLane.isEmpty() || primaryLane.isEmpty()))
			makeOtherLaneRandom(result, noteLane, primaryLane, laneRendaCount);

		// noteLane�걣令뷩겎�겒�걢�겂�걼�굢
		// lastNoteTime�걣弱뤵걬�걚�꺃�꺖�꺍�걢�굢�젂�빁�겓營��걚�겍�걚�걦
		while (!noteLane.isEmpty()) {
			int min = Integer.MAX_VALUE;
			for (int i = 0; i < rendaLane.size(); i++) {
				if (min > lastNoteTime[rendaLane.get(i)]) {
					min = lastNoteTime[rendaLane.get(i)];
				}
			}
			ArrayList<Integer> minLane = new ArrayList<Integer>(rendaLane.size());
			for (int i = 0; i < rendaLane.size(); i++) {
				if (min == lastNoteTime[rendaLane.get(i)]) {
					minLane.add(rendaLane.get(i));
				}
			}
			makeOtherLaneRandom(result, noteLane, minLane, laneRendaCount);
		}

		primaryLane.addAll(rendaLane);
		laneRemover(primaryLane, result, otherLane);

		return result;
	}

	@Override
	void laneRandaCountChange(int[] laneRendaCount, List<Integer> toRandomLane) {
		// There is a no additional change in TimeBasedShuffle.
	}

	@Override
	boolean removeCondition(int[] activeln, int[] keys, int lane) {
		return activeln != null && activeln[keys[lane]] != -1;
	}

	@Override
	void removeNoassignedLane(List<Integer> noAssignedLane, int[] keys, int lane) {
		if(noAssignedLane != null)
			noAssignedLane.remove((Integer) keys[lane]);
	}


}
