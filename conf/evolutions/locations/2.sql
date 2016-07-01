# --- !Ups

INSERT INTO locationFeature VALUES ('Moen', st_geomfromtext('POINT(8.118306 58.221361)',4326));
INSERT INTO locationFeature VALUES ('Ulsvannet', st_geomfromtext('POINT(8.207000 58.224531)',4326));
INSERT INTO locationFeature VALUES ('Oslo', st_geomfromtext('POINT(10.7522 59.9139)',4326));

# --- !Downs

DELETE FROM locationFeature;