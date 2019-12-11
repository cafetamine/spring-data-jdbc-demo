package com.cafetamine.spring.data.jdbc.demo.config;

import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.ParsingUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class JdbcNamingStrategyConfig implements NamingStrategy {

    @Override
    public String getTableName(final Class<?> type) {
        Assert.notNull(type, "Type must not be null.");
        return ParsingUtils.reconcatenateCamelCase(type.getSimpleName(), "_").toUpperCase();
    }

    @Override
    public String getColumnName(final RelationalPersistentProperty property) {
        Assert.notNull(property, "Property must not be null.");
        return property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1);
    }

}
