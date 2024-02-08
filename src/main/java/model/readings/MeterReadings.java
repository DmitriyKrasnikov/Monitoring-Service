package model.readings;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.util.Map;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class MeterReadings {
    @NonNull
    @EqualsAndHashCode.Exclude
    private Map<MeterType, Integer> readings;
    private final Month month;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Месяц: ").append(month).append("\n");
        for (Map.Entry<MeterType, Integer> entry : readings.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}