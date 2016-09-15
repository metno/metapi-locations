# --- !Ups

CREATE TABLE locationFeature (
  name TEXT NOT NULL,
  language TEXT NOT NULL,
  type_status TEXT NOT NULL,
  legal_status TEXT NOT NULL,
  feature_type INTEGER NOT NULL
);

SELECT AddGeometryColumn('public', 'locationfeature', 'geo', 4326, 'GEOMETRY', 2);

CREATE TABLE featureType (
  id INTEGER NOT NULL,
  name TEXT NOT NULL
);

# --- !Downs

DROP TABLE locationFeature;
DROP TABLE featureType;
