package Audio_Handler;

/**
 * Класс - заголовок аудио-файла
 */
public class AudioH {
    // Количество фреймов
    private long countOfFrame;
    // Количество каналов (1 - моно, 2 - стерео)
    private int countOfChannels;
    // Битность файла (16, 32 бита)
    private int sizeOfSample;
    // Частота дискретизации
    private float rateOfFrame;
    // Информация по файлу
    private byte[] fileInformation;

    // Конструктор, куда записываются и складирываются данные
    AudioH (long countOfFrame, int countOfChannels, int sizeOfSample, float rateOfFrame, byte[] fileInformation) {
        this.countOfFrame = countOfFrame;
        this.countOfChannels = countOfChannels;
        this.sizeOfSample = sizeOfSample;
        this.rateOfFrame = rateOfFrame;
        this.fileInformation = fileInformation;
    }

    public int getSizeOfSample() {
        return sizeOfSample;
    }

    public byte[] getFileInformation() {
        return fileInformation;
    }

    public long getCountOfFrame() {
        return countOfFrame;
    }

    public float getRateOfFrame() {
        return rateOfFrame;
    }

    public int getCountOfChannels() {
        return countOfChannels;
    }

    public void setFileInformation(byte[] newFileInformation) {
        fileInformation = newFileInformation;
    }
}