/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.featuredep.algos;

import algorithms.featuredep.Assignment;
import algorithms.featuredep.FDProblem;
import algorithms.featuredep.input.Input;
import algorithms.featuredep.input.impl.RandomInputGenerator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;

/**
 *
 * @author bennyl
 */
public class GeneticSearch {

    public static void main(String[] args) {

        List<EvolutionaryOperator<Assignment>> operators = new LinkedList<EvolutionaryOperator<Assignment>>();
        operators.add(new AssignmentCrossover());
        operators.add(new AssignmentMutation(new Probability(0.02)));
        EvolutionaryOperator<Assignment> pipeline = new EvolutionPipeline<Assignment>(operators);

        RandomInputGenerator rgen = new RandomInputGenerator(1111);
//        rgen.setNumberOfProtocols(1);
//        rgen.setNumberOfDeployableNodes(10);
//        rgen.setNumberOfFeatures(10);
//        rgen.setNumberOfNodesInTopologyGraph(300);
        Input in = rgen.generate();
        final FDProblem prob = new FDProblem();
        prob.reduce(in);


        EvolutionEngine<Assignment> engine =
                new GenerationalEvolutionEngine<Assignment>(
                new FDCandidateFactory(prob),
                pipeline,
                new AssignmentEvaluator(prob),
                new org.uncommons.watchmaker.framework.selection.TournamentSelection(new Probability(0.7)),
                new Random());

        engine.addEvolutionObserver(new EvolutionObserver<Assignment>() {
            public void populationUpdate(PopulationData<? extends Assignment> data) {
                System.out.printf("Generation %d: %s\n",
                        data.getGenerationNumber(),
                        data.getBestCandidate().toString(prob));
            }
        });

        Assignment res = engine.evolve(100, 3, new org.uncommons.watchmaker.framework.termination.GenerationCount(500));
        System.out.println("found solution: " + res.toString(prob));
    }

    public static class FDCandidateFactory extends AbstractCandidateFactory<Assignment> {

        FDProblem problem;

        public FDCandidateFactory(FDProblem problem) {
            this.problem = problem;
        }

        @Override
        public Assignment generateRandomCandidate(Random rng) {
            Assignment ass = new Assignment(problem);
            ass.randomize(rng);
            return ass;
        }
    }

    /**
     * Variable-point (fixed or random) cross-over for String candidates. This
     * implementation assumes that all candidate Strings are the same length. If
     * they are not, an exception will be thrown at runtime.
     *
     * @author Daniel Dyer
     */
    public static class AssignmentCrossover extends AbstractCrossover<Assignment> {

        /**
         * Default is single-point cross-over, applied to all parents.
         */
        public AssignmentCrossover() {
            this(1);
        }

        /**
         * Cross-over with a fixed number of cross-over points.
         *
         * @param crossoverPoints The constant number of cross-over points to
         * use for all cross-over operations.
         */
        public AssignmentCrossover(int crossoverPoints) {
            super(crossoverPoints);
        }

        /**
         * Cross-over with a fixed number of cross-over points. Cross-over may
         * or may not be applied to a given pair of parents depending on the
         * {@code crossoverProbability}.
         *
         * @param crossoverPoints The constant number of cross-over points to
         * use for all cross-over operations.
         * @param crossoverProbability The probability that, once selected, a
         * pair of parents will be subjected to cross-over rather than being
         * copied, unchanged, into the output population.
         */
        public AssignmentCrossover(int crossoverPoints, Probability crossoverProbability) {
            super(crossoverPoints, crossoverProbability);
        }

        /**
         * Cross-over with a variable number of cross-over points.
         *
         * @param crossoverPointsVariable A random variable that provides a
         * number of cross-over points for each cross-over operation.
         */
        public AssignmentCrossover(NumberGenerator<Integer> crossoverPointsVariable) {
            super(crossoverPointsVariable);
        }

