package com.histsys.data.querys;

import com.histsys.data.querys.search.SearchRequest;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static io.github.biezhi.anima.Anima.select;

public interface Searchable<T> {

    /**
     * 读取 pojoFilter 的每一个字段，根据对应的 view 类别进行查询语句的拼装
     *
     * @param searchRequest key-value map, by dist view class type
     * @return
     */
    default <S> Page<T> search(SearchRequest<S> searchRequest) {
        try {
            StringBuffer buffer = new StringBuffer();
            // filter
            Object filter = searchRequest.getFilter();
            if (filter != null) {
                StringBuffer filterBuffer = new StringBuffer();
                boolean activeFlag = false; // 是否有前置 where 语句
                Field[] fields = filter.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(filter);
                    if (value == null || value.toString().isBlank()) continue;
                    // 解析出此处的 k-v
                    String name = JpaSqlSearchHelper.getColumnName(field);
                    Class type = field.getType();
                    if (type.isEnum()) {
                        // 获取序列 index
                        Object[] enums = type.getEnumConstants();
                        int index = -1;
                        for (int i = 0; i < enums.length; i += 1) {
                            if (enums[i] == value) {
                                index = i;
                            }
                        }
                        if (index == -1) continue;
                        // append
                        if (activeFlag) filterBuffer.append(" AND ");
                        filterBuffer.append(name).append(" = ").append(index); // $name = $i
                        activeFlag = true;
                        continue;
                    } else if (type == String.class) {
                        if (activeFlag) filterBuffer.append(" AND ");
                        filterBuffer.append(name).append(" = '").append(value).append("'");
                        activeFlag = true;
                    } else if (type.isInstance(Integer.class)) {
                        if (activeFlag) filterBuffer.append(" AND ");
                        filterBuffer.append(name).append(" = ").append(value);
                        activeFlag = true;
                    } else if (type.isInstance(Long.class)) {
                        if (activeFlag) filterBuffer.append(" AND ");
                        filterBuffer.append(name).append(" = ").append(value);
                        activeFlag = true;
                    } else if (type.isInstance(Date.class)) {
                        // 暂且处理为 =，后续优化为时间段匹配（需传入一对 date 才行）
                        if (activeFlag) filterBuffer.append(" AND ");
                        filterBuffer.append(name).append(" = '").append(value).append("'");
                        activeFlag = true;
                    }
//                    System.out.println(field);
                }
                // add WHERE sql.
                if (activeFlag && filterBuffer.length() != 0) {
                    buffer.append("WHERE (").append(filterBuffer).append(" )");
                }
            }
            // sort
            HashMap<String, SearchRequest.Order> sort = searchRequest.getSort();
            if (sort != null && !sort.isEmpty()) {
                StringBuffer sortBuffer = new StringBuffer();
                boolean activeFlag = false;
                for (String key : sort.keySet()) {
                    String name = JpaSqlSearchHelper.toXiahuaxian(key);
                    SearchRequest.Order value = sort.get(name);
                    if (value == null) continue;
                    if (activeFlag) sortBuffer.append(", ");
                    sortBuffer.append(name).append(" ").append(value.name());
                    activeFlag = true;
                }
                if (activeFlag && sortBuffer.length() != 0) {
                    buffer.append("ORDER BY ").append(sortBuffer);
                }
            }
            // page
//            int page = searchRequest.getCurrent();
//            int size = searchRequest.getPageSize();
//            int limit = size * page;
            Class tClass = GenericTypeResolver.resolveTypeArgument(this.getClass(), Searchable.class);
//            System.out.println(buffer);
            return JpaSqlSearchHelper.page(tClass, buffer.toString(), searchRequest.getCurrent(), searchRequest.getPageSize());

//            io.github.biezhi.anima.page.Page pageResult = select().bySQL(tClass, buffer.toString()).page(page, limit);
//            return new PageImpl<>(pageResult.getRows(), PageRequest.of(page, size), pageResult.getTotalRows());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>());
    }
}
