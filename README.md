# cassandra-trigger
Use with Cassandra 3.0+

Both files need a CASSANDRA_HOME to be configured before you use the file.

# CassandraPlus.java
//Build a jar of this java file using ANT build
//Put the jar in CASSANDRA_HOME\build\classes\main\triggers
//Run Cassandra and in your CQL Client execute the following query
//CREATE TRIGGER IF NOT EXISTS trigger_name ON table_name USING 'cassandra.CassandraPlus';

#projectBuilder.xml
Use this file for your ant build of CassandraPlus.java
