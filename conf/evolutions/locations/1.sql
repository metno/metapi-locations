# --- !Ups

CREATE TABLE locationFeature (
  name TEXT NOT NULL
);

SELECT AddGeometryColumn('public', 'locationfeature', 'geo', 4326, 'GEOMETRY', 2);

# --- !Downs

DROP TABLE locationFeature;