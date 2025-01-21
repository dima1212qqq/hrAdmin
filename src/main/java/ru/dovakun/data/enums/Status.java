package ru.dovakun.data.enums;

public enum Status {
    NEW("Новый"),
    THINK("Подумать"),
    REJECTED("Отклонено"),
    ADOPTED("Принят");

    private final String translationKey;

    Status(String translationKey) {
        this.translationKey = translationKey;
    }
    public String getTranslationKey() {
        return translationKey;
    }
}
