package com.dslplatform.api.patterns

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import java.lang.reflect.{ParameterizedType, Type}

/**
 * Service for resolving other services.
 * One locator per project should be used.
 * <p>
 * When multiple projects are used, locator must be passed around
 * to resolve appropriate service.
 * <p>
 * Custom classes can be resolved if their dependencies can be satisfied.
 */
trait ServiceLocator {
  /**
   * Resolve a service registered in the locator.
   *
   * @param tpe   class or interface
   * @return      registered implementation
   */
  def resolve[T](tpe: Type): T

  /**
   * Resolve a service registered in the locator.
   *
   * @param clazz class or interface
   * @return      registered implementation
   */
  def resolve[T](clazz: Class[T]): T =
    resolve[T](clazz.asInstanceOf[Type])

  /**
   * Resolve a service registered in the locator.
   * Warning: generic types are erased at compile time.
   *
   * @param T class info
   * @return  registered implementation
   */
  def resolve[T](implicit ct: ClassTag[T]) : T =
    resolve(ct.runtimeClass)

  /**
   * Resolve a service registered in the locator.
   * Warning: Scala TypeTag is not thread safe, calling code must be
   * guarded with synchronized block
   * As a workaround, use TypeReference method
   *
   * @param T Type info
   * @return  registered implementation
   */
  def resolveUnsafe[T: TypeTag]: T

  /**
   * Resolve a service registered in the locator.
   * Warning: generic types are erased at compile time.
   *
   * @param T class info
   * @return  registered implementation
   */
  def resolve[T](typeReference: TypeReference[T]) : T = {
    require(typeReference ne null, "Type reference can't be null")
    resolve(typeReference.tpe)
  }
}

abstract class TypeReference[T] {
  val tpe: Type = {
    getClass.getGenericSuperclass match {
      case sc: ParameterizedType =>
        sc.getActualTypeArguments()(0)
      case cl =>
        throw new RuntimeException("Missing type parameter. Found: " + cl)
    }
  }
}
