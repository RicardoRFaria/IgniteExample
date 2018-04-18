# Ignite Example

Project presented on The Developers Conference 2018 (TDC) to demonstrate Apache Ignite.

The project loads data inside ```/texts``` folder from ```.txt``` files and put on Ignite Cache to count letter ocurrences.

## Endpoints

- Cache Bootstrap

Load data to cache

```curl -X GET http://localhost:9000/bootstrap```

- Count Letters

Perform letters count across Ignite nodes

```curl -X GET http://localhost:9000/count```
