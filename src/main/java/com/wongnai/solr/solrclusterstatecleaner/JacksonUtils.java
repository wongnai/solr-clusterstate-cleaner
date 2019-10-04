package com.wongnai.solr.solrclusterstatecleaner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilities for working with Jackson JSON.
 *
 * @author Suparit Krityakien
 */
@Slf4j
public final class JacksonUtils {
	private static ObjectMapper om;

	private JacksonUtils() {
	}

	/**
	 * Parses.
	 *
	 * @param jsonString jsonString
	 * @return map
	 */
	public static Map<String, Object> parse(String jsonString) {
		try {
			return getObjectMapper().readValue(jsonString, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			log.error("Cannot parse json " + jsonString, e);
			return new HashMap<>();
		}
	}

	/**
	 * Gets object mapper.
	 *
	 * @return Gets object mapper
	 */
	public static ObjectMapper getObjectMapper() {
		if (om == null) {
			log.warn("Object mapper is not configured correctly");
			om = new ObjectMapper();
		}
		return om;
	}

	/**
	 * Sets object mapper.
	 *
	 * @param objectMapper object mapper
	 */
	public static void setObjectMapper(ObjectMapper objectMapper) {
		JacksonUtils.om = objectMapper;
	}

	/**
	 * Serializes.
	 *
	 * @param object object
	 * @return json string
	 */
	public static String serialize(Object object) {
		try {
			return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot serialize object.", e);
		}
	}
}
