import time

def _time_ms():
    # time_ns() returns the nanoseconds since epoch.
    # For Windows and most Unixes it's Jan 1st, 1970.
    # TODO: Change to justify timestampe for other os's. 
    return time.time_ns() / 1000

def time_service(func):
    def wrapper(*args, **kwargs):
        entry_time = _time_ms()
        results = func(*args, **kwargs)

        timespan = "%d,%d" % (entry_time, _time_ms())
        if (len(results) < 3):
            return results[0], results[1], { 'X-Timespan' : timespan }
        
        results[2]['X-Timespan'] = timespan
        return results

    return wrapper