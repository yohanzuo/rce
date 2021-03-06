/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.datamanagement.commons;

import java.io.Serializable;
import java.util.Set;

import de.rcenvironment.core.communication.common.InstanceNodeId;

/**
 * Identifier for data references holding {@link BinaryReference}s.
 * 
 * @author Jan Flink
 */
public final class DataReference implements Serializable {

    private static final long serialVersionUID = -5443653424654542352L;

    private final String dataReferenceKey;

    private final InstanceNodeId storageInstanceId;

    private Set<BinaryReference> binaryReferences;

    public DataReference(String dataReferenceKey, InstanceNodeId storageInstanceId, Set<BinaryReference> binaryReferences) {
        this.dataReferenceKey = dataReferenceKey;
        this.storageInstanceId = storageInstanceId;
        this.binaryReferences = binaryReferences;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DataReference) {
            final DataReference other = (DataReference) obj;
            return dataReferenceKey.equals(other.dataReferenceKey);
        }
        return false;
    }

    /**
     * @return the key of this {@link DataReference}.
     */

    public String getDataReferenceKey() {
        return dataReferenceKey;
    }

    /**
     * @return the {@link InstanceNodeId} of the platform this {@link DataReference} is hosted.
     */
    public InstanceNodeId getInstanceId() {
        return storageInstanceId;
    }

    @Override
    public String toString() {
        return dataReferenceKey.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dataReferenceKey.hashCode();
        return result;
    }

    public Set<BinaryReference> getBinaryReferences() {
        return binaryReferences;
    }
}
