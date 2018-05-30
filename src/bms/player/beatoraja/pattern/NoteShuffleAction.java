package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.List;

import bms.model.Note;

public interface NoteShuffleAction {

	public int shuffle(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration);

	public static void initLanes(int[] keys, List<Integer> laneFirst, List<Integer> laneSecond, int max,
			int[] result) {
		for (int key : keys) {
			laneFirst.add(key);
			laneSecond.add(key);
		}
		for (int key : keys) 
			max = Math.max(max, key);
		for (int i = 0; i < result.length; i++) 
			result[i] = i;
	}
	
	
	public static void removeActivatedLane(int[] keys, int[] activeln, List<Integer> assignLane,
			List<Integer> originalLane, List<Integer> noAssignedLane, int[] result) {
		for (int lane = 0; lane < keys.length; lane++) {
			if (activeln != null && activeln[keys[lane]] != -1) {
				result[keys[lane]] = activeln[keys[lane]];
				assignLane.remove((Integer) keys[lane]);
				originalLane.remove((Integer) activeln[keys[lane]]);
				if(noAssignedLane != null)
					noAssignedLane.remove((Integer) keys[lane]);
			}
		}
	}

	public static void removeActivatedLane(int[] random, int[] ln, int[] keys, ArrayList<Integer> original,
			ArrayList<Integer> assign) {
		for (int lane = 0; lane < keys.length; lane++) {
			if (ln[keys[lane]] != -1) {
				random[keys[lane]] = ln[keys[lane]];
				assign.remove((Integer) keys[lane]);
				original.remove((Integer) ln[keys[lane]]);
			}
		}
	}
	
	static void makeOtherLaneRandom(int[] result, List<Integer> noteLane, List<Integer> toRandomLane, int lineCountBias) {
		int r = (int) (Math.random() * toRandomLane.size());
		result[toRandomLane.get(r)] = noteLane.get(0);
		if(lineCountBias == 0)
			laneRendaCount[toRandomLane.get(r)] = 0;
		else if(lineCountBias == 1)
			laneRendaCount[toRandomLane.get(r)]++;
		toRandomLane.remove(r);
		noteLane.remove(0);

	}

}
