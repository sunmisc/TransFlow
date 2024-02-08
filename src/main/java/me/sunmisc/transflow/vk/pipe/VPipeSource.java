package me.sunmisc.transflow.vk.pipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.PipeSource;
import me.sunmisc.transflow.vk.VkWire;
import me.sunmisc.transflow.vk.requests.MappedRequest;
import me.sunmisc.transflow.vk.requests.Request;
import me.sunmisc.transflow.vk.requests.VkMethod;
import me.sunmisc.transflow.vk.requests.VkRequest;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class VPipeSource implements PipeSource {

    private static final ObjectMapper OBJECT_MAPPER
            = new ObjectMapper();

    // threshold
    private static final int DEFAULT_PARTITION = 4;

    private final Spliterator<Audio> spliterator;

    public VPipeSource(Function<Integer, Request> partition) {
        this.spliterator = new PartitionPlaylist(
                partition, 0,
                new Audio[0], Integer.MAX_VALUE
        ).trySplit();
    }

    public VPipeSource(HttpClient client, String token, int ownerId) {
        this(offset -> new VkRequest(
                new VkWire("audio.get", client),
                new VkMethod(
                        new MappedRequest(Map.of(
                                "owner_id", ownerId,
                                "offset", offset,
                                "count", DEFAULT_PARTITION)
                        ),
                        token
                )));
    }


    @Override
    public boolean tryAdvance(Consumer<? super Audio> action) {
        return spliterator.tryAdvance(action);
    }

    @Override
    public Spliterator<Audio> trySplit() {
        return spliterator.trySplit();
    }

    @Override
    public long estimateSize() {
        return spliterator.estimateSize();
    }

    @Override
    public int characteristics() {
        return spliterator.characteristics();
    }

    private static final class PartitionPlaylist implements Spliterator<Audio> {

        private final Function<Integer, Request> function;
        private final int offset;

        private final Audio[] partition;
        private final int totalSize;

        private int cursor = 0;

        private PartitionPlaylist(Function<Integer, Request> function,
                                  int offset,
                                  Audio[] partition,
                                  int totalSize) {
            this.function = function;
            this.offset = offset;
            this.partition = partition;
            this.totalSize = totalSize;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Audio> action) {
            int q = cursor, fence = partition.length;
            if (q < fence) {
                action.accept(partition[q]);
                cursor = q + 1;
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<Audio> trySplit() {
            try {
                int off = offset + cursor;
                Request request = function.apply(off);
                return request.stream().map(x -> {
                    try {

                        JsonNode node = OBJECT_MAPPER.readTree(x);
                        int estimateSize = node.findValue("count").asInt();

                        List<Audio> list = new LinkedList<>();
                        node.findValue("items")
                                .elements()
                                .forEachRemaining(r -> list.add(
                                        new VEncryptedAudio(
                                                new VAudio(r))
                                ));

                        List<Audio> immutable = Collections.unmodifiableList(list);

                        return immutable.isEmpty() ? null : new PartitionPlaylist(
                                function,
                                off,
                                immutable.toArray(Audio[]::new),
                                estimateSize);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public long estimateSize() {
            return totalSize - (offset + cursor);
        }

        @Override
        public int characteristics() {
            return CONCURRENT | NONNULL | ORDERED;
        }
    }
}
