package com.wongnai.solr.solrclusterstatecleaner.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;

@ConfigurationProperties("wongnai.solr.zk")
@Configuration
@Setter
public class CuratorFrameworkConfig {
	private String host = "zookeeper:2181";

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		return CuratorFrameworkFactory.newClient(host, new RetryNTimes(3, 1000));
	}
}
