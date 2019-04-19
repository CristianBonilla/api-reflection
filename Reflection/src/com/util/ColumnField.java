package com.util;

import com.annotation.Column;

import java.lang.reflect.Field;

public class ColumnField {
	private Field field;
	private Column column;

	public ColumnField(Field field) {
		this.field = field;
		column = this.field.getAnnotation(Column.class);
	}

	public String getName() {
		// return field.getName();
		return column.name();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Field getField() {
		return field;
	}
}
