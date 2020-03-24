package com.handtruth.mc.paket.util

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Path @PublishedApi internal constructor(val segments: List<String>): Comparable<Path>, Iterable<String> {
    constructor(path: String, delimiter: String = "/") : this(parse(path, delimiter))
    companion object {
        val empty = Path(emptyList())

        operator fun invoke(segments: List<String>) = Path(segments.filter { it.isNotEmpty() })

        private fun parse(path: String, delimiter: String): List<String> {
            val iter = path.split(delimiter).asSequence().filter { it.isNotEmpty() }.iterator()
            if (!iter.hasNext()) {
                return if (path == delimiter)
                    listOf(delimiter)
                else
                    emptyList()
            }
            val first = if (path.startsWith(delimiter))
                "/${iter.next()}"
            else
                iter.next()
            val result = mutableListOf(first)
            while (iter.hasNext())
                result += iter.next()
            return result
        }
    }

    override fun compareTo(other: Path): Int {
        for ((a, b) in segments.zip(other.segments)) {
            val result = a.compareTo(b)
            if (result != 0)
                return result
        }
        val a = segments.size
        val b = other.segments.size
        return when {
            a < b -> -1
            a > b -> +1
            else -> 0
        }
    }

    fun simplify(): Path {
        val result = segments.toMutableList()
        var i = 1
        while (i < result.size) {
            if (result[i] == "..") {
                result.removeAt(i)
                --i
                result.removeAt(i)
                if (i == 0)
                    ++i
            }
        }
        return Path(result.filter { it != "." })
    }

    override fun iterator() = segments.iterator()

    override fun toString(): String {
        return segments.joinToString("/")
    }

    operator fun plus(segment: String) = Path(segments + segment)
    operator fun plus(path: Path) = Path(segments + path.segments)
}
