package User_graphics;

import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import Audio_Handler.AudioPlay;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Класс-контроллер для нашего файла-дизайна
 */
public class app_GUI implements Initializable {
    @FXML
    private Label distortionCoefLabel, volumeLabel;

    @FXML
    private Button buttonClose, button_pause, button_play, button_select, button_stop;

    @FXML
    private Label myFile;

    @FXML
    private Label label_0, label_1, label_2, label_3, label_4, label_5;

    @FXML
    private LineChart chart1, chart2;

    @FXML
    private CheckBox delayChBox, distortionChBox, graphID;

    @FXML
    private Slider distortionSlider;

    @FXML
    private Slider slider0, slider1, slider2, slider3, slider4, slider5;

    @FXML
    private Slider soundSlider;

    @FXML
    private NumberAxis xAxis1, xAxis2, yAxis1, yAxis2;

    // Поля-массивы, которые обозначают наши графики
    private XYChart.Data[] oldData, newData;

    // Наш аудио плеер
    private AudioPlay audioPlayer;
    private Thread playThread, plotThread;

    // Флаг выхода из программы
    private final int SUCCESS_EXIT = 0;

    // Флаг, означающий необходимость рисования графика
    private boolean graphFlag = false;

    //private double delta = 0.001;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // При инициализации мы сбрасываем все кнопки и чек боксы в состояние недоступности
        this.button_play.setDisable(true);
        this.button_pause.setDisable(true);
        this.soundSlider.setDisable(true);
        this.disableEqualizerSliders(true);

