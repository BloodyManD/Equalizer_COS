package Effects;

/**
 * Абстрактный класс, который содержит в себе основные поля и методы для работы с эффектами
 */
public abstract class Effect_Handler {
    protected short[] inputAudioStream;

    public abstract short[] createEffect();
    public abstract short[] getOutputAudioStream();
}
