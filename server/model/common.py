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

def jsonify(entityException):
    return { "message": str(entityException) }

class EntityException(Exception):
    pass

class IllegalEntity(EntityException):
    def str(entityType, id, msg):
        return "Entity {} with id {} is illegal: {}".format(
            entityType, id, msg)

    def __init__(self, entityType, id, msg):
        self._entityType = entityType
        self._id = id
        self._msg = msg

    def __str__(self):
        return IllegalEntity.str(self._entityType,
                                 self._id, self._msg)

class EntityAlreadyExists(EntityException):
    def str(entityType, id):
        return "Entity {} with id {} already exists!".format(
            entityType, id)

    def __init__(self, entityType, id):
        self._entityType = entityType
        self._id = id

    def __str__(self):
        return EntityAlreadyExists.str(self._entityType, self._id)

class EntityNotFound(EntityException):
    def str(entityType, id):
        return "Entity {} with id {} not found!".format(
            entityType, id)

    def __init__(self, entityType, id):
        self._entityType = entityType
        self._id = id

    def __str__(self):
        return EntityNotFound.str(self._entityType, self._id)

class EntityConstraintViolated(EntityException):
    def str(entityType, id, msg):
        return "Constraint violated for {} with id {}: {}".format(
            entityType, id, msg)

    def __init__(self, entityType, id, msg):
        self._entityType = entityType
        self._id = id
        self._msg = msg

    def __str__(self):
        return EntityConstraintViolated.str(
            self._entityType, self._id, self._msg)
