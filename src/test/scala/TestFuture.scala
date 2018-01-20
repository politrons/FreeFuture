package com.politrons.future

import org.junit.Test


class TestFuture extends FutureDSL {


  @Test
  def futureZip() {
    printThreadInfo("init")
    ParallelFunctions(getSentence, getSecondSentence, concat)
      .doNext(upperCase)
      .appendResult()
      .subscribe
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def futureZipWithErrors() {
    printThreadInfo("init")
    ParallelFunctions(getSentence, () => null.asInstanceOf[String], concat)
      .doNext(upperCase)
      .appendResult()
      .subscribe
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def waitingForFuture() {
    printThreadInfo("init")
    FutureFunction(() => getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .doNext(replace("AWESOME", "cool"))
      .doNext(upperCase)
      .appendResult()
      .subscribe
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def waitingForMultipleFuture() {
    printThreadInfo("init")
    FutureFunction(getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .doNewFuture(replace("AWESOME", "cool"))
      .doNext(upperCase)
      .appendResult()
      .subscribe
    Thread.sleep(2000)
    println(result)
  }

  @Test
  def withoutWait() {
    printThreadInfo("init")
    FutureFunction(getSentence)
      .doNext(upperCase)
      .doNext(concat(". This is awesome!!"))
      .doNext(upperCase)
      .appendResult()
      .subscribe
    println(result)
  }

  def getSentence: () => Either[String, String] = {
    printThreadInfo("sentence")
    () => new Right("hello future DSL world")
  }

  def getSecondSentence: () => Either[String, String] = {
    printThreadInfo("sentence")
    () => new Right(" run in parallel is awesome")
  }

  def upperCase: (String => Option[String]) = a => {
    printThreadInfo("upper")
    Thread.sleep(200)
    Option(a.toUpperCase)
  }

  def concat(value: String): (String => String) = a => {
    printThreadInfo("concat")
    a.concat(value)
  }

  def concat: ((String, String) => String) = (a, b) => {
    printThreadInfo("concat")
    a.concat(b)
  }

  def replace(old: String, newValue: String): (String => String) = a => {
    printThreadInfo("replace")
    a.replace(old, newValue)
  }

  def printThreadInfo(step: String): Unit = {
    println(s"Thread:$step - ${
      Thread.currentThread().getName
    }")
  }

}


