package bms.player.beatoraja.pattern;

import java.util.List;

import bms.model.Note;

public class ExtraShuffle extends NoteShuffleAction {

	@Override
	int[] shuffle(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration1, int duration2,
			int[] laneRendaCount) {
		// ExtraShffle has no shuffle method but it has other useful method
		// to help other logics.
		return null;
	}

	@Override
	void laneRandaCountChange(int[] laneRendaCount, List<Integer> toRandomLane) {
		//
	}

	@Override
	boolean removeCondition(int[] activeln, int[] keys, int lane) {
		return activeln[keys[lane]] != -1;
	}

	@Override
	void removeNoassignedLane(List<Integer> noAssignedLane, int[] keys, int lane) {
		//There is no "noAssignedLane" in ExtraShuffle class.
	}


}
