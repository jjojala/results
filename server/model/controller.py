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
from .common import *

class ModelWrapper:
    def __init__(self, controller, wrapped):
        self._controller = controller
        self._wrapped = wrapped

    def __getattr__(self, member_name):
        member = self._wrapped.__getattribute__(member_name)
        if callable(member):
            def decorator(*args, **kwargs):
                self.setup()
                try:
                    result = member(*args, **kwargs)
                    if (result == self._wrapped):
                        return self
                    self.success()
                    return result
                except:
                    self.error()
                    raise
            return decorator
        else:
            return member

    def setup(self):
        print("setup()") 
        pass

    def success(self):
        #self._controller.get_connection().commit()
        print("success()")

    def error(self):
        print("error()")
        #self._controller.get_connection().rollback()

class ModelController:
    def __init__(self, dbname):
        #self._connection = sqlite3.connect(dbname)
        self._connection = None

    def get_connection(self):
        return self._connection

    def wrap(self, model):
        return ModelWrapper(self, model)
