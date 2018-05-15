# chargerks
Knowledge Space for CharGer

```
PS C:\chargerks> java -jar .\chargerks-1.0-SNAPSHOT.jar help
usage: java -jar chargerks [options] [extract-metadata | merge-metadata |
            delete-context | apply-intent | apply-use | ask-metadata | ask-data]
 -a,--apikey <api key>                          api key for input
                                                operations that need it,
                                                such as google
                                                distance-matrix
 -c,--contextName <context name>                name of the context of
                                                use/intent of the
                                                query/command
 -f,--format <data format>                      input database format:
                                                csv, csv-header,
                                                ascii-bordered-table,
                                                uah-distance-matrix,
                                                uah-classes
 -i,--input <input>                             input cgx graph file or
                                                database uri
 -l,--limit <limit>                             limits the number of
                                                results returned from the
                                                knowledge space
 -m,--maintainContexts                          if present, matching
                                                concepts and relations
                                                will be shown in their
                                                original contexts instead
                                                of a single graph
                                                resembling the query.
 -o,--output <file>                             output cgx graph

    --password <password>                       neo4j password

 -t,--contextType <context type>                type of context: use,
                                                intent
 -T,--maxTraversal <max var length traversal>   set the maximum number of
                                                nodes traversed in
                                                variable length relations
                                                (such as match).  Lower
                                                numbers create cheaper
                                                execution plans, but it
                                                limits the number of
                                                matching concepts that
                                                will be traversed in a
                                                pattern matching query.
    --uri <uri>                                 bolt uri to the neo4j
                                                instance
    --user <user>                               neo4j user name
```
