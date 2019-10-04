package com.wongnai.solr.solrclusterstatecleaner;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@Setter
public class SolrClusterstateCleanerApplication implements ApplicationRunner {
	private static final String CLUSTERSTATE_JSON = "/clusterstate.json";
	private static final String LIVE_NODES = "/live_nodes";
	@Autowired
	private CuratorFramework cf;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SolrClusterstateCleanerApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args.getNonOptionArgs().size() != 1) {
			throw new IllegalArgumentException("only 1 argument - solrPath is required eg. /wongnai/solr");
		}
		clean(args.getNonOptionArgs().get(0));
	}

	private void clean(String solrPath) throws Exception {
		Set<String> liveNodes = listLiveNodes(solrPath);

		Optional<String> newState = getCleanedState(new String(cf.getData().forPath(solrPath + CLUSTERSTATE_JSON)), liveNodes);

		if (newState.isEmpty()) {
			return;
		}
		cf.setData().forPath(solrPath + CLUSTERSTATE_JSON, newState.get().getBytes());
	}

	private Optional<String> getCleanedState(String clusterstateJson, Set<String> liveNodes) throws JsonProcessingException {
		Map<String, Object> clusterstate = JacksonUtils.parse(clusterstateJson);
		boolean changed = false;
		for (Map.Entry<String, Object> collectionEntry : clusterstate.entrySet()) {
			String collectionName = collectionEntry.getKey();
			Map<String, Object> collection = (Map<String, Object>) collectionEntry.getValue();
			Map<String, Object> shards = (Map<String, Object>) collection.get("shards");
			for (Map.Entry<String, Object> shardEntry : shards.entrySet()) {
				String shardName = shardEntry.getKey();
				Map<String, Object> shard = (Map<String, Object>) shardEntry.getValue();
				Map<String, Object> replicas = (Map<String, Object>) shard.get("replicas");

				Set<String> goneReplicas = new HashSet<>();
				for (Map.Entry<String, Object> replicaEntry : replicas.entrySet()) {
					Map<String, Object> replica = (Map<String, Object>) replicaEntry.getValue();

					String nodeName = (String) replica.get("node_name");
					String state = (String) replica.get("state");
					log.info("{} {} on node {} is {}", collectionName, shardName, nodeName, state);

					if (!liveNodes.contains(nodeName)) {
						goneReplicas.add(replicaEntry.getKey());
					}
				}
				for (String r : goneReplicas) {
					replicas.remove(r);
					changed = true;
				}
			}
		}

		if (changed) {
			String newStateJson = JacksonUtils.getObjectMapper().writerWithDefaultPrettyPrinter()
					.writeValueAsString((clusterstate));
			return Optional.of(newStateJson);
		} else {
			return Optional.empty();
		}
	}

	private Set<String> listLiveNodes(String solrPath) throws Exception {
		return new LinkedHashSet<>(cf.getChildren().forPath(solrPath + LIVE_NODES));
	}
}
