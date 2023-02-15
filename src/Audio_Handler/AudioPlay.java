package Audio_Handler;

import Effects.Delay;
import Effects.Distortion;
import Math.Convert;
import Math.FFT;
import Equalizer_Handler.FIREqua;
import Equalizer_Handler.IIREqua;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Класс, который осуществляет проигрывание аудио-файла
 */
public class AudioPlay {
    // Переменные, которые необходимы для воспроизведения аудио
    private SourceDataLine sourceDataLine;
    private AudioInputStream ais;
    private AudioR reader;
    private AudioFormat audioFormat;

    private File file;

    // Константа, которая показывает, какое количество линий будет на графике (соответственно и плотность этих графиков)
    public static final int countLinesInGraph = 2048;

    // Наши эквалайзеры (КИХ и БИХ)
    private FIREqua FIREqua;
    private IIREqua IIREqua;

    private Delay delay;
    private Distortion distortion;

    private int bytesRead;
    private double volume;
    private double distortionCoef;

    // Наши данные по буферам
    private byte[] bufferPlaying;
    private short[] sampleBuff;

    // Размер буфера
    private final int BUFFER_SIZE = 131072;
    private final int END = -1;

    // Инициализация флагов
    private boolean isActiveDelay;
    private boolean isActiveDistortion;
    private boolean isPause;

    // Класс - преобразование Фурье. Входное и выходное значение
    private FFT fftInput;
    private FFT fftOutput;

    /**
     * Конструктор аудио плеера
     * @param file - файл
     */
    public AudioPlay(File file) {
        this.file = file;
        this.initAudioInputStream();

        // Буфер, который будет содержать отсчёты для дальнейшего воспроизведения файла
        this.bufferPlaying = new byte[BUFFER_SIZE];
        this.sampleBuff = new short[BUFFER_SIZE / 2];

        // Инициализируем эквалайзер
        this.FIREqua = new FIREqua(BUFFER_SIZE / 2);
        this.IIREqua = new IIREqua(BUFFER_SIZE / 2);

        // Какое количество байт было прочтено из файлового дескриптора
        this.bytesRead = 0;

        // Громкость звучания (для пользователя - в виде ползунка)
        this.volume = 0.15;

        // Коэффициент дисторшна
        this.distortionCoef = 0.0;

        // Инициализация эффектов
        this.delay = new Delay();
        this.distortion = new Distortion(distortionCoef);

        // Инициализация булевых флагов
        this.isActiveDelay = false;
        this.isActiveDistortion = false;
        this.isPause = false;

        this.fftInput = new FFT();
        this.fftOutput = new FFT();
    }

