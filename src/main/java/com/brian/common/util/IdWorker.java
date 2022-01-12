package com.brian.common.util;

/**
 * SnowFlake
 */
public class IdWorker {

    /**
     * Initial timestamp, cannot be changed
     */
    private final long twepoch = 1641997293296L;

    private final long machineIdBits = 5L;

    private final long maxMachineId = -1L ^ (-1L << machineIdBits);

    private final long dataCenterIdBits = 5L;

    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    private final long sequenceIdBits = 12L;

    private final long maxSequenceId = -1L ^ (-1L << sequenceIdBits);

    private final long machineIdShift = sequenceIdBits;

    private final long dataCenterIdShift = sequenceIdBits + machineIdBits;

    private final long timestampShift = sequenceIdBits + machineIdBits + dataCenterIdBits;

    /**
     * machine id (0~31)
     */
    private long machineId;

    /**
     * datacenter id (0~31)
     */
    private long dataCenterId;

    /**
     * sequence id (0~4095)
     */
    private long sequenceId;

    /**
     * the timestamp of the last time the id was generated
     */
    private long lastTimestamp = -1L;


    public IdWorker(long machineId, long dataCenterId) {

        //TODO check
        this.machineId = machineId;
        this.dataCenterId = dataCenterId;
    }

    public synchronized long nextId() {

        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            new IllegalArgumentException();
        }

        if (timestamp == lastTimestamp) {
            sequenceId = (sequenceId + 1) & maxSequenceId;
            if (sequenceId == 0){
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequenceId = 0L;
        }

        lastTimestamp = timestamp;

        return (timestamp - twepoch) << timestampShift |
                machineId << machineIdShift |
                dataCenterId << dataCenterIdShift |
                sequenceId;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public static void main(String[] arg) {
        IdWorker idWorker = new IdWorker(10L, 10L);
        long id = idWorker.nextId();
        System.out.println(id);
    }


}