        this.sliderInitial();
        this.checkBoxInitial();
        this.initGraph();
    }

    /**
     * Метод, который инициализирует наш график. Причём инициализация происходит как для исходного, так и для
     * отфильтрованного сигналов
     */
    private void initGraph() {
        XYChart.Series oldGraph = new XYChart.Series<>();
        XYChart.Series newGraph = new XYChart.Series<>();

        // Количество линий на графике берётся, исходя из константы, заданной в классе AudioPlayer
        oldData = new XYChart.Data[AudioPlay.countLinesInGraph];
        newData = new XYChart.Data[AudioPlay.countLinesInGraph];

        for (int i = 0; i < oldData.length; ++i) {
            oldData[i] = new XYChart.Data<>(i, 0);
            oldGraph.getData().add(oldData[i]);

            newData[i] = new XYChart.Data<>(i, 0);
            newGraph.getData().add(newData[i]);
        }

        this.chart1.getData().add(oldGraph);
        this.chart2.getData().add(newGraph);

        this.chart1.setCreateSymbols(false);
        this.chart2.setCreateSymbols(false);

        this.chart1.setAnimated(false);
        this.chart2.setAnimated(false);

        this.chart1.getYAxis();
        this.yAxis1.setLowerBound(-0.03);
        this.yAxis1.setUpperBound(0.04);
        this.xAxis1.setLowerBound(0);
        this.xAxis1.setUpperBound(audioPlayer.countLinesInGraph/2);
        this.xAxis1.setAutoRanging(false);
        this.xAxis1.setTickUnit(audioPlayer.countLinesInGraph/16);
        this.yAxis1.setTickUnit(0.01);
        this.yAxis1.setAnimated(false);
        this.yAxis1.setAutoRanging(false);

        this.chart2.getYAxis();
        this.yAxis2.setLowerBound(-0.03);
        this.yAxis2.setUpperBound(0.04);
        this.xAxis2.setLowerBound(0);
        this.xAxis2.setUpperBound(audioPlayer.countLinesInGraph/2);
        this.xAxis2.setAutoRanging(false);
        this.xAxis2.setTickUnit(audioPlayer.countLinesInGraph/16);
        this.yAxis2.setTickUnit(0.01);
        this.yAxis2.setAnimated(false);
        this.yAxis2.setAutoRanging(false);
    }

    /**
     * Событие для слайдера - мы активируем его тогда, когда сдвинули ползунок
     */
    private void sliderInitial() {
        this.soundSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue,
                                                      Number newValue) -> {
            this.audioPlayer.setVolume(newValue.doubleValue());
            this.volumeLabel.setText(" " + (int) (newValue.doubleValue() * 100) + "%");
        });

        this.distortionSlider.valueProperty().addListener((ObservableValue<? extends Number> observable,
                                                           Number oldValue, Number newValue) -> {
            this.audioPlayer.changeDistortionCoef(newValue.doubleValue());
            this.distortionCoefLabel.setText(" " + String.format("%.2f", newValue.doubleValue()));
        });

        // Инициализация слайдеров эквалайзера
        this.slider0.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_0.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 0).setGain(newNumber.doubleValue());
        });

        this.slider1.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_1.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 1).setGain(newNumber.doubleValue());
        });

        this.slider2.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_2.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 2).setGain(newNumber.doubleValue());
        });

        this.slider3.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_3.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 3).setGain(newNumber.doubleValue());
        });

        this.slider4.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_4.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 4).setGain(newNumber.doubleValue());
        });

        this.slider5.valueProperty().addListener((observableValue, oldNumber, newNumber) -> {
            String str = String.format("%d", (int) Math.floor((newNumber.doubleValue() - 1) * 40));
            this.label_5.setText(str + " db");
            this.audioPlayer.getFirEqualizer().getFilter((short) 5).setGain(newNumber.doubleValue());
        });

    }

    // Здесь мы инициализируем наши чек боксы
    private void checkBoxInitial() {
        this.delayChBox = new CheckBox();
        this.distortionChBox = new CheckBox();
        this.graphID = new CheckBox();

        this.delayChBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable,
                                                        Boolean oldValue, Boolean newValue) -> {
            throw new UnsupportedOperationException("Not supported yet.");
        });

        this.distortionChBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable,
                                                             Boolean oldValue, Boolean newValue) -> {
            throw new UnsupportedOperationException("Not supported yet.");
        });

        this.graphID.selectedProperty().addListener((ObservableValue<? extends Boolean> observable,
                                                     Boolean oldValue, Boolean newValue) -> {
            throw new UnsupportedOperationException("No supported yet");
        });
    }

    /**
     * Метод, который рисует график по нажатию на чекбокс
     */
    @FXML
    private void clickPlot() {
        this.graphFlag = !this.graphFlag;

        // Всё исполнение будет происходить в другом потоке
        this.plotThread = new Thread(() -> {
            while (this.graphFlag) {
                /**
                 * Принцип работы таков: пока флаг не активен, то мы идём в бесконечный цикл пока флаг не
                 * де активен. Как только флаг включён продолжаем рисовать график
                 */
                if (!this.graphFlag) {
                    for(;;) {
                        try {
                            if (this.graphFlag) {
                                break;
                            }

                            this.plotThread.sleep(5);
                        } catch (Exception exception) {
                            System.out.println(exception.getStackTrace());
                        }
                    }
                }

                // Вставляем сюда наш график (до фильтрации и после)
                // Одно наблюдение: новый график немного запаздывает, но на визуальный результат во время фильтрации это
                // никак не влияет
                for (int j = 0; j < this.audioPlayer.getFftInput().getFFTDataLength(); ++j) {
                    this.oldData[j].setYValue(Math.log10(this.audioPlayer.getFftInput().getFFTData()[j]) / 100);
                    this.newData[j].setYValue(Math.log10(this.audioPlayer.getFftOutput().getFFTData()[j]) / 100);
                }

                try {
                    this.plotThread.sleep(5);
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        });

        // Проверка на то, что данные не пусты
        if (this.audioPlayer != null && this.audioPlayer.getFftInput().getFFTData() != null) {
            this.plotThread.start();
        }
    }

    /**
     * Событие нажатия чекбокса delay
     * @param event - событие
     */
    @FXML
    void delayBox(ActionEvent event) {
        System.out.println("Click delay checkbox");
        // Переключаем дилей в соответсвующее состояние
        if (this.audioPlayer != null) {
            this.audioPlayer.setDelay(!this.audioPlayer.isActiveDelay());
        }
    }

    /**
     * Событие нажатия чекбокса дисторшн
     * @param event - событие
     */
    @FXML
    void distortionBox(ActionEvent event) {
        System.out.println("Click distortion checkbox");

        // Переключаем дисторшн в соответсвующее состояние
        if (this.audioPlayer != null) {
            this.audioPlayer.setDistortion(!this.audioPlayer.isActiveDistortion());
        }
    }

    /**
     * Событие, которое открывает файл с окном
     * @param event - событие
     */
    @FXML
    void open(ActionEvent event) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Выбор аудио файла!");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null) {
            return;
        }

        // Инициализация файла
        this.audioPlayer = new AudioPlay(selectedFile);

        // Разблокируем кнопку, когда открыли файл
        this.button_play.setDisable(false);
        this.soundSlider.setDisable(false);
        this.delayChBox.setDisable(false);
        this.distortionChBox.setDisable(false);
        this.graphID.setDisable(false);

        // Добавляем текст в соответсвующий лейбл
        this.myFile.setText(" " + selectedFile.getName());

        // Запускаем все слайдеры
        this.disableEqualizerSliders(false);
    }

    /**
     * Событие, которое запускает наш аудиофайл
     */
    @FXML
    void play() {
        if (this.audioPlayer != null) {
            if (!this.audioPlayer.isPaused()) {
                this.playThread = new Thread(() -> {
                    this.audioPlayer.play();

                    // По завершению работы кнопка будет менять своё состояние
                    this.button_play.setDisable(false);
                    this.button_pause.setDisable(true);
                });
                this.playThread.start();
            }

            // Если выходим из паузы, то нужно его указывать
            this.audioPlayer.setPause(false);

            // Блокируем кнопки
            this.button_play.setDisable(true);
            this.button_pause.setDisable(false);

            // Пишем в консоль (в дальнейшем можно и в лог), на всякий случай
            System.out.println("ЗАПУСК");
        }
    }

    /**
     * Событие, которое возникает при нажатии на кнопку pause
     */
    @FXML
    void pause() {
        if (this.audioPlayer != null) {
            this.audioPlayer.setPause(true);

            // Блокируем кнопки в нужной последовательности
            this.button_pause.setDisable(true);
            this.button_play.setDisable(false);

            System.out.println("ПАУЗА");
        }
    }

    /**
     * Событие, которое вызывается, когда хотим завершить программу
     */
    @FXML
    void close() {
        if (this.audioPlayer != null) {
            if (this.playThread != null) {
                this.playThread.interrupt();
            }

            // Закрываем все эквалайзеры
            this.audioPlayer.getFirEqualizer().close();
            this.audioPlayer.getIIREqualizer().close();

            // Закрываем аудио плеер
            this.audioPlayer.closeAudioPlayer();
        }

        // Закрываем программу таким образом, потому что простое закрытие не приводит к завершению программы
        System.exit(SUCCESS_EXIT);
    }

    /**
     * Метод, обрабатывающий нажатие кнопки reset
     */
    @FXML
    protected void reset() {
        this.slider0.setValue(1.0);
        this.slider1.setValue(1.0);
        this.slider2.setValue(1.0);
        this.slider3.setValue(1.0);
        this.slider4.setValue(1.0);
        this.slider5.setValue(1.0);


        this.soundSlider.setValue(0.15);
        this.distortionSlider.setValue(1);
    }

    /**
     * Метод, который отключает или наоборот включает все слайдеры для нашего аудиофайла
     * @param flag - флаг переключения. disable - отключает блокировку, true - включает
     */
    private void disableEqualizerSliders(boolean flag) {
        this.slider0.setDisable(flag);
        this.slider1.setDisable(flag);
        this.slider2.setDisable(flag);
        this.slider3.setDisable(flag);
        this.slider4.setDisable(flag);
        this.slider5.setDisable(flag);
    }
}