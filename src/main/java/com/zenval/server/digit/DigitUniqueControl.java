package com.zenval.server.digit;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitUniqueControl {
    private Set<String> processed;
    private Set<String> duplicated;

    public DigitUniqueControl() {
        processed = Sets.newConcurrentHashSet();
        duplicated = Sets.newConcurrentHashSet();
    }

    public synchronized boolean isUnique(String candidate) {
        if (processed.contains(candidate)) {
            duplicated.add(candidate);
            return false;
        }
        processed.add(candidate);
        return true;
    }

    public int getProcessedSize() {
        return processed.size();
    }

    public int getDuplicatedSize() {
        return duplicated.size();
    }
}
