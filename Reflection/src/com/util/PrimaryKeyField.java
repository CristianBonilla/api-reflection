package com.util;

import com.annotation.PrimaryKey;

import java.lang.reflect.Field;

public class PrimaryKeyField {
	private Field field;
	private PrimaryKey primaryKey;

	public PrimaryKeyField(Field field) {
		this.field = field;
		this.primaryKey = this.field.getAnnotation(PrimaryKey.class);
	}

	public String getName() {
		// return field.getName();
		return primaryKey.name();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public Field getField() {
		return field;
	}
}
