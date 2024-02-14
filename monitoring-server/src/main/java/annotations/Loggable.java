package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация Loggable, которая может быть применена к методам или классам.
 * Эта аннотация может быть использована для указания того, что метод или класс должен быть залогирован.
 *
 * @Retention(RetentionPolicy.RUNTIME) - указывает, что аннотация должна быть доступна во время выполнения JVM.
 * @Target({ElementType.METHOD, ElementType.TYPE}) - указывает, что аннотация может быть применена к методам или классам.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {
}
