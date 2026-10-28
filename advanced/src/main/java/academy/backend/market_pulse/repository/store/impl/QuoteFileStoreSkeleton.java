package academy.backend.market_pulse.repository.store.impl;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {

    @Override
    public void save(Path path, List<Quote> quotes) {
        // TODO: сохранить котировки в файл по указанному пути.
        //  Подсказка: на каждую котировку нужно сохранить тикер, тип и валюту инструмента (чтобы
        //  потом восстановить его через InstrumentFactories.create), а также цену и изменение за
        //  период — этого достаточно, чтобы собрать Quote обратно.
        try {

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            List<String> stringQuotes = new ArrayList<>();
            for (Quote quote : quotes) {
                Instrument instrument = quote.getInstrument();
                String stringQuote = String.join(";",
                        instrument.getTicker(),
                        instrument.getType(),
                        instrument.getCurrency().name(),
                        quote.getPrice().toString(),
                        quote.getChangePercent().toString());
                stringQuotes.add(stringQuote);
            }

            Files.write(path, stringQuotes);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        // TODO: прочитать котировки из файла и восстановить при старте.
        //  Если файла ещё нет (первый запуск) — вернуть пустой список, а не бросать исключение.
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            List<Quote> quotes = new ArrayList<>();
            for (String stringQuote : Files.readAllLines(path)) {
                String[] parts = stringQuote.split(";");
                String ticker = parts[0];
                String type = parts[1];
                String currency = parts[2];
                BigDecimal price = new BigDecimal(parts[3]);
                BigDecimal changePercent = new BigDecimal(parts[4]);

                Instrument instrument = InstrumentFactories.create(type, ticker, type, Currency.valueOf(currency));
                Quote quote = new Quote(instrument, price, changePercent);
                quotes.add(quote);
            }
            return quotes;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
