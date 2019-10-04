# Solr's clusterstate.json cleaner

If solrcloud is running with autoscale cluster enabled, there may be lots of "gone" nodes in clusterstate.json.

This program cleans those 'gone' nodes out of the clusterstate.json.

## Build and dockerize

    mvn clean package
    docker build . -t clusterstatecleaner:0.1.0
    
## Running with docker

The program requires only one argument which is solr path in zookeeper. You may need to change zookeeper host using the "wongnai.solr.zk.host" property.

    docker run --rm clusterstatecleaner:0.1.0 --wongnai.solr.zk.host=172.17.0.1:2181 wongnai/solr
    
## Environment variable
| Name | Description |
|------|-------------|
|wongnai.solr.zk.host | Zookeeper host with port. Default value is zookeeper:2181 | 

