package com.syrtsov.ddao.shards;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.sql.DataSource;

/**
 * todo: add class comments
 * Created-By: Pavel Syrtsov
 * Date: Jun 19, 2008
 * Time: 11:58:40 AM
 */
public class Shard implements Comparable {
    private int startId;
    private int endId;
    private String dsName;
    private DataSource dataSource;

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public void setEndId(int endId) {
        this.endId = endId;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public int compareTo(Object o) {
        //psdo: unit test this code
        if (o instanceof Shard) {
            Shard shard = (Shard) o;
            if (shard.endId < startId) {
                return -1;
            }
            if (endId < shard.startId) {
                return 1;
            }
            return 0;
        }
        int v = (Integer) o;
        if (v < startId) {
            return -1;
        }
        if (v > endId) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
