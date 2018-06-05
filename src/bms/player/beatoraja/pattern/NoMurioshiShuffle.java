package bms.player.beatoraja.pattern;

import java.util.ArrayList;
import java.util.List;

import bms.model.Note;

public class NoMurioshiShuffle extends NoteShuffleAction {

	@Override
	public int[] shuffle(int[] keys, int[] activeln, Note[] notes, int[] lastNoteTime, int now, int duration1, int duration2, int[] laneRendaCount) {
		List<Integer> assignedLane = new ArrayList<Integer>(keys.length);
		List<Integer> noAssignedLane = new ArrayList<Integer>(keys.length);
		List<Integer> originalLane = new ArrayList<Integer>(keys.length);

		int max = 0;
		int[] result = new int[max + 1];
		
		initLanes(keys, noAssignedLane, originalLane, max, result);

		// LN�걣�궋�궚�깇�궍�깣�겒�꺃�꺖�꺍�굮�궋�궢�궎�꺍�걮�겍�걢�굢�솮鸚�
		
		removeActivatedLane(keys, activeln, assignedLane, noAssignedLane, originalLane, result);
		
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
	
	private void preventMoreThanSevenKeys(int[] keys, int[] lastNoteTime, int now, int duration,
			List<Integer> assignedLane, List<Integer> noAssignedLane, int max, int[] result, List<Integer> noteLane) {
		List<Integer> kouhoLane = new ArrayList<Integer>(keys.length); //營��걨�굥�숃짒
		List<Integer> rendaLane = new ArrayList<Integer>(keys.length); //營��걦�겏潁��ｆ돀�겓�겒�굥�꺃�꺖�꺍
		while (!(noteLane.isEmpty() || noAssignedLane.isEmpty())) {
			kouhoLane.clear();
			rendaLane.clear();
			if(assignedLane.size() <= 1) {
				kouhoLane.addAll(noAssignedLane); //�뿢�겓�깕�꺖�깉�걣營��걢�굦�겍�걚�굥�꺃�꺖�꺍�걣1�뗤빳訝뗣겎�걗�굦�겙�뀲�깿�걣�숃짒
			} else {
				int[] referencePoint = new int[2]; //�뿢�겓�깕�꺖�깉�걣營��걢�굦�겍�걚�굥�꺃�꺖�꺍�겗訝��겎藥�塋��겗�꺃�꺖�꺍�겏�뤂塋��겗�꺃�꺖�꺍
				referencePoint[0] = max;
				referencePoint[1] = 0;
				for(int i = 0; i < assignedLane.size(); i++){
					referencePoint[0] = Math.min(referencePoint[0] , assignedLane.get(i));
					referencePoint[1] = Math.max(referencePoint[1] , assignedLane.get(i));
				}
				if(referencePoint[1] - referencePoint[0] <= 2) {
					kouhoLane.addAll(noAssignedLane); //�뿢�겓�깕�꺖�깉�걣營��걢�굦�겍�걚�굥�꺃�꺖�꺍�걣�뎴�뎸�겎�듉�걵�굥影꾢쎊�겎�걗�굦�겙�뀲�깿�걣�숃짒
				} else if(referencePoint[1] - referencePoint[0] == 3) {
					if(noAssignedLane.indexOf(referencePoint[0] - 2) != -1) kouhoLane.add(referencePoint[0] - 2);
					if(noAssignedLane.indexOf(referencePoint[0] - 1) != -1) kouhoLane.add(referencePoint[0] - 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 1) != -1) kouhoLane.add(referencePoint[0] + 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 2) != -1) kouhoLane.add(referencePoint[0] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 2) != -1) kouhoLane.add(referencePoint[1] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 1) != -1) kouhoLane.add(referencePoint[1] + 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 1) != -1) kouhoLane.add(referencePoint[1] - 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 2) != -1) kouhoLane.add(referencePoint[1] - 2);
				} else if(referencePoint[1] - referencePoint[0] == 4) {
					if(noAssignedLane.indexOf(referencePoint[0] - 2) != -1 && noAssignedLane.indexOf(referencePoint[0] + 1) != -1) kouhoLane.add(referencePoint[0] - 2);
					if(noAssignedLane.indexOf(referencePoint[0] - 1) != -1) kouhoLane.add(referencePoint[0] - 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 1) != -1) kouhoLane.add(referencePoint[0] + 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 2) != -1) kouhoLane.add(referencePoint[0] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 2) != -1 && noAssignedLane.indexOf(referencePoint[1] - 1) != -1) kouhoLane.add(referencePoint[1] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 1) != -1) kouhoLane.add(referencePoint[1] + 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 1) != -1) kouhoLane.add(referencePoint[1] - 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 2) != -1) kouhoLane.add(referencePoint[1] - 2);
				} else if(referencePoint[1] - referencePoint[0] >= 5) {
					if(noAssignedLane.indexOf(referencePoint[0] - 2) != -1 && noAssignedLane.indexOf(referencePoint[0] + 1) != -1 && noAssignedLane.indexOf(referencePoint[0] + 2) != -1) kouhoLane.add(referencePoint[0] - 2);
					if(noAssignedLane.indexOf(referencePoint[0] - 1) != -1 && noAssignedLane.indexOf(referencePoint[0] + 2) != -1) kouhoLane.add(referencePoint[0] - 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 1) != -1) kouhoLane.add(referencePoint[0] + 1);
					if(noAssignedLane.indexOf(referencePoint[0] + 2) != -1) kouhoLane.add(referencePoint[0] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 2) != -1 && noAssignedLane.indexOf(referencePoint[1] - 1) != -1 && noAssignedLane.indexOf(referencePoint[1] - 2) != -1) kouhoLane.add(referencePoint[1] + 2);
					if(noAssignedLane.indexOf(referencePoint[1] + 1) != -1 && noAssignedLane.indexOf(referencePoint[1] - 2) != -1) kouhoLane.add(referencePoint[1] + 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 1) != -1) kouhoLane.add(referencePoint[1] - 1);
					if(noAssignedLane.indexOf(referencePoint[1] - 2) != -1) kouhoLane.add(referencePoint[1] - 2);
				}
			}
			for(int i = 0; i < kouhoLane.size(); i++){
				if (now - lastNoteTime[kouhoLane.get(i)] < duration) {
					rendaLane.add(kouhoLane.get(i));
				}
			}
			if(kouhoLane.size() > rendaLane.size())
				kouhoLane.removeAll(rendaLane); //潁��ｆ돀�겓�겒�굥�꺃�꺖�꺍�굮�솮鸚뽧�귙걼�걽�걮�숃짒�뀲�깿�걣潁��ｆ돀�겓�겒�굥�졃�릦�꽒�릤�듉�걮�겎�겒�걚�걪�겏�겗�뼶�굮�꽛�뀍
			if(kouhoLane.isEmpty())
				break;
			
			int r = (int) (Math.random() * kouhoLane.size());
			result[kouhoLane.get(r)] = noteLane.get(0);
			assignedLane.add(kouhoLane.get(r));
			noAssignedLane.remove(kouhoLane.get(r));
			noteLane.remove(0);
		}
	}

	@Override
	void laneRandaCountChange(int[] laneRendaCount,List<Integer> toRandomLane) {
		// TODO Auto-generated method stub
		
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
