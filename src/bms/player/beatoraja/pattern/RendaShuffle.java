package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.List;

import bms.model.Note;

public class RendaShuffle extends NoteShuffleAction {

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

		classifyOriginalLane(notes, originalLane, noteLane, otherLane, true);

		// �쑋�궋�궢�궎�꺍�꺃�꺖�꺍�굮潁��ｆ돀�쇇�뵟�걢�겑�걝�걢�겎�늽窈�
		List<Integer> rendaLane,mainRendaLane, noRendaLane;
		rendaLane = new ArrayList<Integer>(keys.length);
		mainRendaLane = new ArrayList<Integer>(keys.length);
		noRendaLane = new ArrayList<Integer>(keys.length);
		while (!assignLane.isEmpty()) {
			if (now - lastNoteTime[assignLane.get(0)] < duration2) {
				rendaLane.add(assignLane.get(0));
			} else if(now - lastNoteTime[assignLane.get(0)] < duration1) {
				mainRendaLane.add(assignLane.get(0));
			} else {
				noRendaLane.add(assignLane.get(0));
			}
			assignLane.remove(0);
		}

		// �깕�꺖�깂�걣�걗�굥�꺃�꺖�꺍�굮潁��ｆ돀�걣�쇇�뵟�걲�굥�꺃�꺖�꺍�겓�빓�걚�젂�겓�뀓營�
		while (!(noteLane.isEmpty() || mainRendaLane.isEmpty())) {
			int maxRenda = Integer.MIN_VALUE;

			for (int i = 0; i < mainRendaLane.size(); i++) 
				if (maxRenda < laneRendaCount[mainRendaLane.get(i)])
					maxRenda = laneRendaCount[mainRendaLane.get(i)];

			ArrayList<Integer> maxLane = new ArrayList<Integer>(mainRendaLane.size());
			for (int i = 0; i < mainRendaLane.size(); i++) 
				if (maxRenda == laneRendaCount[mainRendaLane.get(i)]) 
					maxLane.add(mainRendaLane.get(i));

			makeOtherLaneRandom(result, noteLane, mainRendaLane, laneRendaCount);
		}

		// noteLane�걣令뷩겎�겒�걢�겂�걼�굢餘뗣굤�겗�깕�꺖�깉�굮潁��ｆ돀�겓�겒�굢�겒�걚�꺃�꺖�꺍�걢�굢�꺀�꺍���깲�겓營��걚�겍�걚�걦
		while (!(noteLane.isEmpty() || noRendaLane.isEmpty()))
			laneRandaCountMask(result, noteLane, noRendaLane, laneRendaCount);

		// noteLane�걣令뷩겎�겒�걢�겂�걼�굢餘뗣굤�겗�깕�꺖�깉�굮�꺀�꺍���깲�겓營��걚�겍�걚�걦
		while (!(noteLane.isEmpty() || rendaLane.isEmpty()))
			makeOtherLaneRandom(result, noteLane, rendaLane, laneRendaCount);

		// 餘뗣굤�굮�꺀�꺍���깲�겓營��걚�겍�걚�걦
		noRendaLane.addAll(rendaLane);
		noRendaLane.addAll(mainRendaLane);
		
		while (!otherLane.isEmpty()) {
			int r = (int) (Math.random() * noRendaLane.size());
			result[noRendaLane.get(r)] = otherLane.get(0);
			if(rendaLane.indexOf(noRendaLane.get(r)) == -1)
				laneRendaCount[noRendaLane.get(r)] = 0;
			noRendaLane.remove(r);
			otherLane.remove(0);
		}

		return result;
	}

	@Override
	void laneRandaCountChange(int[] laneRendaCount, List<Integer> toRandomLane) {
		int r = (int) (Math.random() * toRandomLane.size());
		laneRendaCount[toRandomLane.get(r)]++;
	}
	
	void laneRandaCountMask(int[] result, List<Integer> noteLane, List<Integer> toRandomLane, int[] laneRendaCount) {
		int r = (int) (Math.random() * toRandomLane.size());
		result[toRandomLane.get(r)] = noteLane.get(0);
		laneRendaCount[toRandomLane.get(r)] = 0;
		toRandomLane.remove(r);
		noteLane.remove(0);
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
