package Math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Класс, который осуществляет конвертацию из типа short[] в byte[] и наоборот
 */
public class Convert {
    /**
     * Метод, который осуществляется конвертацию из byte[] в short[]
     * @param inputData - входной поток (из музыкального файла)
     * @return - сконвертированный результат
     */
    public static short[] convertFromByteToShort(byte[] inputData, double volume) {
        short[] toReturn = new short[inputData.length / 2];

        for (int i = 0, j = 0; i < inputData.length; i += 2, j++) {
            toReturn[j] = (short) (((ByteBuffer.wrap(inputData, i, 2).order(
                    ByteOrder.LITTLE_ENDIAN).getShort()) >> 1 )* volume);
        }

        return toReturn;
    }

    /**
     * Метод, который осуществляется конвертацию из byte[] в int[]
     * @param inputData - входной поток (из музыкального файла)
     * @return - сконвертированный результат
     */
    public static int[] convertFromByteToInt(byte[] inputData, double volume) {
        int[] toReturn = new int[inputData.length / 2];

        for (int i = 0, j = 0; i < inputData.length; i += 2, j++) {
            toReturn[j] = (int) (0.5 * (ByteBuffer.wrap(inputData, i, 2).order(
                    ByteOrder.LITTLE_ENDIAN).getShort()) * volume);
        }

        return toReturn;
    }

    /**
     * Метод, осуществляющий конвертацию из short[] в byte[]
     * @param inputData - входной поток
     * @return - возвращаемые данные
     */
    public static byte[] convertFromShortToByte(short[] inputData) {
        byte[] toReturn = new byte[inputData.length * 2];

        for (int i = 0, j = 0; i < inputData.length && j < (toReturn.length); i++, j += 2) {
            toReturn[j] = (byte) inputData[i];
            toReturn[j + 1] = (byte) (inputData[i] >>> 8);
        }

        return toReturn;
    }

    public static byte[] convertFromIntToByte(int[] inputData) {
        byte[] toReturn = new byte[inputData.length * 2];

        for (int i = 0, j = 0; i < inputData.length && j < (toReturn.length); i++, j += 2) {
            toReturn[j] = (byte) inputData[i];
            toReturn[j + 1] = (byte) (inputData[i] >>> 8);
        }

        return toReturn;
    }
}