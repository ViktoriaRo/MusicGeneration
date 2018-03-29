class Chord {
    int notes[];

    Chord(int notes[]) {
        int notesAmount = 3;
        this.notes = new int[notesAmount];
        System.arraycopy(notes, 0, this.notes, 0, notesAmount);
    }
}
