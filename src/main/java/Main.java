import com.google.gson.Gson;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static final String filePath = "src/main/resources/tickets.json";

    public static void main(String[] args) {

        List<Double> diffList = new ArrayList<>();

        Gson gson = new Gson();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            Tickets tickets = gson.fromJson(br, Tickets.class);

            if (tickets != null) {

                for (TicketInfo info : tickets.getTickets()) {
                    String departure = info.getDeparture_date() + " " + info.getDeparture_time();
                    String arrive = info.getArrival_date() + " " + info.getArrival_time();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

                    LocalDateTime dateDeparture = LocalDateTime.parse(departure, formatter);
                    ZoneId Vladivostok_id = ZoneId.of("Asia/Vladivostok");
                    ZonedDateTime VladivostokDateDeparture = ZonedDateTime.of(dateDeparture, Vladivostok_id);

                    LocalDateTime dateArrive = LocalDateTime.parse(arrive, formatter);
                    ZoneId Israel_id = ZoneId.of("Asia/Jerusalem");
                    ZonedDateTime IsraelDateArrive = ZonedDateTime.of(dateArrive, Israel_id);

                    Duration duration = Duration.between(VladivostokDateDeparture, IsraelDateArrive);
                    double diff = Math.abs(duration.toMinutes());

                    diffList.add(diff);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Среднее время полета между городами Владивосток и Тель-Авив : " + averageTime(diffList).getAsDouble() + " мин.");

        System.out.println("90-й процентиль времени полета между городами  Владивосток и Тель-Авив : " + percentile(diffList, 90) + " мин.");

    }

    public static OptionalDouble averageTime(List<Double> diffList) {
        return diffList.stream().mapToDouble(e -> e).average();
    }

    public static double percentile(List<Double> diffList, double percentile) {
        double[] array = diffList.stream().mapToDouble(Double::doubleValue).toArray();
        Percentile percent = new Percentile();
        percent.setData(array);
        return percent.evaluate(percentile);
    }
}

