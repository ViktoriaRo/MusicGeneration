import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.*;

class NotesPSO {
    private final int notesInChord = 3;// Amount of notes in one chord
    private Random rnd;
    private double basements[];
    private double globalBest[];

    /**
     * the class represents music particle for PSO
     */
    private class Particle {
        private double[] notes = new double[notesInChord];
        private double[] velocity = new double[notesInChord];
        private double[] myBest = new double[notesInChord];
        private boolean[] blocked = new boolean[notesInChord];
        double fitness;

        Particle() {
            getNotes()[0] = 0;
            getNotes()[1] = rnd.nextDouble() * 12;
            getNotes()[2] = rnd.nextDouble() * 12;
            fitness = Double.MAX_VALUE;
        }

        double[] getVelocity() {
            return velocity;
        }

        double[] getMyBest() {
            return myBest;
        }

        public void setMyBest(double[] myBest) {
            this.myBest = myBest;
        }

        double[] getNotes() {
            return notes;
        }

        public void setNotes(double[] notes) {
            this.notes = notes;
        }

        boolean[] getBlocked() {
            return blocked;
        }

        public void setBlocked(boolean[] blocked) {
            this.blocked = blocked;
        }
    }

    NotesPSO(Random random, double[] basements) {
        this.rnd = random;
        this.basements = basements;
        globalBest = new double[notesInChord];
    }

    /**
     *
     * Calculates particle fitness
     */
    private double fitnessFunction(Particle p) {
        double diff = abs(p.getNotes()[1] - (p.getNotes()[0] + 4));
        double diff2 = abs(p.getNotes()[2] - (p.getNotes()[0] + 7));
        if (diff < 1 && diff > -1) {
            p.getBlocked()[1] = true;
        }
        if (diff2 < 1 && diff2 > -1)
            p.getBlocked()[2] = true;

        return diff + diff2;
    }

    Chord[] generateChords() {
        int chordsAmount = 16;
        Chord[] chords = new Chord[chordsAmount];
        int index = 0;
        while (index < basements.length) {
            int population = 20;
            Particle[] particles = new Particle[population];
            for (int i = 0; i < population; i++)
                particles[i] = new Particle();

            optimize(particles);

            int best = 0;
            for (int i = 1; i < particles.length; i++)
                if (particles[i].fitness < particles[best].fitness)
                    best = i;

            Particle bestParticle = particles[best];
            int notes[] = new int[notesInChord];
            notes[0] = (int) round(basements[index]);
            for (int k = 1; k < notesInChord; k++)
                notes[k] = (int) round(bestParticle.getNotes()[k] + notes[0]);

            chords[index] = new Chord(notes);
            index++;
        }

        return chords;
    }

    private void optimize(Particle... particles) {
        int iteration;
        double globalFitness = Double.MAX_VALUE;
        double block = 0.3;
        int iterations = 300;
        iteration = 0;
        while (iteration < iterations && globalFitness > block) {
            for (Particle p : particles) {
                double fitness = fitnessFunction(p);
                if (fitness < p.fitness) {
                    p.fitness = fitness;
                    System.arraycopy(p.getNotes(), 0, p.getMyBest(), 0, notesInChord);
                }

                if (fitness < globalFitness) {
                    globalFitness = fitness;
                    System.arraycopy(p.getNotes(), 0, globalBest, 0, notesInChord);
                }
            }

            for (Particle p : particles) {
                AtomicInteger rand1 = new AtomicInteger(rnd.nextInt(2));
                AtomicInteger rand2 = new AtomicInteger(rnd.nextInt(2));

                int i = 0;
                while (i < p.getVelocity().length) {
                    if (!p.getBlocked()[i]) {
                        double c1 = 1.13;
                        double c2 = 1.18;
                        double m = 0.75;
                        p.getVelocity()[i] = (m * p.getVelocity()[i]) + (c1 * rand1.get() *
                                (p.getMyBest()[i] - p.getNotes()[i])) + (c2 * rand2.get() *
                                (globalBest[i] - p.getNotes()[i]));
                        p.getNotes()[i] += p.getVelocity()[i];
                    }
                    i++;
                }

            }
            iteration++;
        }
    }
}