package org.jeecgframework.minidao.annotation.id;

import java.lang.annotation.*;

/**
 * @author scott
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableId {

    /**
     * 字段名（类型为ID_SEQ时必须设置）
     */
    String seqName() default "";

    /**
     * 主键类型
     * {@link IdType}
     */
    IdType type() default IdType.UUID;
}
