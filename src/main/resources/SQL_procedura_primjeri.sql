DROP TABLE popular_routes;

CREATE TABLE popular_routes (
				path_id     INT    NOT NULL
				,lat_from        DOUBLE PRECISION   NOT NULL
				,lng_from         DOUBLE PRECISION  NOT NULL
				,lat_to        DOUBLE PRECISION   NOT NULL
				,lng_to         DOUBLE PRECISION  NOT NULL
				,counter     INT  NOT NULL
				,stored_at timestamptz NOT NULL DEFAULT now());


CREATE OR REPLACE FUNCTION upsert_popular_routes(path_id int, latFrom DECIMAL, lngFrom DECIMAL, latTo DECIMAL, lngTo DECIMAL)
RETURNS VOID AS $$
DECLARE
BEGIN
UPDATE popular_routes SET counter = counter + 1, stored_at = now()
						WHERE popular_routes.lat_from = latFrom 
						AND popular_routes.lng_from = lngFrom 
						AND popular_routes.lat_to = latTo 
						AND popular_routes.lng_to = lngTo;
					IF NOT FOUND THEN
					 INSERT INTO popular_routes values (path_id, latFrom, lngFrom, latTo, lngTo, 1, now());
					END IF;
END;
$$ LANGUAGE 'plpgsql';
select upsert_popular_routes(123, 16.156586, 48.6415, 16.146546, 48.54415);
select * from popular_routes;
SELECT counter FROM popular_routes ORDER BY counter DESC LIMIT 1;