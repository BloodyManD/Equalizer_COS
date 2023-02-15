package Filters;

import Inter.IFIRFil;


import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Класс - КИХ-фильтр

 */
public class FIRFilter implements Callable<short[]>, IFIRFil {
    // Количество коэффициентов фильтра (порядок фильтра)
    private int countCoefs;
    // Массив коэффициентов фильтра
    private double[] coefsFilter;

    private int lengthOfInputSignal;

    // Входной и выходные сигналы
    private short[] inputSignal;
    private short[] outputSignal;

    // Усиление нашего фильтра (будет использоваться в дальнейшем для эквалайзера)
    private double gain;

    // Конструктор, который инициализирует КИХ-фильтр

    private FIRFilter(double[] coefsFilter, short[] inputSignal) {
        this.coefsFilter = coefsFilter;
        countCoefs = coefsFilter.length;

        this.inputSignal = inputSignal;
        this.outputSignal = new short[inputSignal.length];

        this.lengthOfInputSignal = inputSignal.length;

        this.gain = 1;
    }

    /**
     * Укороченный конструктор, в котором передаётся только размер буфера
     * @param lengthOfInputSignal - длина массива входных данных
     */
    private FIRFilter(int lengthOfInputSignal) {
        this.lengthOfInputSignal = lengthOfInputSignal;
        this.gain = 1;

        this.outputSignal = new short[lengthOfInputSignal];
    }

    // Метод, осуществляющий инициализацию
    public static FIRFilter init(double[] coefsFilter, short[] inputSignal) {
        return new FIRFilter(coefsFilter, inputSignal);
    }

    /**
     * Метод, который формирует конструктор класса Filter
     * @param lengthOfInputSignal - длина входного сигнала
     * @return - возвращает фильтр
     */
    public static FIRFilter init(int lengthOfInputSignal) {
        return new FIRFilter(lengthOfInputSignal);
    }

    /**
     * Внешняя настройка фильтра
     * @param coefsFilter - коэффициенты фильтра
     * @param inputSignal - входной сигнал
     */
    @Override
    public void setSettingsFilter(double[] coefsFilter, short[] inputSignal) {
        this.inputSignal = inputSignal;
        this.coefsFilter = coefsFilter;
        this.countCoefs = coefsFilter.length;

        // Эта строчка важна. Или можно попробовать обнулить имеющийся массив
        Arrays.fill(this.outputSignal, (short) 0);
    }

    /**
     * Метод, который осуществляет настройку параметров фильтра: для данного случая - только вставку коэффициентов
     * @param coefsFilter - коэффициенты фильтра
     */
    @Override
    public void setSettingsFilter(double[] coefsFilter) {
        this.coefsFilter = coefsFilter;
        this.countCoefs = coefsFilter.length;
    }

    /**
     * Метод, который вставляет входной сигнал в фильтр
     * @param inputSignal - входной сигнал
     */
    @Override
    public void setInputSignal(short[] inputSignal) {
        this.inputSignal = inputSignal;

        // Обнуляем массив итоговых значений
        Arrays.fill(this.outputSignal, (short) 0);
    }

    /**
     * Метод, который вставляет уровень усиления сигнала (имитация движения ползунков)
     * @param gain - значение усиления (от 0 до 1)
     */
    @Override
    public void setGain(double gain) {
        this.gain = gain;
    }

    // Метод, который осуществляет фильтрацию данных методом свёртки

