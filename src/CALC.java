/**
 * CALC class calculates required number of steps to reach wanted Hero aspects
 * and recalculates current Hero aspects to avoid frequent refresh of Hero
 * aspects.
 * 
 * @author medenko
 * @version 1.10
 */
public class CALC {

	/**
	 * Calculate required number of steps to reach wanted Hero aspects.
	 * 
	 * @param asp   current Hero aspects
	 * @param goals wanted Hero aspects
	 * @return required number of steps
	 */
	public int numSteps(int[] asp, int[] goals) {
		int steps = 0;

		// loop simulation until Hero would reach wanted aspects
		while (canLoop(asp, goals)) {
			boolean chosen = false;
			int choice = 0;

			// choose good aspect
			int[] candidates1 = prioritize(asp, goals, 1);
			// choose good candidate
			if (candidates1.length > 0) {
				choice = candidates1[0];
				chosen = true;
			}

			// choose bad aspect if good aspect cannot be chosen
			if (!chosen) {
				int[] candidates2 = prioritize(asp, goals, 2);
				// choose bad candidate
				if (candidates2.length > 0) {
					choice = mod(candidates2[0] + 4);
					chosen = true;
				}
			}

			// recalculate simulated aspects
			if (asp[mod(choice + 4)] >= 5) {
				asp[mod(choice + 4)] -= 5;
				asp[mod(choice - 1)] += 1;
				asp[choice] += 3;
				asp[mod(choice + 1)] += 1;
			}

			steps++;
		}

		return steps;
	}

	/**
	 * Prioritize aspects from their highest to their lowest opposites.
	 * 
	 * @param asp      current Hero aspects
	 * @param goals    wanted Hero aspects
	 * @param priority indicator to sort (1) wanted or (2) unwanted aspects
	 * @return array of aspects, sorted from highest to lowest priority
	 */
	int[] prioritize(int[] asp, int[] goals, int priority) {
		int[] array = { -1, -1, -1, -1, -1, -1, -1, -1 };
		int a = 0;

		switch (priority) {
		case 1: // collect all aspects with their positive opposites
			for (int i = 0; i < 8; i++) {
				if ((asp[mod(i + 4)] - goals[mod(i + 4)]) >= 0) {
					array[a++] = i;
				}
			}
			break;
		case 2: // collect all aspects with their negative opposites
			for (int i = 0; i < 8; i++) {
				if ((asp[mod(i + 4)] - goals[mod(i + 4)]) < 0) {
					array[a++] = i;
				}
			}
			break;
		}

		// sort aspects from their highest to lowest opposite aspects
		if (a >= 2) {
			for (int i = 0; i < 7 && array[i + 1] >= 0; i++) {
				if ((asp[mod(array[i + 1] + 4)] - goals[mod(array[i + 1] + 4)]) > (asp[mod(array[i] + 4)]
						- goals[mod(array[i] + 4)])) {
					int temp = array[i + 1];
					array[i + 1] = array[i];
					array[i] = temp;
					i = -1;
				}
			}
		}

		// return array of candidates
		int[] candidates = new int[a];
		for (int i = 0; i < a; i++) {
			candidates[i] = array[i];
		}

		return candidates;
	}

	/**
	 * Prioritize aspects from most wanted to least wanted.
	 * 
	 * @param asp   current Hero aspects
	 * @param goals wanted Hero aspects
	 * @return array of aspects, sorted from most wanted to least wanted
	 */
	public int[] prioritizeAspects(int[] asp, int[] goals) {

		int[] candidates1 = prioritize(asp, goals, 1);
		int[] candidates2 = prioritize(asp, goals, 2);
		int neutral = 8;
		int bad = 9;

		int len1 = candidates1.length;
		int len2 = candidates2.length;

		int[] priorities = new int[len1 + len2 + 2];
		int a = 0;

		// 1) add aspects with positive opposite aspects
		for (int i = 0; i < len1; i++) {
			priorities[a++] = candidates1[i];
		}
		// 2) add neutral actions
		priorities[a++] = neutral;
		// 3) add aspects with negative opposite aspects
		// (not wanted because it lowers wanted aspects but still needed)
		for (int i = 0; i < len2; i++) {
			priorities[a++] = candidates2[i];
		}
		// 4) add bad actions
		priorities[a++] = bad;

		// return array of aspects which are sorted from most to least wanted
		return priorities;
	}

