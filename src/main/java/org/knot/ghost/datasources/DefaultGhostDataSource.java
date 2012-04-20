package org.knot.ghost.datasources;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

 
public class DefaultGhostDataSource implements IGhostDataSource {
    private Map<String, DataSource>        dataSources             = new HashMap<String, DataSource>();

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }
    
    public void setDataSources(Map<String, DataSource> dataSources) {
        for (Map.Entry<String, DataSource> entry : dataSources.entrySet()) {
            DataSource dataSource = entry.getValue();
            dataSource = new LazyConnectionDataSourceProxy(dataSource);
            if (dataSource instanceof TransactionAwareDataSourceProxy) {
                // If we got a TransactionAwareDataSourceProxy, we need to
                // perform
                // transactions for its underlying target DataSource, else data
                // access code won't see properly exposed transactions (i.e.
                // transactions for the target DataSource).
                dataSource = ((TransactionAwareDataSourceProxy) dataSource)
                        .getTargetDataSource();
            }
        }
        this.dataSources = dataSources;
    }
}
