package academy.backend.market_pulse.history.impl;

import java.io.IOException;
import java.io.PipedReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    @Override
    public Path append(Path directory, PricePoint point) {
        // TODO: записать точку в новый файл внутри directory — существующие файлы не трогать.
        //  Подсказка: имя файла на основе point.timestamp() даёт сортировку по имени = сортировку
        //  по времени.
        try {
            Files.createDirectories(directory);
            String fileName = point.ticker() + "-" + point.timestamp().toEpochMilli() + ".md";
            Path path = directory.resolve(fileName);
            Files.writeString(path, point.price().toString());
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        // TODO: прочитать все точки по тикеру в порядке возрастания времени.
        //  Если для тикера ещё нет точек — вернуть пустой список, а не бросать исключение.
        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }
        try (Stream<Path> filesStream = Files.list(directory)) {
            return filesStream.filter(p -> p.getFileName().toString().startsWith(ticker + "-"))
                    .map( p -> {
                        try {
                            String[] fileParts = p.getFileName().toString().split("-");
                            long time = Long.parseLong(fileParts[1].replace(".md", ""));
                            BigDecimal price = new BigDecimal(Files.readString(p).trim());
                            PricePoint pricePoint = new PricePoint(ticker, Instant.ofEpochMilli(time), price);
                            return pricePoint;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).sorted((p1, p2) -> p1.timestamp().compareTo(p2.timestamp())).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
