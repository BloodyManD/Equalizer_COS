package Math;

import java.util.Arrays;

// Класс - структура данных "кольцевая очередь"

public class CBuff {
    // массив, где непосредственно будут храниться значения
    private short[] buffer;
    // Размер массива
    private int SIZE;
    // Указатели на начало и конец кольцевого буфера
    private int front;
    private int rear;
    // Флаг блокировки кольцевого буфера
    private boolean isLocked;
    // Флаг, означающий, что данные перестали поступать в кольцевой буфер
    private boolean isStopUpdateData;

    /**
     * Конструктор кольцевого буфера
     * @param size - размер кольцевого буфера
     */
    public CBuff(int size) {
        SIZE = size;

        // В начале жизни инициализируем всё как -1
        front = -1;
        rear = -1;

        isStopUpdateData = false;

        buffer = new short[SIZE];
    }

    /**
     * Метод проверяет, пуста ли очередь
     * @return - пуст ли массив (указатель на голову должен быть равен хвосту)
     */
    public boolean isEmpty() {
        return front == -1;
    }

    /**
     * Метод осуществляет проверку на полную заполненность кольцевого буфера
     * @return - true, если буфер полностью заполнен, false - в противном случае
     */
    public boolean isFull() {
        if (front == 0 && rear == SIZE - 1) {
            return true;
        }

        return front == (rear + 1);
    }

    /**
     * Метод осуществляет вставку данных в кольцевой буфер
     * @param data - вставляемая дата
     * @throws Exception - ошибка, если вдруг буфер уже полностью заполнен
     */
    public void push(short data) throws Exception {
        if (isFull()) {
            throw new Exception("The queue is full. You're need to clear the data");
        } else {
            if (front == -1) {
                front = 0;
            }

            rear = (rear + 1) % SIZE;
            buffer[rear] = data;
        }
    }

    /**
     * Метод, который осуществляет удаление данных из кольцевого буфера
     * @return - удалённую информацию
     * @throws Exception - ошибка, если вдруг буфер пуст и идёт попытка удаления пустого информации
     */
    public short pop() throws Exception {
        if (isEmpty()) {
            throw new Exception("The queue is empty. You're need to add the data");
        } else {
            short result = buffer[front];
            if (front == rear) {
                front = -1;
                rear = -1;
            } else {
                front = (front + 1) % SIZE;
            }

            return result;
        }

    }

    /**
     * Метод проверяет, заблокирована ли очередь
     * @return - true, если данные в кольцо перестают поступать и очередь опустела.
     * False, если условие истинности не выполнено
     */
    public boolean isLocked() {
        return (isStopUpdateData && isEmpty());
    }

    /**
     * Проверяет, приостановлено ли обновление данных
     * @return - возвращает true, если остановлено, false в противном случае
     */
    public boolean isStopUpdateData() {
        return isStopUpdateData;
    }

    /**
     * Метод, который даёт сигнал о том, что данные больше не будут записываться. И, если очередь окажется пустой, то
     * очередь необходимо заблокировать (то есть воспроизведение файла завершено)
     */
    public void stopUpdate() {
        isStopUpdateData = true;
    }

    /**
     * Получаем размер буфера
     * @return - возвращает размер буфера
     */
    public int getSIZE() {
        return SIZE;
    }

    /**
     * Вывод информации по поводу заполненного кольцевого буфера
     * @return - строку, в которой записана основная информация по поводу буфера
     */
    @Override
    public String toString() {
        return "CQueue{" +
                "buffer=" + Arrays.toString(buffer) +
                ", bufferSize=" + SIZE +
                ", head=" + front +
                ", tail=" + rear +
                ", isLocked=" + isLocked +
                ", isStopUpdateData=" + isStopUpdateData +
                '}';
    }

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
    }
}
