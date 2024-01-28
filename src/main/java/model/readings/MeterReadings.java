package main.java.model.readings;

import java.time.Month;

/**
 * Класс MeterReadings представляет собой модель данных для хранения показаний счетчиков.
 * Этот класс содержит информацию о показаниях холодной и горячей воды, отопления и месяца, к которому относятся
 * эти показания.
 */
public class MeterReadings {
    private final Integer coldWater;
    private final Integer hotWater;
    private final Integer heating;
    private final Month month;

    public MeterReadings(Integer coldWater, Integer hotWater, Integer heating, Month month) {
        this.coldWater = coldWater;
        this.hotWater = hotWater;
        this.heating = heating;
        this.month = month;
    }

    public Integer getColdWater() {
        return coldWater;
    }

    public Integer getHotWater() {
        return hotWater;
    }

    public Integer getHeating() {
        return heating;
    }

    public Month getMonth() {
        return month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeterReadings that)) return false;

        return month == that.month;
    }

    @Override
    public int hashCode() {
        return month.hashCode();
    }
}
