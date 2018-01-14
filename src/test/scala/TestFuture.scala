package com.politrons.future

import org.junit.Test


class TestFuture extends FutureDSL {

  @Test
  def waitingForFuture() {
    FutureAction(() => getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .doNext(replace("AWESOME", "cool"))
      .doNext(upperCase)
      .appendResult()
      .~>
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def waitingForMultipleFuture() {
    FutureAction(() => getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .doNewFuture(replace("AWESOME", "cool"))
      .doNext(upperCase)
      .appendResult()
      .~>
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def withoutWait() {
    FutureAction(() => getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .appendResult()
      .~>
    println(result)
  }

  def getSentence: Either[String, String] = {
    printThreadInfo
    new Right("hello future DSL world")
  }

  def upperCase: (String => Option[String]) = a => {
    printThreadInfo
    Thread.sleep(200)
    Option(a.toUpperCase)
  }

  def concat(value: String): (String => String) = a => {
    printThreadInfo
    a.concat(value)
  }

  def replace(old: String, newValue: String): (String => String) = a => {
    printThreadInfo
    a.replace(old, newValue)
  }

  def printThreadInfo: Unit = {
    println(s"Thread:${
      Thread.currentThread().getName()
    }")
  }

}


