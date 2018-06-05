package bms.player.beatoraja.pattern;

import java.util.List;

import bms.model.LongNote;
import bms.model.NormalNote;
import bms.model.Note;

public abstract class NoteShuffleAction {
	int[] keys;
	int[] activelane;
	
	// This abstract method is a main process of subclasses from NoteShuffleAction. 
	abstract int[] shuffle(int[] keys, int[] activeln,
			Note[] notes, int[] lastNoteTime, int now, int duration1, int duration2, int[] laneRendaCount);
	
	public void removeActivatedLane(int[] keys, int[] activateLane, List<Integer> assignedLane,
			List<Integer> noAssignedLane, List<Integer> originalLane, int[] result) {
		for(int lane = 0; lane < keys.length; lane++) {
			if(removeCondition(activateLane, keys, lane)) {
				result[keys[lane]] = activateLane[keys[lane]];
				assignedLane.remove((Integer) keys[lane]);
				originalLane.remove((Integer) activateLane[keys[lane]]);
				removeNoassignedLane(noAssignedLane, keys, lane);
			}
		}
	}
	
	public void makeOtherLaneRandom(int[] result, List<Integer> noteLane, List<Integer> toRandomLane, int[] laneRendaCount) {
		int r = (int) (Math.random() * toRandomLane.size());
		result[toRandomLane.get(r)] = noteLane.get(0);
		laneRandaCountChange(laneRendaCount, toRandomLane);
		toRandomLane.remove(r);
		noteLane.remove(0);
	}

	abstract void laneRandaCountChange(int[] laneRendaCount, List<Integer> toRandomLane);

	abstract boolean removeCondition(int[] activeln, int[] keys, int lane);
	
	abstract void removeNoassignedLane(List<Integer> noAssignedLane, int[] keys, int lane);

	protected void initLanes(int[] keys, List<Integer> laneFirst, List<Integer> laneSecond, int max,
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
	
	protected void classifyOriginalLane(Note[] notes, List<Integer> originalLane, List<Integer> noteLane,
			List<Integer> otherLane, boolean noteTypeCheck) {
		while (!originalLane.isEmpty()) {
			if (notes[originalLane.get(0)] != null && 
					((!noteTypeCheck) || (notes[originalLane.get(0)] instanceof NormalNote || notes[originalLane.get(0)] instanceof LongNote))) {
				noteLane.add(originalLane.get(0));
			} else {
				otherLane.add(originalLane.get(0));
			}
			originalLane.remove(0);
		}
	}
	
	protected void laneRemover(List<Integer> noAssignedLane, int[] result, List<Integer> Lane) {
		while (!Lane.isEmpty()) {
			int r = (int) (Math.random() * noAssignedLane.size());
			result[noAssignedLane.get(r)] = Lane.get(0);
			noAssignedLane.remove(r);
			Lane.remove(0);
		}
	}
}
