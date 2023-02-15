package Equalizer_Handler;

import Coef.FIR;
import Coef.FIR_1;
import Filters.FIRFilter;
import Inter.IFIREqua;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Класс - эквалайзер (использует КИХ-фильтры для фильтрации звука)
 */
public class FIREqua implements IFIREqua<FIRFilter> {
    private short[] outputSignal;
    private FIRFilter[] FIRfilters;
    ExecutorService executorService;

    // Константы
    private final int lengthOfInputSignal;

    private final static int COUNT_BANDS = 6;
    private final static char COUNT_THREADS = 6;

    /**
     * Конструктор, который в себя принимает длину входного сигнала (буфера)
     * @param lengthOfInputSignal - длина массива с входными сигналами
     */
    public FIREqua(final int lengthOfInputSignal) {
        this.executorService = Executors.newFixedThreadPool(COUNT_THREADS);
        this.lengthOfInputSignal = lengthOfInputSignal;

        this.createFilter();
    }

    // Метод формирует фильтр

    private void createFilter() {
        this.FIRfilters = new FIRFilter[COUNT_BANDS];

        for (int i = 0; i < 6; ++i) {
            this.FIRfilters[i] = FIRFilter.init(this.lengthOfInputSignal);
            this.FIRfilters[i].setSettingsFilter(FIR.COEFS_OF_BAND[i]);
        }

        /*for (int i = 5; i < FIRfilters.length; ++i) {
            this.FIRfilters[i] = FIRFilter.init(this.lengthOfInputSignal);
            this.FIRfilters[i].setSettingsFilter(FIR_1.COEFS_OF_BAND[i - 5]);
        }*/
    }

    /**
     * Метод осуществляет запись входного сигнала и коэффициентов фильтра
     * @param inputSignal - входной сигнал
     */
    @Override
    public void setInputSignal(short[] inputSignal) {
        this.outputSignal = new short[inputSignal.length];

        for (FIRFilter firFilter : this.FIRfilters) {
            firFilter.setInputSignal(inputSignal);
        }
    }

    //Метод, который осуществляет процесс фильтрации сигнала

    @Override
    public void equalization() throws InterruptedException, ExecutionException {
        Future<short[]>[] fs = new Future[FIREqua.COUNT_BANDS];

        for (int i = 0; i < FIREqua.COUNT_BANDS; ++i) {
            fs[i] = executorService.submit(this.FIRfilters[i]);
        }

        // Сумматор, который стоит в самом конце эквалайзера
        for (int i = 0; i < outputSignal.length; ++i) {
            this.outputSignal[i] += fs[0].get()[i] + fs[1].get()[i] + fs[2].get()[i] + fs[3].get()[i] + fs[4].get()[i] +
                    fs[5].get()[i];
        }
    }

    /**
     * Метод возвращает фильтр по конкретному индексу
     * @param nF - индекс фильтра
     * @return - фильтр
     */
    @Override
    public FIRFilter getFilter(short nF) {
        return this.FIRfilters[nF];
    }

    /**
     * Метод, выполняющий возвращение выходного сигнала
     * @return - выходной сигнал
     */
    @Override
    public short[] getOutputSignal() {
        /*try {
            Thread.sleep(0);
        } catch (InterruptedException interruptedException) {
            System.out.println(interruptedException.getMessage());
        }*/
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
