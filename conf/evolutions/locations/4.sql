# --- !Ups

CREATE VIEW get_locations_v AS
SELECT
  t1.name AS name,
  t2.name AS feature,
  ST_X(geo) AS lon,
  ST_Y(geo) AS lat
FROM
  locationFeature t1 LEFT OUTER JOIN featureType t2
    ON (t1.feature_type = t2.id);

# --- !Downs

DROP VIEW IF EXISTS get_locations_v;