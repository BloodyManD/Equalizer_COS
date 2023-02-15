package Effects;


/**
 * Класс является реализацией эффекта "задержка"
 */
public class Delay extends Effect_Handler {
    // Поле - выходной аудио-поток
    private short[] outputAudioStream;

    /**
     * Конструктор класса задержки. Является конструктором по умолчанию
     */
    public Delay() {
        super();
    }

    /**
     * Дополнительный конструктор
     * @param inputAudioStream - входной поток данных
     */
    public Delay(short[] inputAudioStream) {
        super();

        this.setSettings(inputAudioStream);
    }

    /**
     * Сеттер, который устанавливает текущий аудио-поток
     * @param inputAudioStream - текущий аудио-поток (входные данные)
     */
    public void setInputAudioStream(short[] inputAudioStream) {
        this.setSettings(inputAudioStream);
    }

    private void setSettings(short[] inputAudioStream) {
        this.inputAudioStream = inputAudioStream;
        this.outputAudioStream = new short[this.inputAudioStream.length];

        System.arraycopy(this.inputAudioStream, 0, this.outputAudioStream, 0, this.outputAudioStream.length);
    }


    // Метод, который реализует эффект "Дилей"
    @Override
    public synchronized short[] createEffect() {
        short delayAmplitude;
        int delay = 10000;

        for (int i = 0; i < this.outputAudioStream.length - delay; ++i) {
            delayAmplitude = (short) (this.outputAudioStream[i] * 0.5);
            this.outputAudioStream[i + delay] += delayAmplitude;
        }
        return this.outputAudioStream;
    }

    /**
     * Геттер обычного массива входных данных
     * @return - массив входных данных
     */
    @Override
    public short[] getOutputAudioStream() {
        return this.outputAudioStream;
    }
}
