import java.util.Random;

/**
 * Generates music based on chords and notes generated in ChordsPSO and NotesPSO
 */
class Music {
    private Random rnd;
    private double values[];

    Music(double values[]) {
        this.values = values;
        rnd = new Random();
    }

    Chord[] generateMusic() {
        ChordsPSO chSequence = new ChordsPSO(rnd, values);
        ChordsPSO.MusicParticle bestChordBases = chSequence.baseChords();
        NotesPSO optimization = new NotesPSO(rnd, bestChordBases.getNotes());
        return optimization.generateChords();
    }
}