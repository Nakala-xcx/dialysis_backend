package com.histsys.data.querys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;

/**
 * 供 @Searchable 使用
 */
@Service
public class JpaSqlSearchHelper {
    @PersistenceContext
    private EntityManager entityManager;
    private static JpaSqlSearchHelper jpaSql;

    public JpaSqlSearchHelper() {
        jpaSql = this;
    }

    public static <T> Page page(Class<T> tClass, String sql, int page, int size) {
        // count
        String countSql = "SELECT count(*) FROM " + getTableName(tClass) + " " + sql;
        Query countQuery = jpaSql.entityManager.createNativeQuery(countSql);
        BigInteger count = (BigInteger) countQuery.getSingleResult();

        // list
        int offset = (page - 1) * size;
        String selectSql = "SELECT * FROM " + getTableName(tClass) + " " + sql + " LIMIT " + size + " OFFSET " + offset;
        Query query = jpaSql.entityManager.createNativeQuery(selectSql, tClass);
        List list = query.getResultList();
        return new PageImpl(list, PageRequest.of(page - 1, size), count.longValue());
    }

    /**
     * 获取 表 名称
     *
     * @param tClass
     * @return
     */
    public static String getTableName(Class tClass) {
        Table table = (Table) tClass.getDeclaredAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return toXiahuaxian(tClass.getName()) + "s";
    }

    /**
     * 获取 表字段 名称
     *
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        // 查看是否有 Column / JoinColumn 注解
        Column column = field.getDeclaredAnnotation(Column.class);
        if (column != null && !column.name().isBlank()) {
            return column.name();
        }
        JoinColumn joinColumn = field.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isBlank()) {
            return joinColumn.name();
        }
        // default
        return toXiahuaxian(field.getName());
    }


    public static String toXiahuaxian(String camlString) {
        char[] chars = camlString.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isUpperCase(c) && i > 0) {
                buffer.append('_').append(Character.toLowerCase(c));
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
}
