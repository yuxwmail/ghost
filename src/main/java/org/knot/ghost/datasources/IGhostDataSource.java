package org.knot.ghost.datasources;

import java.util.Map;

import javax.sql.DataSource;

public interface IGhostDataSource {

    Map<String, DataSource> getDataSources();
}
