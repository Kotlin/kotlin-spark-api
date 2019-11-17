class PartitioningIterator<T>(
    private val source: Iterator<T>,
    private val size: Int,
    private val cutIncomplete: Boolean = false
) : AbstractIterator<List<T>>() {
    override fun computeNext() {
        if (!source.hasNext()) return done()
        val interimResult = arrayListOf<T>()
        repeat(size) {
            if (source.hasNext()) interimResult.add(source.next())
            else return if (cutIncomplete) done() else setNext(interimResult)
        }
        setNext(interimResult)
    }
}

class MappingIterator<T, R>(
    private val self: Iterator<T>,
    private val func: (T) -> R
) : AbstractIterator<R>() {
    override fun computeNext() = if (self.hasNext()) setNext(func(self.next())) else done()
}

class FilteringIterator<T>(
    private val source: Iterator<T>,
    private val predicate: (T) -> Boolean
) : AbstractIterator<T>() {
    override fun computeNext() {
        while (source.hasNext()) {
            val next = source.next()
            if (predicate(next)) {
                setNext(next)
                return
            }
        }
        done()
    }
}
fun <T, R> Iterator<T>.map(func: (T) -> R): Iterator<R> = MappingIterator(this, func)

fun <T> Iterator<T>.filter(func: (T) -> Boolean): Iterator<T> = FilteringIterator(this, func)

fun <T> Iterator<T>.partition(size: Int): Iterator<List<T>> = PartitioningIterator(this, size)

