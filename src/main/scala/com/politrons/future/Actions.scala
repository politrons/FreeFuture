package com.politrons.future

import scala.concurrent.Future
import scalaz.Free.liftF
import scalaz.~>

trait Actions extends Algebras {

  implicit class customFree(free: ActionMonad[Any]) {

    def doNext[T](f: T => Any): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_OnNext(any.asFuture, f)))
    }

    def doNewFuture[T](f: T => Any): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_DoNewFuture(any.asFuture, f)))
    }

    def appendResult(): ActionMonad[Any] = {
      free.flatMap(any => liftF[Action, Any](_WhenFinish(any.asFuture)))
    }

    def subscribe(onNext: Any => Unit = _ => Unit,
                  onError: Throwable => Unit = _ => Unit,
                  onComplete: () => Unit = () => Unit): FutureM[Any] = {
      exec(free.flatMap(any => liftF[Action, Any](_Subscribe(any.asFuture, onNext, onError, onComplete))))
    }

    def exec(free: ActionMonad[Any]): FutureM[Any] = free.foldMap(interpreter)

  }

  def interpreter: Action ~> FutureM

  implicit class customAny(any: Any) {

    def asFuture = any.asInstanceOf[Future[Any]]

    def asEither = any.asInstanceOf[Either[Any, Any]]

    def asOption = any.asInstanceOf[Option[Any]]

    def asFutureM[A] = any.asInstanceOf[FutureM[A]]

  }

}
