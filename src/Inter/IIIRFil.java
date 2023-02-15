package Inter;

public interface IIIRFil {
    void setSettingsFilter(double[] coefsFilterA, double[] coefsFilterB, int[] inputSignal);
    void setSettingsFilter(double[] coefsFilterA, double[] coefsFilterB);
    void setInputSignal(int[] inputSignal);
    void setGain(double gain);
}