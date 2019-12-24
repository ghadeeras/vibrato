package vibrato.oscillators;

public interface Operation {

    State state();

    void readPhase();

    void writePhase();

}
