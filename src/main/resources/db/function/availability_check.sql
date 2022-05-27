
-- Check the availability of a trip before making a new reservation
CREATE OR REPLACE FUNCTION trigger_check_availability_before_reservation()
    RETURNS trigger AS
$func$
DECLARE
    total_seats BIGINT;
    total_reserved_seats BIGINT;
BEGIN
    total_seats = (SELECT t.total_seats
                   FROM trip t
                   WHERE t.id = NEW.trip_id);

    total_reserved_seats = (SELECT COALESCE(SUM(r.num_of_seats), 0)
                            FROM reservation r
                            WHERE r.trip_id = NEW.trip_id);

    IF (total_seats - total_reserved_seats) < (NEW.num_of_seats)
    THEN
        RAISE EXCEPTION 'Maximum trip capacity exceeded!';
    END IF;

    RETURN NEW;
END
$func$ LANGUAGE plpgsql;

---------------------------------------------------------------------

-- Create trigger on all "reservations" and check for availability before INSERT to avoid overbooking
DROP TRIGGER IF EXISTS check_availability
    ON "reservation";
CREATE TRIGGER check_availability
    BEFORE INSERT ON "reservation"
    FOR EACH ROW WHEN (NEW.type = 'RESERVATION') EXECUTE PROCEDURE trigger_check_availability_before_reservation();