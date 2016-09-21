//Build a jar of this java file using ANT build
//Put the jar in CASSANDRA_HOME\build\classes\main\triggers
//Run Cassandra and in your CQL Client execute the following query
//CREATE TRIGGER IF NOT EXISTS trigger_name ON table_name USING 'cassandra.CassandraPlus';

package cassandra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.apache.cassandra.config.ColumnDefinition;
import org.apache.cassandra.db.Clustering;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.db.partitions.Partition;
import org.apache.cassandra.db.rows.Cell;
import org.apache.cassandra.db.rows.Unfiltered;
import org.apache.cassandra.db.rows.UnfilteredRowIterator;
import org.apache.cassandra.triggers.ITrigger;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraPlus implements ITrigger
{
	private static final Logger logger = LoggerFactory.getLogger(CassandraPlus.class);
		
    public Collection<Mutation> augment(Partition partition)
    {
    	Map message = new HashMap();
    	
    	//Setting the ID 
    	message.put("table_id", partition.metadata().getKeyValidator().getString(partition.partitionKey().getKey()));
    	
    	//Parsing Non-ID related fields 
    	try {
            UnfilteredRowIterator it = partition.unfilteredIterator();
            while (it.hasNext()) {
                Unfiltered un = it.next();
                Clustering clt = (Clustering) un.clustering();  
                Iterator<Cell> cells = partition.getRow(clt).cells().iterator();
                Iterator<ColumnDefinition> columns = partition.getRow(clt).columns().iterator();
                                
            	while(columns.hasNext()){
            		ColumnDefinition columnDef = columns.next();
	          	    Cell cell = cells.next();
	                String data = new String(cell.value().array()); // If cell type is text
	                message.put(columnDef.toString(), data);
          	    }
            }
        } catch (Exception e) {
          
        }
    	
    	logger.debug(createJsonString(message));
    	
    	return Collections.emptyList();
    }
    
    private String createJsonString( Map<String, String> map) {
        String jsonString = "{ ";
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {

            jsonString = jsonString + "\"" + entry.getKey() + "\"";
            jsonString += " : ";
            jsonString = jsonString + getJsonValue(entry.getValue());
            jsonString += ",  ";
        }
        int i = jsonString.lastIndexOf(",");
        jsonString = jsonString.substring(0, i);
        jsonString += " }";
        return jsonString;
    }
    
    private String getJsonValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + value + "\"";
        }
    }
    
}
