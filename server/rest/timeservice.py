# -*- coding: utf-8 -*-
"""
   Copyright 2019 Jari ojala (jari.ojala@iki.fi)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
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