	/**
	 * Allow simulation function @numSteps to loop until Hero would reach wanted
	 * aspects.
	 * 
	 * @param asp   current Hero aspects
	 * @param goals wanted Hero aspects
	 * @return condition of reaching all Hero wanted aspects in @numSteps simulation
	 *         loop
	 */
	private boolean canLoop(int[] asp, int[] goals) {
		for (int i = 0; i < 8; i++) {
			if (asp[i] < goals[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculate n modulo 8.
	 * 
	 * @param n number
	 * @return number modulo 8
	 */
	private int mod(int n) {
		while (n > 7) {
			n -= 8;
		}
		while (n < 0) {
			n += 8;
		}
		return n;
	}

	/**
	 * Recalculate current Hero aspects after choosing story action.
	 * 
	 * @param asp    current Hero aspects
	 * @param chosen index of chosen story action
	 */
	public void recalculate(int[] asp, int chosen) {
		switch (chosen) {
		case 1: // Examine,
		case 8: // Talk,
		case 22: // Use chance,
		case 30: // Throw a coin (1 KNOWLEDGE)
			if (asp[5] >= 5) {
				asp[5] -= 5;
				asp[0] += 1;
				asp[1] += 3;
				asp[2] += 1;
			}
			break;
		case 3: // Smash everything (1 DESTRUCTION)
			if (asp[1] >= 5) {
				asp[1] -= 5;
				asp[4] += 1;
				asp[5] += 3;
				asp[6] += 1;
			}
			break;
		case 4: // Look for a better path,
		case 10: // Enter city (1 HUMAN)
			if (asp[4] >= 5) {
				asp[4] -= 5;
				asp[7] += 1;
				asp[0] += 3;
				asp[1] += 1;
			}
			break;
		case 5: // Jump over it
		case 11: // Rob city (2 BEAST)
			if (asp[0] >= 5) {
				asp[0] -= 5;
				asp[3] += 1;
				asp[4] += 3;
				asp[5] += 1;
			}
			if (asp[0] >= 5) {
				asp[0] -= 5;
				asp[3] += 1;
				asp[4] += 3;
				asp[5] += 1;
			}
			break;
		case 6: // Mislead,
		case 23: // Ask for more,
		case 38: // Assassinate (1 CORRUPTION)
			if (asp[3] >= 5) {
				asp[3] -= 5;
				asp[6] += 1;
				asp[7] += 3;
				asp[0] += 1;
			}
			break;
		case 7: // Devour,
		case 12: // Rob (1 BEAST)
			if (asp[0] >= 5) {
				asp[0] -= 5;
				asp[3] += 1;
				asp[4] += 3;
				asp[5] += 1;
			}
			break;
		case 9: // Terrorize (1 CHAOS)
			if (asp[2] >= 5) {
				asp[2] -= 5;
				asp[5] += 1;
				asp[6] += 3;
				asp[7] += 1;
			}
			break;
		case 20: // Brave,
		case 27: // Escort,
		case 37: // Confront the enemy (2 ORDER)
			if (asp[6] >= 5) {
				asp[6] -= 5;
				asp[1] += 1;
				asp[2] += 3;
				asp[3] += 1;
			}
			if (asp[6] >= 5) {
				asp[6] -= 5;
				asp[1] += 1;
				asp[2] += 3;
				asp[3] += 1;
			}
			break;
		case 21: // Accept,
		case 24: // Hide (1 NATURE)
			if (asp[7] >= 5) {
				asp[7] -= 5;
				asp[2] += 1;
				asp[3] += 3;
				asp[4] += 1;
			}
			break;
		case 25: // Full attack (1 BEAST, 1 DESTRUCTION)
			if (asp[0] >= 5) {
				asp[0] -= 5;
				asp[3] += 1;
				asp[4] += 3;
				asp[5] += 1;
			}
			if (asp[1] >= 5) {
				asp[1] -= 5;
				asp[4] += 1;
				asp[5] += 3;
				asp[6] += 1;
			}
			break;
		case 26: // Set everything alight (2 CHAOS)
			if (asp[2] >= 5) {
				asp[2] -= 5;
				asp[5] += 1;
				asp[6] += 3;
				asp[7] += 1;
			}
			if (asp[2] >= 5) {
				asp[2] -= 5;
				asp[5] += 1;
				asp[6] += 3;
				asp[7] += 1;
			}
			break;
		case 28: // Beguile (1 HUMAN, 1 CORRUPTION)
			if (asp[4] >= 5) {
				asp[4] -= 5;
				asp[7] += 1;
				asp[0] += 3;
				asp[1] += 1;
			}
			if (asp[3] >= 5) {
				asp[3] -= 5;
				asp[6] += 1;
				asp[7] += 3;
				asp[0] += 1;
			}
			break;
		case 29: // Snoop (1 BEAST, 1 NATURE)
			if (asp[0] >= 5) {
				asp[0] -= 5;
				asp[3] += 1;
				asp[4] += 3;
				asp[5] += 1;
			}
			if (asp[7] >= 5) {
				asp[7] -= 5;
				asp[2] += 1;
				asp[3] += 3;
				asp[4] += 1;
			}
			break;
		case 31: // Look for coins (2 CORRUPTION)
			if (asp[3] >= 5) {
				asp[3] -= 5;
				asp[6] += 1;
				asp[7] += 3;
				asp[0] += 1;
			}
			if (asp[3] >= 5) {
				asp[3] -= 5;
				asp[6] += 1;
				asp[7] += 3;
				asp[0] += 1;
			}
			break;
		case 32: // Party,
		case 43: // Warn of dangers (2 HUMAN)
			if (asp[4] >= 5) {
				asp[4] -= 5;
				asp[7] += 1;
				asp[0] += 3;
				asp[1] += 1;
			}
			if (asp[4] >= 5) {
				asp[4] -= 5;
				asp[7] += 1;
				asp[0] += 3;
				asp[1] += 1;
			}
			break;
		case 39: // Investigate,
		case 53: // Observe (2 KNOWLEDGE)
			if (asp[5] >= 5) {
				asp[5] -= 5;
				asp[0] += 1;
				asp[1] += 3;
				asp[2] += 1;
			}
			if (asp[5] >= 5) {
				asp[5] -= 5;
				asp[0] += 1;
				asp[1] += 3;
				asp[2] += 1;
			}
			break;
		}
	}

}
