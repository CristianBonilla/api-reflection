package com.orm;

import com.util.ColumnField;
import com.util.Metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class EntityManager<T> implements EntityManagerBase<T> {

	private AtomicLong idGenerator = new AtomicLong(0L);

	@Override
	public void persist(T model) throws SQLException, IllegalAccessException {
		Metamodel metamodel = Metamodel.of(model.getClass());
		String sql = metamodel.buildInsertRequest();
		// release of resources
		try (PreparedStatement statement = prepareStatementWith(sql).andParameters(model)) {
			statement.executeUpdate();
		}
	}

	@Override
	public T find(Class<T> modelClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Metamodel metamodel = Metamodel.of(modelClass);
		String sql = metamodel.buildSelectRequest();
		// release of resources
		try (PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
			ResultSet resultSet = statement.executeQuery()) {
			return buildInstanceFrom(modelClass, resultSet);
		}
	}

	public abstract Connection buildConnection() throws SQLException;

	private T buildInstanceFrom(Class<T> modelClass, ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
		Metamodel metamodel = Metamodel.of(modelClass);

		T model = modelClass.getConstructor().newInstance();
		Field primaryKeyField = metamodel.getPrimaryKey().getField();
		String primaryKeyColumnName = metamodel.getPrimaryKey().getName();
		Class<?> primaryKeyType = primaryKeyField.getType();

		resultSet.next();
		if (primaryKeyType == long.class) {
			long primaryKey = resultSet.getLong(primaryKeyColumnName);
			primaryKeyField.setAccessible(true);
			primaryKeyField.set(model, primaryKey);
		}
		List<ColumnField> columns = metamodel.getColumns();
		for (ColumnField columnField : columns) {
			Field field = columnField.getField();
			field.setAccessible(true);
			Class<?> columnType = columnField.getType();
			String columnName = columnField.getName();
			if (columnType == int.class) {
				int value = resultSet.getInt(columnName);
				field.set(model, value);
			} else if (columnType == String.class) {
				String value = resultSet.getString(columnName);
				field.set(model, value);
			}
		}

		return model;
	}

	private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
		Connection connection = buildConnection();
		PreparedStatement statement = connection.prepareStatement(sql);

		return new PreparedStatementWrapper(statement);
	}

	private class PreparedStatementWrapper {
		private PreparedStatement statement;

		public PreparedStatementWrapper(PreparedStatement preparedStatement) {
			this.statement = preparedStatement;
		}

		public PreparedStatement andParameters(T model) throws SQLException, IllegalAccessException {
			Metamodel metamodel = Metamodel.of(model.getClass());
			Class<?> primaryKeyType = metamodel.getPrimaryKey().getType();
			if (primaryKeyType == long.class) {
				long id = idGenerator.incrementAndGet();
				statement.setLong(1, id);
				Field field = metamodel.getPrimaryKey().getField();
				field.setAccessible(true);
				field.set(model, id);
			}
			List<ColumnField> columns = metamodel.getColumns();
			for (int i = 0; i < metamodel.getColumns().size(); i++) {
				ColumnField columnField = columns.get(i);
				Class<?> fieldType = columnField.getType();
				Field field = columnField.getField();
				field.setAccessible(true);
				Object value = field.get(model);
				if (fieldType == int.class) {
					statement.setInt(i + 2, (int)value);
				} else if (fieldType == String.class) {
					statement.setString(i + 2, (String)value);
				}
			}

			return statement;
		}

		public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
			if (primaryKey.getClass() == Long.class) {
				statement.setLong(1, (Long)primaryKey);
			}

			return statement;
		}
	}
}