    /**
     * Метод, который воспроизводит трек (также здесь реализуются звуковые эффекты
     * фильтрация звука, остановку/запуск трека и т.п)
     */
    public void play() {
        try {
            // Открываем поток данных на чтение, используя те параметры, которые заданы в коде
            this.sourceDataLine.open(audioFormat);
            this.sourceDataLine.start();

            // Цикл: пока данные есть в потоке, мы их читаем. -1 означает, что данные закончились и пора выходить
            while ((this.bytesRead = ais.read(this.bufferPlaying, 0, BUFFER_SIZE)) != END) {
                this.sampleBuff = Convert.convertFromByteToShort(this.bufferPlaying, this.volume);

                // Делаем отрисовку графика в режиме "до"
                this.fftInput.FFTAnalysis(this.sampleBuff, AudioPlay.countLinesInGraph);
                // Если активен флаг паузы, то прекращаем воспроизводить аудио, путём введения бесконечного цикла
                if (this.isPause) {
                    this.stopPlayingAudio();
                }

                // Проверяем на активность флага дисторшна
                if (this.isActiveDistortion) {
                    this.distortion.setSettings(this.sampleBuff, this.distortionCoef);
                    this.sampleBuff = this.distortion.createEffect();
                }

                // Проверяем на активность задержки. Если флаг активен, то запускаем эффект "задержка"
                if (this.isActiveDelay) {
                    this.delay.setInputAudioStream(this.sampleBuff);
                    this.sampleBuff = this.delay.createEffect();
                }

                // Заводим входной сигнал и проводим процесс выполнения фильтрации

                //this.IIREqualizer.setInputSignal(this.sampleBuff);
                //this.IIREqualizer.equalization();

                this.FIREqua.setInputSignal(this.sampleBuff);
                this.FIREqua.equalization();

                // Получаем выходной сигнал и конвертируем его из short[] в byte[]
                //this.sampleBuff = IIREqualizer.getOutputSignal();
                this.sampleBuff = FIREqua.getOutputSignal();

                // Вызываем преобразование Фурье для отфильтрованного сигнала
                this.fftOutput.FFTAnalysis(this.sampleBuff, AudioPlay.countLinesInGraph);

                // Конвертируем из short[] в byte[]
                this.bufferPlaying = Convert.convertFromShortToByte(this.sampleBuff);

                // Записываем информацию в выходной дескриптор (консоль, наушники, файл)
                this.sourceDataLine.write(this.bufferPlaying, 0, bytesRead);
            }

            System.out.println("END");

            // Закрываем поток на чтение
            this.sourceDataLine.drain();
            this.sourceDataLine.close();

            // На случай, если будем повторно пере воспроизводить трек
            this.initAudioInputStream();

        } catch (LineUnavailableException | IOException | ExecutionException | InterruptedException exception) {
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
    }

    /**
     * Метод возвращает эквалайзер (тип - КИХ)
     * @return - объект эквалайзера, в котором содержатся фильтры, которыми, впоследствии, можно управлять
     */
    public FIREqua getFirEqualizer() {
        return this.FIREqua;
    }

    /**
     * Метод возвращает эквалайзер (тип - БИХ)
     * @return - эквалайзер БИХ-типа
     */
    public IIREqua getIIREqualizer() {
        return this.IIREqua;
    }

    /**
     * Метод возвращает громкость текущего аудио-файла
     * @return - текущее значение громкости
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Метод, который осуществляет изменение громкости воспроизводимого файла
     * @param volume - новое значение громкости (от 0 до 1)
     */
    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     * Сеттеры, которые переводят эффекты в то или иное состояние
     * @param flag - текущий флаг. true / false
     */
    public void setDelay(boolean flag) {
        this.isActiveDelay = flag;
    }

    /**
     * Метод, который переводит эффект Дисторшна в то или иное состояние
     * @param flag - текущий флаг. true/false
     */
    public void setDistortion(boolean flag) {
        this.isActiveDistortion = flag;

        // Проверка на то, единичен ли коэффициент дисторшна
        if (this.distortionCoef == 0) {
            this.distortionCoef = 1;
        }
    }

    /**
     * Метод, который проверяет, активен ли дилей
     * @return - флаг активности дилея
     */
    public boolean isActiveDelay() {
        return this.isActiveDelay;
    }

    /**
     * Метод, который проверяет, активен ли дисторшн
     * @return - флаг активности дисторшна
     */
    public boolean isActiveDistortion() {
        return this.isActiveDistortion;
    }

    /**
     * Метод, который меняет коэффициент дисторшна
     * @param coef - новое значение коэффициента
     */
    public void changeDistortionCoef(double coef) {
        this.distortionCoef = coef;
    }

    /**
     * Метод, который останавливает воспроизведение аудио
     */
    private void stopPlayingAudio() {
        if (this.isPause) {
            for (;;) {
                try {
                    // Если пауза снята
                    if (!this.isPause) {
                        break;
                    }

                    // Принцип задержки таков: мы заглушаем наш поток на 50 мс каждый раз, до тех пор, пока
                    // не снят флаг паузы
                    this.sourceDataLine.flush();
                    Thread.sleep(1);
                } catch (InterruptedException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

    /**
     * Метод, который устанавливает флаг паузы в соответсвующее состояние
     * @param pause - значение паузы. True, если активна. False, в противном случае
     */
    public void setPause(boolean pause) {
        this.isPause = pause;
    }

    /**
     * Метод, который проверяет, активна ли пауза
     * @return - true, если воспроизведение на паузе, false в противном случае
     */
    public boolean isPaused() {
        return this.isPause;
    }

    /**
     * Метод, который закрывает аудио-поток на чтение данных
     */
    public void closeAudioPlayer() {
        if (this.ais != null) {
            try {
                // Закрываем дескриптор аудио файла
                this.ais.close();

                // Также закрываем текущую линию, если она открыта
                if (this.sourceDataLine != null) {
                    this.sourceDataLine.close();
                }
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
            }
        }
    }

    /**
     * Метод, который инициализирует наш поток на чтение
     */
    private void initAudioInputStream() {
        try {
            this.reader = AudioR.init(this.file);

            this.ais = reader.getAis();
            this.audioFormat = reader.getAudioFormat();

            this.sourceDataLine = reader.getSourceDataLine();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод, который возвращает преобразование Фурье для входного (не отфильтрованного) сигнала
     * @return - не отфильтрованный сигнал
     */
    public FFT getFftInput() {
        return this.fftInput;
    }

    /**
     * Метод, который возвращает преобразование Фурье
     * @return - отфильтрованный сигнал для преобразования Фурье
     */
    public FFT getFftOutput() {
        return this.fftOutput;
    }
}