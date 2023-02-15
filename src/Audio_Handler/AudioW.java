package Audio_Handler;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

// Класс, который осуществляет запись данных в файл

public class AudioW{
    private AudioH audioHead;
    private AudioInputStream ais;
    private AudioFormat af;


    private AudioW(AudioH head) {
        audioHead = head;

        af = new AudioFormat(head.getRateOfFrame(), head.getSizeOfSample() * 8, head.getCountOfChannels(),
                true, false);
        ais = new AudioInputStream(new ByteArrayInputStream(head.getFileInformation()), af, head.getCountOfFrame());
    }


     // Вызываем конструктор класса AudioFileWriter

    public static AudioW init(AudioH header) {
        return new AudioW(header);
    }

    // Метод осуществляет сохранение информации в файл

    public void save (File file) throws IOException {
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
    }
}