package io.trane.ndbc.postgres.encoding;

import io.trane.ndbc.value.ShortArrayValue;

final class ShortArrayEncoding extends ArrayEncoding<Short, ShortArrayValue> {

	private final ShortEncoding shortEncoding;
	private final Short[] emptyArray = new Short[0];

	public ShortArrayEncoding(final ShortEncoding shortEncoding) {
		this.shortEncoding = shortEncoding;
	}

	@Override
	public final Integer oid() {
		return Oid.INT2_ARRAY;
	}

	@Override
	public final Class<ShortArrayValue> valueClass() {
		return ShortArrayValue.class;
	}

	@Override
	protected Short[] newArray(final int length) {
		return new Short[length];
	}

	@Override
	protected Short[] emptyArray() {
		return emptyArray;
	}

	@Override
	protected Encoding<Short, ?> itemEncoding() {
		return shortEncoding;
	}

	@Override
	protected ShortArrayValue box(final Short[] value) {
		return new ShortArrayValue(value);
	}

	@Override
	protected Short[] unbox(final ShortArrayValue value) {
		return value.getShortArray();
	}
}
