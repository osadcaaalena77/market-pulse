package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, построчный текст и т.д.); главное,
 * чтобы {@code load(save(tickers))} возвращал тот же набор тикеров в том же порядке. Проверьте себя
 * тестом {@code WatchlistStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class WatchlistStoreSkeleton implements WatchlistStore {

    @Override
    public void save(Path path, Set<String> tickers) {
        // TODO: сохранить тикеры watchlist-а в файл по указанному пути, сохранив порядок.
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, tickers);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить watchlist",e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        // TODO: прочитать тикеры watchlist-а из файла.
        //  Если файла ещё нет (первый запуск) — вернуть пустой набор, а не бросать исключение.
        if (!Files.exists(path)) {
            return new LinkedHashSet<>();
        }
        try {
            return new LinkedHashSet<>(Files.readAllLines(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
