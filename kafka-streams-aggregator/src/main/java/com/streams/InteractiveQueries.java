package com.streams;

import com.model.Aggregation;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class InteractiveQueries {

  private static final Logger LOG = LoggerFactory.getLogger(InteractiveQueries.class);

  @ConfigProperty(name = "hostname")
  String host;

  @Inject
  KafkaStreams streams;

  public GetWeatherStationDataResult getWeatherStationData(int id) {

    final KeyQueryMetadata keyQueryMetadata =
            streams.queryMetadataForKey(
                    TopologyProducer.WEATHER_STATIONS_STORE, id, Serdes.Integer().serializer());

    if (keyQueryMetadata == null || keyQueryMetadata == KeyQueryMetadata.NOT_AVAILABLE) {
      LOG.warn("Found no metadata for key {}", id);
      return GetWeatherStationDataResult.notFound();
    } else if (keyQueryMetadata.getActiveHost().host().equals(host)) {
      LOG.info("Found data for key {} locally", id);
      Aggregation result = getWeatherStationStore().get(id);

      if (result != null) {
        return GetWeatherStationDataResult.found(WeatherStationData.from(result));
      } else {
        return GetWeatherStationDataResult.notFound();
      }
    } else {
      LOG.info(
              "Found data for key {} on remote host {}:{}",
              id,
              keyQueryMetadata.getActiveHost().host(),
              keyQueryMetadata.getActiveHost().port());
      return GetWeatherStationDataResult.foundRemotely(
              keyQueryMetadata.getActiveHost().host(), keyQueryMetadata.getActiveHost().port());
    }
  }

  public List<PipelineMetadata> getMetaData() {
    return streams.allMetadataForStore(TopologyProducer.WEATHER_STATIONS_STORE).stream()
            .map(
                    m ->
                            new PipelineMetadata(
                                    m.hostInfo().host() + ":" + m.hostInfo().port(),
                                    m.topicPartitions().stream()
                                            .map(TopicPartition::toString)
                                            .collect(Collectors.toSet())))
            .collect(Collectors.toList());
  }

  private ReadOnlyKeyValueStore<Integer, Aggregation> getWeatherStationStore() {
    while (true) {
      try {
        final StoreQueryParameters<ReadOnlyKeyValueStore<Integer, Aggregation>> parameters =
                StoreQueryParameters.fromNameAndType(
                        TopologyProducer.WEATHER_STATIONS_STORE, QueryableStoreTypes.keyValueStore());
        return streams.store(parameters);
      } catch (InvalidStateStoreException e) {
        // ignore, store not ready yet
      }
    }
  }
}
