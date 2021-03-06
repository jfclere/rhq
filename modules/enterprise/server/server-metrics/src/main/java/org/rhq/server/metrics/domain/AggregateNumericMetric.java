/*
 * RHQ Management Platform
 * Copyright (C) 2005-2013 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package org.rhq.server.metrics.domain;

import com.google.common.base.Objects;

/**
 * @author John Sanda
 */
public class AggregateNumericMetric implements NumericMetric {

    private int scheduleId;

    private Bucket bucket;

    private Double min = Double.NaN;

    private Double max = Double.NaN;

    private Double avg = Double.NaN;

    private long timestamp;

    public AggregateNumericMetric() {
    }

    public AggregateNumericMetric(int scheduleId, Bucket bucket, Double avg, Double min, Double max, long timestamp) {
        this.scheduleId = scheduleId;
        this.bucket = bucket;
        this.avg = avg;
        this.min = min;
        this.max = max;
        this.timestamp = timestamp;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(AggregateNumericMetric.class)
            .add("scheduleId", scheduleId)
            .add("bucket", bucket)
            .add("avg", avg)
            .add("max", max)
            .add("min", min)
            .add("timestamp", timestamp)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AggregateNumericMetric metric = (AggregateNumericMetric) o;

        if (scheduleId != metric.scheduleId) return false;
        if (bucket != metric.bucket) return false;
        if (timestamp != metric.timestamp) return false;
        if (!avg.equals(metric.avg)) return false;
        if (!max.equals(metric.max)) return false;
        if (!min.equals(metric.min)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scheduleId;
        result = 31 * result + bucket.hashCode();
        result = 31 * result + min.hashCode();
        result = 31 * result + max.hashCode();
        result = 31 * result + avg.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
