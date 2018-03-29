import jm.JMC;
import jm.music.data.CPhrase;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Write;


public class Main implements JMC {
    public static void main(String[] args) {

        double values[] = new double[16];
        double step = 360.0 / 16;

        Score s = new Score("Music");
        Part chords1 = new Part("Chords", 0, 0);
        Part notes = new Part("Notes", 0, 1);
        s.setTempo(120);

        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }
        Music music = new Music(values);
        Chord chords[] = music.generateMusic();

        for (int i = 0; i < 16; i++) {
            CPhrase chord = new CPhrase();
            chord.addChord(chords[i].notes, C);
            chords1.addCPhrase(chord);
        }

        s.addPart(chords1);
        s.addPart(notes);
        Write.midi(s, "VictoriaRotaru.mid");
    }
}