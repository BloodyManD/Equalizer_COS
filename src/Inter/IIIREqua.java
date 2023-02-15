package Inter;


import Filters.IIRFilter;

import java.util.concurrent.ExecutionException;

public interface IIIREqua {
    void setInputSignal(int[] inputSignal);
    void equalization() throws InterruptedException, ExecutionException;
    IIRFilter getFilter(short nF);
    int[] getOutputSignal();
    void close();
}