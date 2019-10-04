package com.wongnai.solr.solrclusterstatecleaner.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wongnai.solr.solrclusterstatecleaner.JacksonUtils;

import lombok.Setter;

@Configuration
@Setter
public class JacksonUtilsConfig implements InitializingBean {
	@Override
	public void afterPropertiesSet() throws Exception {
		JacksonUtils.setObjectMapper(new ObjectMapper());
	}
}
