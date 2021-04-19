package br.com.zupacademy.desafioproposta.compartilhado.validators;

import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static br.com.zupacademy.desafioproposta.compartilhado.validators.CpfOrCnpj.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.validator.constraints.CompositionType.OR;

/**
 * valores null são considerados válidos
 * */
@Documented
@ConstraintComposition(OR)
@CPF
@CNPJ
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { })
@Repeatable(List.class)
public @interface CpfOrCnpj {
    String message() default "{br.com.zupacademy.desafioproposta.compartilhado.validators.CpfOrCnpj.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Documented
    @Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @interface List {
        CpfOrCnpj[] value();
    }
}
