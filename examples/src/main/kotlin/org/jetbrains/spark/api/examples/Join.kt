package org.jetbrains.spark.api.examples

import org.jetbrains.spark.api.*


data class Left(val id: Int, val name: String)

data class Right(val id: Int, val value: Int)


fun main() {
    withSpark {
        val first = dsOf(Left(1, "a"), Left(2, "b"))
        val second = dsOf(Right(1, 100), Right(3, 300))
        first
                .leftJoin(second, first.col("id").eq(second.col("id")))
                .debugCodegen()
                .also { it.show() }
                .map { c(it.first.id, it.first.name, it.second?.value) }
                .show()

    }
}

fun <A> c(a: A) = Arity1(a)
fun <A, B> c(a: A, b: B) = Arity2(a, b)
fun <A, B, C> c(a: A, b: B, c: C) = Arity3(a, b, c)
fun <A, B, C, D> c(a: A, b: B, c: C, d: D) = Arity4(a, b, c, d)
fun <A, B, C, D, E> c(a: A, b: B, c: C, d: D, e: E) = Arity5(a, b, c, d, e)
fun <A, B, C, D, E, F> c(a: A, b: B, c: C, d: D, e: E, f: F) = Arity6(a, b, c, d, e, f)
fun <A, B, C, D, E, F, G> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G) = Arity7(a, b, c, d, e, f, g)
fun <A, B, C, D, E, F, G, H> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) = Arity8(a, b, c, d, e, f, g, h)
fun <A, B, C, D, E, F, G, H, I> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) = Arity9(a, b, c, d, e, f, g, h, i)
fun <A, B, C, D, E, F, G, H, I, J> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) = Arity10(a, b, c, d, e, f, g, h, i, j)
fun <A, B, C, D, E, F, G, H, I, J, K> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K) = Arity11(a, b, c, d, e, f, g, h, i, j, k)
fun <A, B, C, D, E, F, G, H, I, J, K, L> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L) = Arity12(a, b, c, d, e, f, g, h, i, j, k, l)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M) = Arity13(a, b, c, d, e, f, g, h, i, j, k, l, m)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N) = Arity14(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O) = Arity15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P) = Arity16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q) = Arity17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R) = Arity18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S) = Arity19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T) = Arity20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U) = Arity21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V) = Arity22(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V, w: W) = Arity23(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V, w: W, x: X) = Arity24(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V, w: W, x: X, y: Y) = Arity25(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y)
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z> c(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V, w: W, x: X, y: Y, z: Z) = Arity26(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z)
data class Arity1<A>(val a: A)
data class Arity2<A, B>(val a: A, val b: B)
data class Arity3<A, B, C>(val a: A, val b: B, val c: C)
data class Arity4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
data class Arity5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
data class Arity6<A, B, C, D, E, F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)
data class Arity7<A, B, C, D, E, F, G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G)
data class Arity8<A, B, C, D, E, F, G, H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H)
data class Arity9<A, B, C, D, E, F, G, H, I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I)
data class Arity10<A, B, C, D, E, F, G, H, I, J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J)
data class Arity11<A, B, C, D, E, F, G, H, I, J, K>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K)
data class Arity12<A, B, C, D, E, F, G, H, I, J, K, L>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L)
data class Arity13<A, B, C, D, E, F, G, H, I, J, K, L, M>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M)
data class Arity14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N)
data class Arity15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O)
data class Arity16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P)
data class Arity17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q)
data class Arity18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R)
data class Arity19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S)
data class Arity20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T)
data class Arity21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U)
data class Arity22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V)
data class Arity23<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V, val w: W)
data class Arity24<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V, val w: W, val x: X)
data class Arity25<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V, val w: W, val x: X, val y: Y)
data class Arity26<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J, val k: K, val l: L, val m: M, val n: N, val o: O, val p: P, val q: Q, val r: R, val s: S, val t: T, val u: U, val v: V, val w: W, val x: X, val y: Y, val z: Z)
