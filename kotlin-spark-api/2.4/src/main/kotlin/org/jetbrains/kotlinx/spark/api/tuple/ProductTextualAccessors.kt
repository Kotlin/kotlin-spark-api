package org.jetbrains.kotlinx.spark.api.tuple

import scala.Product1
import scala.Product2
import scala.Product3
import scala.Product4
import scala.Product5
import scala.Product6
import scala.Product7
import scala.Product8
import scala.Product9
import scala.Product10
import scala.Product11
import scala.Product12
import scala.Product13
import scala.Product14
import scala.Product15
import scala.Product16
import scala.Product17
import scala.Product18
import scala.Product19
import scala.Product20
import scala.Product21
import scala.Product22

/**
 *
 * This file provides textual accessors instead of _1(), _2() etc. for Scala classes implementing ProductX, like Tuples.
 *
 * This means you can type `yourTuple.first`, `yourTuple.second`, etc., but also `yourTuple.last` to access
 * the value you require, similar to how [Pair] and [Triple] name their values in Kotlin.
 *
 * by Jolan Rensen, 18-02-2021
 */

val <T> Product1<T>.first: T get() = this._1()
val <T> Product1<T>.last: T get() = this._1()
val <T> Product2<T, *>.first: T get() = this._1()
val <T> Product2<*, T>.second: T get() = this._2()
val <T> Product2<*, T>.last: T get() = this._2()
val <T> Product3<T, *, *>.first: T get() = this._1()
val <T> Product3<*, T, *>.second: T get() = this._2()
val <T> Product3<*, *, T>.third: T get() = this._3()
val <T> Product3<*, *, T>.last: T get() = this._3()
val <T> Product4<T, *, *, *>.first: T get() = this._1()
val <T> Product4<*, T, *, *>.second: T get() = this._2()
val <T> Product4<*, *, T, *>.third: T get() = this._3()
val <T> Product4<*, *, *, T>.fourth: T get() = this._4()
val <T> Product4<*, *, *, T>.last: T get() = this._4()
val <T> Product5<T, *, *, *, *>.first: T get() = this._1()
val <T> Product5<*, T, *, *, *>.second: T get() = this._2()
val <T> Product5<*, *, T, *, *>.third: T get() = this._3()
val <T> Product5<*, *, *, T, *>.fourth: T get() = this._4()
val <T> Product5<*, *, *, *, T>.fifth: T get() = this._5()
val <T> Product5<*, *, *, *, T>.last: T get() = this._5()
val <T> Product6<T, *, *, *, *, *>.first: T get() = this._1()
val <T> Product6<*, T, *, *, *, *>.second: T get() = this._2()
val <T> Product6<*, *, T, *, *, *>.third: T get() = this._3()
val <T> Product6<*, *, *, T, *, *>.fourth: T get() = this._4()
val <T> Product6<*, *, *, *, T, *>.fifth: T get() = this._5()
val <T> Product6<*, *, *, *, *, T>.sixth: T get() = this._6()
val <T> Product6<*, *, *, *, *, T>.last: T get() = this._6()
val <T> Product7<T, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product7<*, T, *, *, *, *, *>.second: T get() = this._2()
val <T> Product7<*, *, T, *, *, *, *>.third: T get() = this._3()
val <T> Product7<*, *, *, T, *, *, *>.fourth: T get() = this._4()
val <T> Product7<*, *, *, *, T, *, *>.fifth: T get() = this._5()
val <T> Product7<*, *, *, *, *, T, *>.sixth: T get() = this._6()
val <T> Product7<*, *, *, *, *, *, T>.seventh: T get() = this._7()
val <T> Product7<*, *, *, *, *, *, T>.last: T get() = this._7()
val <T> Product8<T, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product8<*, T, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product8<*, *, T, *, *, *, *, *>.third: T get() = this._3()
val <T> Product8<*, *, *, T, *, *, *, *>.fourth: T get() = this._4()
val <T> Product8<*, *, *, *, T, *, *, *>.fifth: T get() = this._5()
val <T> Product8<*, *, *, *, *, T, *, *>.sixth: T get() = this._6()
val <T> Product8<*, *, *, *, *, *, T, *>.seventh: T get() = this._7()
val <T> Product8<*, *, *, *, *, *, *, T>.eighth: T get() = this._8()
val <T> Product8<*, *, *, *, *, *, *, T>.last: T get() = this._8()
val <T> Product9<T, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product9<*, T, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product9<*, *, T, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product9<*, *, *, T, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product9<*, *, *, *, T, *, *, *, *>.fifth: T get() = this._5()
val <T> Product9<*, *, *, *, *, T, *, *, *>.sixth: T get() = this._6()
val <T> Product9<*, *, *, *, *, *, T, *, *>.seventh: T get() = this._7()
val <T> Product9<*, *, *, *, *, *, *, T, *>.eighth: T get() = this._8()
val <T> Product9<*, *, *, *, *, *, *, *, T>.ninth: T get() = this._9()
val <T> Product9<*, *, *, *, *, *, *, *, T>.last: T get() = this._9()
val <T> Product10<T, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product10<*, T, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product10<*, *, T, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product10<*, *, *, T, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product10<*, *, *, *, T, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product10<*, *, *, *, *, T, *, *, *, *>.sixth: T get() = this._6()
val <T> Product10<*, *, *, *, *, *, T, *, *, *>.seventh: T get() = this._7()
val <T> Product10<*, *, *, *, *, *, *, T, *, *>.eighth: T get() = this._8()
val <T> Product10<*, *, *, *, *, *, *, *, T, *>.ninth: T get() = this._9()
val <T> Product10<*, *, *, *, *, *, *, *, *, T>.tenth: T get() = this._10()
val <T> Product10<*, *, *, *, *, *, *, *, *, T>.last: T get() = this._10()
val <T> Product11<T, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product11<*, T, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product11<*, *, T, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product11<*, *, *, T, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product11<*, *, *, *, T, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product11<*, *, *, *, *, T, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product11<*, *, *, *, *, *, T, *, *, *, *>.seventh: T get() = this._7()
val <T> Product11<*, *, *, *, *, *, *, T, *, *, *>.eighth: T get() = this._8()
val <T> Product11<*, *, *, *, *, *, *, *, T, *, *>.ninth: T get() = this._9()
val <T> Product11<*, *, *, *, *, *, *, *, *, T, *>.tenth: T get() = this._10()
val <T> Product11<*, *, *, *, *, *, *, *, *, *, T>.eleventh: T get() = this._11()
val <T> Product11<*, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._11()
val <T> Product12<T, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product12<*, T, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product12<*, *, T, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product12<*, *, *, T, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product12<*, *, *, *, T, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product12<*, *, *, *, *, T, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product12<*, *, *, *, *, *, T, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product12<*, *, *, *, *, *, *, T, *, *, *, *>.eighth: T get() = this._8()
val <T> Product12<*, *, *, *, *, *, *, *, T, *, *, *>.ninth: T get() = this._9()
val <T> Product12<*, *, *, *, *, *, *, *, *, T, *, *>.tenth: T get() = this._10()
val <T> Product12<*, *, *, *, *, *, *, *, *, *, T, *>.eleventh: T get() = this._11()
val <T> Product12<*, *, *, *, *, *, *, *, *, *, *, T>.twelfth: T get() = this._12()
val <T> Product12<*, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._12()
val <T> Product13<T, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product13<*, T, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product13<*, *, T, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product13<*, *, *, T, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product13<*, *, *, *, T, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product13<*, *, *, *, *, T, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product13<*, *, *, *, *, *, T, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product13<*, *, *, *, *, *, *, T, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product13<*, *, *, *, *, *, *, *, T, *, *, *, *>.ninth: T get() = this._9()
val <T> Product13<*, *, *, *, *, *, *, *, *, T, *, *, *>.tenth: T get() = this._10()
val <T> Product13<*, *, *, *, *, *, *, *, *, *, T, *, *>.eleventh: T get() = this._11()
val <T> Product13<*, *, *, *, *, *, *, *, *, *, *, T, *>.twelfth: T get() = this._12()
val <T> Product13<*, *, *, *, *, *, *, *, *, *, *, *, T>.thirteenth: T get() = this._13()
val <T> Product13<*, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._13()
val <T> Product14<T, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product14<*, T, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product14<*, *, T, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product14<*, *, *, T, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product14<*, *, *, *, T, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product14<*, *, *, *, *, T, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product14<*, *, *, *, *, *, T, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product14<*, *, *, *, *, *, *, T, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product14<*, *, *, *, *, *, *, *, T, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product14<*, *, *, *, *, *, *, *, *, T, *, *, *, *>.tenth: T get() = this._10()
val <T> Product14<*, *, *, *, *, *, *, *, *, *, T, *, *, *>.eleventh: T get() = this._11()
val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, T, *, *>.twelfth: T get() = this._12()
val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, T, *>.thirteenth: T get() = this._13()
val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, *, T>.fourteenth: T get() = this._14()
val <T> Product14<*, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._14()
val <T> Product15<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product15<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product15<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product15<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product15<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product15<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product15<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product15<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product15<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product15<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.twelfth: T get() = this._12()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.thirteenth: T get() = this._13()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.fourteenth: T get() = this._14()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.fifteenth: T get() = this._15()
val <T> Product15<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._15()
val <T> Product16<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product16<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product16<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product16<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product16<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product16<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product16<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product16<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product16<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product16<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.thirteenth: T get() = this._13()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.fourteenth: T get() = this._14()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.fifteenth: T get() = this._15()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.sixteenth: T get() = this._16()
val <T> Product16<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._16()
val <T> Product17<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product17<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product17<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product17<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product17<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product17<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product17<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product17<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product17<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product17<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.fourteenth: T get() = this._14()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.fifteenth: T get() = this._15()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.sixteenth: T get() = this._16()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.seventeenth: T get() = this._17()
val <T> Product17<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._17()
val <T> Product18<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product18<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product18<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product18<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product18<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product18<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product18<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product18<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product18<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product18<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.fourteenth: T get() = this._14()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.fifteenth: T get() = this._15()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.sixteenth: T get() = this._16()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.seventeenth: T get() = this._17()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.eighteenth: T get() = this._18()
val <T> Product18<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._18()
val <T> Product19<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product19<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product19<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product19<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product19<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product19<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product19<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product19<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product19<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product19<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.fourteenth: T get() = this._14()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.fifteenth: T get() = this._15()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.sixteenth: T get() = this._16()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.seventeenth: T get() = this._17()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.eighteenth: T get() = this._18()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.nineteenth: T get() = this._19()
val <T> Product19<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._19()
val <T> Product20<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product20<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product20<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product20<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product20<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product20<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product20<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product20<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product20<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product20<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.fourteenth: T get() = this._14()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.fifteenth: T get() = this._15()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.sixteenth: T get() = this._16()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.seventeenth: T get() = this._17()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.eighteenth: T get() = this._18()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.nineteenth: T get() = this._19()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.twentieth: T get() = this._20()
val <T> Product20<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._20()
val <T> Product21<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product21<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product21<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product21<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product21<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product21<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product21<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product21<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product21<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product21<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.fourteenth: T get() = this._14()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.fifteenth: T get() = this._15()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.sixteenth: T get() = this._16()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.seventeenth: T get() = this._17()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.eighteenth: T get() = this._18()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.nineteenth: T get() = this._19()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.twentieth: T get() = this._20()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.twentyFirst: T get() = this._21()
val <T> Product21<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._21()
val <T> Product22<T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.first: T get() = this._1()
val <T> Product22<*, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.second: T get() = this._2()
val <T> Product22<*, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.third: T get() = this._3()
val <T> Product22<*, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fourth: T get() = this._4()
val <T> Product22<*, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.fifth: T get() = this._5()
val <T> Product22<*, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.sixth: T get() = this._6()
val <T> Product22<*, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.seventh: T get() = this._7()
val <T> Product22<*, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *, *>.eighth: T get() = this._8()
val <T> Product22<*, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *, *>.ninth: T get() = this._9()
val <T> Product22<*, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *, *>.tenth: T get() = this._10()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *, *>.eleventh: T get() = this._11()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *, *>.twelfth: T get() = this._12()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *, *>.thirteenth: T get() = this._13()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *, *>.fourteenth: T get() = this._14()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *, *>.fifteenth: T get() = this._15()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *, *>.sixteenth: T get() = this._16()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *, *>.seventeenth: T get() = this._17()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *, *>.eighteenth: T get() = this._18()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *, *>.nineteenth: T get() = this._19()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *, *>.twentieth: T get() = this._20()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T, *>.twentyFirst: T get() = this._21()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.twentySecond: T get() = this._22()
val <T> Product22<*, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, *, T>.last: T get() = this._22()