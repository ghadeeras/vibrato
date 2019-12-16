package vibrato.testtools;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Generator<T> extends Supplier<T> {

    default Generator<T> filter(Predicate<T> predicate) {
        return () -> Stream.generate(this).filter(predicate).findFirst().orElse(null);
    }

    default <R> Generator<R> map(Function<T, R> mapper) {
        return () -> mapper.apply(get());
    }

    default <R> Generator<R> flatMap(Function<T, Generator<R>> mapper) {
        return () -> mapper.apply(get()).get();
    }

    @SafeVarargs
    static <T> Generator<T> create(Supplier<T> supplier, T... degeneracies) {
        LinkedList<T> degeneraciesList = Stream.of(degeneracies).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
        return () -> degeneraciesList.isEmpty() ? supplier.get() : degeneraciesList.removeFirst();
    }

    static <T> Generator<T> empty() {
        return () -> null;
    }

}
