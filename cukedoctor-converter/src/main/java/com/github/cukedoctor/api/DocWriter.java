package com.github.cukedoctor.api;

/**
 * Created by pestano on 10/06/15.
 */
public interface DocWriter<T> {

	DocWriter write(Object value);

	DocWriter write(Object... value);

	T getCurrentDoc();

	void clear();
}