        /**
         * Sets up a cross-over implementation that uses a variable number of
         * cross-over points. Cross-over is applied to a proportion of selected
         * parent pairs, with the remainder copied unchanged into the output
         * population. The size of this evolved proportion is controlled by the
         * {@code crossoverProbabilityVariable} parameter.
         *
         * @param crossoverPointsVariable A variable that provides a (possibly
         * constant, possibly random) number of cross-over points for each
         * cross-over operation.
         * @param crossoverProbabilityVariable A variable that controls the
         * probability that, once selected, a pair of parents will be subjected
         * to cross-over rather than being copied, unchanged, into the output
         * population.
         */
        public AssignmentCrossover(NumberGenerator<Integer> crossoverPointsVariable,
                NumberGenerator<Probability> crossoverProbabilityVariable) {
            super(crossoverPointsVariable, crossoverProbabilityVariable);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<Assignment> mate(Assignment parent1,
                Assignment parent2,
                int numberOfCrossoverPoints,
                Random rng) {

            Assignment offspring1 = parent1.deepCopy();
            Assignment offspring2 = parent2.deepCopy();
            // Apply as many cross-overs as required.
            for (int i = 0; i < numberOfCrossoverPoints; i++) {
                // Cross-over index is always greater than zero and less than
                // the length of the parent so that we always pick a point that
                // will result in a meaningful cross-over.

                int crossoverIndex = (1 + rng.nextInt(parent1.getContinuesSize() - 1));
                for (int j = 0; j < crossoverIndex; j++) {
                    boolean temp = offspring1.getContinuesValue(j);
                    offspring1.assignContinues(j, offspring2.getContinuesValue(j));
                    offspring2.assignContinues(j, temp);
                }
            }
            List<Assignment> result = new ArrayList<Assignment>(2);
            result.add(offspring1);
            result.add(offspring2);
            return result;
        }
    }

    public static class AssignmentMutation implements EvolutionaryOperator<Assignment> {

        private final NumberGenerator<Probability> mutationProbability;

        /**
         * Creates a mutation operator that is applied with the given
         * probability and draws its characters from the specified alphabet.
         *
         * @param alphabet The permitted values for each character in a string.
         * @param mutationProbability The probability that a given character is
         * changed.
         */
        public AssignmentMutation(Probability mutationProbability) {
            this(new ConstantGenerator<Probability>(mutationProbability));
        }

        /**
         * Creates a mutation operator that is applied with the given
         * probability and draws its characters from the specified alphabet.
         *
         * @param alphabet The permitted values for each character in a string.
         * @param mutationProbability The (possibly variable) probability that a
         * given character is changed.
         */
        public AssignmentMutation(NumberGenerator<Probability> mutationProbability) {
            this.mutationProbability = mutationProbability;
        }

        public List<Assignment> apply(List<Assignment> selectedCandidates, Random rng) {
            List<Assignment> mutatedPopulation = new ArrayList<Assignment>(selectedCandidates.size());
            for (Assignment s : selectedCandidates) {
                mutatedPopulation.add(mutateAssignment(s, rng));
            }
            return mutatedPopulation;
        }

        private Assignment mutateAssignment(Assignment s, Random rng) {
            Assignment a = s.deepCopy();
            for (int i = 0; i < s.getContinuesSize(); i++) {
                if (mutationProbability.nextValue().nextEvent(rng)) {
                    a.assignContinues(i, !a.getContinuesValue(i));
                }
            }

            return a;
        }
    }

    public static class AssignmentEvaluator implements FitnessEvaluator<Assignment> {

        FDProblem problem;

        public AssignmentEvaluator(FDProblem problem) {
            this.problem = problem;
        }

        @Override
        public double getFitness(Assignment candidate, List<? extends Assignment> population) {
            //keep the value positive allways.
            return candidate.calcQuility(problem) + problem.getMaxPrice();
        }

        @Override
        public boolean isNatural() {
            return true;
        }
    }
}