    /*private void convolution() {
        int multiplication;
        for (int i = 0; i < inputSignal.length - countCoefs; ++i) {
            for (int j = 0; j < this.countCoefs; ++j) {
                // Используем формулу для КИХ-фильтра
                multiplication = (int) (inputSignal[i] * coefsFilter[j]);
                outputSignal[i + j] += gain * (short) (multiplication);
            }
        }*/
    private void convolution() {
        int[] multiplication = new int[16];
        for (int i = 0; i < inputSignal.length - countCoefs - 4; i += 4) {
            for (int j = 0; j < this.countCoefs - 16; j += 16) {
                // Используем формулу для КИХ-фильтра
                multiplication[0] = (int) (inputSignal[i] * coefsFilter[j]);
                multiplication[1] = (int) (inputSignal[i] * coefsFilter[j + 1]);
                multiplication[2] = (int) (inputSignal[i] * coefsFilter[j + 2]);
                multiplication[3] = (int) (inputSignal[i] * coefsFilter[j + 3]);
                multiplication[4] = (int) (inputSignal[i] * coefsFilter[j + 4]);
                multiplication[5] = (int) (inputSignal[i] * coefsFilter[j + 5]);
                multiplication[6] = (int) (inputSignal[i] * coefsFilter[j + 6]);
                multiplication[7] = (int) (inputSignal[i] * coefsFilter[j + 7]);
                multiplication[8] = (int) (inputSignal[i] * coefsFilter[j + 8]);
                multiplication[9] = (int) (inputSignal[i] * coefsFilter[j + 9]);
                multiplication[10] = (int) (inputSignal[i] * coefsFilter[j + 10]);
                multiplication[11] = (int) (inputSignal[i] * coefsFilter[j + 11]);
                multiplication[12] = (int) (inputSignal[i] * coefsFilter[j + 12]);
                multiplication[13] = (int) (inputSignal[i] * coefsFilter[j + 13]);
                multiplication[14] = (int) (inputSignal[i] * coefsFilter[j + 14]);
                multiplication[15] = (int) (inputSignal[i] * coefsFilter[j + 15]);
                outputSignal[i + j] += gain * (short) (multiplication[0]);
                outputSignal[i + j + 1] += gain * (short) (multiplication[1]);
                outputSignal[i + j + 2] += gain * (short) (multiplication[2]);
                outputSignal[i + j + 3] += gain * (short) (multiplication[3]);
                outputSignal[i + j + 4] += gain * (short) (multiplication[4]);
                outputSignal[i + j + 5] += gain * (short) (multiplication[5]);
                outputSignal[i + j + 6] += gain * (short) (multiplication[6]);
                outputSignal[i + j + 7] += gain * (short) (multiplication[7]);
                outputSignal[i + j + 8] += gain * (short) (multiplication[8]);
                outputSignal[i + j + 9] += gain * (short) (multiplication[9]);
                outputSignal[i + j + 10] += gain * (short) (multiplication[10]);
                outputSignal[i + j + 11] += gain * (short) (multiplication[11]);
                outputSignal[i + j + 12] += gain * (short) (multiplication[12]);
                outputSignal[i + j + 13] += gain * (short) (multiplication[13]);
                outputSignal[i + j + 14] += gain * (short) (multiplication[14]);
                outputSignal[i + j + 15] += gain * (short) (multiplication[15]);

                multiplication[0] = (int) (inputSignal[i + 1] * coefsFilter[j]);
                multiplication[1] = (int) (inputSignal[i + 1] * coefsFilter[j + 1]);
                multiplication[2] = (int) (inputSignal[i + 1] * coefsFilter[j + 2]);
                multiplication[3] = (int) (inputSignal[i + 1] * coefsFilter[j + 3]);
                multiplication[4] = (int) (inputSignal[i + 1] * coefsFilter[j + 4]);
                multiplication[5] = (int) (inputSignal[i + 1] * coefsFilter[j + 5]);
                multiplication[6] = (int) (inputSignal[i + 1] * coefsFilter[j + 6]);
                multiplication[7] = (int) (inputSignal[i + 1] * coefsFilter[j + 7]);
                multiplication[8] = (int) (inputSignal[i + 1] * coefsFilter[j + 8]);
                multiplication[9] = (int) (inputSignal[i + 1] * coefsFilter[j + 9]);
                multiplication[10] = (int) (inputSignal[i + 1] * coefsFilter[j + 10]);
                multiplication[11] = (int) (inputSignal[i + 1] * coefsFilter[j + 11]);
                multiplication[12] = (int) (inputSignal[i + 1] * coefsFilter[j + 12]);
                multiplication[13] = (int) (inputSignal[i + 1] * coefsFilter[j + 13]);
                multiplication[14] = (int) (inputSignal[i + 1] * coefsFilter[j + 14]);
                multiplication[15] = (int) (inputSignal[i + 1] * coefsFilter[j + 15]);
                outputSignal[i + j + 1] += gain * (short) (multiplication[0]);
                outputSignal[i + j + 2] += gain * (short) (multiplication[1]);
                outputSignal[i + j + 3] += gain * (short) (multiplication[2]);
                outputSignal[i + j + 4] += gain * (short) (multiplication[3]);
                outputSignal[i + j + 5] += gain * (short) (multiplication[4]);
                outputSignal[i + j + 6] += gain * (short) (multiplication[5]);
                outputSignal[i + j + 7] += gain * (short) (multiplication[6]);
                outputSignal[i + j + 8] += gain * (short) (multiplication[7]);
                outputSignal[i + j + 9] += gain * (short) (multiplication[8]);
                outputSignal[i + j + 10] += gain * (short) (multiplication[9]);
                outputSignal[i + j + 11] += gain * (short) (multiplication[10]);
                outputSignal[i + j + 12] += gain * (short) (multiplication[11]);
                outputSignal[i + j + 13] += gain * (short) (multiplication[12]);
                outputSignal[i + j + 14] += gain * (short) (multiplication[13]);
                outputSignal[i + j + 15] += gain * (short) (multiplication[14]);
                outputSignal[i + j + 16] += gain * (short) (multiplication[15]);

                multiplication[0] = (int) (inputSignal[i + 2] * coefsFilter[j]);
                multiplication[1] = (int) (inputSignal[i + 2] * coefsFilter[j + 1]);
                multiplication[2] = (int) (inputSignal[i + 2] * coefsFilter[j + 2]);
                multiplication[3] = (int) (inputSignal[i + 2] * coefsFilter[j + 3]);
                multiplication[4] = (int) (inputSignal[i + 2] * coefsFilter[j + 4]);
                multiplication[5] = (int) (inputSignal[i + 2] * coefsFilter[j + 5]);
                multiplication[6] = (int) (inputSignal[i + 2] * coefsFilter[j + 6]);
                multiplication[7] = (int) (inputSignal[i + 2] * coefsFilter[j + 7]);
                multiplication[8] = (int) (inputSignal[i + 2] * coefsFilter[j + 8]);
                multiplication[9] = (int) (inputSignal[i + 2] * coefsFilter[j + 9]);
                multiplication[10] = (int) (inputSignal[i + 2] * coefsFilter[j + 10]);
                multiplication[11] = (int) (inputSignal[i + 2] * coefsFilter[j + 11]);
                multiplication[12] = (int) (inputSignal[i + 2] * coefsFilter[j + 12]);
                multiplication[13] = (int) (inputSignal[i + 2] * coefsFilter[j + 13]);
                multiplication[14] = (int) (inputSignal[i + 2] * coefsFilter[j + 14]);
                multiplication[15] = (int) (inputSignal[i + 2] * coefsFilter[j + 15]);
                outputSignal[i + j + 2] += gain * (short) (multiplication[0]);
                outputSignal[i + j + 3] += gain * (short) (multiplication[1]);
                outputSignal[i + j + 4] += gain * (short) (multiplication[2]);
                outputSignal[i + j + 5] += gain * (short) (multiplication[3]);
                outputSignal[i + j + 6] += gain * (short) (multiplication[4]);
                outputSignal[i + j + 7] += gain * (short) (multiplication[5]);
                outputSignal[i + j + 8] += gain * (short) (multiplication[6]);
                outputSignal[i + j + 9] += gain * (short) (multiplication[7]);
                outputSignal[i + j + 10] += gain * (short) (multiplication[8]);
                outputSignal[i + j + 11] += gain * (short) (multiplication[9]);
                outputSignal[i + j + 12] += gain * (short) (multiplication[10]);
                outputSignal[i + j + 13] += gain * (short) (multiplication[11]);
                outputSignal[i + j + 14] += gain * (short) (multiplication[12]);
                outputSignal[i + j + 15] += gain * (short) (multiplication[13]);
                outputSignal[i + j + 16] += gain * (short) (multiplication[14]);
                outputSignal[i + j + 17] += gain * (short) (multiplication[15]);

                multiplication[0] = (int) (inputSignal[i + 3] * coefsFilter[j]);
                multiplication[1] = (int) (inputSignal[i + 3] * coefsFilter[j + 1]);
                multiplication[2] = (int) (inputSignal[i + 3] * coefsFilter[j + 2]);
                multiplication[3] = (int) (inputSignal[i + 3] * coefsFilter[j + 3]);
                multiplication[4] = (int) (inputSignal[i + 3] * coefsFilter[j + 4]);
                multiplication[5] = (int) (inputSignal[i + 3] * coefsFilter[j + 5]);
                multiplication[6] = (int) (inputSignal[i + 3] * coefsFilter[j + 6]);
                multiplication[7] = (int) (inputSignal[i + 3] * coefsFilter[j + 7]);
                multiplication[8] = (int) (inputSignal[i + 3] * coefsFilter[j + 8]);
                multiplication[9] = (int) (inputSignal[i + 3] * coefsFilter[j + 9]);
                multiplication[10] = (int) (inputSignal[i + 3] * coefsFilter[j + 10]);
                multiplication[11] = (int) (inputSignal[i + 3] * coefsFilter[j + 11]);
                multiplication[12] = (int) (inputSignal[i + 3] * coefsFilter[j + 12]);
                multiplication[13] = (int) (inputSignal[i + 3] * coefsFilter[j + 13]);
                multiplication[14] = (int) (inputSignal[i + 3] * coefsFilter[j + 14]);
                multiplication[15] = (int) (inputSignal[i + 3] * coefsFilter[j + 15]);
                outputSignal[i + j + 3] += gain * (short) (multiplication[0]);
                outputSignal[i + j + 4] += gain * (short) (multiplication[1]);
                outputSignal[i + j + 5] += gain * (short) (multiplication[2]);
                outputSignal[i + j + 6] += gain * (short) (multiplication[3]);
                outputSignal[i + j + 7] += gain * (short) (multiplication[4]);
                outputSignal[i + j + 8] += gain * (short) (multiplication[5]);
                outputSignal[i + j + 9] += gain * (short) (multiplication[6]);
                outputSignal[i + j + 10] += gain * (short) (multiplication[7]);
                outputSignal[i + j + 11] += gain * (short) (multiplication[8]);
                outputSignal[i + j + 12] += gain * (short) (multiplication[9]);
                outputSignal[i + j + 13] += gain * (short) (multiplication[10]);
                outputSignal[i + j + 14] += gain * (short) (multiplication[11]);
                outputSignal[i + j + 15] += gain * (short) (multiplication[12]);
                outputSignal[i + j + 16] += gain * (short) (multiplication[13]);
                outputSignal[i + j + 17] += gain * (short) (multiplication[14]);
                outputSignal[i + j + 18] += gain * (short) (multiplication[15]);
            }
        }
    }

    /**
     * Метод, который возвращает нашу свёртку
     * @return итоговый массив short[]
     */
    @Override
    public short[] call() throws Exception {
        this.convolution();
        return this.outputSignal;
    }
}
