package br.com.itau.geradornotafiscal.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReconciliacaoTotaisValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReconciliacaoTotais {
    String message() default "O valor total dos itens não corresponde à soma dos itens";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
