package Inter;

import java.util.concurrent.ExecutionException;

public interface IFIREqua<T> {
    void setInputSignal(short[] inputSignal);
    void equalization() throws InterruptedException, ExecutionException;
    T getFilter(short nF);
    short[] getOutputSignal();
    void close();
}
