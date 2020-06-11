package com.streams;

import com.model.Aggregation;
import com.model.TemperatureMeasurement;
import com.model.WeatherStation;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;

@ApplicationScoped
public class TopologyProducer {

    static final String WEATHER_STATIONS_STORE = "weather-stations-store";

    private static final String WEATHER_STATIONS_TOPIC = "weather-stations";
    private static final String TEMPERATURE_VALUES_TOPIC = "temperature-values";
    private static final String TEMPERATURES_AGGREGATED_TOPIC = "temperatures-aggregated";

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        JsonbSerde<WeatherStation> weatherStationSerde = new JsonbSerde<>(WeatherStation.class);
        JsonbSerde<Aggregation> aggregationSerde = new JsonbSerde<>(Aggregation.class);

        KeyValueBytesStoreSupplier storeSupplier =
                Stores.persistentKeyValueStore(WEATHER_STATIONS_STORE);

        GlobalKTable<Integer, WeatherStation> stations =
                builder.globalTable(
                        WEATHER_STATIONS_TOPIC, Consumed.with(Serdes.Integer(), weatherStationSerde));

        builder.stream(TEMPERATURE_VALUES_TOPIC, Consumed.with(Serdes.Integer(), Serdes.String()))
                .join(
                        stations,
                        (stationId, timestampAndValue) -> stationId,
                        (timestampAndValue, station) -> {
                            String[] parts = timestampAndValue.split(";");
                            return new TemperatureMeasurement(
                                    station.id, station.name, Instant.parse(parts[0]), Double.valueOf(parts[1]));
                        })
                .groupByKey()
                .aggregate(
                        Aggregation::new,
                        (stationId, value, aggregation) -> aggregation.updateFrom(value),
                        Materialized.<Integer, Aggregation>as(storeSupplier)
                                .withKeySerde(Serdes.Integer())
                                .withValueSerde(aggregationSerde))
                .toStream()
                .to(TEMPERATURES_AGGREGATED_TOPIC, Produced.with(Serdes.Integer(), aggregationSerde));

        return builder.build();
    }
}
