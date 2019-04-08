package com.khaliuk.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T, ID> implements GenericDao<T, ID> {
    protected final Connection connection;

    protected AbstractDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public T save(T t) {
        try {
            PreparedStatement statement = createInsertPreparedStatement(connection, t);
            statement.executeUpdate();
            loadObjectIdFromQueryResult(statement, t);
        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public T get(ID id) {
        T result = null;
        try {
            result = getParameterizedTypeClass().newInstance();
            PreparedStatement statement = createSelectByIdPreparedStatement(connection, result, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                loadObjectFromQueryResult(resultSet, result);
            } else {
                result = null;
            }
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<T> getAll() {
        List<T> resultList = new ArrayList<>();
        try {
            T result = getParameterizedTypeClass().newInstance();
            PreparedStatement statement = connection.prepareStatement(createSelectAllQuery(result),
                    Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.executeQuery();
            loadObjectsListFromQueryResult(resultSet, resultList);
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public T update(T t) {
        try {
            PreparedStatement statement = createUpdatePreparedStatement(connection, t);
            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public void delete(ID id) {
        try {
            PreparedStatement statement = connection.prepareStatement(createDeleteQuery());
            statement.setObject(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String createInsertQuery(T t) {
        Class<?> clazz = t.getClass();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (columns.length() > 1) {
                columns.append(", ");
                values.append(", ");
            }
            field.setAccessible(true);
            columns.append(getColumnFromField(field.getName()));
            values.append("?");
        }
        return "INSERT INTO " +
                getTableName(clazz) +
                " (" + columns.toString() + ") VALUES (" +
                values.toString() + ")";
    }

    private String getTableName(Class<?> clazz) {
        String tableName = null;
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            tableName = table.name();
        }
        return tableName;
    }

    private String getColumnFromField(String field) {
        StringBuilder column = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            if (Character.isUpperCase(field.charAt(i))) {
                column.append("_");
            }
            column.append(field.charAt(i));
        }
        return column.toString().toUpperCase();
    }

    private PreparedStatement createInsertPreparedStatement(Connection connection, T t)
            throws SQLException, IllegalAccessException {

        PreparedStatement statement = connection.prepareStatement(createInsertQuery(t),
                Statement.RETURN_GENERATED_KEYS);
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = fields[i].get(t);
            statement.setObject(i + 1, value);
        }
        return statement;
    }

    private void loadObjectIdFromQueryResult(PreparedStatement statement, T t)
            throws SQLException, NoSuchFieldException, IllegalAccessException {

        ResultSet keys = statement.getGeneratedKeys();
        if (keys.next()) {
            Class<?> clazz = t.getClass();
            Field id = clazz.getField("id");
            id.setAccessible(true);
            id.set(t, keys.getObject("ID"));
        }
    }

    private String createSelectByIdQuery(T t) {
        Class<?> clazz = t.getClass();
        String table = getTableName(clazz);
        return "SELECT " + getColumnsFromClass(clazz) +
                " FROM " + table + " WHERE " + table + ".ID = ?";
    }

    private String getColumnsFromClass(Class<?> clazz) {
        String table = getTableName(clazz);
        StringBuilder columns = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            // Skip lists
            if (List.class.isAssignableFrom(field.getType())) {
                continue;
            }

            if (columns.length() > 1) {
                columns.append(", ");
            }
            field.setAccessible(true);
            columns.append(table).append(".")
                    .append(getColumnFromField(field.getName()));
        }
        return columns.toString();
    }

    private PreparedStatement createSelectByIdPreparedStatement(Connection connection,
                                                                T t, ID id) throws SQLException {

        PreparedStatement statement = connection.prepareStatement(createSelectByIdQuery(t),
                Statement.RETURN_GENERATED_KEYS);
        statement.setObject(1, id);
        return statement;
    }

    private void loadObjectFromQueryResult(ResultSet resultSet, T t)
            throws SQLException, IllegalAccessException {

        Class<?> clazz = t.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            // Skip lists
            if (List.class.isAssignableFrom(field.getType())) {
                continue;
            }

            field.setAccessible(true);
            String column = getColumnFromField(field.getName());
            Object value = resultSet.getObject(column);
            field.set(t, value);
        }
    }

    private String createSelectAllQuery(T t) {
        Class<?> clazz = t.getClass();
        String table = getTableName(clazz);
        return "SELECT " + getColumnsFromClass(clazz) + " FROM " + table;
    }

    private void loadObjectsListFromQueryResult(ResultSet resultSet, List<T> resultList)
            throws SQLException, IllegalAccessException, InstantiationException {

        while (resultSet.next()) {
            T result = getParameterizedTypeClass().newInstance();
            loadObjectFromQueryResult(resultSet, result);
            resultList.add(result);
        }
    }

    private String createUpdateQuery(T t) {
        Class<?> clazz = t.getClass();
        StringBuilder values = new StringBuilder();
        String where = null;
        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            String namePair = fieldName + " = ?";
            if (fieldName.equals("id")) {
                where = " WHERE " + namePair;
            } else {
                if (values.length() > 1) {
                    values.append(", ");
                }
                values.append(namePair);
            }
        }
        String table = getTableName(clazz);
        return "UPDATE " + table + " SET " + values.toString() + where;
    }

    private PreparedStatement createUpdatePreparedStatement(Connection connection, T t)
            throws SQLException, IllegalAccessException {

        Class<?> clazz = t.getClass();
        PreparedStatement statement = connection.prepareStatement(createUpdateQuery(t));
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object fieldValue = fields[i].get(t);
            String fieldName = fields[i].getName();
            if (fieldName.equals("id")) {
                statement.setObject(fields.length, fieldValue);
            } else {
                statement.setObject(i + 1, fieldValue);
            }
        }
        return statement;
    }

    private String createDeleteQuery() {
        Class<T> clazz = getParameterizedTypeClass();

        return "DELETE FROM " +
                getTableName(clazz) +
                " WHERE ID = ?";
    }

    private Class<T> getParameterizedTypeClass() {
        return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]);
    }
}
