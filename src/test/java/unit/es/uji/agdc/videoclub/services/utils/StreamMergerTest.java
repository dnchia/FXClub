package unit.es.uji.agdc.videoclub.services.utils;

import es.uji.agdc.videoclub.models.AbstractEntity;
import es.uji.agdc.videoclub.services.utils.StreamMerger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Alberto on 08/01/2017.
 */
public class StreamMergerTest {

    private StreamMerger<AbstractEntity> streamMerger;

    @Before
    public void setUp() throws Exception {
        streamMerger = new StreamMerger<>();
    }

    @Test
    public void addStream_adds() throws Exception {
        streamMerger.addStream(Stream.empty());
        assertEquals(1, streamMerger.getStreamsToMerge().size());
    }

    @Test(expected = Error.class)
    public void addStream_onAddAfterJoinStreams_fails() throws Exception {
        streamMerger.getJoinedStreams();
        streamMerger.addStream(Stream.empty());
    }

    @Test
    public void joinStreams_withNoStream_returnsEmptyStream() throws Exception {
        assertEquals(0, streamMerger.getJoinedStreams().count());
    }

    @Test
    public void joinStreams_withOneStreamWithOneEntity_returnsOneStreamAndEntity() throws Exception {
        AbstractEntity entity = mock(AbstractEntity.class);
        when(entity.getId()).thenReturn(0L);
        streamMerger.addStream(Stream.of(entity));

        Stream<AbstractEntity> entityStream = streamMerger.getJoinedStreams();

        assertEquals(1, entityStream.count());
    }


    @Test
    public void joinStreams_withOneStreamWithMultipleEntities_returnsOneStreamWithMultipleEntities() throws Exception {
        int COUNT = 10;
        List<AbstractEntity> entities = new LinkedList<>();
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(Long.valueOf(i));
            entities.add(entity);
        });
        streamMerger.addStream(entities.stream());

        Stream<AbstractEntity> entityStream = streamMerger.getJoinedStreams();

        assertEquals(COUNT, entityStream.count());
    }

    @Test
    public void joinStreams_withMultipleStreamsWithOneEntity_returnsOneStreamWithMultipleEntities() throws Exception {
        int COUNT = 10;
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(0L);
            streamMerger.addStream(Stream.of(entity));
        });

        Stream<AbstractEntity> entityStream = streamMerger.getJoinedStreams();

        assertEquals(COUNT, entityStream.count());
    }

    @Test
    public void joinStreams_canOperateMultipleTimes_returnsSameResult() throws Exception {
        AbstractEntity entity = mock(AbstractEntity.class);
        when(entity.getId()).thenReturn(0L);
        streamMerger.addStream(Stream.of(entity));

        assertEquals(streamMerger.getJoinedStreams().count(), streamMerger.getJoinedStreams().count());
    }

    @Test
    public void calculateEntityWeights_withNoStream_returnsEmptyMap() throws Exception {
        assertEquals(0, streamMerger.getEntityWeights().size());
    }

    @Test
    public void calculateEntityWeights_withOneStreamWithOneEntity_returnsEntityWithWeight1() throws Exception {
        AbstractEntity entity = mock(AbstractEntity.class);
        when(entity.getId()).thenReturn(0L);
        streamMerger.addStream(Stream.of(entity));

        Map<Long, Long> entityWeights = streamMerger.getEntityWeights();

        assertEquals(1, (long) entityWeights.get(0L));
    }


    @Test
    public void calculateEntityWeights_withOneStreamWithMultipleEntities_returnsSameWeightForEachEntity() throws Exception {
        int COUNT = 10;
        List<AbstractEntity> entities = new LinkedList<>();
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(Long.valueOf(i));
            entities.add(entity);
        });
        streamMerger.addStream(entities.stream());

        Map<Long, Long> entityWeights = streamMerger.getEntityWeights();

        assertTrue(entityWeights.values().stream().allMatch(weight -> weight == 1));
    }

    @Test
    public void calculateEntityWeights_withMultipleStreamsWithOneEntity_returnsOneEntityWithFullWeight() throws Exception {
        int COUNT = 10;
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(0L);
            streamMerger.addStream(Stream.of(entity));
        });

        Map<Long, Long> entityWeights = streamMerger.getEntityWeights();

        assertEquals(COUNT, (long) entityWeights.get(0L));
    }

    @Test
    public void merge_withNoStream_returnsEmptyStream() throws Exception {
        assertEquals(0, streamMerger.merge().count());
    }

    @Test
    public void merge_twoStreamsWithSameEntities_returnsAStreamWithUniqueEntities() throws Exception {
        List<AbstractEntity> entities = new LinkedList<>();
        int COUNT = 2;
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(Long.valueOf(i));
            entities.add(entity);
        });

        streamMerger.addStream(entities.stream());
        streamMerger.addStream(entities.stream());

        Stream<AbstractEntity> mergedStreams = streamMerger.merge();

        assertEquals(COUNT, mergedStreams.count());
    }

    @Test
    public void merge_twoStreamsWithDifferentEntities_returnsAStreamWithWeigherEntityAsFirstOne() throws Exception {
        List<AbstractEntity> entities = new LinkedList<>();
        int COUNT = 10;
        int weigherEntityId = 7;
        IntStream.range(0, COUNT).forEach(i -> {
            AbstractEntity entity = mock(AbstractEntity.class);
            when(entity.getId()).thenReturn(Long.valueOf(i));
            entities.add(entity);
        });

        streamMerger.addStream(Stream.of(entities.get(weigherEntityId)));
        streamMerger.addStream(entities.stream());

        Stream<AbstractEntity> mergedStreams = streamMerger.merge();

        assertEquals(weigherEntityId, (long) mergedStreams.findFirst().get().getId());
    }

    @Test
    public void merge_multipleEmptyStreams_returnsEmptyStream() throws Exception {
        int COUNT = 10;
        IntStream.range(0, COUNT).forEach(i ->
                streamMerger.addStream(Stream.empty()));

        Stream<AbstractEntity> mergedStreams = streamMerger.merge();

        assertEquals(0, mergedStreams.count());
    }

    @After
    public void tearDown() throws Exception {
        streamMerger = null;
    }

}