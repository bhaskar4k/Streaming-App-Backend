package com.app.authentication.bloomfilter;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BloomFilter<T> implements Serializable {
    private final BitSet bitSet;
    private final int bitSize;
    private final int numHashFunctions;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private int filterSize;

    public BloomFilter(int expectedInsertions, double falsePositiveRate) {
        if (falsePositiveRate <= 0.0 || falsePositiveRate >= 1.0) {
            throw new IllegalArgumentException("False positive rate must be between 0 and 1.");
        }

        this.bitSize = optimalBitSize(expectedInsertions, falsePositiveRate);
        this.numHashFunctions = optimalHashFunctions(expectedInsertions, bitSize);
        this.bitSet = new BitSet(bitSize);
        this.filterSize = 0;
    }

    public void add(T item) {
        lock.writeLock().lock();
        try {
            byte[] bytes = serialize(item);
            long[] hashes = murmurHash3_x64_128(bytes);

            for (int i = 0; i < numHashFunctions; i++) {
                int combinedHash = (int) ((hashes[0] + i * hashes[1]) & 0x7fffffff);
                int index = combinedHash % bitSize;
                bitSet.set(index);
            }
        } finally {
            lock.writeLock().unlock();
            this.filterSize++;
            System.out.println(this.filterSize + ((this.filterSize <= 1) ? " Item" : " Items") + " Have Been Seeded");
        }
    }

    public boolean mightContain(T item) {
        lock.readLock().lock();
        try {
            byte[] bytes = serialize(item);
            long[] hashes = murmurHash3_x64_128(bytes);

            for (int i = 0; i < numHashFunctions; i++) {
                int combinedHash = (int) ((hashes[0] + i * hashes[1]) & 0x7fffffff);
                int index = combinedHash % bitSize;
                if (!bitSet.get(index)) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.readLock().unlock();
        }
    }

    private int getFilterSize(){
        return this.filterSize;
    }

    private int optimalBitSize(int n, double p) {
        return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    private int optimalHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    private byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    // MurmurHash3 128-bit (x64 variant)
    private long[] murmurHash3_x64_128(byte[] data) {
        final int length = data.length;
        final int nblocks = length >> 4; // 16 bytes per block

        long h1 = 0;
        long h2 = 0;

        long c1 = 0x87c37b91114253d5L;
        long c2 = 0x4cf5ad432745937fL;

        ByteBuffer buffer = ByteBuffer.wrap(data);

        for (int i = 0; i < nblocks; i++) {
            long k1 = buffer.getLong();
            long k2 = buffer.getLong();

            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;

            h1 = Long.rotateLeft(h1, 27);
            h1 += h2;
            h1 = h1 * 5 + 0x52dce729;

            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;

            h2 = Long.rotateLeft(h2, 31);
            h2 += h1;
            h2 = h2 * 5 + 0x38495ab5;
        }

        long k1 = 0;
        long k2 = 0;

        int tailStart = nblocks << 4;
        int remaining = length & 15;

        for (int i = 0; i < remaining; i++) {
            byte b = data[tailStart + i];
            if (i < 8) {
                k1 |= ((long) b & 0xff) << (i * 8);
            } else {
                k2 |= ((long) b & 0xff) << ((i - 8) * 8);
            }
        }

        if (remaining > 0) {
            k1 *= c1;
            k1 = Long.rotateLeft(k1, 31);
            k1 *= c2;
            h1 ^= k1;

            k2 *= c2;
            k2 = Long.rotateLeft(k2, 33);
            k2 *= c1;
            h2 ^= k2;
        }

        h1 ^= length;
        h2 ^= length;

        h1 += h2;
        h2 += h1;

        h1 = fmix64(h1);
        h2 = fmix64(h2);

        h1 += h2;
        h2 += h1;

        return new long[]{h1, h2};
    }

    private long fmix64(long k) {
        k ^= k >>> 33;
        k *= 0xff51afd7ed558ccdL;
        k ^= k >>> 33;
        k *= 0xc4ceb9fe1a85ec53L;
        k ^= k >>> 33;
        return k;
    }
}

