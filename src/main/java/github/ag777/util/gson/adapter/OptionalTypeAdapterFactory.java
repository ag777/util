package github.ag777.util.gson.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Optional类型适配器，将Optional本身视为其包装值进行JSON转换。
 *
 * @author ag777
 * @version 2026/6/15
 */
public class OptionalTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (type.getRawType() != Optional.class) {
			return null;
		}

		Type valueType = getValueType(type.getType());
		TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
		@SuppressWarnings("unchecked")
		TypeAdapter<T> adapter = (TypeAdapter<T>) new OptionalTypeAdapter<>(valueAdapter);
		return adapter;
	}

	private Type getValueType(Type type) {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		}
		return Object.class;
	}

	private static class OptionalTypeAdapter<T> extends TypeAdapter<Optional<T>> {

		private final TypeAdapter<T> valueAdapter;

		@SuppressWarnings("unchecked")
		private OptionalTypeAdapter(TypeAdapter<?> valueAdapter) {
			this.valueAdapter = (TypeAdapter<T>) valueAdapter;
		}

		@Override
		public void write(JsonWriter out, Optional<T> value) throws IOException {
			if (value == null || !value.isPresent()) {
				out.nullValue();
				return;
			}
			valueAdapter.write(out, value.get());
		}

		@Override
		public Optional<T> read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return Optional.empty();
			}
			return Optional.ofNullable(valueAdapter.read(in));
		}
	}
}
