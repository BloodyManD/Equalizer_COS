package Audio_Handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.*;


// Класс, который осуществляет чтение данных из определённого файлового дескриптора

public class AudioR {
    private AudioInputStream ais;
    private AudioFormat af;
    private SourceDataLine sourceDataLine;

    // Конструктор, в который помещаем наш аудио-файл

    private AudioR(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        // Открываем дескриптор для чтения аудиофайла и получаем формат файла
        this.ais = AudioSystem.getAudioInputStream(file);
        this.af = ais.getFormat();
        this.sourceDataLine = AudioSystem.getSourceDataLine(this.af);
    }

    // Реализация паттерна одиночка
    public static AudioR init(File file) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        return new AudioR(file);
    }

    // Геттер аудио формата
    public AudioFormat getAudioFormat() {
        return this.af;
    }

    // Геттер текущей линии
    public SourceDataLine getSourceDataLine() {
        return this.sourceDataLine;
    }

    // Геттер текущего аудиопотока
    public AudioInputStream getAis() {
        return this.ais;
    }
}
