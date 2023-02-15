package Filters;

import Coef.IIR;

import Inter.IIIRFil;

import java.util.Arrays;
import java.util.concurrent.Callable;


/**
 * Класс - КИХ-фильтр

 */
public class IIRFilter implements Callable<int[]>, IIIRFil {
    // Количество коэффициентов фильтра (порядок фильтра)
    private int countCoefs;

    // Массив коэффициентов фильтра (A и B, потому что это БИХ-фильтр)
    private double[] coefsFilterA;
    private double[] coefsFilterB;

    private int lengthOfInputSignal;

    // Входной и выходные сигналы
    private int[] inputSignal;
    private int[] outputSignal;

    // Усиление нашего фильтра (будет использоваться в дальнейшем для эквалайзера)
    private double gain;

    /**
     * Конструктор, который инициализирует наш КИХ-фильтр
     * @param coefsFilterA - массив коэффициентов КИХ-фильтра (тип А)
     * @param coefsFilterB массив коэффициентов КИХ-фильтра (тип B)
     * @param inputSignal - входной сигнал КИХ-фильтра
     */
    private IIRFilter(double[] coefsFilterA, double[] coefsFilterB, int[] inputSignal) {
        this.coefsFilterA = coefsFilterA;
        this.coefsFilterB = coefsFilterB;
        countCoefs = IIR.COUNT_OF_COEFS;

        this.inputSignal = inputSignal;
        this.outputSignal = new int[inputSignal.length];

        this.lengthOfInputSignal = inputSignal.length;

        this.gain = 1;
    }

    /**
     * Укороченный конструктор, в котором передаётся только размер буфера
     * @param lengthOfInputSignal - длина массива входных данных
     */
    private IIRFilter(int lengthOfInputSignal) {
        this.lengthOfInputSignal = lengthOfInputSignal;
        this.gain = 1;

        this.outputSignal = new int[lengthOfInputSignal];
    }

    // Метод, осуществляющий инициализацию
    public static IIRFilter init(double[] coefsFilterA, double[] coefsFilterB, int[] inputSignal) {
        return new IIRFilter(coefsFilterA, coefsFilterB, inputSignal);
    }

    /**
     * Метод, который формирует конструктор класса Filter
     * @param lengthOfInputSignal - длина входного сигнала
     * @return - возвращает фильтр
     */
    public static IIRFilter init(int lengthOfInputSignal) {
        return new IIRFilter(lengthOfInputSignal);
    }

    @Override
    public void setSettingsFilter(double[] coefsFilterA, double[] coefsFilterB, int[] inputSignal) {
        this.inputSignal = inputSignal;

        this.coefsFilterA = coefsFilterA;
        this.coefsFilterB = coefsFilterB;

        this.countCoefs = IIR.COUNT_OF_COEFS;

        // Обнуление массива итоговых значений (чтобы не повторялось одно и то же в процессе воспроизведения)
        Arrays.fill(this.outputSignal, (short) 0);
    }

    @Override
    public void setSettingsFilter(double[] coefsFilterA, double[] coefsFilterB) {
        this.coefsFilterA = coefsFilterA;
        this.coefsFilterB = coefsFilterB;

        this.countCoefs = IIR.COUNT_OF_COEFS;
    }

    @Override
    public void setInputSignal(int[] inputSignal) {
        this.inputSignal = inputSignal;

        // Обнуляем массив выходных значений
        Arrays.fill(this.outputSignal, (short) 0);
    }

    @Override
    public void setGain(double gain) {
        this.gain = gain;
    }

    private void convolution() {
        int size = 100;

        double[] regX = new double[size];
        double[] regY = new double[size];

        // По-хорошему инициализация должна быть уже на этапе создания массива
        for (int i = 0; i < size; ++i) {
            regX[i] = regY[i] = 0.;
        }

        double center;
        for (int i = 0; i < this.inputSignal.length; ++i) {
            for (int k = IIR.COUNT_OF_COEFS - 1; k > 0; --k) {
                regX[k] = regX[k - 1];
            }

            for (int k = IIR.COUNT_OF_COEFS - 1; k > 0; --k) {
                regY[k] = regY[k - 1];
            }

            center = 0.;

            // Numerator
            regX[0] = inputSignal[i];

            for (int k = 0; k <= IIR.COUNT_OF_COEFS - 1; ++k) {
                center += (coefsFilterB[k] / 100) * regX[k];
            }

            // Denominator
            regY[0] = center * coefsFilterA[0];
            for (int k = 1; k <= IIR.COUNT_OF_COEFS - 1; ++k) {
                regY[0] -= coefsFilterA[k] * regY[k];
            }

            outputSignal[i] = (int) (this.gain * regY[0]);
        }
    }

    @Override
    public int[] call() throws Exception {
        this.convolution();
        return this.outputSignal;
    }
}
