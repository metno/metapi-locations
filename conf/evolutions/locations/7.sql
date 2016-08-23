# --- !Ups

INSERT INTO featureType VALUES (100, "City or large town");
INSERT INTO featureType VALUES (132, "Part of a city");
INSERT INTO featureType VALUES (101, "Small town");

# --- !Downs

DELETE FROM featureType;