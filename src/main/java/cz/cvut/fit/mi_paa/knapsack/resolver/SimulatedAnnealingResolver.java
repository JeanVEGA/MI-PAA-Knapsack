package cz.cvut.fit.mi_paa.knapsack.resolver;

import java.util.List;
import java.util.Random;

import ure.phd.simulatedannealing.defaults.DefaultSAScheduler;
import ure.phd.simulatedannealing.interfaces.SimulatedAnnealingProblem;
import ure.phd.simulatedannealing.solver.SimulatedAnnealingProblemSolver;
import cz.cvut.fit.mi_paa.knapsack.Knapsack;
import cz.cvut.fit.mi_paa.knapsack.result.AbstractResult;
import cz.cvut.fit.mi_paa.knapsack.result.SimulatedAnnealingResult;
import cz.cvut.fit.mi_paa.knapsack.results.Results;
import cz.cvut.fit.mi_paa.knapsack.results.SimulatedAnnealingResults;

public class SimulatedAnnealingResolver extends AbstractResolver implements SimulatedAnnealingProblem {

	private Random random = new Random();
	private Knapsack current;
	private Knapsack next;

	private long numOfStates;
	private long totalNumOfStates;

	@Override
	public AbstractResult solve(Knapsack knapsack) {
		setOriginal(knapsack);
		initTotalNumOfStates();
		current = getOriginal();

		DefaultSAScheduler scheduler = new DefaultSAScheduler(100D, 0.0001, 0.5);
		scheduler.reset();
		
		SimulatedAnnealingProblemSolver problemSolver = new SimulatedAnnealingProblemSolver(scheduler, this);

		problemSolver.solve();

		SimulatedAnnealingResult result = new SimulatedAnnealingResult(current);
		return result;
	}

	private void initTotalNumOfStates() {
		BruteForceResolver bruteForceResolver = new BruteForceResolver();
		bruteForceResolver.setOriginal(getOriginal());

		totalNumOfStates = bruteForceResolver.getStackSize();
	}

	@Override
	public Results getResults(int numOfRepeats, List<Knapsack> knapsacks) {
		return new SimulatedAnnealingResults(numOfRepeats, this, knapsacks);
	}

	@Override
	public void init() {
	}

	@Override
	public double getCostForCurrentState() {
		return getCost(current);
	}

	@Override
	public void createNextState() {
		numOfStates++;
		next = current.clone();
		// randomly add or remove one item from knapsack
		swapUsed(getRandomInt());
	}

	private void swapUsed(int index) {
		boolean used = next.getItems()[index].isUsed();
		next.getItems()[index].setUsed(!used);
	}

	private int getRandomInt() {
		return random.nextInt(getOriginal().getNumOfItems());
	}

	@Override
	public double getCostForNextState() {
		return getCost(next);
	}

	private double getCost(Knapsack knapsack) {
		return knapsack.getSumValues();
	}

	@Override
	public void goToNextState() {
		current = next;
		next = null;
	}

	@Override
	public boolean isTotalNumberOfStatesReached() {
		return totalNumOfStates == numOfStates;
	}

	@Override
	public String getSolutionString() {
		return null;
	}

}
