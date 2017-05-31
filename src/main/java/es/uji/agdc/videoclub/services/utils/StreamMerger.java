package es.uji.agdc.videoclub.services.utils;

import es.uji.agdc.videoclub.models.AbstractEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingByConcurrent;

/**
 * Created by Alberto on 08/01/2017.
 */
public class StreamMerger<T extends AbstractEntity> {
    private final List<Stream<T>> streamsToMerge;
    private List<T> joinedStreams;

    public StreamMerger() {
        this.streamsToMerge = new LinkedList<>();
    }

    public void addStream(Stream<T> stream) {
        if (joinedStreams != null) throw new Error("Dirty merger, generate a new one");
        streamsToMerge.add(stream);
    }

    public List<Stream<T>> getStreamsToMerge() {
        return streamsToMerge;
    }

    public Stream<T> getJoinedStreams() {
        if (joinedStreams == null) joinStreams();
        return joinedStreams.stream();
    }

    private void joinStreams() {
        Optional<Stream<T>> possibleJoinedStreams = streamsToMerge
                .stream()
                .parallel()
                .reduce(Stream::concat);
        Stream<T> joinedStream =
                possibleJoinedStreams.isPresent() ? possibleJoinedStreams.get() : Stream.empty();

        this.joinedStreams = joinedStream.collect(Collectors.toList());
    }

    public Map<Long, Long> getEntityWeights() {
        return getJoinedStreams()
                .parallel()
                .collect(groupingByConcurrent(AbstractEntity::getId, counting()));
    }

    public Stream<T> merge() {
        Map<Long, Long> entityWeights = getEntityWeights();
        Stream<T> joinedStreams = getJoinedStreams();

        return joinedStreams
                .parallel()
                .distinct()
                .sorted((entity1, entity2) -> entityWeights.get(entity2.getId()).compareTo(entityWeights.get(entity1.getId())));
    }
}
