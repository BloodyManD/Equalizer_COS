package Effects;

public class Distortion extends Effect_Handler {
    // Максимальные и минимальные амплитуды на основании которых мы будем работать
    private short maxAmplitude;
    private short minAmplitude;

    private double coefDistortion;

    private short[] outputAudioStream;

    // Опорные амплитуды, на основании которых мы будем резать наши текущие амплитуды
    private static final short MAX = 6000;
    private static final short MIN = -6000;

    /**
     * Конструктор по умолчанию
     */
    public Distortion() {
        super();
        this.coefDistortion = 0;
    }
    public Distortion(double coefDistortion) {
        super();
        this.coefDistortion = coefDistortion;
    }

    // Метод, который реализует эффект Дисторшн

    @Override
    public synchronized short[] createEffect() {
        // Устанавливаем максимальную и минимальную амплитуду на основании коэффициента дисторшна
        this.maxAmplitude = (short) (Distortion.MAX * this.coefDistortion);
        this.minAmplitude = (short) (Distortion.MIN * this.coefDistortion);

        // Проходим по циклу по всем отсчётам аудио-файла
        for (int i = 0; i < this.inputAudioStream.length; ++i) {
            // Если амплитуда больше разрешённой, то срезаем её
            if (this.inputAudioStream[i] > this.maxAmplitude) {
                this.inputAudioStream[i] = this.maxAmplitude;
            } else if (this.inputAudioStream[i] < this.minAmplitude) {
                this.inputAudioStream[i] = this.minAmplitude;
            }
        }
        return this.inputAudioStream;
    }

    /**
     * Метод, который устанавливает настройки для дисторшна
     * @param inputAudioStream - входной потока
     * @param coefDistortion - коэффициент дисторшна
     */
    public void setSettings(short[] inputAudioStream, double coefDistortion) {
        this.coefDistortion = coefDistortion;

        this.inputAudioStream = inputAudioStream;
        this.outputAudioStream = new short[this.inputAudioStream.length];

        System.arraycopy(this.inputAudioStream, 0, this.outputAudioStream, 0, this.outputAudioStream.length);
    }

    /**
     * Метод возвращает поток аудио-данных
     * @return - поток аудио-данных
     */
    @Override
    public short[] getOutputAudioStream() {
        return this.inputAudioStream;
    }
}