package com.util;

import com.annotation.Column;
import com.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Metamodel<T> {
	private Class<T> modelClass;

	public Metamodel(Class<T> modelClass) {
		this.modelClass = modelClass;
	}

	public static <T> Metamodel<T> of(Class<T> modelClass) {
		return new Metamodel<>(modelClass);
	}

	public PrimaryKeyField getPrimaryKey() {
		Field[] fields = modelClass.getDeclaredFields();
		for (Field field : fields) {
			PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
			if (primaryKey != null) {
				PrimaryKeyField primaryKeyField = new PrimaryKeyField(field);
				return primaryKeyField;
			}
		}

		throw new IllegalArgumentException("No primary key found in class: " + modelClass.getSimpleName());
	}

	public List<ColumnField> getColumns() {
		List<ColumnField> columnFields = new ArrayList<>();
		Field[] fields = modelClass.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				ColumnField columnField = new ColumnField(field);
				columnFields.add(columnField);
			}
		}

		return columnFields;
	}

	public String buildSelectRequest() {
		// select id, name, age from Person where id = ?
		String columnElement = buildColumnNames();

		return "SELECT " + columnElement + " FROM " + modelClass.getSimpleName() +
				" WHERE " + getPrimaryKey().getName() + " = ?";
	}

	public String buildInsertRequest() {
		// insert into Person (id, name, age) value (?, ?, ?)
		String columnElement = buildColumnNames();

		String questionMarksElement = buildQuestionMarksElement();

		return "INSERT INTO " +  this.modelClass.getSimpleName() +
				" (" + columnElement + ") VALUES(" + questionMarksElement + ")";
	}

	private String buildQuestionMarksElement() {
		int numberOfColumns = getColumns().size() + 1;

		return IntStream.range(0, numberOfColumns)
				.mapToObj(i -> "?").collect(Collectors.joining(", "));
	}

	private String buildColumnNames() {
		String primaryKeyColumnName = getPrimaryKey().getName();

		List<String> columnNames = getColumns().stream()
				.map(ColumnField::getName)
				.collect(Collectors.toList());
		columnNames.add(0, primaryKeyColumnName);

		return String.join(", ", columnNames);
	}
}
