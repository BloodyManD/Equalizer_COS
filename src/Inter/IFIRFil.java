package Inter;

public interface IFIRFil {
    void setSettingsFilter(double[] coefsFilter, short[] inputSignal);
    void setSettingsFilter(double[] coefsFilter);
    void setInputSignal(short[] inputSignal);
    void setGain(double gain);
}