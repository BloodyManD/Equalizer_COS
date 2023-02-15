package Equalizer_Handler;

import Filters.IIRFilter;
import Coef.IIR;
import Inter.IIIREqua;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Класс - эквалайзер (использует БИХ-фильтры для фильтрации звука)
 */
public class IIREqua implements IIIREqua {
    private int[] outputSignal;
    private IIRFilter[] IIRFilters;
    ExecutorService executorService;

    // Константы
    private final int lengthOfInputSignal;


    private final static int COUNT_BANDS = 10;
    private final static char COUNT_THREADS = 12;

    public IIREqua(final int lengthOfInputSignal) {
        this.executorService = Executors.newFixedThreadPool(COUNT_THREADS);
        this.lengthOfInputSignal = lengthOfInputSignal;

        this.createFilter();
    }

    /**
     * Метод формирует фильтр
     */
    private void createFilter() {
        this.IIRFilters = new IIRFilter[COUNT_BANDS];

        for (int i = 0; i < IIRFilters.length; ++i) {
            this.IIRFilters[i] = IIRFilter.init(this.lengthOfInputSignal);
            this.IIRFilters[i].setSettingsFilter(IIR.RATES_OF_FILTER_A[i], IIR.RATES_OF_FILTER_B[i]);
        }
    }

    /**
     * Метод осуществляет запись входного сигнала и коэффициентов фильтра
     * @param inputSignal - входной сигнал
     */
    @Override
    public void setInputSignal(int[] inputSignal) {
        this.outputSignal = new int[inputSignal.length];

        for (IIRFilter iirFilter: this.IIRFilters) {
            iirFilter.setInputSignal(inputSignal);
        }
    }

    /**
     * Метод, который осуществляет процесс фильтрации сигнала
     * @throws InterruptedException - ошибка в многопоточном приложении
     * @throws ExecutionException - ошибка в исполнении какого-либо потока
     */
    @Override
    public void equalization() throws InterruptedException, ExecutionException {
        Future<int[]>[] fs = new Future[IIREqua.COUNT_BANDS];

        for (int i = 0; i < IIREqua.COUNT_BANDS; ++i) {
            fs[i] = executorService.submit(this.IIRFilters[i]);
        }

        // Сумматор, который стоит в самом конце эквалайзера (см. модель эквалайзера в РПЗ)
        for (int i = 0; i < outputSignal.length; ++i) {
            this.outputSignal[i] += fs[0].get()[i] + fs[1].get()[i] + fs[2].get()[i] + fs[3].get()[i] + fs[4].get()[i] +
                    fs[5].get()[i] + fs[6].get()[i] + fs[7].get()[i] + fs[8].get()[i] + fs[9].get()[i];
        }
    }

    /**
     * Метод возвращает фильтра по конкретному индексу
     * @param nF - индекс фильтра
     * @return - фильтр
     */
    @Override
    public IIRFilter getFilter(short nF) {
        return this.IIRFilters[nF];
    }

    /**
     * Метод, выполняющий возвращение выходного сигнала
     * @return - выходной сигнал
     */
    @Override
    public int[] getOutputSignal() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException interruptedException) {
            System.out.println(interruptedException.getMessage());
        }

        return this.outputSignal;
    }

    /**
     * Закрываем входное многопоточное соединение
     */
    @Override
    public void close() {
        if (this.executorService != null) {
            this.executorService.shutdown();
        }
    }
}
