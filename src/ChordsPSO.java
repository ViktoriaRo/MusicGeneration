import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.*;

/**
 * Generate chords with PSO algorithm
 */
class ChordsPSO {
    private final int ChordsAmount = 16; // total amount of chords
    private final double[] values;
    private final int Population = 25;
    private Random rnd;

    private double globalBest[];
    private double globalFitness;

    ChordsPSO(Random rnd, double[] values) {
        this.rnd = rnd;
        this.values = values;
        globalBest = new double[ChordsAmount];
        globalFitness = Double.MAX_VALUE;
    }
    /**
     * the class represents chord particle for PSO
     */
    class MusicParticle {
        private double[] notes;
        private double[] localBest;
        private double[] velocity;
        private double fitness = Double.MAX_VALUE;

        MusicParticle(int notesAmount) {
            setNotes(new double[notesAmount]);
            setLocalBest(new double[notesAmount]);
            setVelocity(new double[notesAmount]);
            for (int i = 0; i < notesAmount; i++) {
                getNotes()[i] = (rnd.nextDouble() * 2) - 1;
                getLocalBest()[i] = getNotes()[i];
                getVelocity()[i] = 0;
            }
        }

        double[] getNotes() {
            return notes;
        }

        void setNotes(double[] notes) {
            this.notes = notes;
        }

        double getFitness() {
            return fitness;
        }

        void setFitness(double fitness) {
            this.fitness = fitness;
        }

        double[] getVelocity() {
            return velocity;
        }

        void setVelocity(double[] velocity) {
            this.velocity = velocity;
        }


        double[] getLocalBest() {
            return localBest;
        }

        void setLocalBest(double[] localBest) {
            this.localBest = localBest;
        }
    }

    private MusicParticle[] generateParticles() {
        MusicParticle[] musicParticles = new MusicParticle[Population];
        int i = 0;
        while (Population > i) {
            musicParticles[i] = new MusicParticle(ChordsAmount);
            i++;
        }
        return musicParticles;
    }

    // Creation of base chords
    //Find the closest possible value for the beginning of chord from tonic, subdominant and dominant
    //Calculates fitness for every particle
    private double fitnessFunction(MusicParticle musicParticle) {
        double value = 0;
        int i = 0;
        while (ChordsAmount > i) {
            double err = musicParticle.getNotes()[i] - values[i];
            value += pow(err, 2);
            i++;
        }
        return value;
    }

    MusicParticle baseChords() {
        MusicParticle[] musicParticles = generateParticles();

        optimizeGeneration(musicParticles);

        int index = 0;
        for (int i = 1; i < Population; i++)
            if (musicParticles[index].getFitness() > musicParticles[i].getFitness())
                index = i;

        MusicParticle p = musicParticles[index];
        int i = 0;
        while (i < ChordsAmount) {
            p.getNotes()[i] = round(66 + p.getNotes()[i] * 6);
            i++;
        }

        return p;
    }

    private void optimizeGeneration(MusicParticle... musicParticles) {

        //Optimization
        int iteration;
        int iterations = 250;
        double block = 0.92;
        for (iteration = 0; iteration < iterations && globalFitness > block; iteration++) {
            for (MusicParticle p : musicParticles) {
                double fitness = fitnessFunction(p);
                if (fitness < p.getFitness()) {
                    p.setFitness(fitness);
                    System.arraycopy(p.getNotes(), 0, p.getLocalBest(), 0, ChordsAmount);
                }

                if (fitness < globalFitness) {
                    globalFitness = fitness;
                    System.arraycopy(p.getNotes(), 0, globalBest, 0, ChordsAmount);
                }
            }
            //If fitness equal 0, it means that we have already reached possible value of notes for chord
            int i1 = 0;
            AtomicInteger particlesLength = new AtomicInteger(musicParticles.length);
            while (i1 < particlesLength.get()) {
                MusicParticle particle = musicParticles[i1];
                int rand1 = rnd.nextInt(2);
                int rand2 = rnd.nextInt(2);

                int i = 0;
                while (i < ChordsAmount) {
                    double chord1 = 1.035;
                    double chord2 = 1.035;
                    double m = 0.65;
                    particle.getVelocity()[i] = (m * particle.getVelocity()[i]) + (chord1 * rand1 *
                            (particle.getLocalBest()[i] - particle.getNotes()[i])) + (chord2 * rand2 *
                            (globalBest[i] - particle.getNotes()[i]));
                    particle.getNotes()[i] += particle.getVelocity()[i];
                    i++;
                }
                i1++;
            }
        }
    }

}